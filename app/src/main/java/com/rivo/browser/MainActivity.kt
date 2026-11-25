package com.rivo.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val browserViewModel: BrowserViewModel by viewModels()
    private var diagnosticMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val crashReport = if (BuildConfig.CRASH_DIAGNOSTICS_ENABLED) {
            RivoApplication.readReport(application)
        } else null
        diagnosticMode = crashReport != null
        setContent {
            if (crashReport != null) {
                CrashReportScreen(
                    report = crashReport,
                    onClear = {
                        RivoApplication.clearReport(application)
                        finish()
                    }
                )
            } else {
                BrowserScreen(browserViewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!diagnosticMode) browserViewModel.browser.onHostResumed()
    }

    override fun onStop() {
        if (!diagnosticMode) browserViewModel.browser.onHostPaused()
        super.onStop()
    }
}

@Composable
private fun CrashReportScreen(report: String, onClear: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Rivo Browser diagnostic report", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "A previous launch crashed. The report below is stored only on this device. It is not uploaded automatically.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { clipboard.setText(AnnotatedString(report)) }) {
                    Text("Copy crash report")
                }
                Button(onClick = onClear) {
                    Text("Clear report and close")
                }
                Text(report, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
