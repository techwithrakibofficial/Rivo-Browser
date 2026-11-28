package com.rivo.browser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivo.browser.data.SavedPage

/** Chrome-inspired start page using local browser data only; no fabricated feed or account state. */
@Composable
internal fun RivoHomeScreen(
    bookmarks: List<SavedPage>,
    history: List<SavedPage>,
    privateMode: Boolean,
    onSearch: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNewPrivateTab: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenTabs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val shortcuts = remember(bookmarks, history) { (bookmarks + history).distinctBy { it.url }.take(8) }
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text("Google", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium, letterSpacing = (-2).sp), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 20.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            shadowElevation = 2.dp
        ) {
            Row(Modifier.padding(start = 16.dp, end = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("⌕", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    if (query.isEmpty()) Text("Search Google or type URL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    BasicTextField(
                        value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search Google or enter a website" },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            val value = query.trim()
                            if (value.isNotEmpty()) onSearch(value)
                            keyboard?.hide()
                        })
                    )
                }
                if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Text("×") }
                else Text("G", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(onClick = onOpenHistory, modifier = Modifier.weight(1f), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                Box(Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) { Text("◷  History", color = MaterialTheme.colorScheme.onSurface) }
            }
            Surface(onClick = onNewPrivateTab, modifier = Modifier.weight(1f), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                Box(Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) { Text("◉  Incognito", color = MaterialTheme.colorScheme.onSurface) }
            }
        }
        if (shortcuts.isNotEmpty()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Shortcuts", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    shortcuts.forEach { page ->
                        Surface(onClick = { onOpenUrl(page.url) }, shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(width = 104.dp, height = 104.dp)) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), modifier = Modifier.size(42.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val initial = (runCatching { android.net.Uri.parse(page.url).host?.firstOrNull() }.getOrNull() ?: page.title.firstOrNull() ?: 'W').uppercase()
                                        Text(initial, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                Text(page.title.ifBlank { page.url }, maxLines = 2, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        } else {
            Text("Your saved sites will appear here", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onOpenBookmarks, modifier = Modifier.weight(1f)) { Text("Bookmarks") }
            Button(onClick = onOpenTabs, modifier = Modifier.weight(1f)) { Text("All tabs") }
        }
        if (privateMode) Text("Private browsing is on", color = MaterialTheme.colorScheme.primary)
    }
}
