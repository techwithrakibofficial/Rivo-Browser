package com.rivo.browser

import android.net.Uri
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

internal val TEST_PAGE_URL: String = "data:text/html;charset=utf-8," + Uri.encode(
    """
    <!doctype html>
    <html lang="en">
      <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Rivo Engine Benchmark</title>
        <style>
          body { font: 16px sans-serif; margin: 24px; line-height: 1.5; }
          h1 { font-size: 24px; }
          .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
          .card { border: 1px solid #888; border-radius: 12px; padding: 12px; }
        </style>
      </head>
      <body>
        <h1>Rivo Browser engine test</h1>
        <p>Deterministic local HTML workload shared by both engine variants.</p>
        <section class="grid">
          <div class="card">Layout and typography</div>
          <div class="card">CSS grid rendering</div>
          <div class="card">Viewport scaling</div>
          <div class="card">Text wrapping</div>
        </section>
      </body>
    </html>
    """.trimIndent()
)

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
                    "Variant: ${BuildConfig.FLAVOR} | Deterministic local HTML workload",
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
