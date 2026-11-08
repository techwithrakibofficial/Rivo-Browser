package com.rivo.browser.engine.gecko

import android.content.Context
import android.view.ViewGroup
import com.rivo.browser.engine.api.BrowserEngine
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

/**
 * Phase 0 GeckoView probe.
 *
 * The caller owns one process-scoped GeckoRuntime and must pass it here.
 * This probe owns only its GeckoSession and GeckoView. Do not shut down the
 * shared runtime when disposing an individual probe/session.
 */
class GeckoViewEngineProbe(
    context: Context,
    runtime: GeckoRuntime
) : BrowserEngine {
    override val engineName: String = "Mozilla GeckoView"

    val session: GeckoSession = GeckoSession().apply {
        // Required no-op delegate workaround documented by Mozilla's quick start.
        setContentDelegate(object : GeckoSession.ContentDelegate {})
        open(runtime)
    }

    val view: GeckoView = GeckoView(context).apply {
        setSession(session)
    }

    fun loadUrl(url: String) {
        check(!disposed) { "Probe has already been disposed." }
        session.loadUri(url)
    }

    private var disposed = false

    override fun dispose() {
        if (disposed) return
        disposed = true
        (view.parent as? ViewGroup)?.removeView(view)
        view.releaseSession()
        session.close()
    }
}
