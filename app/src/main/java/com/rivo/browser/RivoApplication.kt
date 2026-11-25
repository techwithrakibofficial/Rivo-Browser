package com.rivo.browser

import android.app.Application
import android.os.Build
import android.os.Process
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * Writes a privacy-minimal fatal crash report for the next app launch.
 * Reports stay in app-private internal storage and are never uploaded.
 */
class RivoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching {
                val trace = StringWriter().also { writer ->
                    error.printStackTrace(PrintWriter(writer))
                }.toString().takeLast(MAX_REPORT_CHARS)
                val report = buildString {
                    appendLine("Rivo Browser startup/runtime crash report")
                    appendLine("App version: 0.2.3-phase1 (diagnostic build)")
                    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                    appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
                    appendLine("Thread: ${thread.name}")
                    appendLine()
                    append(trace)
                }
                File(filesDir, REPORT_FILE).writeText(report)
            }
            if (previous != null) previous.uncaughtException(thread, error)
            else {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
    }

    companion object {
        const val REPORT_FILE = "rivo-crash-report.txt"
        private const val MAX_REPORT_CHARS = 24000

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
