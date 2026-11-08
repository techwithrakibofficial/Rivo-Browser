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
import java.io.InputStream
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
                    val host = Uri.parse(origin).host ?: origin
                    permissionPrompt = SitePermissionPrompt(tab.id, host, permission, privateMode, onShown, respond)
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
        tabs.firstOrNull { it.id == id }?.engine?.setActive(hostStarted)
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
        tabs.forEach { tab -> tab.engine.setActive(tab.id == activeTabId) }
    }

    /** Release Gecko's foreground/focus state while the Activity is backgrounded. */
    fun onHostPaused() {
        hostStarted = false
        if (permissionPrompt != null && !awaitingNotificationPermissionResult) respondToSitePermission(false)
        if (pendingDownload != null) cancelPendingDownload()
        tabs.forEach { it.engine.setActive(false) }
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
            if (permissions == null) {
                sitePermissionsLoadError = true
            } else {
                sitePermissions.addAll(permissions)
            }
            sitePermissionsLoading = false
        }
    }

    fun revokeSitePermission(permission: GeckoStoredSitePermission) {
        val sourceTab = activeTab ?: tabs.firstOrNull() ?: return
        if (!permission.canRevoke) {
            notice = "GeckoView does not support revoking this permission after it has been granted."
            return
        }
        sourceTab.engine.revokeStoredSitePermission(permission) { revoked ->
            if (revoked) {
                notice = "Site permission reset. The website will need to ask again."
                refreshSitePermissions()
            } else {
                notice = "This site permission could not be reset."
            }
        }
    }

    fun updateAppearance(value: BrowserAppearance) {
        appearance = value
        scope.launch { storage.saveTheme(value.name.lowercase()) }
    }

    fun openPanel(value: BrowserPanel?) { panel = value }
    fun showNotice(value: String) { notice = value }
    fun dismissNotice() { notice = null }

    fun beginNotificationPermissionRequest() {
        if (permissionPrompt?.permission == "Notifications") {
            awaitingNotificationPermissionResult = true
        }
    }

    fun completeNotificationPermissionRequest(granted: Boolean) {
        awaitingNotificationPermissionResult = false
        respondToSitePermission(granted)
    }

    fun respondToSitePermission(granted: Boolean) {
        val pending = permissionPrompt ?: run {
            awaitingNotificationPermissionResult = false
            return
        }
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

        val dispositionName = Regex("filename\\s*=\\s*\\\"?([^\\\";]+)", RegexOption.IGNORE_CASE)
            .find(disposition.orEmpty())?.groupValues?.getOrNull(1)
        val rawName = dispositionName ?: uri.lastPathSegment ?: "download-" + System.currentTimeMillis()
        val safeName = DownloadSecurityPolicy.sanitizeFilename(
            rawName,
            "download-" + System.currentTimeMillis()
        )

        val item = BrowserDownload(nextId++, safeName, mimeType, tab.privateMode, if (tab.privateMode) tab.id else null)
        downloads.add(0, item)
        scope.launch(Dispatchers.IO) {
            val result = runCatching {
                val directory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: File(appContext.filesDir, "downloads")
                if (!directory.exists() && !directory.mkdirs()) error("Unable to create downloads directory")
                val temporary = File.createTempFile(".rivo-", ".part", directory)
                try {
                    body.use { input ->
                        FileOutputStream(temporary).use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var totalBytes = 0L
                            while (true) {
                                val count = input.read(buffer)
                                if (count < 0) break
                                totalBytes += count
                                if (totalBytes > MAX_DOWNLOAD_BYTES) {
                                    error("Download exceeds the 250 MB MVP safety limit")
                                }
                                output.write(buffer, 0, count)
                            }
                        }
                    }
                    var target = File(directory, safeName)
                    var suffix = 1
                    while (target.exists()) {
                        val base = safeName.substringBeforeLast('.', safeName)
                        val extension = safeName.substringAfterLast('.', "").let { if (it.isBlank()) "" else ".$it" }
                        target = File(directory, "$base ($suffix)$extension")
                        suffix++
                    }
                    if (!temporary.renameTo(target)) error("Unable to finalize downloaded file")
                    target
                } catch (error: Throwable) {
                    temporary.delete()
                    throw error
                }
            }
            withContext(Dispatchers.Main.immediate) {
                result.onSuccess { file ->
                    item.file = file
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
        directories.forEach { directory ->
            directory.listFiles { file ->
                file.isFile && file.name.startsWith(".rivo-") && file.name.endsWith(".part")
            }?.forEach { it.delete() }
        }
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
                if (DownloadSecurityPolicy.isAllowedRestoredFile(
                        file,
                        saved.filename,
                        allowedRoots,
                        MAX_DOWNLOAD_BYTES
                    )
                ) saved to file.canonicalFile else null
            }
        }
        downloads.addAll(restored.map { (saved, file) ->
            BrowserDownload(nextId++, saved.filename, saved.mimeType).apply {
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
        val saved = tabs.filterNot { it.privateMode }.map { tab ->
            SavedTab(
                url = tab.url,
                title = tab.title,
                selected = tab.id == activeTabId
            )
        }
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
                    history.removeAll { it.url == page.url }
                    history.add(0, page)
                    while (history.size > 300) history.removeAt(history.lastIndex)
                }
                .onFailure { notice = it.message ?: "History could not be saved." }
        }
    }

    private fun resolveInput(input: String): String = NavigationInputResolver.resolve(input)

    fun dispose() {
        cancelPendingDownload()
        awaitingNotificationPermissionResult = false
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

class BrowserDownload(
    val id: Long,
    val filename: String,
    val mimeType: String?,
    val privateMode: Boolean = false,
    val privateTabId: Long? = null
) {
    var status by mutableStateOf("Downloading")
    var file: File? = null
}

enum class BrowserPanel { TABS, BOOKMARKS, HISTORY, DOWNLOADS, APPEARANCE, SITE_PERMISSIONS, MENU }
