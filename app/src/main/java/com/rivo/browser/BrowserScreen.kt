package com.rivo.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rivo.browser.designsystem.RivoColorSchemes
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import java.text.DateFormat
import java.util.Date

@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val browser = viewModel.browser
    val active = browser.activeTab
    val keyboard = LocalSoftwareKeyboardController.current
    var address by remember(active?.id) { mutableStateOf(active?.url.orEmpty()) }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(active?.url) {
        if (active != null) address = active.url
    }

    MaterialTheme(colorScheme = RivoColorSchemes.forAppearance(browser.appearance)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = if (browser.appearance == BrowserAppearance.GLASS) 0.88f else 1f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            placeholder = { Text("Search or enter address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                browser.navigate(address)
                                keyboard?.hide()
                            })
                        )
                        Button(onClick = {
                            browser.navigate(address)
                            keyboard?.hide()
                        }) { Text("Go") }
                        Box {
                            TextButton(onClick = { menuExpanded = true }) { Text("⋮") }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(text = { Text("New tab") }, onClick = {
                                    menuExpanded = false
                                    browser.createTab()
                                })
                                DropdownMenuItem(text = { Text("New private tab") }, onClick = {
                                    menuExpanded = false
                                    browser.createTab(privateMode = true)
                                })
                                DropdownMenuItem(text = { Text("Bookmarks") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.BOOKMARKS)
                                })
                                DropdownMenuItem(text = { Text("History") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.HISTORY)
                                })
                                DropdownMenuItem(text = { Text("Appearance") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.APPEARANCE)
                                })
                            }
                        }
                    }
                    if (active?.loading == true) {
                        LinearProgressIndicator(
                            progress = active.progress / 100f,
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        )
                    }
                    if (active?.privateMode == true) {
                        Text(
                            "PRIVATE TAB · This session is not saved to local history",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp, top = 5.dp)
                        )
                    }
                    if (active != null && !active.privateMode && active.securityKnown) {
                        Text(
                            when {
                                active.certificateException -> "Certificate security exception"
                                active.secureConnection -> "Secure connection verified by browser engine"
                                else -> "Connection is not secure"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active.secureConnection && !active.certificateException)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                if (active != null) {
                    keyTab(active.id) {
                        AndroidView(
                            factory = { active.engine.view },
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        )
                    }
                } else {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Starting Rivo Browser…")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { browser.goBack() }, enabled = active?.canGoBack == true) { Text("‹") }
                    TextButton(onClick = { browser.goForward() }, enabled = active?.canGoForward == true) { Text("›") }
                    TextButton(onClick = { browser.reloadOrStop() }) { Text(if (active?.loading == true) "Stop" else "Reload") }
                    TextButton(onClick = { browser.addCurrentBookmark() }) { Text("☆") }
                    TextButton(onClick = { browser.openPanel(BrowserPanel.TABS) }) { Text("${browser.tabs.size} tabs") }
                }
            }
        }
    }

    if (browser.panel != null) {
        BrowserPanelDialog(
            browser = browser,
            onDismiss = { browser.openPanel(null) }
        )
    }

    if (browser.notice != null) {
        AlertDialog(
            onDismissRequest = { browser.dismissNotice() },
            title = { Text("Rivo Browser") },
            text = { Text(browser.notice.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { browser.dismissNotice() }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun keyTab(id: Long, content: @Composable () -> Unit) {
    androidx.compose.runtime.key(id) { content() }
}

@Composable
private fun BrowserPanelDialog(browser: BrowserController, onDismiss: () -> Unit) {
    val panel = browser.panel ?: return
    val title = when (panel) {
        BrowserPanel.TABS -> "Your tabs"
        BrowserPanel.BOOKMARKS -> "Bookmarks"
        BrowserPanel.HISTORY -> "History"
        BrowserPanel.APPEARANCE -> "Quick appearance"
        BrowserPanel.MENU -> "Browser menu"
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                when (panel) {
                    BrowserPanel.TABS -> LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(browser.tabs, key = { it.id }) { tab ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    browser.selectTab(tab.id)
                                    onDismiss()
                                }, modifier = Modifier.weight(1f)) {
                                    Text(
                                        (if (tab.privateMode) "Private · " else "") + tab.title,
                                        maxLines = 1
                                    )
                                }
                                TextButton(onClick = { browser.closeTab(tab.id) }) { Text("×") }
                            }
                        }
                    }
                    BrowserPanel.BOOKMARKS -> {
                        if (browser.bookmarks.isEmpty()) Text("No bookmarks saved yet.")
                        LazyColumn(modifier = Modifier.height(300.dp)) {
                            items(browser.bookmarks, key = { it.url }) { page ->
                                TextButton(onClick = {
                                    browser.navigate(page.url)
                                    onDismiss()
                                }) {
                                    Column {
                                        Text(page.title, maxLines = 1)
                                        Text(page.url, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                    BrowserPanel.HISTORY -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent pages", modifier = Modifier.weight(1f))
                            TextButton(onClick = { browser.clearHistory() }) { Text("Clear") }
                        }
                        if (browser.history.isEmpty()) Text("No browsing history yet.")
                        LazyColumn(modifier = Modifier.height(300.dp)) {
                            items(browser.history, key = { it.url }) { page ->
                                TextButton(onClick = {
                                    browser.navigate(page.url)
                                    onDismiss()
                                }) {
                                    Column {
                                        Text(page.title, maxLines = 1)
                                        Text(
                                            DateFormat.getDateTimeInstance().format(Date(page.visitedAt)),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    BrowserPanel.APPEARANCE -> {
                        BrowserAppearance.values().forEach { appearance ->
                            TextButton(
                                onClick = {
                                    browser.setAppearance(appearance)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    (if (browser.appearance == appearance) "✓  " else "") +
                                        appearance.name.lowercase().replaceFirstChar { it.uppercase() }
                                )
                            }
                        }
                        Text(
                            "Glass mode uses translucent surfaces without heavy real-time blur for better low-end performance.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    BrowserPanel.MENU -> Unit
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}
