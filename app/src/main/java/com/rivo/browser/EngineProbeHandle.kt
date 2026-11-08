package com.rivo.browser

import android.view.View

/** Lifecycle boundary for the Phase 0 engine comparison screen. */
interface EngineProbeHandle {
    val view: View
    fun dispose()
}
