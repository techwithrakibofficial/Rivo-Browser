package com.rivo.browser

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import com.rivo.browser.data.BrowserStorage
import com.rivo.browser.data.SavedPage
import com.rivo.browser.data.SavedDownload
import com.rivo.browser.data.SavedTab
import com.rivo.browser.data.RestorableTabCandidate
import com.rivo.browser.data.RestorableTabPolicy
import com.rivo.browser.data.HistoryVisitPolicy
import com.rivo.browser.engine.gecko.GeckoBrowserRuntime
import com.rivo.browser.engine.gecko.GeckoBrowserTab
import com.rivo.browser.engine.gecko.GeckoTabListener
import com.rivo.browser.engine.gecko.GeckoStoredSitePermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.io.InputStream
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.mozilla.geckoview.StorageController

enum class BrowserAppearance { DARK, LIGHT, GLASS }

class BrowserTabState(
    val id: Long,
    val privateMode: Boolean,
    initialUrl: String
) {
    var url by mutableStateOf(initialUrl)
    var title by mutableStateOf(if (privateMode) "Private tab" else "New tab")
    var loading by mutableStateOf(false)
    var progress by mutableStateOf(0)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    var secureConnection by mutableStateOf(false)
    var securityKnown by mutableStateOf(false)
    var certificateException by mutableStateOf(false)
    var trackingProtectionEnabled by mutableStateOf(false)
    var trackingProtectionException by mutableStateOf(false)
    lateinit var engine: GeckoBrowserTab
}

/**
 * Activity-scoped browser state. GeckoRuntime lives for the process; each tab
 * owns a GeckoSession and is explicitly activated/deactivated/closed.
 */
class BrowserController(context: Context) {
    private val appContext = context.applicationContext
    private val storage = BrowserStorage(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val runtime = GeckoBrowserRuntime.get(appContext)
    private var nextId = 1L
    private var readerRequestId = 0L
    private var hostStarted = true

    val tabs = mutableStateListOf<BrowserTabState>()
    val bookmarks = mutableStateListOf<SavedPage>()
    val history = mutableStateListOf<SavedPage>()
    val downloads = mutableStateListOf<BrowserDownload>()
    val sitePermissions = mutableStateListOf<GeckoStoredSitePermission>()
    var sitePermissionsLoading by mutableStateOf(false)
    var sitePermissionsLoadError by mutableStateOf(false)
    var clearingBrowsingData by mutableStateOf(false)
    private var awaitingNotificationPermissionResult = false
    var pendingAndroidPermissionNames by mutableStateOf<List<String>?>(null)
        private set
    private var pendingAndroidPermissionResponse: ((Boolean) -> Unit)? = null
    private var awaitingAndroidPermissionResult = false
    private val deniedAndroidPermissions = mutableSetOf<String>()

    var activeTabId by mutableStateOf(0L)
        private set
    var appearance by mutableStateOf(BrowserAppearance.DARK)
        private set
    var panel by mutableStateOf<BrowserPanel?>(null)
        private set
    var notice by mutableStateOf<String?>(null)
        private set
    var permissionPrompt by mutableStateOf<SitePermissionPrompt?>(null)
        private set
    var pendingDownload by mutableStateOf<PendingDownload?>(null)
        private set
    var readerArticle by mutableStateOf<ReaderArticle?>(null)
        private set
    var readerLoading by mutableStateOf(false)
        private set
    var qrScanResult by mutableStateOf<String?>(null)
        private set

    val activeTab: BrowserTabState?
        get() = tabs.firstOrNull { it.id == activeTabId }

    val deniedAndroidPermissionLabels: List<String>
        get() = buildList {
            if (android.Manifest.permission.ACCESS_COARSE_LOCATION in deniedAndroidPermissions ||
                android.Manifest.permission.ACCESS_FINE_LOCATION in deniedAndroidPermissions
            ) add("Location")
            if (android.Manifest.permission.CAMERA in deniedAndroidPermissions) add("Camera")
            if (android.Manifest.permission.RECORD_AUDIO in deniedAndroidPermissions) add("Microphone")
        }

    init {
        scope.launch {
            appearance = runCatching {
                BrowserAppearance.valueOf(storage.loadTheme().uppercase())
            }.getOrDefault(BrowserAppearance.DARK)
            try {
                bookmarks.addAll(storage.loadBookmarks())
                history.addAll(storage.loadHistory())
                deniedAndroidPermissions.addAll(storage.loadDeniedAndroidPermissions())
                cleanupInterruptedDownloads()
                restoreDownloads()
                val restoredTabs = storage.loadOpenTabs()
                if (restoredTabs.isEmpty()) {
                    createTab()
                } else {
                    restoredTabs.forEach { saved ->
                        createTab(url = saved.url, restoredTitle = saved.title)
                    }
                    // Select by saved record position, not URL: duplicate URLs are valid tabs.
                    val selectedIndex = restoredSelectedTabIndex(restoredTabs.map { it.selected })
                    tabs.getOrNull(selectedIndex)?.let { selectTab(it.id) }
                }
            } catch (error: Exception) {
                // Keep browsing available, but BrowserStorage switches to read-only after
                // an integrity failure so the fallback tab cannot overwrite saved records.
                notice = "Saved browser data could not be read. Existing data was preserved; sensitive saving is paused."
                if (tabs.isEmpty()) createTab()
            }
        }
    }

    fun createTab(
        privateMode: Boolean = false,
        url: String = HOME_URL,
        restoredTitle: String? = null
    ) {
        if (tabs.size >= MAX_TABS) {
            notice = "For memory stability, Rivo currently supports up to $MAX_TABS open tabs."
            return
        }
        val tab = BrowserTabState(nextId++, privateMode, url)
        if (!restoredTitle.isNullOrBlank()) tab.title = restoredTitle
        tab.engine = GeckoBrowserTab(
            runtime,
            privateMode,
            object : GeckoTabListener {
                override fun onLocationChanged(url: String, trackingProtectionException: Boolean) {
                    tab.url = url
                    if (readerArticle?.tabId == tab.id && readerArticle?.url != url) {
                        readerArticle = null
                        readerLoading = false
                        readerRequestId++
                    }
                    tab.trackingProtectionException = trackingProtectionException
                    if (!tab.privateMode && url.startsWith("http")) {
                        saveHistory(tab)
                        persistOpenTabs()
                    }
                }

                override fun onTitleChanged(title: String?) {
                    if (!title.isNullOrBlank()) tab.title = title
                    if (!tab.privateMode && tab.url.startsWith("http")) saveHistory(tab)
                }

                override fun onLoadingChanged(loading: Boolean, progress: Int) {
                    tab.loading = loading
                    tab.progress = progress.coerceIn(0, 100)
                }

                override fun onNavigationChanged(canGoBack: Boolean, canGoForward: Boolean) {
                    tab.canGoBack = canGoBack
                    tab.canGoForward = canGoForward
                }

                override fun onContentCrashed() {
                    tab.loading = false
                    notice = "A page process stopped. Reload the tab to continue."
                }

                override fun onSecurityChanged(secure: Boolean, exception: Boolean) {
                    tab.secureConnection = secure
                    tab.certificateException = exception
                    tab.securityKnown = true
                }

                override fun onExternalDownload(
                    url: String,
                    mimeType: String?,
                    disposition: String?,
                    contentLength: Long?,
                    body: InputStream?
                ) {
                    requestDownloadConfirmation(tab, url, mimeType, disposition, contentLength, body)
                }

                override fun onSitePermissionRequest(
                    origin: String,
                    permission: String,
                    privateMode: Boolean,
                    onShown: () -> Unit,
                    respond: (Boolean) -> Unit
                ) {
                    val cameraNeeded = permission.contains("Camera", ignoreCase = true)
                    val microphoneNeeded = permission.contains("Microphone", ignoreCase = true)
                    val devicePermissionMissing =
                        (cameraNeeded && appContext.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (microphoneNeeded && appContext.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    if (devicePermissionMissing || permissionPrompt != null || tab.id != activeTabId || tab.id !in tabs.map { it.id }) {
                        respond(false)
                        return
                    }
                    // Preserve scheme and non-default port so users can distinguish full origins.
                    permissionPrompt = SitePermissionPrompt(tab.id, origin, permission, privateMode, onShown, respond)
                }

                override fun onAndroidPermissionRequest(permissions: List<String>, respond: (Boolean) -> Unit) {
                    val requested = permissions.distinct()
                    val supported = setOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO
                    )
                    if (tab.id != activeTabId || requested.isEmpty() || requested.any { it !in supported } ||
                        pendingAndroidPermissionResponse != null
                    ) {
                        respond(false)
                        return
                    }
                    val newlyGranted = requested.filter(::hasAndroidPermission).toSet()
                    if (deniedAndroidPermissions.removeAll(newlyGranted)) persistDeniedAndroidPermissions()
                    if (requested.any { it in deniedAndroidPermissions && !hasAndroidPermission(it) }) {
                        respond(false)
                        return
                    }
                    val alreadyGranted = requested.all(::hasAndroidPermission)
                    if (alreadyGranted) {
                        respond(true)
                    } else {
                        pendingAndroidPermissionNames = requested
                        pendingAndroidPermissionResponse = respond
                    }
                }
            }
        )
        tab.trackingProtectionEnabled = tab.engine.isTrackingProtectionEnabled
        tabs.add(tab)
        selectTab(tab.id)
        tab.engine.load(url)
        persistOpenTabs()
    }

    fun selectTab(id: Long) {
        tabs.firstOrNull { it.id == activeTabId }?.engine?.setActive(false)
        activeTabId = id
        tabs.firstOrNull { it.id == id }?.let { tab ->
            tab.engine.setActive(
                TabSessionActivationPolicy.shouldActivate(tab.id, activeTabId, hostStarted)
            )
        }
        persistOpenTabs()
    }

    fun closeTab(id: Long) {
        val index = tabs.indexOfFirst { it.id == id }
        if (index < 0) return
        val closing = tabs[index]
        val wasActive = closing.id == activeTabId
        if (readerArticle?.tabId == id) {
            readerArticle = null
            readerLoading = false
            readerRequestId++
        }
        if (permissionPrompt?.tabId == id) respondToSitePermission(false)
        if (pendingDownload?.tabId == id) cancelPendingDownload()
        if (closing.privateMode) downloads.filter { it.privateTabId == id }.forEach { download ->
            if (download.status == "Downloading" || download.status == "Cancelling") {
                cancelDownload(download)
            }
            download.file?.let(::deletePrivateDownloadFile)
        }
        closing.engine.dispose()
        downloads.removeAll { it.privateTabId == id }
        tabs.removeAt(index)
        if (tabs.isEmpty()) {
            createTab()
        } else if (wasActive) {
            selectTab(tabs.getOrNull(index.coerceAtMost(tabs.lastIndex))?.id ?: tabs.last().id)
        } else {
            persistOpenTabs()
        }
    }

    fun navigate(rawInput: String) {
        val input = rawInput.trim()
        if (input.isEmpty()) return
        val url = resolveInput(input)
        val tab = activeTab ?: return
        if (readerArticle?.tabId == tab.id) {
            readerArticle = null
            readerLoading = false
            readerRequestId++
        }
        tab.url = url
        tab.engine.load(url)
    }

    /** Called by the Activity when its window becomes visible to the user. */
    fun onHostResumed() {
        hostStarted = true
        tabs.forEach { tab ->
            tab.engine.setActive(
                TabSessionActivationPolicy.shouldActivate(tab.id, activeTabId, hostStarted)
            )
        }
    }

    /** Release Gecko's foreground/focus state while the Activity is backgrounded. */
    fun onHostPaused() {
        hostStarted = false
        if (permissionPrompt != null && !awaitingNotificationPermissionResult && !awaitingAndroidPermissionResult) respondToSitePermission(false)
        if (pendingAndroidPermissionResponse != null && !awaitingAndroidPermissionResult) {
            pendingAndroidPermissionResponse?.invoke(false)
            pendingAndroidPermissionResponse = null
            pendingAndroidPermissionNames = null
        }
        if (pendingDownload != null) cancelPendingDownload()
        tabs.forEach { tab ->
            tab.engine.setActive(
                TabSessionActivationPolicy.shouldActivate(tab.id, activeTabId, hostStarted)
            )
        }
    }

    fun openReaderMode() {
        val tab = activeTab ?: return
        if (!tab.url.startsWith("https://") && !tab.url.startsWith("http://")) {
            notice = "Reader Mode is available only for web pages."
            return
        }
        if (tab.loading) {
            notice = "Wait until the page finishes loading, then try Reader Mode again."
            return
        }
        readerArticle = null
        readerLoading = true
        val requestId = ++readerRequestId
        val requestedUrl = tab.url
        tab.engine.extractReaderContent { result ->
            if (requestId != readerRequestId || !readerLoading || tabs.none { it.id == tab.id } || activeTabId != tab.id || tab.url != requestedUrl) {
                if (activeTabId == tab.id) readerLoading = false
                return@extractReaderContent
            }
            readerLoading = false
            result.onSuccess { extracted ->
                readerArticle = ReaderArticle(
                    tabId = tab.id,
                    title = tab.title,
                    url = requestedUrl,
                    content = extracted.content,
                    language = extracted.language,
                    wordCount = extracted.wordCount,
                    wasTruncated = extracted.wasTruncated
                )
            }.onFailure { error ->
                notice = error.message ?: "Reader Mode could not extract this page."
            }
        }
    }

    fun closeReaderMode() {
        readerRequestId++
        readerArticle = null
        readerLoading = false
    }

    fun showQrScanResult(value: String?) {
        val cleaned = QrCodePolicy.sanitize(value)
        if (cleaned == null) {
            notice = if ((value?.length ?: 0) > QrCodePolicy.MAX_RESULT_CHARS) {
                "This QR result is too long to open safely."
            } else {
                "The QR code did not contain readable text."
            }
            return
        }
        qrScanResult = cleaned
    }

    fun openQrScanResult() {
        val value = qrScanResult ?: return
        qrScanResult = null
        // NavigationInputResolver only permits HTTP(S); other schemes become a search.
        navigate(value)
    }

    fun dismissQrScanResult() {
        qrScanResult = null
    }

    fun cancelDownload(download: BrowserDownload) {
        if (download.status != "Downloading") return
        download.cancelRequested = true
        download.status = "Cancelling"
        runCatching { download.inputStream?.close() }
        notice = "Cancelling transfer…"
    }

    fun removeDownload(download: BrowserDownload) {
        if (download.status == "Downloading" || download.status == "Cancelling") {
            notice = "Cancel the active transfer before removing it."
            return
        }
        val file = download.file
        if (file != null && file.exists()) {
            val downloadsRoot = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: File(appContext.filesDir, "downloads")
            val allowedRoots = listOf(
                downloadsRoot,
                File(appContext.filesDir, "downloads"),
                File(downloadsRoot, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY)
            ).mapNotNull { runCatching { it.canonicalFile }.getOrNull() }
            val canonical = runCatching { file.canonicalFile }.getOrNull()
            if (canonical == null || allowedRoots.none { canonical.parentFile == it }) {
                notice = "This file is outside Rivo's managed Downloads folder."
                return
            }
            if (!canonical.delete()) {
                notice = "The downloaded file could not be deleted."
                return
            }
        }
        downloads.removeAll { it.id == download.id }
        persistDownloads()
    }

    fun goBack() { activeTab?.engine?.goBack() }
    fun goForward() { activeTab?.engine?.goForward() }
    fun reloadOrStop() {
        val tab = activeTab ?: return
        if (tab.loading) tab.engine.stop() else tab.engine.reload()
    }

    fun addCurrentBookmark() {
        val tab = activeTab ?: return
        if (tab.privateMode || !tab.url.startsWith("http")) {
            notice = "Private tabs and non-web pages are not saved as bookmarks."
            return
        }
        val page = SavedPage(tab.url, tab.title, System.currentTimeMillis())
        scope.launch {
            runCatching { storage.addBookmark(page) }
                .onSuccess {
                    bookmarks.removeAll { it.url == page.url }
                    bookmarks.add(0, page)
                    notice = "Bookmark saved"
                }
                .onFailure { notice = it.message ?: "Bookmark could not be saved." }
        }
    }

    fun removeBookmark(url: String) {
        scope.launch {
            runCatching { storage.removeBookmark(url) }
                .onSuccess { bookmarks.removeAll { it.url == url } }
                .onFailure { notice = it.message ?: "Bookmark could not be removed." }
        }
    }

    fun clearBrowsingData() {
        if (clearingBrowsingData) return
        clearingBrowsingData = true
        val savedTabs = tabs.filterNot { it.privateMode }.map { tab ->
            SavedTab(tab.url, tab.title, tab.id == activeTabId)
        }.take(MAX_TABS)

        cancelPendingDownload()
        downloads.filter { it.status == "Downloading" || it.status == "Cancelling" }.forEach(::cancelDownload)
        permissionPrompt?.respond?.invoke(false)
        permissionPrompt = null
        pendingAndroidPermissionResponse?.invoke(false)
        pendingAndroidPermissionResponse = null
        pendingAndroidPermissionNames = null
        awaitingAndroidPermissionResult = false
        awaitingNotificationPermissionResult = false
        downloads.filter { it.privateMode }.forEach { download ->
            download.file?.let(::deletePrivateDownloadFile)
        }
        tabs.toList().forEach { it.engine.dispose() }
        tabs.clear()
        activeTabId = 0L
        sitePermissions.clear()
        sitePermissionsLoading = false
        sitePermissionsLoadError = false

        fun restoreNormalTabs() {
            if (savedTabs.isEmpty()) {
                createTab()
                return
            }
            savedTabs.forEach { saved -> createTab(url = saved.url, restoredTitle = saved.title) }
            val selectedIndex = savedTabs.indexOfFirst { it.selected }.takeIf { it >= 0 } ?: savedTabs.lastIndex
            tabs.getOrNull(selectedIndex)?.let { selectTab(it.id) }
        }

        runtime.storageController.clearData(
            StorageController.ClearFlags.SITE_DATA or StorageController.ClearFlags.SITE_SETTINGS
        ).accept({
            scope.launch {
                val localClear = runCatching {
                    storage.clearHistory()
                    storage.saveDownloads(emptyList())
                    storage.saveOpenTabs(emptyList())
                }
                history.clear()
                downloads.clear()
                restoreNormalTabs()
                refreshSitePermissions()
                clearingBrowsingData = false
                notice = if (localClear.isSuccess) {
                    "Browsing history, cookies, cache, site permissions and download history cleared. Bookmarks and normal downloads were kept; private downloads were removed."
                } else {
                    "Website data was cleared, but local history or download metadata could not be cleared: " +
                        (localClear.exceptionOrNull()?.message ?: "storage error")
                }
            }
        }, { error ->
            scope.launch {
                restoreNormalTabs()
                refreshSitePermissions()
                clearingBrowsingData = false
                notice = "Website data could not be cleared: " + (error?.message ?: "GeckoView storage error")
            }
        })
    }

    fun clearHistory() {
        scope.launch {
            runCatching { storage.clearHistory() }
                .onSuccess {
                    history.clear()
                    notice = "Browsing history cleared"
                }
                .onFailure { notice = it.message ?: "History could not be cleared." }
        }
    }


    fun openDownload(download: BrowserDownload) {
        val file = download.file ?: return
        runCatching {
            val uri = FileProvider.getUriForFile(
                appContext,
                appContext.packageName + ".fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, download.mimeType ?: "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        }.onFailure {
            notice = "No compatible app could open this download."
        }
    }

    fun shareDownload(download: BrowserDownload) {
        val file = download.file ?: return
        runCatching {
            val uri = FileProvider.getUriForFile(
                appContext,
                appContext.packageName + ".fileprovider",
                file
            )
            val send = Intent(Intent.ACTION_SEND).apply {
                type = download.mimeType ?: "*/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            appContext.startActivity(
                Intent.createChooser(send, "Share download").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure {
            notice = "Unable to share this download."
        }
    }

    fun refreshSitePermissions() {
        val sourceTab = activeTab ?: tabs.firstOrNull() ?: return
        sitePermissionsLoading = true
        sitePermissionsLoadError = false
        sourceTab.engine.loadStoredSitePermissions { permissions ->
            sitePermissions.clear()
            if (permissions == null) sitePermissionsLoadError = true else sitePermissions.addAll(permissions)
            sitePermissionsLoading = false
        }
    }
    fun revokeSitePermission(permission: GeckoStoredSitePermission) {
        val sourceTab = activeTab ?: tabs.firstOrNull() ?: return
        if (!permission.canRevoke) { notice = "This permission cannot be revoked in GeckoView."; return }
        sourceTab.engine.revokeStoredSitePermission(permission) { ok ->
            if (ok) { notice = "Site permission reset."; refreshSitePermissions() }
            else notice = "Permission reset failed."
        }
    }
    fun beginAndroidPermissionRequest() {
        if (pendingAndroidPermissionResponse != null) awaitingAndroidPermissionResult = true
    }

    fun completeAndroidPermissionRequest(results: Map<String, Boolean>) {
        val requested = pendingAndroidPermissionNames ?: return
        val respond = pendingAndroidPermissionResponse ?: return
        pendingAndroidPermissionNames = null
        pendingAndroidPermissionResponse = null
        awaitingAndroidPermissionResult = false
        val locationRequested = requested.any {
            it == android.Manifest.permission.ACCESS_COARSE_LOCATION ||
                it == android.Manifest.permission.ACCESS_FINE_LOCATION
        }
        val locationGranted = !locationRequested ||
            appContext.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            appContext.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val otherPermissionsGranted = requested.filterNot {
            it == android.Manifest.permission.ACCESS_COARSE_LOCATION ||
                it == android.Manifest.permission.ACCESS_FINE_LOCATION
        }.all { permission ->
            results[permission] == true ||
                appContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        val granted = locationGranted && otherPermissionsGranted
        if (granted) {
            deniedAndroidPermissions.removeAll(requested.toSet())
        } else {
            requested.filterNot(::hasAndroidPermission).forEach(deniedAndroidPermissions::add)
        }
        respond(granted)
        persistDeniedAndroidPermissions()
        if (!granted) notice = "Android device permission was denied for this app; future site requests will be blocked until the permission is enabled in Android Settings."
    }

    private fun hasAndroidPermission(permission: String): Boolean =
        if (permission == android.Manifest.permission.ACCESS_COARSE_LOCATION ||
            permission == android.Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            appContext.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                appContext.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            appContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

    private fun persistDeniedAndroidPermissions() {
        val snapshot = deniedAndroidPermissions.toSet()
        scope.launch {
            runCatching { storage.saveDeniedAndroidPermissions(snapshot) }
                .onFailure { notice = "Android permission denial could not be saved securely." }
        }
    }

    fun beginNotificationPermissionRequest() {
        if (permissionPrompt?.permission == "Notifications") awaitingNotificationPermissionResult = true
    }
    fun completeNotificationPermissionRequest(granted: Boolean) {
        awaitingNotificationPermissionResult = false
        respondToSitePermission(granted)
    }

    fun updateAppearance(value: BrowserAppearance) {
        appearance = value
        scope.launch { storage.saveTheme(value.name.lowercase()) }
    }

    fun openPanel(value: BrowserPanel?) { panel = value }
    fun showNotice(value: String) { notice = value }
    fun dismissNotice() { notice = null }

    fun respondToSitePermission(granted: Boolean) {
        val pending = permissionPrompt ?: run { awaitingNotificationPermissionResult = false; return }
        awaitingNotificationPermissionResult = false
        permissionPrompt = null
        pending.respond(granted)
    }


    private fun requestDownloadConfirmation(
        tab: BrowserTabState,
        url: String,
        mimeType: String?,
        disposition: String?,
        contentLength: Long?,
        body: InputStream?
    ) {
        if (body == null || !DownloadSecurityPolicy.isAllowedUrl(url)) {
            runCatching { body?.close() }
            notice = "This download could not be safely handled."
            return
        }
        if (pendingDownload != null) {
            runCatching { body.close() }
            notice = "Finish or cancel the current download confirmation first."
            return
        }
        pendingDownload = PendingDownload(tab.id, tab.privateMode, url, mimeType, disposition, contentLength?.takeIf { it > 0 }, body)
    }

    fun confirmPendingDownload() {
        val pending = pendingDownload ?: return
        pendingDownload = null
        val tab = tabs.firstOrNull { it.id == pending.tabId }
        if (tab == null) {
            runCatching { pending.body.close() }
            return
        }
        saveDownload(tab, pending.url, pending.mimeType, pending.disposition, pending.contentLength, pending.body)
    }

    fun cancelPendingDownload() {
        val pending = pendingDownload ?: return
        pendingDownload = null
        runCatching { pending.body.close() }
        notice = "Download cancelled"
    }

    private fun saveDownload(
        tab: BrowserTabState,
        url: String,
        mimeType: String?,
        disposition: String?,
        contentLength: Long?,
        body: InputStream?
    ) {
        val uri = Uri.parse(url)
        if (!DownloadSecurityPolicy.isAllowedUrl(url) || body == null) {
            notice = "This download could not be safely handled."
            runCatching { body?.close() }
            return
        }

        val safeName = DownloadFileWriter.sanitizeFilename(
            disposition,
            uri.lastPathSegment,
            System.currentTimeMillis()
        )
        val safeMimeType = DownloadSecurityPolicy.normalizeMimeType(mimeType) ?: "application/octet-stream"
        val item = BrowserDownload(
            nextId++,
            safeName,
            safeMimeType,
            tab.privateMode,
            if (tab.privateMode) tab.id else null
        ).apply {
            inputStream = body
            totalBytes = contentLength
        }
        downloads.add(0, item)
        scope.launch(Dispatchers.IO) {
            val result = runCatching {
                val downloadsRoot = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: File(appContext.filesDir, "downloads")
                val directory = if (tab.privateMode) {
                    File(downloadsRoot, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY)
                } else {
                    downloadsRoot
                }
                DownloadFileWriter.save(body, directory, safeName, MAX_DOWNLOAD_BYTES) { bytes ->
                    mainHandler.post {
                        if (!item.cancelRequested && item.status == "Downloading") {
                            item.bytesReceived = bytes
                        }
                    }
                }
            }
            withContext(Dispatchers.Main.immediate) {
                item.inputStream = null
                result.onSuccess { file ->
                    when {
                        item.cancelRequested -> {
                            if (tab.privateMode) deletePrivateDownloadFile(file) else file.delete()
                            item.status = "Cancelled"
                            notice = "Transfer cancelled"
                        }
                        tab.privateMode && tabs.none { it.id == tab.id } -> {
                            deletePrivateDownloadFile(file)
                            downloads.removeAll { it.id == item.id }
                        }
                        else -> {
                            item.file = file
                            item.filename = file.name
                            item.bytesReceived = file.length()
                            item.status = if (tab.privateMode) {
                                "Private download (deleted when tab closes)"
                            } else {
                                "Saved in app Downloads"
                            }
                            persistDownloads()
                            notice = if (tab.privateMode) {
                                "Private download saved. It will be deleted when this tab closes."
                            } else {
                                "Download saved: " + file.name
                            }
                        }
                    }
                }.onFailure {
                    item.status = if (item.cancelRequested) "Cancelled" else "Failed"
                    notice = if (item.cancelRequested) {
                        "Transfer cancelled"
                    } else {
                        "Download failed: " + (it.message ?: "unknown error")
                    }
                }
            }
        }
    }

    private fun deletePrivateDownloadFile(file: File) {
        runCatching {
            val root = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: File(appContext.filesDir, "downloads")
            val privateRoot = File(root, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY).canonicalFile
            val canonical = file.canonicalFile
            if (privateRoot.parentFile == root.canonicalFile &&
                canonical.parentFile == privateRoot && canonical.isFile
            ) {
                canonical.delete()
            }
        }
    }

    private suspend fun cleanupInterruptedDownloads() = withContext(Dispatchers.IO) {
        val directories = listOfNotNull(
            appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            File(appContext.filesDir, "downloads")
        )
        DownloadTempFileCleanup.removeInterruptedFiles(directories)
        DownloadTempFileCleanup.removePrivateDownloads(directories)
    }

    private suspend fun restoreDownloads() {
        val externalRoot = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val internalRoot = File(appContext.filesDir, "downloads")
        val allowedRoots = listOfNotNull(externalRoot, internalRoot).mapNotNull {
            runCatching { it.canonicalFile }.getOrNull()
        }
        val restored = withContext(Dispatchers.IO) {
            storage.loadDownloads().mapNotNull { saved ->
                val file = File(saved.absolutePath)
                if (DownloadSecurityPolicy.isAllowedRestoredFile(file, saved.filename, allowedRoots, MAX_DOWNLOAD_BYTES))
                    saved to file.canonicalFile else null
            }
        }
        downloads.addAll(restored.map { (saved, file) ->
            BrowserDownload(nextId++, saved.filename, DownloadSecurityPolicy.normalizeMimeType(saved.mimeType) ?: "application/octet-stream").apply {
                this.file = file
                status = "Saved in app Downloads"
            }
        })
    }

    private fun persistDownloads() {
        val saved = downloads.filter { DownloadSecurityPolicy.shouldPersistMetadata(it.privateMode) }
            .mapNotNull { item ->
                val file = item.file ?: return@mapNotNull null
                SavedDownload(item.filename, item.mimeType, file.absolutePath)
            }
        scope.launch {
            runCatching { storage.saveDownloads(saved) }
                .onFailure { notice = it.message ?: "Download metadata could not be saved." }
        }
    }

    private fun persistOpenTabs() {
        val saved = RestorableTabPolicy.snapshot(
            tabs.map { tab ->
                RestorableTabCandidate(tab.id, tab.privateMode, tab.url, tab.title)
            },
            activeTabId
        )
        scope.launch {
            runCatching { storage.saveOpenTabs(saved) }
                .onFailure { notice = it.message ?: "Tab restoration data could not be saved." }
        }
    }

    private fun saveHistory(tab: BrowserTabState) {
        val page = SavedPage(tab.url, tab.title, System.currentTimeMillis())
        scope.launch {
            runCatching { storage.addHistory(page) }
                .onSuccess {
                    val updated = HistoryVisitPolicy.recordVisit(history.toList(), page)
                    history.clear()
                    history.addAll(updated)
                }
                .onFailure { notice = it.message ?: "History could not be saved." }
        }
    }

    private fun resolveInput(input: String): String = NavigationInputResolver.resolve(input)

    fun dispose() {
        cancelPendingDownload()
        closeReaderMode()
        dismissQrScanResult()
        permissionPrompt?.respond?.invoke(false)
        permissionPrompt = null
        downloads.filter { it.status == "Downloading" }.forEach(::cancelDownload)
        pendingAndroidPermissionResponse?.invoke(false)
        pendingAndroidPermissionResponse = null
        pendingAndroidPermissionNames = null
        awaitingAndroidPermissionResult = false
        downloads.filter { it.privateMode }.forEach { download ->
            download.file?.let(::deletePrivateDownloadFile)
        }
        tabs.toList().forEach { it.engine.dispose() }
        tabs.clear()
        scope.cancel()
    }

    companion object {
        const val HOME_URL = "https://duckduckgo.com/"
        const val MAX_TABS = 8
        const val MAX_DOWNLOAD_BYTES = 250L * 1024L * 1024L
    }
}

data class PendingDownload(
    val tabId: Long,
    val privateMode: Boolean,
    val url: String,
    val mimeType: String?,
    val disposition: String?,
    val contentLength: Long?,
    val body: InputStream
)

data class ReaderArticle(
    val tabId: Long,
    val title: String,
    val url: String,
    val content: String,
    val language: String,
    val wordCount: Int,
    val wasTruncated: Boolean
)

data class SitePermissionPrompt(
    val tabId: Long,
    val origin: String,
    val permission: String,
    val privateMode: Boolean,
    val onShown: () -> Unit,
    val respond: (Boolean) -> Unit
)

class BrowserDownload(val id: Long, filename: String, val mimeType: String?, val privateMode: Boolean = false, val privateTabId: Long? = null) {
    var filename by mutableStateOf(filename)
    var status by mutableStateOf("Downloading")
    var bytesReceived by mutableStateOf(0L)
    var totalBytes by mutableStateOf<Long?>(null)
    var cancelRequested by mutableStateOf(false)
    var inputStream: InputStream? = null
    var file: File? = null
}

enum class BrowserPanel { TABS, BOOKMARKS, HISTORY, DOWNLOADS, APPEARANCE, DEVELOPER, SITE_PERMISSIONS, MENU }


/** Only the selected tab on a started host may activate/focus Gecko. */
internal object TabSessionActivationPolicy {
    fun shouldActivate(tabId: Long, selectedTabId: Long, hostStarted: Boolean): Boolean =
        hostStarted && tabId == selectedTabId
}

/**
 * Writes downloads through an app-private staging file. A createNewFile reservation
 * gives concurrent downloads distinct names before the staging file is atomically
 * renamed, preventing an existing completed file from being overwritten.
 */
internal object DownloadFileWriter {
    private val extendedFilenamePattern =
        Regex("""filename\*\s*=\s*[^']*'[^']*'([^;]+)""", RegexOption.IGNORE_CASE)
    private val quotedFilenamePattern =
        Regex("filename\\s*=\\s*\\\"([^\\\"]*)\\\"", RegexOption.IGNORE_CASE)
    private val plainFilenamePattern =
        Regex("""filename\s*=\s*([^;]+)""", RegexOption.IGNORE_CASE)

    fun sanitizeFilename(contentDisposition: String?, fallbackName: String?, nowMillis: Long): String {
        val header = contentDisposition.orEmpty()
        val encoded = extendedFilenamePattern.find(header)?.groupValues?.getOrNull(1)?.trim()
        val decoded = encoded?.let {
            runCatching {
                URLDecoder.decode(it.replace("+", "%2B"), StandardCharsets.UTF_8.name())
            }.getOrNull()
        }
        val quoted = quotedFilenamePattern.find(header)?.groupValues?.getOrNull(1)
        val plain = plainFilenamePattern.find(header)?.groupValues?.getOrNull(1)?.trim()?.trim('"')
        val rawName = decoded ?: quoted ?: plain ?: fallbackName.orEmpty()
        val name = rawName.trim()
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .take(120)
        val normalized = if (name.isBlank() || name == "." || name == "..") {
            "download-$nowMillis"
        } else {
            name
        }
        // Startup cleanup owns .rivo-*.part; a final user file must not match it.
        return if (normalized.startsWith(".rivo-") && normalized.endsWith(".part")) {
            "download-$normalized"
        } else normalized
    }

    /** Always closes input and removes staging/reservation files on every failure path. */
    fun save(input: InputStream, directory: File, requestedFilename: String, maxBytes: Long, onProgress: (Long) -> Unit = {}): File {
        require(maxBytes > 0) { "Download size limit must be positive" }
        val safeFilename = sanitizeFilename(null, requestedFilename, System.currentTimeMillis())
        var stagingFile: File? = null
        var reservedTarget: File? = null
        var finalizedTarget: File? = null
        try {
            val saved = input.use { source ->
                if (!directory.isDirectory && !directory.mkdirs()) {
                    throw IOException("Unable to create downloads directory")
                }
                val staging = File.createTempFile(".rivo-", ".part", directory)
                stagingFile = staging
                FileOutputStream(staging).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var totalBytes = 0L
                    while (true) {
                        val count = source.read(buffer)
                        if (count < 0) break
                        if (count == 0) continue
                        if (count.toLong() > maxBytes - totalBytes) {
                            throw IOException("Download exceeds the configured size limit")
                        }
                        totalBytes += count
                        output.write(buffer, 0, count)
                        if (totalBytes % (256L * 1024L) < count || totalBytes - count == 0L) onProgress(totalBytes)
                    }
                    output.fd.sync()
                    onProgress(totalBytes)
                }
                val target = reserveUniqueTarget(directory, safeFilename)
                reservedTarget = target
                if (!staging.renameTo(target)) throw IOException("Unable to finalize downloaded file")
                stagingFile = null
                reservedTarget = null
                finalizedTarget = target
                target
            }
            finalizedTarget = null
            return saved
        } finally {
            stagingFile?.delete()
            reservedTarget?.delete()
            finalizedTarget?.delete()
        }
    }

    private fun reserveUniqueTarget(directory: File, filename: String): File {
        for (suffix in 0..9999) {
            val candidateName = if (suffix == 0) filename else withSuffix(filename, suffix)
            val candidate = File(directory, candidateName)
            if (candidate.createNewFile()) return candidate
        }
        throw IOException("Unable to allocate a unique download filename")
    }

    private fun withSuffix(filename: String, suffix: Int): String {
        val dot = filename.lastIndexOf('.').takeIf { it > 0 } ?: filename.length
        return filename.substring(0, dot) + " ($suffix)" + filename.substring(dot)
    }
}
