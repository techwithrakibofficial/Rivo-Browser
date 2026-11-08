package com.rivo.browser.engine.webview

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import com.rivo.browser.engine.api.BrowserEngine

/**
 * Phase 0 comparison probe. No JavaScript-to-native bridge is installed.
 * The caller owns the view lifecycle and must call dispose() exactly once
 * when finished (repeated calls are safe).
 */
class WebViewEngineProbe(context: Context) : BrowserEngine {
    override val engineName: String = "Android System WebView"
    val view: WebView = WebView(context)
    private var disposed = false

    init {
        view.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportMultipleWindows(false)
        }
        view.settings.safeBrowsingEnabled = true
    }

    fun loadUrl(url: String) {
        check(!disposed) { "Probe has already been disposed." }
        view.loadUrl(url)
    }

    override fun dispose() {
        if (disposed) return
        disposed = true
        (view.parent as? ViewGroup)?.removeView(view)
        view.stopLoading()
        view.removeAllViews()
        view.destroy()
    }
}
