package com.rivo.browser.enginebenchmark

import android.content.Context
import android.view.View

interface BenchmarkTab {
    val view: View
    fun setActive(active: Boolean)
    fun reload()
    fun dispose()
}

interface BenchmarkEngineProvider {
    val engineName: String
    fun engineVersion(context: Context): String
    fun createTab(context: Context, tabId: Int, startMs: Long, onReady: (Int, Long) -> Unit): BenchmarkTab
}
