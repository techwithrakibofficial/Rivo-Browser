package com.rivo.browser

import android.app.Application
import android.os.Build
import android.os.Process
import java.io.File
import java.util.IdentityHashMap
import kotlin.system.exitProcess

/**
 * Writes a bounded crash report without exception messages or user-provided text.
 * Reports stay in app-private internal storage and are never uploaded.
 */
class RivoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching {
                val report = buildString {
                    appendLine("Rivo Browser startup/runtime crash report")
                    appendLine("App version: 0.2.3-phase1 (diagnostic build)")
                    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                    appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
                    appendLine()
                    appendSafeStack(error)
                }.takeLast(MAX_REPORT_CHARS)
                File(filesDir, REPORT_FILE).writeText(report)
            }
            if (previous != null) previous.uncaughtException(thread, error)
            else {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
    }

    private fun StringBuilder.appendSafeStack(error: Throwable) {
        val seen = IdentityHashMap<Throwable, Boolean>()
        var current: Throwable? = error
        var depth = 0
        while (current != null && depth < MAX_CAUSE_DEPTH && seen.put(current, true) == null) {
            appendLine("Cause ${depth + 1}: ${current.javaClass.name}")
            current.stackTrace.take(MAX_FRAMES_PER_CAUSE).forEach { frame ->
                appendLine("  at ${frame.className}.${frame.methodName}(${frame.fileName ?: "Unknown Source"})")
            }
            current = current.cause
            depth++
        }
    }

    companion object {
        const val REPORT_FILE = "rivo-crash-report.txt"
        private const val MAX_REPORT_CHARS = 26000
        private const val MAX_CAUSE_DEPTH = 8
        private const val MAX_FRAMES_PER_CAUSE = 40

        fun readReport(application: Application): String? =
            runCatching {
                File(application.filesDir, REPORT_FILE)
                    .takeIf { it.isFile && it.length() in 1..MAX_REPORT_CHARS.toLong() }
                    ?.readText()
            }.getOrNull()

        fun clearReport(application: Application) {
            runCatching { File(application.filesDir, REPORT_FILE).delete() }
        }
    }
}
