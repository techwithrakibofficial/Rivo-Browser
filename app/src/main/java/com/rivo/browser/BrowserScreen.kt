package com.rivo.browser

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import com.rivo.browser.designsystem.RivoColorSchemes
import com.rivo.browser.designsystem.RivoSpacing
import com.rivo.browser.designsystem.RivoShapes
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.app.Activity
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.foundation.verticalScroll
import java.util.Locale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var address by remember(active?.id) { mutableStateOf(active?.url?.takeUnless { it == BrowserController.HOME_URL }.orEmpty()) }
    var isEditingAddress by remember(active?.id) { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var profileMenuExpanded by remember { mutableStateOf(false) }
    var showClearDataConfirmation by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> browser.completeNotificationPermissionRequest(granted) }
    val devicePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        browser.completeAndroidPermissionRequest(results)
    }
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
        if (active != null && !isEditingAddress) address = active.url.takeUnless { it == BrowserController.HOME_URL }.orEmpty()
    }
    LaunchedEffect(browser.pendingAndroidPermissionNames) {
        val requested = browser.pendingAndroidPermissionNames ?: return@LaunchedEffect
        browser.beginAndroidPermissionRequest()
        devicePermissionLauncher.launch(requested.toTypedArray())
    }

    val currentPermissionPrompt = browser.permissionPrompt
    LaunchedEffect(currentPermissionPrompt) { currentPermissionPrompt?.onShown?.invoke() }

    MaterialTheme(colorScheme = RivoColorSchemes.forAppearance(browser.appearance)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                val currentPageBookmarked = active?.url?.startsWith("http") == true &&
                    browser.bookmarks.any { it.url == active.url }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.78f))
                        .padding(horizontal = RivoSpacing.small, vertical = RivoSpacing.compact)
                ) {
                    // Chrome-inspired primary toolbar: navigation, omnibox, tab switcher and menu.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(onClick = { browser.goBack() }, enabled = active?.canGoBack == true, modifier = Modifier.size(36.dp)) {
                            Text("‹", style = MaterialTheme.typography.headlineSmall, color = if (active?.canGoBack == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f), modifier = Modifier.semantics { contentDescription = "Go back" })
                        }
                        IconButton(onClick = { browser.goForward() }, enabled = active?.canGoForward == true, modifier = Modifier.size(36.dp)) {
                            Text("›", style = MaterialTheme.typography.headlineSmall, color = if (active?.canGoForward == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f), modifier = Modifier.semantics { contentDescription = "Go forward" })
                        }
                        IconButton(onClick = { browser.reloadOrStop() }, modifier = Modifier.size(36.dp)) {
                            Text(if (active?.loading == true) "×" else "↻", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.semantics { contentDescription = if (active?.loading == true) "Stop loading" else "Reload page" })
                        }
                        Surface(
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 10.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    when {
                                        active?.url == BrowserController.HOME_URL -> "⌕"
                                        active?.securityKnown == true && active.secureConnection && !active.certificateException -> "▣"
                                        else -> "ⓘ"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(Modifier.weight(1f).padding(start = 7.dp)) {
                                    if (address.isEmpty()) {
                                        Text("Search Google or enter website", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    }
                                    BasicTextField(
                                        value = address,
                                        onValueChange = {
                                            address = it
                                            isEditingAddress = true
                                        },
                                        modifier = Modifier.fillMaxWidth().onFocusChanged {
                                            isEditingAddress = it.isFocused
                                        }.semantics { contentDescription = "Address and Google search bar" },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                                        keyboardActions = KeyboardActions(onGo = {
                                            val submittedAddress = address
                                            if (submittedAddress.isNotBlank()) browser.navigate(submittedAddress)
                                            isEditingAddress = false
                                            keyboard?.hide()
                                        })
                                    )
                                }
                                if (address.isNotEmpty()) {
                                    IconButton(onClick = { address = ""; isEditingAddress = true }, modifier = Modifier.size(30.dp)) {
                                        Text("×", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                        IconButton(onClick = { browser.openPanel(BrowserPanel.TABS) }, modifier = Modifier.size(38.dp)) {
                            Text("[${browser.tabs.size}]", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.semantics { contentDescription = "Open tabs" })
                        }
                        Box {
                            TextButton(onClick = { menuExpanded = true }, modifier = Modifier.size(34.dp)) {
                                Text("⋮", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.semantics { contentDescription = "More browser options" })
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(text = { Text(if (active?.loading == true) "Stop" else "Reload") }, onClick = { menuExpanded = false; browser.reloadOrStop() })
                                DropdownMenuItem(text = { Text("New tab") }, onClick = { menuExpanded = false; browser.createTab() })
                                DropdownMenuItem(text = { Text("New private tab") }, onClick = { menuExpanded = false; browser.createTab(privateMode = true) })
                                DropdownMenuItem(text = { Text("Bookmarks") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.BOOKMARKS) })
                                DropdownMenuItem(text = { Text("History") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.HISTORY) })
                                DropdownMenuItem(text = { Text("Downloads") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.DOWNLOADS) })
                                DropdownMenuItem(text = { Text("Recent tabs") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.TABS) })
                                DropdownMenuItem(text = { Text("Appearance") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.APPEARANCE) })
                                DropdownMenuItem(text = { Text("Text size · ${(browser.textZoom * 100).toInt()}%") }, onClick = { menuExpanded = false; browser.updateTextZoom(1f) })
                                DropdownMenuItem(text = { Text("Decrease text size") }, enabled = browser.textZoom > 0.8f, onClick = { menuExpanded = false; browser.updateTextZoom(browser.textZoom - 0.1f) })
                                DropdownMenuItem(text = { Text("Increase text size") }, enabled = browser.textZoom < 2f, onClick = { menuExpanded = false; browser.updateTextZoom(browser.textZoom + 0.1f) })
                                DropdownMenuItem(text = { Text(if (active?.desktopMode == true) "Use mobile site" else "Desktop site") }, trailingIcon = { if (active?.desktopMode == true) Text("✓", color = MaterialTheme.colorScheme.primary) }, enabled = active?.url?.startsWith("https://") == true || active?.url?.startsWith("http://") == true, onClick = { menuExpanded = false; browser.toggleDesktopMode() })
                                DropdownMenuItem(text = { Text("Reader mode") }, onClick = { menuExpanded = false; browser.openReaderMode() })
                                DropdownMenuItem(text = { Text("Scan QR code") }, onClick = {
                                    menuExpanded = false
                                    try {
                                        GmsBarcodeScanning.getClient(context, GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()).startScan()
                                            .addOnSuccessListener { barcode -> browser.showQrScanResult(barcode.rawValue) }
                                            .addOnCanceledListener { }
                                            .addOnFailureListener { browser.showNotice("QR scanner is unavailable. Check Google Play services and try again.") }
                                    } catch (_: Exception) {
                                        browser.showNotice("QR scanner could not start. Check Google Play services and try again.")
                                    }
                                })
                                DropdownMenuItem(text = { Text("Privacy Intelligence Center") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.SITE_PERMISSIONS) })
                                DropdownMenuItem(text = { Text("Developer mode") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.DEVELOPER) })
                                DropdownMenuItem(text = { Text("Clear browsing data") }, onClick = { menuExpanded = false; showClearDataConfirmation = true })
                                DropdownMenuItem(text = { Text("Settings") }, onClick = { menuExpanded = false; browser.openPanel(BrowserPanel.SETTINGS) })
                            }
                        }
                    }
                    // Secondary action strip keeps the full set of commonly used Chrome-style actions reachable.
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TextButton(onClick = { browser.navigate(BrowserController.HOME_URL) }) { Text("⌂  Home") }
                        TextButton(onClick = {
                            if (currentPageBookmarked) active?.url?.let(browser::removeBookmark) else browser.addCurrentBookmark()
                        }, enabled = active?.url?.startsWith("http") == true) {
                            Text(if (currentPageBookmarked) "★  Saved" else "☆  Bookmark")
                        }
                        TextButton(onClick = { browser.shareCurrentPage() }, enabled = active?.url?.startsWith("http") == true) { Text("↗  Share") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.DOWNLOADS) }) { Text("↓  Downloads") }
                        TextButton(onClick = { browser.createTab() }) { Text("+  New tab") }
                        TextButton(onClick = { browser.createTab(privateMode = true) }) { Text("◉  Incognito") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.HISTORY) }) { Text("◷  History") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.BOOKMARKS) }) { Text("★  Bookmarks") }
                        Box {
                            TextButton(onClick = { profileMenuExpanded = true }) { Text("●  Profile") }
                            DropdownMenu(expanded = profileMenuExpanded, onDismissRequest = { profileMenuExpanded = false }) {
                                DropdownMenuItem(text = { Text("Guest profile") }, onClick = { profileMenuExpanded = false; browser.openPanel(BrowserPanel.SETTINGS) })
                                DropdownMenuItem(text = { Text("Passwords & autofill") }, onClick = { profileMenuExpanded = false; browser.openPanel(BrowserPanel.SETTINGS) })
                                DropdownMenuItem(text = { Text("Account sync unavailable") }, enabled = false, onClick = {})
                            }
                        }
                    }
                    if (active?.loading == true) {
                        LinearProgressIndicator(progress = { active.progress / 100f }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }
                    if (active?.url != BrowserController.HOME_URL && active?.privateMode == true) {
                        Text("PRIVATE TAB · This session is not saved to local history", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = RivoSpacing.small, top = RivoSpacing.compact))
                    }
                    if (active?.url != BrowserController.HOME_URL && active?.desktopMode == true) {
                        Text("DESKTOP SITE REQUESTED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = RivoSpacing.small, top = RivoSpacing.compact))
                    }
                    if (active?.url != BrowserController.HOME_URL && active != null && !active.privateMode && active.securityKnown) {
                        Text(
                            BrowserSecurityStatusPolicy.label(secure = active.secureConnection, certificateException = active.certificateException, activeMixedContentLoaded = active.activeMixedContentLoaded, passiveMixedContentLoaded = active.passiveMixedContentLoaded, mixedContentStatusUnknown = active.mixedContentStatusUnknown),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                active.certificateException || !active.secureConnection || active.activeMixedContentLoaded || active.passiveMixedContentLoaded -> MaterialTheme.colorScheme.error
                                active.mixedContentStatusUnknown -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.padding(start = RivoSpacing.small, top = RivoSpacing.compact)
                        )
                    }
                }

                if (active?.url == BrowserController.HOME_URL) {
                    RivoHomeScreen(
                        bookmarks = browser.bookmarks,
                        history = browser.history,
                        privateMode = active.privateMode,
                        onSearch = { browser.navigate(it) },
                        onOpenUrl = { browser.navigate(it) },
                        onNewPrivateTab = { browser.createTab(privateMode = true) },
                        onOpenBookmarks = { browser.openPanel(BrowserPanel.BOOKMARKS) },
                        onOpenHistory = { browser.openPanel(BrowserPanel.HISTORY) },
                        onOpenTabs = { browser.openPanel(BrowserPanel.TABS) },
                        modifier = Modifier.weight(1f)
                    )
                } else if (active != null) {
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

                Surface(
                    // Two stacked controls (icon + label) need more than the old 48 dp slot.
                    // safeDrawingPadding already accounts for the system navigation inset.
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.68f),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { browser.goBack() }, enabled = active?.canGoBack == true, modifier = Modifier.weight(1f).height(60.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("‹", style = MaterialTheme.typography.titleMedium)
                                Text("Back", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        TextButton(onClick = { browser.goForward() }, enabled = active?.canGoForward == true, modifier = Modifier.weight(1f).height(60.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("›", style = MaterialTheme.typography.titleMedium)
                                Text("Forward", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        TextButton(onClick = { browser.createTab() }, modifier = Modifier.weight(1f).height(60.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("+", style = MaterialTheme.typography.titleMedium)
                                Text("New tab", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        TextButton(onClick = { browser.addCurrentBookmark() }, enabled = active?.url?.startsWith("http") == true, modifier = Modifier.weight(1f).height(60.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☆", style = MaterialTheme.typography.titleMedium)
                                Text("Save", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.TABS) }, modifier = Modifier.weight(1f).height(60.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("▢ ${browser.tabs.size}", style = MaterialTheme.typography.titleSmall)
                                Text("Tabs", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

    if (browser.panel != null) {
        BrowserPanelDialog(
            browser = browser,
            onDismiss = { browser.openPanel(null) },
            onClearBrowsingData = { showClearDataConfirmation = true }
        )
    }

    browser.readerArticle?.takeIf { it.tabId == browser.activeTabId }?.let { article ->
        ReaderModeDialog(article = article, onDismiss = { browser.closeReaderMode() })
    }

    if (browser.readerLoading) {
        AlertDialog(
            onDismissRequest = { browser.closeReaderMode() },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            title = { Text("Preparing Reader Mode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                    Text("Extracting the article using GeckoView.")
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = { browser.closeReaderMode() }) { Text("Cancel") }
            }
        )
    }

    browser.qrScanResult?.let { value ->
        AlertDialog(
            onDismissRequest = { browser.dismissQrScanResult() },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            title = { Text("QR code result") },
            text = { Text(value, maxLines = 8) },
            confirmButton = {
                TextButton(onClick = { browser.openQrScanResult() }) { Text("Open or search") }
            },
            dismissButton = {
                TextButton(onClick = { browser.dismissQrScanResult() }) { Text("Cancel") }
            }
        )
    }

    if (showClearDataConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirmation = false },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            title = { Text("Clear browsing data?") },
            text = { Text("This closes all open tabs and clears browsing history, cookies, site storage, cache, site permissions and download history. Bookmarks and normal downloads are kept; private downloads are deleted. Android app-level permission denials are retained.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearDataConfirmation = false
                    browser.openPanel(null)
                    browser.clearBrowsingData()
                }, enabled = !browser.clearingBrowsingData) { Text("Clear data") }
            },
            dismissButton = { TextButton(onClick = { showClearDataConfirmation = false }) { Text("Cancel") } }
        )
    }
    browser.pendingDownload?.let { request ->
        val downloadUri = android.net.Uri.parse(request.url)
        val fileName = downloadUri.lastPathSegment ?: "download"
        AlertDialog(
            onDismissRequest = { browser.cancelPendingDownload() },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
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
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            title = { Text("Website permission") },
            text = {
                Text(
                    "${request.origin} wants to access ${request.permission}. " +
                        if (request.privateMode) "This decision applies only to this private tab." else "Allow only if you trust this website."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (request.permission == "Notifications" && Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        browser.beginNotificationPermissionRequest()
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else browser.respondToSitePermission(true)
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
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            title = { Text("Rivo Browser") },
            text = { Text(browser.notice.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { browser.dismissNotice() }) { Text("OK") }
            }
        )
    }
    }
}

@Composable
private fun ReaderModeDialog(article: ReaderArticle, onDismiss: () -> Unit) {
    var textScale by remember(article.url) { mutableStateOf(1f) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(RivoShapes.panel),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f),
            modifier = Modifier.fillMaxWidth().heightIn(max = 640.dp)
        ) {
            Column(modifier = Modifier.padding(RivoSpacing.large)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Reader Mode", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f).semantics { heading() })
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Text(article.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    article.language + " · " + article.wordCount + " words",
                    style = MaterialTheme.typography.labelSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Text size", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        valueRange = 0.85f..1.5f,
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Article text size" }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
                ) {
                    Text(
                        article.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * textScale,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * textScale
                        )
                    )
                    if (article.wasTruncated) {
                        Text(
                            "Article preview shortened to protect memory use.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

private fun formatTransferSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = -1
    do {
        size /= 1024.0
        unitIndex++
    } while (size >= 1024.0 && unitIndex < units.lastIndex)
    return String.format(Locale.ROOT, "%.1f %s", size, units[unitIndex])
}

@Composable
private fun keyTab(id: Long, content: @Composable () -> Unit) {
    androidx.compose.runtime.key(id) { content() }
}

@Composable
private fun BrowserPanelDialog(browser: BrowserController, onDismiss: () -> Unit, onClearBrowsingData: () -> Unit) {
    val panel = browser.panel ?: return
    var tabQuery by remember(panel) { mutableStateOf("") }
    var tabFilter by remember(panel) { mutableStateOf(TabUniverseFilter.ALL) }
    var tabSort by remember(panel) { mutableStateOf(TabUniverseSort.ACTIVE_FIRST) }
    var sortMenuExpanded by remember(panel) { mutableStateOf(false) }
    var transferQuery by remember(panel) { mutableStateOf("") }
    var transferFilter by remember(panel) { mutableStateOf(TransferFilter.ALL) }
    LaunchedEffect(panel) {
        if (panel == BrowserPanel.SITE_PERMISSIONS) browser.refreshSitePermissions()
    }
    val title = when (panel) {
        BrowserPanel.TABS -> "Your tabs"
        BrowserPanel.BOOKMARKS -> "Bookmarks"
        BrowserPanel.HISTORY -> "History"
        BrowserPanel.DOWNLOADS -> "Transfer Studio"
        BrowserPanel.APPEARANCE -> "Quick appearance"
        BrowserPanel.SETTINGS -> "Settings"
        BrowserPanel.DEVELOPER -> "Developer mode"
        BrowserPanel.SITE_PERMISSIONS -> "Privacy Intelligence Center"
        BrowserPanel.MENU -> "Browser menu"
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(RivoShapes.panel),
            color = if (browser.appearance == BrowserAppearance.GLASS) Color(0xFF202842) else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().heightIn(max = 640.dp)
        ) {
            Column(modifier = Modifier.padding(RivoSpacing.large).heightIn(max = 600.dp).verticalScroll(rememberScrollState())) {
                Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(RivoSpacing.medium))
                when (panel) {
                    BrowserPanel.TABS -> {
                        val tabItems = browser.tabs.map { tab -> TabUniverseItem(tab.id, tab.title, tab.url, tab.privateMode, tab.id == browser.activeTabId) }
                        val visibleTabs = TabUniversePolicy.visible(tabItems, tabQuery, tabFilter, tabSort)
                        OutlinedTextField(value = tabQuery, onValueChange = { tabQuery = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Search tabs by title or URL") }, placeholder = { Text("Search open tabs") })
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(RivoSpacing.compact), verticalAlignment = Alignment.CenterVertically) {
                            listOf(TabUniverseFilter.ALL to "All", TabUniverseFilter.NORMAL to "Normal", TabUniverseFilter.PRIVATE to "Private", TabUniverseFilter.ACTIVE to "Active").forEach { (filter, label) ->
                                FilterChip(selected = tabFilter == filter, onClick = { tabFilter = filter }, label = { Text(label) })
                            }
                        }
                        Box {
                            TextButton(onClick = { sortMenuExpanded = true }) {
                                Text("Organize: " + when (tabSort) { TabUniverseSort.ACTIVE_FIRST -> "Active first"; TabUniverseSort.TITLE -> "Title"; TabUniverseSort.TYPE -> "Normal / private" })
                            }
                            DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                                listOf(TabUniverseSort.ACTIVE_FIRST to "Active first", TabUniverseSort.TITLE to "Title", TabUniverseSort.TYPE to "Normal / private").forEach { (sort, label) ->
                                    DropdownMenuItem(text = { Text(label) }, onClick = { tabSort = sort; sortMenuExpanded = false })
                                }
                            }
                        }
                        Text("${visibleTabs.size} of ${browser.tabs.size} tabs · ${browser.tabs.count { it.privateMode }} private", style = MaterialTheme.typography.labelSmall)
                        if (visibleTabs.isEmpty()) Text("No tabs match this search.") else {
                            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                gridItems(visibleTabs, key = { it.id }) { item ->
                                    val tab = browser.tabs.firstOrNull { it.id == item.id } ?: return@gridItems
                                    Surface(onClick = { browser.selectTab(tab.id); onDismiss() }, shape = RoundedCornerShape(14.dp), color = if (item.active) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f), border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = if (item.active) 0.75f else 0.22f))) {
                                        Column(Modifier.padding(7.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                            Box(Modifier.fillMaxWidth().height(76.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f), RoundedCornerShape(8.dp)).padding(3.dp)) {
                                                if (!item.privateMode && tab.previewBitmap != null) {
                                                    Image(
                                                        bitmap = tab.previewBitmap!!.asImageBitmap(),
                                                        contentDescription = "Preview of ${item.title}",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                } else {
                                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(3.dp)) {
                                                        Text(if (item.privateMode) "PRIVATE" else "WEB PAGE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                                        Text(item.title, style = MaterialTheme.typography.labelSmall, maxLines = 3)
                                                    }
                                                }
                                            }
                                            Text(item.title, style = MaterialTheme.typography.labelSmall, maxLines = 2)
                                            Text(runCatching { android.net.Uri.parse(item.url).host }.getOrNull() ?: "New tab", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (item.active) Text("●", color = MaterialTheme.colorScheme.primary)
                                                Spacer(Modifier.weight(1f))
                                                TextButton(onClick = { browser.closeTab(tab.id) }, modifier = Modifier.size(28.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("×") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Text("4-column tab overview with title and site previews. Live page screenshots are not represented as if available.", style = MaterialTheme.typography.bodySmall)
                    }
                    BrowserPanel.BOOKMARKS -> {
                        if (browser.bookmarks.isEmpty()) Text("No bookmarks saved yet.")
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
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
                                    TextButton(
                                        onClick = { browser.removeBookmark(page.url) },
                                        modifier = Modifier.semantics {
                                            contentDescription = "Remove bookmark: ${page.title}"
                                        }
                                    ) { Text("×") }
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
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
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
                        val records = browser.downloads.take(100).map { download ->
                            TransferRecord(
                                id = download.id,
                                filename = download.filename,
                                status = download.status,
                                mimeType = download.mimeType,
                                sizeBytes = download.file?.let { file -> runCatching { file.length() }.getOrNull() },
                                hasFile = download.file?.exists() == true,
                                bytesReceived = download.bytesReceived,
                                totalBytes = download.totalBytes
                            )
                        }
                        val visibleTransfers = TransferStudioPolicy.visible(records, transferQuery, transferFilter)
                        val completedCount = browser.downloads.count { it.file?.exists() == true }
                        val activeCount = browser.downloads.count { it.status == "Downloading" || it.status == "Cancelling" }
                        val failedCount = browser.downloads.count { it.status == "Failed" || it.status == "Cancelled" }
                        Text(
                            "$completedCount completed · $activeCount active · $failedCount failed/cancelled",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (browser.downloads.size > records.size) {
                            Text("Showing the most recent ${records.size} of ${browser.downloads.size} transfers.",
                                style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedTextField(
                            value = transferQuery,
                            onValueChange = { transferQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Search transfers") },
                            placeholder = { Text("Filename or file type") }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(RivoSpacing.compact),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                TransferFilter.ALL to "All",
                                TransferFilter.ACTIVE to "Active",
                                TransferFilter.COMPLETED to "Completed",
                                TransferFilter.FAILED to "Failed / cancelled"
                            ).forEach { (filter, label) ->
                                FilterChip(
                                    selected = transferFilter == filter,
                                    onClick = { transferFilter = filter },
                                    label = { Text(label) }
                                )
                            }
                        }
                        if (visibleTransfers.isEmpty()) {
                            Text(
                                if (records.isEmpty()) "No transfers yet." else "No transfers match this search or filter.",
                                modifier = Modifier.padding(vertical = RivoSpacing.medium)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 340.dp)) {
                                items(visibleTransfers, key = { it.id }) { item ->
                                    val download = browser.downloads.firstOrNull { it.id == item.id } ?: return@items
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = RivoSpacing.small)) {
                                        Text(item.filename, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                                        Text(
                                            buildString {
                                                append(item.status)
                                                item.sizeBytes?.let { append(" · ").append(formatTransferSize(it)) }
                                                item.mimeType?.let { append(" · ").append(it) }
                                            },
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        if (item.status == "Downloading" || item.status == "Cancelling") {
                                            val totalBytes = item.totalBytes
                                            if (totalBytes != null && totalBytes > 0) {
                                                LinearProgressIndicator(
                                                    progress = { (item.bytesReceived.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Text(
                                                    "${formatTransferSize(item.bytesReceived)} / ${formatTransferSize(totalBytes)}",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            } else {
                                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                                Text(
                                                    "${formatTransferSize(item.bytesReceived)} received",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(RivoSpacing.compact)) {
                                            if (item.hasFile) {
                                                TextButton(onClick = { browser.openDownload(download) }) { Text("Open") }
                                                TextButton(onClick = { browser.shareDownload(download) }) { Text("Share") }
                                            }
                                            if (item.status == "Downloading") {
                                                TextButton(onClick = { browser.cancelDownload(download) }) { Text("Cancel") }
                                            }
                                            if (item.status != "Downloading" && item.status != "Cancelling") {
                                                TextButton(onClick = { browser.removeDownload(download) }) { Text("Remove") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            "Files stay in Rivo's app-specific Downloads folder. Removing a completed transfer deletes its managed file. Private browsing does not automatically erase saved files.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    BrowserPanel.DEVELOPER -> {
                        val tab = browser.activeTab
                        val settings = tab?.engine?.session?.settings
                        Text("Engine: GeckoView 159.0.20261022150525")
                        Text("Android API: " + Build.VERSION.SDK_INT)
                        Text("Supported ABIs: " + Build.SUPPORTED_ABIS.joinToString())
                        Text("Session: " + when {
                            tab == null -> "No active tab"
                            tab.privateMode -> "Private"
                            else -> "Normal"
                        })
                        Text("JavaScript: " + if (settings?.getAllowJavascript() == false) "Disabled" else "Enabled")
                        Text("Tracking protection: " + if (settings?.getUseTrackingProtection() == true) "Enabled" else "Not reported")
                        Text("Remote debugging: Disabled")
                        Text(
                            "GeckoView supports WebExtensions, but Rivo does not yet ship an add-on manager or marketplace. Only Mozilla-signed external XPI installs and APK-bundled built-in extensions are documented integration paths.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Developer mode is read-only diagnostics in this build. Remote debugging, console logging and about:config are not exposed, to avoid creating a production attack surface.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    BrowserPanel.SITE_PERMISSIONS -> {
                        Text("Secure storage", style = MaterialTheme.typography.titleSmall)
                        Text("Browser metadata uses AES-256-GCM with Android Keystore. Downloaded file contents are not encrypted by this metadata cipher.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Private browsing", style = MaterialTheme.typography.titleSmall)
                        Text("Private tabs are excluded from app history, bookmarks and tab restoration. Private download entries are not persisted; downloaded files remain on the device. Private browsing does not hide network activity or your IP address.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Tracking protection", style = MaterialTheme.typography.titleSmall)
                        Text(when {
                            browser.activeTab == null -> "Status unavailable."
                            !browser.activeTab!!.trackingProtectionEnabled -> "Disabled for this GeckoView session."
                            browser.activeTab!!.trackingProtectionException -> "GeckoView reports a site-specific exception."
                            else -> "Enabled; no site exception reported."
                        })
                        if (browser.sitePermissionsLoading) Text("Loading permissions from GeckoView…")
                        else if (browser.sitePermissionsLoadError) Text("Permission data unavailable; no state is assumed.")
                        else if (browser.sitePermissions.isEmpty()) Text("No saved normal-mode site permissions.")
                        LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                            items(browser.sitePermissions, key = { it.origin + "|" + it.permissionName + "|" + it.contextKey.orEmpty() }) { permission ->
                                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Text(permission.origin, style = MaterialTheme.typography.titleSmall)
                                    Text(permission.permissionName)
                                    Text(if (permission.allowed) "Allowed" else "Denied")
                                    if (permission.canRevoke) TextButton(onClick = { browser.revokeSitePermission(permission) }) { Text("Reset permission") }
                                    else Text("This GeckoView permission cannot be revoked after grant.", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        Text("Location, camera and microphone requests require HTTPS, Android runtime permission and explicit site consent. Private site grants are not persisted. Unsupported capabilities and persistent-storage grants remain denied.", style = MaterialTheme.typography.bodySmall)
                        if (browser.deniedAndroidPermissionLabels.isNotEmpty()) {
                            Text(
                                "Android app permissions denied: ${browser.deniedAndroidPermissionLabels.joinToString()}. Enable them in Android Settings before retrying a site request.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    BrowserPanel.SETTINGS -> {
                        Text("Basics", style = MaterialTheme.typography.titleSmall)
                        Text("Search engine", style = MaterialTheme.typography.bodyMedium)
                        Text("Google · default search provider", style = MaterialTheme.typography.bodySmall)
                        Text("Homepage", style = MaterialTheme.typography.bodyMedium)
                        Text("Rivo home · opens when you create a new tab", style = MaterialTheme.typography.bodySmall)
                        Text("Address bar", style = MaterialTheme.typography.bodyMedium)
                        Text("Shown at the top while browsing websites", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Appearance & accessibility", style = MaterialTheme.typography.titleSmall)
                        BrowserAppearance.values().forEach { option ->
                            Row(modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).selectable(selected = browser.appearance == option, role = Role.RadioButton, onClick = { browser.updateAppearance(option) }), verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material3.RadioButton(selected = browser.appearance == option, onClick = null)
                                Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                        Text("Website text size · ${(browser.textZoom * 100).toInt()}%", style = MaterialTheme.typography.titleSmall)
                        Slider(value = browser.textZoom, onValueChange = browser::updateTextZoom, valueRange = 0.8f..2.0f, steps = 11)
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text("80%", style = MaterialTheme.typography.labelSmall); Text("100%", style = MaterialTheme.typography.labelSmall); Text("200%", style = MaterialTheme.typography.labelSmall) }
                        Text("GeckoView global web-content font scaling. The active page reloads to apply changes.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Privacy & security", style = MaterialTheme.typography.titleSmall)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("JavaScript", style = MaterialTheme.typography.bodyMedium)
                                Text("Some websites need JavaScript to work.", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(checked = browser.javascriptEnabled, onCheckedChange = browser::updateJavascriptEnabled)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Global Privacy Control", style = MaterialTheme.typography.bodyMedium)
                                Text("Send a GPC privacy preference signal to supported websites. GeckoView enables GPC in private mode by default.", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(checked = browser.globalPrivacyControl, onCheckedChange = browser::updateGlobalPrivacyControl)
                        }
                        Text("Private tabs are excluded from local history and tab restoration. Tracking protection is enabled by the GeckoView session configuration.", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { browser.openPanel(BrowserPanel.SITE_PERMISSIONS) }) { Text("Site settings & Privacy Intelligence Center") }
                        TextButton(onClick = onClearBrowsingData, enabled = !browser.clearingBrowsingData) { Text(if (browser.clearingBrowsingData) "Clearing…" else "Clear browsing data") }
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Passwords & autofill", style = MaterialTheme.typography.titleSmall)
                        Text("Android Autofill compatibility", style = MaterialTheme.typography.bodyMedium)
                        Text("GeckoView can expose supported web forms to an Autofill provider already configured on your phone. Rivo does not yet ship its own password vault or account sync.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(RivoSpacing.small))
                        Text("Your browser", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { browser.openPanel(BrowserPanel.TABS) }) { Text("Tabs and tab groups") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.BOOKMARKS) }) { Text("Bookmarks") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.HISTORY) }) { Text("History") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.DOWNLOADS) }) { Text("Downloads · Transfer Studio") }
                        TextButton(onClick = { browser.openPanel(BrowserPanel.DEVELOPER) }) { Text("About Rivo & developer diagnostics") }
                        Text("A built-in password vault, Rivo account sign-in, cloud sync and an extension marketplace are not currently shipped. Android's system default-browser selection is managed outside Rivo.", style = MaterialTheme.typography.bodySmall)
                    }
                    BrowserPanel.APPEARANCE -> {
                        BrowserAppearance.values().forEach { appearance ->
                            val selected = browser.appearance == appearance
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .selectable(
                                        selected = selected,
                                        role = Role.RadioButton,
                                        onClick = {
                                            browser.updateAppearance(appearance)
                                            onDismiss()
                                        }
                                    )
                                    .padding(vertical = RivoSpacing.compact),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                                Text(appearance.name.lowercase().replaceFirstChar { it.uppercase() })
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
