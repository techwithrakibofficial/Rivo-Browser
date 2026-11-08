package com.rivo.browser.engine.gecko

import android.content.Context
import android.view.ViewGroup
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView

/**
 * Owns the single GeckoRuntime for the application process.
 * The runtime must outlive individual tab sessions.
 */
class GeckoBrowserRuntime(context: Context) {
    val runtime: GeckoRuntime = GeckoRuntime.create(
        context.applicationContext,
        GeckoRuntimeSettings.Builder().build()
    )
}

interface GeckoTabListener {
    fun onLocationChanged(url: String)
    fun onTitleChanged(title: String?)
    fun onLoadingChanged(loading: Boolean, progress: Int)
    fun onNavigationChanged(canGoBack: Boolean, canGoForward: Boolean)
    fun onContentCrashed()
}

/** One GeckoSession per browser tab. */
class GeckoBrowserTab(
    context: Context,
    runtime: GeckoRuntime,
    val isPrivate: Boolean,
    private val listener: GeckoTabListener
) {
    val session: GeckoSession = GeckoSession(
        GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .build()
    )
    val view: GeckoView = GeckoView(context)
    private var disposed = false

    init {
        session.setContentDelegate(object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                listener.onTitleChanged(title)
            }

            override fun onCrash(session: GeckoSession) {
                listener.onContentCrashed()
            }

            override fun onKill(session: GeckoSession) {
                listener.onContentCrashed()
            }
        })
        session.setProgressDelegate(object : GeckoSession.ProgressDelegate {
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
                url: String,
                permissions: List<GeckoSession.PermissionDelegate.ContentPermission>?,
                hasUserGesture: Boolean?
            ) {
                listener.onLocationChanged(url)
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                listener.onNavigationChanged(canGoBack, session.canGoForward())
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                listener.onNavigationChanged(session.canGoBack(), canGoForward)
            }
        })
        session.open(runtime)
        view.setSession(session)
    }

    fun load(url: String) {
        check(!disposed)
        session.loadUri(url)
    }

    fun goBack() { if (!disposed && session.canGoBack()) session.goBack() }
    fun goForward() { if (!disposed && session.canGoForward()) session.goForward() }
    fun reload() { if (!disposed) session.reload() }
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
        (view.parent as? ViewGroup)?.removeView(view)
        view.releaseSession()
        session.close()
    }
}
