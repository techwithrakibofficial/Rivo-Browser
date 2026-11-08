package com.rivo.browser

import android.content.Context
import com.rivo.browser.engine.webview.WebViewEngineProbe

/** WebView implementation selected only by the webview product flavor. */
object EngineProbeFactory {
    fun create(context: Context): EngineProbeHandle {
        val probe = WebViewEngineProbe(context)
        probe.loadUrl(TEST_PAGE_URL)
        return object : EngineProbeHandle {
            override val view = probe.view
            override fun dispose() = probe.dispose()
        }
    }
}
