package com.rivo.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

internal const val TEST_PAGE_URL = "https://example.com/"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EngineProbeScreen() }
    }
}

@Composable
private fun EngineProbeScreen() {
    val context = LocalContext.current
    val probe = remember(context) { EngineProbeFactory.create(context) }

    DisposableEffect(probe) {
        onDispose { probe.dispose() }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text("Rivo Browser — Phase 0 engine probe", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Variant: ${BuildConfig.FLAVOR} | Test page: example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                AndroidView(
                    factory = { probe.view },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                Text(
                    "Evaluation build only. Browser features are not enabled.",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
