package com.rivo.browser.engine.gecko

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.net.Uri
import android.view.ViewGroup
import org.mozilla.geckoview.ExperimentalGeckoViewApi
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.PageExtractionController
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView
import org.mozilla.geckoview.WebResponse
import org.mozilla.geckoview.WebNotification
import org.mozilla.geckoview.WebNotificationDelegate
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
                GeckoRuntimeSettings.Builder()
                    .remoteDebuggingEnabled(false)
                    .consoleOutput(false)
                    .build()
            ).also { runtime ->
                runtime.setWebNotificationDelegate(RivoWebNotificationDelegate(context.applicationContext))
                instance = runtime
            }
        }
    }
}

interface GeckoTabListener {
    fun onLocationChanged(url: String, trackingProtectionException: Boolean)
    fun onTitleChanged(title: String?)
    fun onLoadingChanged(loading: Boolean, progress: Int)
    fun onNavigationChanged(canGoBack: Boolean, canGoForward: Boolean)
    fun onContentCrashed()
    fun onSecurityChanged(secure: Boolean, exception: Boolean)
    fun onExternalDownload(url: String, mimeType: String?, disposition: String?, contentLength: Long?, body: InputStream?)
    fun onSitePermissionRequest(origin: String, permission: String, privateMode: Boolean, onShown: () -> Unit, respond: (Boolean) -> Unit)
    fun onAndroidPermissionRequest(permissions: List<String>, respond: (Boolean) -> Unit)
}

data class GeckoStoredSitePermission(
    val origin: String,
    val permissionName: String,
    val allowed: Boolean,
    val canRevoke: Boolean,
    val contextKey: String?,
    internal val permission: GeckoSession.PermissionDelegate.ContentPermission
)

/** Allow only site permissions that Rivo currently mediates and can explain. */
object GeckoSitePermissionPolicy {
    fun shouldPrompt(
        permission: Int,
        secureOrigin: Boolean,
        privateMode: Boolean = false
    ): Boolean = secureOrigin &&
        !(privateMode && permission == GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION) &&
        permission in setOf(
            GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION,
            GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION,
            GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE,
            GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_INAUDIBLE
        )

    fun shouldDisplay(privateMode: Boolean, value: Int): Boolean =
        !privateMode && value != GeckoSession.PermissionDelegate.ContentPermission.VALUE_PROMPT

    fun shouldDisplay(permission: GeckoSession.PermissionDelegate.ContentPermission): Boolean =
        shouldDisplay(permission.privateMode, permission.value)

    fun label(permission: Int): String? = when (permission) {
        GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION -> "Notifications"
        GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION -> "Location"
        GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE -> "Persistent storage"
        GeckoSession.PermissionDelegate.PERMISSION_XR -> "Immersive XR"
        GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE -> "Audible autoplay"
        GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_INAUDIBLE -> "Silent autoplay"
        GeckoSession.PermissionDelegate.PERMISSION_MEDIA_KEY_SYSTEM_ACCESS -> "Protected media"
        GeckoSession.PermissionDelegate.PERMISSION_TRACKING -> "Tracking protection exception"
        GeckoSession.PermissionDelegate.PERMISSION_STORAGE_ACCESS -> "Third-party storage access"
        GeckoSession.PermissionDelegate.PERMISSION_LOCAL_DEVICE_ACCESS -> "Local device access"
        GeckoSession.PermissionDelegate.PERMISSION_LOCAL_NETWORK_ACCESS -> "Local network access"
        else -> null
    }

    fun canRevoke(permission: Int): Boolean =
        permission != GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE
}

object GeckoAndroidPermissionPolicy {
    fun shouldRequest(permissions: List<String>, secureOrigin: Boolean): Boolean {
        val supported = setOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        return secureOrigin && permissions.isNotEmpty() && permissions.all { it in supported }
    }
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
                    response.headers["content-length"]?.toLongOrNull(),
                    response.body
                )
            }
        })
        session.setPermissionDelegate(object : GeckoSession.PermissionDelegate {
            override fun onContentPermissionRequest(
                session: GeckoSession,
                permission: GeckoSession.PermissionDelegate.ContentPermission
            ): GeckoResult<Int> {
                val requestUri = Uri.parse(permission.uri)
                val secureOrigin = requestUri.scheme.equals("https", ignoreCase = true) &&
                    !requestUri.host.isNullOrBlank() && requestUri.userInfo == null
                val permissionName = GeckoSitePermissionPolicy.label(permission.permission)
                    ?: return GeckoResult.fromValue(
                        GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY
                    )
                if (permission.privateMode != isPrivate ||
                    !GeckoSitePermissionPolicy.shouldPrompt(permission.permission, secureOrigin, permission.privateMode)
                ) {
                    return GeckoResult.fromValue(
                        GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY
                    )
                }
                val origin = buildString {
                    append(requestUri.scheme)
                    append("://")
                    append(requestUri.host)
                    if (requestUri.port != -1) append(":").append(requestUri.port)
                }
                val result = GeckoResult<Int>()
                listener.onSitePermissionRequest(
                    origin,
                    permissionName,
                    permission.privateMode,
                    onShown = { permission.notifyShown() }
                ) { granted ->
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
                val requested = permissions?.distinct().orEmpty()
                if (session !== this@GeckoBrowserTab.session ||
                    !GeckoAndroidPermissionPolicy.shouldRequest(requested, isSecureOrigin(currentUrl))
                ) {
                    callback.reject()
                    return
                }
                listener.onAndroidPermissionRequest(requested) { granted ->
                    if (granted) callback.grant() else callback.reject()
                }
            }

            override fun onMediaPermissionRequest(
                session: GeckoSession,
                uri: String,
                video: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                audio: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                callback: GeckoSession.PermissionDelegate.MediaCallback
            ) {
                val videoSource = video?.firstOrNull {
                    it.source == GeckoSession.PermissionDelegate.MediaSource.SOURCE_CAMERA
                }
                val audioSource = audio?.firstOrNull {
                    it.source == GeckoSession.PermissionDelegate.MediaSource.SOURCE_MICROPHONE
                }
                if (session !== this@GeckoBrowserTab.session ||
                    !isSecureOrigin(uri) ||
                    (video != null && videoSource == null) ||
                    (audio != null && audioSource == null) ||
                    (video == null && audio == null)
                ) {
                    callback.reject()
                    return
                }
                val requestUri = Uri.parse(uri)
                val origin = buildString {
                    append(requestUri.scheme)
                    append("://")
                    append(requestUri.host)
                    if (requestUri.port != -1) append(":").append(requestUri.port)
                }
                val requested = buildList {
                    if (videoSource != null) add("Camera")
                    if (audioSource != null) add("Microphone")
                }.joinToString(" and ")
                listener.onSitePermissionRequest(origin, requested, isPrivate, onShown = {}) { granted ->
                    if (granted) callback.grant(videoSource, audioSource) else callback.reject()
                }
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
                listener.onLocationChanged(
                    url,
                    permissions.any { permission ->
                        permission.permission == GeckoSession.PermissionDelegate.PERMISSION_TRACKING &&
                            permission.value == GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW
                    }
                )
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

    private fun isSecureOrigin(rawUri: String): Boolean {
        val uri = Uri.parse(rawUri)
        return uri.scheme.equals("https", ignoreCase = true) &&
            !uri.host.isNullOrBlank() && uri.userInfo == null
    }

    val isTrackingProtectionEnabled: Boolean
        get() = session.settings.useTrackingProtection

    fun loadStoredSitePermissions(callback: (List<GeckoStoredSitePermission>?) -> Unit) {
        runtime.storageController.getAllPermissions().accept({ permissions ->
            val stored = permissions.orEmpty()
                .filter(GeckoSitePermissionPolicy::shouldDisplay)
                .mapNotNull { permission ->
                    val label = GeckoSitePermissionPolicy.label(permission.permission) ?: return@mapNotNull null
                    val uri = Uri.parse(permission.uri)
                    val host = uri.host ?: return@mapNotNull null
                    val origin = buildString {
                        append(uri.scheme ?: "https")
                        append("://")
                        append(host)
                        if (uri.port != -1) append(":").append(uri.port)
                    }
                    GeckoStoredSitePermission(
                        origin = origin,
                        permissionName = label,
                        allowed = permission.value == GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW,
                        canRevoke = GeckoSitePermissionPolicy.canRevoke(permission.permission),
                        contextKey = permission.contextId,
                        permission = permission
                    )
                }
                .distinctBy { Triple(it.origin, it.permissionName, it.permission.contextId) }
                .sortedWith(compareBy<GeckoStoredSitePermission> { it.origin }.thenBy { it.permissionName })
            callback(stored)
        }, {
            callback(null)
        })
    }

    fun revokeStoredSitePermission(
        stored: GeckoStoredSitePermission,
        callback: (Boolean) -> Unit
    ) {
        if (!stored.canRevoke || stored.permission.privateMode) {
            callback(false)
            return
        }
        runCatching {
            runtime.storageController.setPermission(
                stored.permission,
                GeckoSession.PermissionDelegate.ContentPermission.VALUE_PROMPT
            )
            runtime.storageController.getAllPermissions().accept({ permissions ->
                val stillStored = permissions.orEmpty().any { permission ->
                    !permission.privateMode &&
                        permission.uri == stored.permission.uri &&
                        permission.permission == stored.permission.permission &&
                        permission.contextId == stored.permission.contextId &&
                        permission.value != GeckoSession.PermissionDelegate.ContentPermission.VALUE_PROMPT
                }
                callback(!stillStored)
            }, {
                callback(false)
            })
        }.onFailure {
            callback(false)
        }
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

    /**
     * Extracts article text through GeckoView's page-extraction API. The native
     * reader never injects JavaScript into the page and does not persist content.
     */
    @OptIn(ExperimentalGeckoViewApi::class)
    fun extractReaderContent(callback: (Result<ReaderPageContent>) -> Unit) {
        if (disposed || !session.isOpen) {
            callback(Result.failure(IllegalStateException("This tab is not available.")))
            return
        }
        val mainHandler = Handler(Looper.getMainLooper())
        fun finish(result: Result<ReaderPageContent>) {
            mainHandler.post { callback(result) }
        }

        val extractor = session.getSessionPageExtractor()
        extractor.getPageMetadata().accept(
            { metadata ->
                if (metadata == null) {
                    finish(Result.failure(IllegalStateException("GeckoView did not return page metadata.")))
                } else if (metadata.isGated) {
                    finish(Result.failure(IllegalStateException("This page marks its content as gated. Reader Mode will not bypass it.")))
                } else if (!metadata.isReaderable) {
                    finish(Result.failure(IllegalStateException("This page does not appear to contain a readable article.")))
                } else {
                    extractor.getPageContent(PageExtractionController.ContentParams(true, true)).accept(
                        { content ->
                            val normalized = content.orEmpty().trim()
                            if (normalized.isBlank()) {
                                finish(Result.failure(IllegalStateException("No readable article text was found.")))
                            } else {
                                finish(
                                    Result.success(
                                        ReaderPageContent(
                                            content = normalized.take(MAX_READER_CONTENT_CHARS),
                                            language = metadata.language,
                                            wordCount = metadata.wordCount,
                                            wasTruncated = normalized.length > MAX_READER_CONTENT_CHARS
                                        )
                                    )
                                )
                            }
                        },
                        { error ->
                            finish(Result.failure(error ?: IllegalStateException("GeckoView could not extract page content.")))
                        }
                    )
                }
            },
            { error ->
                finish(Result.failure(error ?: IllegalStateException("GeckoView could not read page metadata.")))
            }
        )
    }

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

/** Plain-text article extracted from the currently loaded GeckoSession. */
data class ReaderPageContent(
    val content: String,
    val language: String,
    val wordCount: Int,
    val wasTruncated: Boolean
)

private const val MAX_READER_CONTENT_CHARS = 120_000


internal object WebNotificationPolicy {
    /** Private-session notifications and non-HTTPS origins must never escape to Android UI. */
    fun isAllowed(privateBrowsing: Boolean, source: String?): Boolean {
        if (privateBrowsing || source.isNullOrBlank()) return false
        val uri = runCatching { java.net.URI(source) }.getOrNull() ?: return false
        return uri.scheme.equals("https", ignoreCase = true) &&
            !uri.host.isNullOrBlank() && uri.rawUserInfo == null
    }
}

private class RivoWebNotificationDelegate(context: Context) : WebNotificationDelegate {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Website notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    override fun onShowNotification(notification: WebNotification) {
        mainHandler.post {
            if (!WebNotificationPolicy.isAllowed(notification.privateBrowsing, notification.source)) {
                notification.dismiss()
                return@post
            }
            if ((Build.VERSION.SDK_INT >= 33 &&
                    appContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) ||
                !manager.areNotificationsEnabled() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    manager.getNotificationChannel(CHANNEL_ID)?.importance == NotificationManager.IMPORTANCE_NONE)
            ) {
                notification.dismiss()
                return@post
            }
            val source = notification.source.orEmpty()
            val host = Uri.parse(source).host ?: "Website"
            val notificationId = notificationKey(notification)
            val clickIntent = Intent().setClassName(
                appContext,
                "com.rivo.browser.RivoWebNotificationReceiver"
            ).apply {
                putExtra("com.rivo.browser.extra.WEB_NOTIFICATION", notification)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                appContext,
                notificationId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val publicVersion = Notification.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(WebNotificationPrivacyPolicy.PUBLIC_TITLE)
                .setContentText(WebNotificationPrivacyPolicy.PUBLIC_TEXT)
                .build()
            val builder = Notification.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notification.title?.takeIf { it.isNotBlank() } ?: host)
                .setContentText(notification.text.orEmpty())
                .setSubText(host)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setPublicVersion(publicVersion)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            runCatching {
                manager.notify(NOTIFICATION_TAG, notificationId, builder.build())
                notification.show()
            }.onFailure {
                notification.dismiss()
            }
        }
    }

    override fun onCloseNotification(notification: WebNotification) {
        mainHandler.post {
            manager.cancel(NOTIFICATION_TAG, notificationKey(notification))
            notification.dismiss()
        }
    }

    private fun notificationKey(notification: WebNotification): Int =
        ((notification.source ?: "") + "|" + notification.tag).hashCode()

    companion object {
        private const val CHANNEL_ID = "rivo-website-notifications"
        private const val NOTIFICATION_TAG = "rivo-web"
    }
}
