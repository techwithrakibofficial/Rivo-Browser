package com.rivo.browser.enginebenchmark

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class BenchmarkActivity : Activity() {
    private val startMs = SystemClock.elapsedRealtime()
    private val tabs = mutableListOf<BenchmarkTab>()
    private var nextId = 1
    private var selected = -1
    private var hostStarted = true
    private lateinit var status: TextView
    private lateinit var content: FrameLayout
    private val provider: BenchmarkEngineProvider get() = BenchmarkEngineProviderImpl

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val version = provider.engineVersion(this)
        Log.i(TAG, "event=activity_created engine=${provider.engineName} version=$version " +
            "model=${Build.MODEL} android=${Build.VERSION.RELEASE} " +
            "sdk=${Build.VERSION.SDK_INT} abis=${Build.SUPPORTED_ABIS.joinToString(",")}")
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        root.addView(TextView(this).apply { text = "${provider.engineName} | $version"; textSize = 16f })
        status = TextView(this).apply { text = "Starting deterministic page…"; textSize = 13f }
        root.addView(status)
        content = FrameLayout(this)
        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row1.addView(button("Add tab") { addTab() }, weighted())
        row1.addView(button("Next tab") { nextTab() }, weighted())
        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row2.addView(button("Close tab") { closeTab() }, weighted())
        row2.addView(button("Reload") { tabs.getOrNull(selected)?.reload() }, weighted())
        root.addView(row1)
        root.addView(row2)
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        val count = intent.getIntExtra(EXTRA_TABS, 1).coerceIn(1, MAX_TABS)
        repeat(count) { addTab() }
    }

    private fun addTab() {
        if (tabs.size >= MAX_TABS) return
        val id = nextId++
        tabs.add(provider.createTab(this, id, startMs) { tab, elapsed ->
            runOnUiThread {
                Log.i(TAG, "event=dom_ready tab=$tab elapsed_ms=$elapsed")
                status.text = "DOM ready: tab $tab · $elapsed ms"
            }
        })
        selected = tabs.lastIndex
        showSelected()
    }

    private fun nextTab() {
        if (tabs.isNotEmpty()) { selected = (selected + 1) % tabs.size; showSelected() }
    }

    private fun closeTab() {
        if (selected !in tabs.indices) return
        tabs.removeAt(selected).dispose()
        selected = if (tabs.isEmpty()) -1 else selected.coerceAtMost(tabs.lastIndex)
        showSelected()
    }

    private fun showSelected() {
        content.removeAllViews()
        tabs.forEachIndexed { index, tab -> tab.setActive(hostStarted && index == selected) }
        if (selected in tabs.indices) {
            content.addView(tabs[selected].view, FrameLayout.LayoutParams(-1, -1))
        }
        status.text = "Active tab ${selected + 1}/${tabs.size}"
        Log.i(TAG, "event=tab_state active_index=$selected tab_count=${tabs.size}")
    }

    override fun onStart() { super.onStart(); hostStarted = true; updateActive() }
    override fun onStop() { hostStarted = false; updateActive(); super.onStop() }
    private fun updateActive() {
        tabs.forEachIndexed { index, tab -> tab.setActive(hostStarted && index == selected) }
    }
    override fun onDestroy() {
        tabs.toList().forEach { it.dispose() }
        tabs.clear()
        super.onDestroy()
    }
    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        setOnClickListener { action() }
    }
    private fun weighted() = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val MAX_TABS = 10
        const val EXTRA_TABS = "benchmark_tab_count"
        const val TAG = "RivoEngineBench"
    }
}
