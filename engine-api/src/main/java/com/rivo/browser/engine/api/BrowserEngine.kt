package com.rivo.browser.engine.api

/**
 * Minimal engine boundary for Phase 0 evaluation.
 * This is deliberately not a complete browser API.
 */
interface BrowserEngine {
    val engineName: String
    fun dispose()
}
