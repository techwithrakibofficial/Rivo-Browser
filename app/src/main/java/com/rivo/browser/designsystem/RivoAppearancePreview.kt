package com.rivo.browser.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rivo.browser.BrowserAppearance

/** Lightweight previews for checking the semantic tokens in all three modes. */
@Preview(name = "Rivo · Dark", showBackground = true)
@Composable
private fun DarkAppearancePreview() = AppearancePreview(BrowserAppearance.DARK)

@Preview(name = "Rivo · Light", showBackground = true)
@Composable
private fun LightAppearancePreview() = AppearancePreview(BrowserAppearance.LIGHT)

@Preview(name = "Rivo · Premium Glass", showBackground = true)
@Composable
private fun GlassAppearancePreview() = AppearancePreview(BrowserAppearance.GLASS)

@Composable
private fun AppearancePreview(appearance: BrowserAppearance) {
    MaterialTheme(colorScheme = RivoColorSchemes.forAppearance(appearance)) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Rivo Browser · ${appearance.name}", style = MaterialTheme.typography.titleLarge)
                Surface(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Browser surface", color = MaterialTheme.colorScheme.onSurface)
                        Text("Readable secondary text", color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Text("Primary action", color = MaterialTheme.colorScheme.primary)
                Text("Security warning", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
