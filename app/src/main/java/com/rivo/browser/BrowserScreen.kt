package com.rivo.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import com.rivo.browser.designsystem.RivoColorSchemes
import com.rivo.browser.designsystem.RivoSpacing
import com.rivo.browser.designsystem.RivoShapes
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
    BackHandler(enabled = active?.canGoBack == true) { browser.goBack() }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val view = LocalView.current
    var address by remember(active?.id) { mutableStateOf(active?.url.orEmpty()) }
    var isEditingAddress by remember(active?.id) { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> browser.completeNotificationPermissionRequest(granted) }

    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            val useLightSystemIcons = browser.appearance == BrowserAppearance.LIGHT
            controller.isAppearanceLightStatusBars = useLightSystemIcons
            controller.isAppearanceLightNavigationBars = useLightSystemIcons
        }
    }

    DisposableEffect(active?.id) {
        onDispose { active?.engine?.releaseView() }
    }

    LaunchedEffect(active?.url, isEditingAddress) {
        if (active != null && !isEditingAddress) address = active.url
    }

    val currentPermissionPrompt = browser.permissionPrompt
    LaunchedEffect(currentPermissionPrompt) {
        currentPermissionPrompt?.onShown?.invoke()
    }

    MaterialTheme(colorScheme = RivoColorSchemes.forAppearance(browser.appearance)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = if (browser.appearance == BrowserAppearance.GLASS) 0.88f else 1f))
                        .padding(horizontal = RivoSpacing.medium, vertical = RivoSpacing.small + RivoSpacing.compact)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = {
                                address = it
                                isEditingAddress = true
                            },
                            modifier = Modifier.weight(1f).onFocusChanged {
                                isEditingAddress = it.isFocused
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(RivoShapes.control),
                            placeholder = { Text("Search or enter address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                val submittedAddress = address
                                browser.navigate(submittedAddress)
                                address = NavigationInputResolver.resolve(submittedAddress)
                                isEditingAddress = false
                                keyboard?.hide()
                            })
                        )
                        Button(onClick = {
                            val submittedAddress = address
                            browser.navigate(submittedAddress)
                            address = NavigationInputResolver.resolve(submittedAddress)
                            isEditingAddress = false
                            keyboard?.hide()
                        }) { Text("Go") }
                        Box {
                            TextButton(onClick = { menuExpanded = true }) { Text("Menu") }
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
                                DropdownMenuItem(text = { Text("Downloads") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.DOWNLOADS)
                                })
                                DropdownMenuItem(text = { Text("Appearance") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.APPEARANCE)
                                })
                                DropdownMenuItem(text = { Text("Privacy Intelligence Center") }, onClick = {
                                    menuExpanded = false
                                    browser.openPanel(BrowserPanel.SITE_PERMISSIONS)
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
                            modifier = Modifier.padding(start = RivoSpacing.small, top = RivoSpacing.compact)
                        )
                    }
                    if (active != null && !active.privateMode && active.securityKnown) {
                        Text(
                            when {
                                active.certificateException -> "Certificate security exception"
                                active.secureConnection -> "Secure connection"
                                else -> "Connection is not secure"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active.secureConnection && !active.certificateException)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = RivoSpacing.small, top = RivoSpacing.compact)
                        )
                    }
                }

                if (active != null) {
                    keyTab(active.id) {
                        AndroidView(
                            factory = { active.engine.createView(context) },
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
                        .padding(horizontal = RivoSpacing.small, vertical = RivoSpacing.compact + 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { browser.goBack() }, enabled = active?.canGoBack == true) { Text("Back") }
                    TextButton(onClick = { browser.goForward() }, enabled = active?.canGoForward == true) { Text("Forward") }
                    TextButton(onClick = { browser.reloadOrStop() }) { Text(if (active?.loading == true) "Stop" else "Reload") }
                    TextButton(onClick = { browser.addCurrentBookmark() }) { Text("Bookmark") }
                    TextButton(onClick = { browser.openPanel(BrowserPanel.TABS) }) { Text("Tabs ${browser.tabs.size}") }
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

    browser.pendingDownload?.let { request ->
        val downloadUri = android.net.Uri.parse(request.url)
        val fileName = downloadUri.lastPathSegment ?: "download"
        AlertDialog(
            onDismissRequest = { browser.cancelPendingDownload() },
            title = { Text("Download file?") },
            text = {
                Text(
                    "$fileName from ${downloadUri.host ?: "this website"} will be saved in Rivo's app-specific Downloads folder. " +
                        "The maximum file size is 250 MB." +
                        if (request.privateMode) " Private browsing does not automatically erase saved files." else ""
                )
            },
            confirmButton = {
                TextButton(onClick = { browser.confirmPendingDownload() }) { Text("Download") }
            },
            dismissButton = {
                TextButton(onClick = { browser.cancelPendingDownload() }) { Text("Cancel") }
            }
        )
    }

    currentPermissionPrompt?.let { request ->
        AlertDialog(
            onDismissRequest = { browser.respondToSitePermission(false) },
            title = { Text("Website permission") },
            text = {
                Text(
                    "${request.origin} wants to access ${request.permission}. " +
                        if (request.privateMode) "This decision applies only to this private browsing session and is not saved as a persistent site permission." else "Allow only if you trust this website."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (request.permission == "Notifications" &&
                        Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        browser.beginNotificationPermissionRequest()
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        browser.respondToSitePermission(true)
                    }
                }) { Text("Allow") }
            },
            dismissButton = {
                TextButton(onClick = { browser.respondToSitePermission(false) }) { Text("Deny") }
            }
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
        BrowserPanel.DOWNLOADS -> "Downloads"
        BrowserPanel.APPEARANCE -> "Quick appearance"
        BrowserPanel.SITE_PERMISSIONS -> "Privacy Intelligence Center"
        BrowserPanel.MENU -> "Browser menu"
    }
    LaunchedEffect(panel) {
        if (panel == BrowserPanel.SITE_PERMISSIONS) browser.refreshSitePermissions()
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(RivoShapes.panel),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(RivoSpacing.large)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(RivoSpacing.medium))
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            browser.navigate(page.url)
                                            onDismiss()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column {
                                            Text(page.title, maxLines = 1)
                                            Text(page.url, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                        }
                                    }
                                    TextButton(onClick = { browser.removeBookmark(page.url) }) { Text("×") }
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
                            // Multiple visits to one URL are distinct history records.
                            items(browser.history) { page ->
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
                    BrowserPanel.DOWNLOADS -> {
                        if (browser.downloads.isEmpty()) Text("No downloads yet.")
                        LazyColumn(modifier = Modifier.height(300.dp)) {
                            items(browser.downloads, key = { it.id }) { download ->
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                    Text(download.filename, maxLines = 1)
                                    Text(download.status, style = MaterialTheme.typography.labelSmall)
                                    download.mimeType?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
                                    if (download.file != null) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(onClick = { browser.openDownload(download) }) { Text("Open") }
                                            TextButton(onClick = { browser.shareDownload(download) }) { Text("Share") }
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            "Files are stored in Rivo's app-specific Downloads folder. Private mode does not erase downloaded files; private-tab download entries are not saved in persistent browser download history.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    BrowserPanel.SITE_PERMISSIONS -> {
                        Text("Secure storage", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Bookmarks, history, regular-tab restoration data and download metadata are encrypted at rest with AES-256-GCM using an Android Keystore key. The theme preference is non-sensitive and is not encrypted. Downloaded file contents are not encrypted by the metadata cipher.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Private browsing", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Private tabs use GeckoView private sessions and are excluded from app history, bookmarks and tab restoration. Private-session permission decisions are not listed as persistent site permissions. Downloaded files remain on the device; private-tab download entries are not saved in persistent browser download history.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Tracking protection", style = MaterialTheme.typography.titleSmall)
                        Text(
                            when {
                                browser.activeTab == null -> "Tracking protection status is unavailable for the active tab."
                                !browser.activeTab!!.trackingProtectionEnabled -> "Tracking protection is disabled for the active GeckoView session."
                                browser.activeTab!!.trackingProtectionException -> "GeckoView reports a site-specific tracking-protection exception for this page."
                                else -> "Tracking protection is enabled for the active GeckoView session; no site exception is reported."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Rivo does not show a blocked-tracker number because this screen does not have a verified per-page tracker count. Location, camera, microphone, persistent-storage requests and unsupported content permissions are denied by default.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(RivoSpacing.small))
                        if (browser.sitePermissionsLoading) {
                            Text("Loading permissions from GeckoView…")
                        } else if (browser.sitePermissionsLoadError) {
                            Text("GeckoView permission data is unavailable. No permission state is being assumed.")
                        } else if (browser.sitePermissions.isEmpty()) {
                            Text("GeckoView reports no saved normal-browsing site permissions.")
                        }
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(
                                browser.sitePermissions,
                                key = { "${it.origin}|${it.permissionName}|${it.contextKey.orEmpty()}" }
                            ) { permission ->
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                    Text(permission.origin, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                                    Text(permission.permissionName, style = MaterialTheme.typography.bodyMedium)
                                    if (permission.permissionName == "Tracking protection exception") {
                                        Text(
                                            "This permission lets trackers operate on this site by disabling GeckoView tracking protection for it.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(
                                        if (permission.allowed) "Allowed" else "Denied",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (permission.allowed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    if (permission.canRevoke) {
                                        TextButton(onClick = { browser.revokeSitePermission(permission) }) {
                                            Text(if (permission.allowed) "Revoke" else "Reset")
                                        }
                                    } else {
                                        Text(
                                            "GeckoView does not support revoking this permission after it is granted.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            "Private-session permission records are not shown here. GeckoView keeps private-mode decisions separate and clears them with the private session.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    BrowserPanel.APPEARANCE -> {
                        BrowserAppearance.values().forEach { appearance ->
                            TextButton(
                                onClick = {
                                    browser.updateAppearance(appearance)
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
