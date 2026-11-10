package com.rivo.browser.engine.api

import android.content.Context
import android.view.View
import java.io.InputStream

/**
 * Engine-neutral contract for one browser tab.
 *
 * An implementation owns exactly one engine session per tab. The app layer
 * depends only on this contract so the browser engine (GeckoView today, possibly
 * Android WebView after device benchmarking) can be swapped without touching tab,
 * history, download or UI logic.
 *
 * All methods are called on the main thread.
 */
interface BrowserTab {
    val isPrivate: Boolean

    /** Creates a view bound to [context]; any previously created view is released first. */
    fun createView(context: Context): View

    /** Detaches the current view while keeping the engine session alive. */
    fun releaseView()

    fun load(url: String)
    fun goBack()
    fun goForward()
    fun reload()
    fun stop()

    /** Marks the tab as the foreground (active + focused) or a background tab. */
    fun setActive(active: Boolean)

    /** Closes the engine session. The tab must not be used afterwards. */
    fun dispose()
}

/** Callbacks from an engine session back into the app layer. */
interface BrowserTabListener {
    fun onLocationChanged(url: String)
    fun onTitleChanged(title: String?)
    fun onLoadingChanged(loading: Boolean, progress: Int)
    fun onNavigationChanged(canGoBack: Boolean, canGoForward: Boolean)
    fun onContentCrashed()
    fun onSecurityChanged(secure: Boolean, exception: Boolean)
    fun onExternalDownload(url: String, mimeType: String?, disposition: String?, body: InputStream?)
    fun onSitePermissionRequest(
        origin: String,
        permission: String,
        privateMode: Boolean,
        respond: (Boolean) -> Unit
    )
}

/** Creates engine-backed tabs. The factory owns any process-wide engine runtime. */
fun interface BrowserTabFactory {
    fun create(isPrivate: Boolean, listener: BrowserTabListener): BrowserTab
}
