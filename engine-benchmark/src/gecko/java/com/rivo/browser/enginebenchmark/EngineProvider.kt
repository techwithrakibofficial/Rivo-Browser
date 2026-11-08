package com.rivo.browser.enginebenchmark

import android.content.Context
import android.os.SystemClock
import android.view.ViewGroup
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

private object BenchmarkRuntime {
    @Volatile private var value: GeckoRuntime? = null
    fun get(context: Context): GeckoRuntime = value ?: synchronized(this) {
        value ?: GeckoRuntime.create(context.applicationContext, GeckoRuntimeSettings.Builder().build())
            .also { value = it }
    }
}

object BenchmarkEngineProviderImpl : BenchmarkEngineProvider {
    override val engineName = "Mozilla GeckoView"
    override fun engineVersion(context: Context) = "159.0.20261022150525"
    override fun createTab(context: Context, tabId: Int, startMs: Long,
        onReady: (Int, Long) -> Unit): BenchmarkTab =
        GeckoBenchmarkTab(context, tabId, startMs, onReady)
}

private class GeckoBenchmarkTab(
    context: Context, private val tabId: Int, private val startMs: Long,
    private val onReady: (Int, Long) -> Unit
) : BenchmarkTab {
    private val session = GeckoSession()
    override val view = GeckoView(context)
    private var disposed = false

    init {
        session.setContentDelegate(object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                if (title == BenchmarkFixture.readyTitle(tabId)) {
                    onReady(tabId, SystemClock.elapsedRealtime() - startMs)
                }
            }
        })
        session.open(BenchmarkRuntime.get(context))
        view.setSession(session)
        session.loadUri(BenchmarkFixture.dataUri(tabId))
    }

    override fun setActive(active: Boolean) {
        if (!disposed) { session.setActive(active); session.setFocused(active) }
    }
    override fun reload() { if (!disposed) session.reload() }
    override fun dispose() {
        if (disposed) return
        disposed = true
        (view.parent as? ViewGroup)?.removeView(view)
        view.releaseSession()
        session.close()
    }
}
