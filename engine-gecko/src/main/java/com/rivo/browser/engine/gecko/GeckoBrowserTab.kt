package com.rivo.browser.engine.gecko

import android.content.Context
import android.view.ViewGroup
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView
import org.mozilla.geckoview.WebResponse
import java.io.InputStream

/**
 * Process-scoped GeckoRuntime provider. GeckoView permits only one active Gecko
 * instance; keep it alive across Activity/ViewModel recreation and close only
 * individual tab sessions during normal navigation.
 */
object GeckoBrowserRuntime {
    @Volatile
    private var instance: GeckoRuntime? = null

    fun get(context: Context): GeckoRuntime {
        return instance ?: synchronized(this) {
            instance ?: GeckoRuntime.create(
                context.applicationContext,
                GeckoRuntimeSettings.Builder().build()
            ).also { instance = it }
        }
    }
}

interface GeckoTabListener {
    fun onLocationChanged(url: String)
    fun onTitleChanged(title: String?)
    fun onLoadingChanged(loading: Boolean, progress: Int)
    fun onNavigationChanged(canGoBack: Boolean, canGoForward: Boolean)
    fun onContentCrashed()
    fun onSecurityChanged(secure: Boolean, exception: Boolean)
    fun onExternalDownload(url: String, mimeType: String?, disposition: String?, body: InputStream?)
    fun onSitePermissionRequest(origin: String, permission: String, privateMode: Boolean, respond: (Boolean) -> Unit)
}

/** One GeckoSession per browser tab. */
class GeckoBrowserTab(
    private val runtime: GeckoRuntime,
    val isPrivate: Boolean,
    private val listener: GeckoTabListener
) {
    val session: GeckoSession = GeckoSession(
        GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .build()
    )
    private var attachedView: GeckoView? = null
    private var disposed = false
    private var canGoBack = false
    private var canGoForward = false
    private var crashed = false
    private var currentUrl = "about:blank"

    init {
        session.setContentDelegate(object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                listener.onTitleChanged(title)
            }

            override fun onCrash(session: GeckoSession) {
                crashed = true
                listener.onContentCrashed()
            }

            override fun onKill(session: GeckoSession) {
                crashed = true
                listener.onContentCrashed()
            }

            override fun onExternalResponse(session: GeckoSession, response: WebResponse) {
                listener.onExternalDownload(
                    response.uri,
                    response.headers["content-type"],
                    response.headers["content-disposition"],
                    response.body
                )
            }
        })
        session.setPermissionDelegate(object : GeckoSession.PermissionDelegate {
            override fun onContentPermissionRequest(
                session: GeckoSession,
                permission: GeckoSession.PermissionDelegate.ContentPermission
            ): GeckoResult<Int> {
                val permissionName = when (permission.permission) {
                    GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION -> "notifications"
                    GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE -> "persistent storage"
                    else -> return GeckoResult.fromValue(
                        GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY
                    )
                }
                if (permission.privateMode != isPrivate) {
                    return GeckoResult.fromValue(GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY)
                }
                val result = GeckoResult<Int>()
                listener.onSitePermissionRequest(permission.uri, permissionName, permission.privateMode) { granted ->
                    result.complete(
                        if (granted) GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW
                        else GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY
                    )
                }
                return result
            }

            override fun onAndroidPermissionsRequest(
                session: GeckoSession,
                permissions: Array<String>?,
                callback: GeckoSession.PermissionDelegate.Callback
            ) {
                callback.reject()
            }

            override fun onMediaPermissionRequest(
                session: GeckoSession,
                uri: String,
                video: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                audio: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                callback: GeckoSession.PermissionDelegate.MediaCallback
            ) {
                callback.reject()
            }
        })
        session.setProgressDelegate(object : GeckoSession.ProgressDelegate {
            override fun onSecurityChange(
                session: GeckoSession,
                securityInfo: GeckoSession.ProgressDelegate.SecurityInformation
            ) {
                listener.onSecurityChanged(securityInfo.isSecure, securityInfo.isException)
            }

            override fun onPageStart(session: GeckoSession, url: String) {
                listener.onLoadingChanged(true, 0)
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                listener.onLoadingChanged(progress < 100, progress)
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                listener.onLoadingChanged(false, 100)
            }
        })
        session.setNavigationDelegate(object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                permissions: List<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean
            ) {
                if (url.isNullOrBlank()) return
                currentUrl = url
                listener.onLocationChanged(url)
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                this@GeckoBrowserTab.canGoBack = canGoBack
                listener.onNavigationChanged(this@GeckoBrowserTab.canGoBack, this@GeckoBrowserTab.canGoForward)
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                this@GeckoBrowserTab.canGoForward = canGoForward
                listener.onNavigationChanged(this@GeckoBrowserTab.canGoBack, this@GeckoBrowserTab.canGoForward)
            }
        })
        session.open(runtime)
    }

    /** Create a UI-context GeckoView for the current Activity window. */
    fun createView(context: Context): GeckoView {
        check(!disposed)
        releaseView()
        return GeckoView(context).also { newView ->
            newView.setSession(session)
            attachedView = newView
        }
    }

    /** Detach the current Activity-bound view while keeping this tab session alive. */
    fun releaseView() {
        val oldView = attachedView ?: return
        (oldView.parent as? ViewGroup)?.removeView(oldView)
        oldView.releaseSession()
        attachedView = null
    }

    fun load(url: String) {
        check(!disposed)
        currentUrl = url
        if (crashed) {
            session.open(runtime)
            crashed = false
        }
        session.loadUri(url)
    }

    fun goBack() { if (!disposed && !crashed && canGoBack) session.goBack() }
    fun goForward() { if (!disposed && !crashed && canGoForward) session.goForward() }
    fun reload() {
        if (disposed) return
        if (crashed) {
            session.open(runtime)
            crashed = false
            session.loadUri(currentUrl)
        } else {
            session.reload()
        }
    }
    fun stop() { if (!disposed) session.stop() }

    fun setActive(active: Boolean) {
        if (!disposed) {
            session.setActive(active)
            session.setFocused(active)
        }
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        releaseView()
        session.close()
    }
}
