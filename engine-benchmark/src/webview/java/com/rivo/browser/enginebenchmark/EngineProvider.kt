package com.rivo.browser.enginebenchmark

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView

object BenchmarkEngineProviderImpl : BenchmarkEngineProvider {
    override val engineName = "Android System WebView"
    override fun engineVersion(context: Context) =
        WebView.getCurrentWebViewPackage()?.versionName ?: "provider-unavailable"
    override fun createTab(context: Context, tabId: Int, startMs: Long,
        onReady: (Int, Long) -> Unit): BenchmarkTab =
        WebBenchmarkTab(context, tabId, startMs, onReady)
}

@SuppressLint("SetJavaScriptEnabled")
private class WebBenchmarkTab(
    context: Context, private val tabId: Int, private val startMs: Long,
    private val onReady: (Int, Long) -> Unit
) : BenchmarkTab {
    override val view = WebView(context)
    private var disposed = false

    init {
        view.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportMultipleWindows(false)
            allowFileAccess = false
            allowContentAccess = false
            safeBrowsingEnabled = true
        }
        view.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (title == BenchmarkFixture.readyTitle(tabId)) {
                    onReady(tabId, SystemClock.elapsedRealtime() - startMs)
                }
            }
        }
        view.loadUrl(BenchmarkFixture.dataUri(tabId))
    }

    override fun setActive(active: Boolean) {
        if (!disposed) { if (active) view.onResume() else view.onPause() }
    }
    override fun reload() { if (!disposed) view.reload() }
    override fun dispose() {
        if (disposed) return
        disposed = true
        view.onPause()
        (view.parent as? ViewGroup)?.removeView(view)
        view.stopLoading()
        view.removeAllViews()
        view.destroy()
    }
}
