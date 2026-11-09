package com.rivo.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
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
    private val runtime = GeckoBrowserRuntime.get(appContext)
    private var nextId = 1L
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

    val activeTab: BrowserTabState?
        get() = tabs.firstOrNull { it.id == activeTabId }

    init {
        scope.launch {
            appearance = runCatching {
                BrowserAppearance.valueOf(storage.loadTheme().uppercase())
            }.getOrDefault(BrowserAppearance.DARK)
            try {
                bookmarks.addAll(storage.loadBookmarks())
                history.addAll(storage.loadHistory())
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
                    body: InputStream?
                ) {
                    requestDownloadConfirmation(tab, url, mimeType, disposition, body)
                }

                override fun onSitePermissionRequest(
                    origin: String,
                    permission: String,
                    privateMode: Boolean,
                    onShown: () -> Unit,
                    respond: (Boolean) -> Unit
                ) {
                    if (permissionPrompt != null || tab.id !in tabs.map { it.id }) {
                        respond(false)
                        return
                    }
                    // Preserve scheme and non-default port so users can distinguish full origins.
                    permissionPrompt = SitePermissionPrompt(tab.id, origin, permission, privateMode, onShown, respond)
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
        if (permissionPrompt?.tabId == id) respondToSitePermission(false)
        if (pendingDownload?.tabId == id) cancelPendingDownload()
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
        if (permissionPrompt != null && !awaitingNotificationPermissionResult) respondToSitePermission(false)
        if (pendingDownload != null) cancelPendingDownload()
        tabs.forEach { tab ->
            tab.engine.setActive(
                TabSessionActivationPolicy.shouldActivate(tab.id, activeTabId, hostStarted)
            )
        }
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
        permissionPrompt?.respond?.invoke(false)
        permissionPrompt = null
        awaitingNotificationPermissionResult = false
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
                clearingBrowsingData = false
                notice = if (localClear.isSuccess) {
                    "Browsing history, cookies, cache, site permissions and download history cleared. Bookmarks and downloaded files were kept."
                } else {
                    "Website data was cleared, but local history or download metadata could not be cleared: " +
                        (localClear.exceptionOrNull()?.message ?: "storage error")
                }
            }
        }, { error ->
            scope.launch {
                restoreNormalTabs()
                clearingBrowsingData = false
                notice = "Website data could not be cleared: " + (error.message ?: "GeckoView storage error")
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
        pendingDownload = PendingDownload(tab.id, tab.privateMode, url, mimeType, disposition, body)
    }

    fun confirmPendingDownload() {
        val pending = pendingDownload ?: return
        pendingDownload = null
        val tab = tabs.firstOrNull { it.id == pending.tabId }
        if (tab == null) {
            runCatching { pending.body.close() }
            return
        }
        saveDownload(tab, pending.url, pending.mimeType, pending.disposition, pending.body)
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
        val item = BrowserDownload(nextId++, safeName, safeMimeType, tab.privateMode, if (tab.privateMode) tab.id else null)
        downloads.add(0, item)
        scope.launch(Dispatchers.IO) {
            val result = runCatching {
                val directory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: File(appContext.filesDir, "downloads")
                DownloadFileWriter.save(body, directory, safeName, MAX_DOWNLOAD_BYTES)
            }
            withContext(Dispatchers.Main.immediate) {
                result.onSuccess { file ->
                    // Persist the collision-resolved name, not the requested name.
                    item.file = file
                    item.filename = file.name
                    item.status = "Saved in app Downloads"
                    persistDownloads()
                    notice = if (tab.privateMode) {
                        "Download saved. Private browsing does not automatically erase downloaded files."
                    } else {
                        "Download saved: " + file.name
                    }
                }.onFailure {
                    item.status = "Failed"
                    notice = "Download failed: " + (it.message ?: "unknown error")
                }
            }
        }
    }

    private suspend fun cleanupInterruptedDownloads() = withContext(Dispatchers.IO) {
        val directories = listOfNotNull(
            appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            File(appContext.filesDir, "downloads")
        )
        DownloadTempFileCleanup.removeInterruptedFiles(directories)
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
        permissionPrompt?.respond?.invoke(false)
        permissionPrompt = null
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
    val body: InputStream
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
    var file: File? = null
}

enum class BrowserPanel { TABS, BOOKMARKS, HISTORY, DOWNLOADS, APPEARANCE, SITE_PERMISSIONS, MENU }


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
    fun save(input: InputStream, directory: File, requestedFilename: String, maxBytes: Long): File {
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
                    }
                    output.fd.sync()
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
