package com.rivo.browser

import android.content.Context
import com.rivo.browser.engine.gecko.GeckoViewEngineProbe
import org.mozilla.geckoview.GeckoRuntime

/** GeckoView implementation selected only by the gecko product flavor. */
object EngineProbeFactory {
    private var processRuntime: GeckoRuntime? = null

    fun create(context: Context): EngineProbeHandle {
        // Mozilla documents GeckoRuntime as process-scoped: create it once.
        val runtime = processRuntime ?: GeckoRuntime.create(context.applicationContext).also {
            processRuntime = it
        }
        val probe = GeckoViewEngineProbe(context, runtime)
        probe.loadUrl(TEST_PAGE_URL)
        return object : EngineProbeHandle {
            override val view = probe.view
            override fun dispose() = probe.dispose()
        }
    }
}
