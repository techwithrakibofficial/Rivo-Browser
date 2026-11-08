package com.rivo.browser.engine.webview

import android.content.Context
import android.webkit.WebView
import com.rivo.browser.engine.api.BrowserEngine

/**
 * Phase 0 probe only. Do not expose this view to untrusted native bridges.
 * Call dispose() when the probe is no longer used.
 */
class WebViewEngineProbe(context: Context) : BrowserEngine {
    override val engineName: String = "Android System WebView"
    val view: WebView = WebView(context.applicationContext)

    override fun dispose() {
        (view.parent as? android.view.ViewGroup)?.removeView(view)
        view.stopLoading()
        view.removeAllViews()
        view.destroy()
    }
}
