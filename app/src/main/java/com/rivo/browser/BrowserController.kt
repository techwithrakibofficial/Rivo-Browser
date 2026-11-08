package com.rivo.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.rivo.browser.data.BrowserStorage
import com.rivo.browser.data.SavedPage
import com.rivo.browser.engine.gecko.GeckoBrowserRuntime
import com.rivo.browser.engine.gecko.GeckoBrowserTab
import com.rivo.browser.engine.gecko.GeckoTabListener
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

    val tabs = mutableStateListOf<BrowserTabState>()
    val bookmarks = mutableStateListOf<SavedPage>()
    val history = mutableStateListOf<SavedPage>()
    val downloads = mutableStateListOf<BrowserDownload>()

    var activeTabId by mutableStateOf(0L)
        private set
    var appearance by mutableStateOf(BrowserAppearance.DARK)
        private set
    var panel by mutableStateOf<BrowserPanel?>(null)
        private set
    var notice by mutableStateOf<String?>(null)
        private set

    val activeTab: BrowserTabState?
        get() = tabs.firstOrNull { it.id == activeTabId }

    init {
        scope.launch {
            appearance = runCatching {
                BrowserAppearance.valueOf(storage.loadTheme().uppercase())
            }.getOrDefault(BrowserAppearance.DARK)
            bookmarks.addAll(storage.loadBookmarks())
            history.addAll(storage.loadHistory())
            if (tabs.isEmpty()) createTab()
        }
    }

    fun createTab(privateMode: Boolean = false, url: String = HOME_URL) {
        if (tabs.size >= MAX_TABS) {
            notice = "For memory stability, Rivo currently supports up to $MAX_TABS open tabs."
            return
        }
        val tab = BrowserTabState(nextId++, privateMode, url)
        tab.engine = GeckoBrowserTab(
            appContext,
            runtime,
            privateMode,
            object : GeckoTabListener {
                override fun onLocationChanged(url: String) {
                    tab.url = url
                    if (!tab.privateMode && url.startsWith("http")) {
                        saveHistory(tab)
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
                    saveDownload(tab, url, mimeType, disposition, body)
                }
            }
        )
        tabs.add(tab)
        selectTab(tab.id)
        tab.engine.load(url)
    }

    fun selectTab(id: Long) {
        tabs.firstOrNull { it.id == activeTabId }?.engine?.setActive(false)
        activeTabId = id
        tabs.firstOrNull { it.id == id }?.engine?.setActive(true)
    }

    fun closeTab(id: Long) {
        val index = tabs.indexOfFirst { it.id == id }
        if (index < 0) return
        val closing = tabs[index]
        val wasActive = closing.id == activeTabId
        closing.engine.dispose()
        tabs.removeAt(index)
        if (tabs.isEmpty()) {
            createTab()
        } else if (wasActive) {
            selectTab(tabs.getOrNull(index.coerceAtMost(tabs.lastIndex))?.id ?: tabs.last().id)
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
            storage.addBookmark(page)
            bookmarks.removeAll { it.url == page.url }
            bookmarks.add(0, page)
            notice = "Bookmark saved"
        }
    }

    fun removeBookmark(url: String) {
        scope.launch {
            storage.removeBookmark(url)
            bookmarks.removeAll { it.url == url }
        }
    }

    fun clearHistory() {
        scope.launch {
            storage.clearHistory()
            history.clear()
            notice = "Browsing history cleared"
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

    fun setAppearance(value: BrowserAppearance) {
        appearance = value
        scope.launch { storage.saveTheme(value.name.lowercase()) }
    }

    fun openPanel(value: BrowserPanel?) { panel = value }
    fun showNotice(value: String) { notice = value }
    fun dismissNotice() { notice = null }


    private fun saveDownload(
        tab: BrowserTabState,
        url: String,
        mimeType: String?,
        disposition: String?,
        body: InputStream?
    ) {
        val uri = Uri.parse(url)
        if (uri.scheme !in listOf("http", "https") || body == null) {
            notice = "This download could not be safely handled."
            runCatching { body?.close() }
            return
        }

        val dispositionName = Regex("filename\\s*=\\s*\\\"?([^\\\";]+)", RegexOption.IGNORE_CASE)
            .find(disposition.orEmpty())?.groupValues?.getOrNull(1)
        val rawName = dispositionName ?: uri.lastPathSegment ?: "download-" + System.currentTimeMillis()
        val safeName = rawName.substringAfterLast('/')
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .take(120)
            .ifBlank { "download-" + System.currentTimeMillis() }
            .let { if (it == "." || it == "..") "download-" + System.currentTimeMillis() else it }

        val item = BrowserDownload(nextId++, safeName, mimeType)
        downloads.add(0, item)
        scope.launch(Dispatchers.IO) {
            val result = runCatching {
                val directory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: File(appContext.filesDir, "downloads")
                if (!directory.exists() && !directory.mkdirs()) error("Unable to create downloads directory")
                var target = File(directory, safeName)
                var suffix = 1
                while (target.exists()) {
                    val base = safeName.substringBeforeLast('.', safeName)
                    val extension = safeName.substringAfterLast('.', "").let { if (it.isBlank()) "" else ".$it" }
                    target = File(directory, "$base ($suffix)$extension")
                    suffix++
                }
                body.use { input -> FileOutputStream(target).use { output -> input.copyTo(output) } }
                target
            }
            withContext(Dispatchers.Main.immediate) {
                result.onSuccess { file ->
                    item.file = file
                    item.status = "Saved in app Downloads"
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

    private fun saveHistory(tab: BrowserTabState) {
        val page = SavedPage(tab.url, tab.title, System.currentTimeMillis())
        scope.launch {
            storage.addHistory(page)
            history.removeAll { it.url == page.url }
            history.add(0, page)
            while (history.size > 300) history.removeAt(history.lastIndex)
        }
    }

    private fun resolveInput(input: String): String = NavigationInputResolver.resolve(input)

    fun dispose() {
        tabs.toList().forEach { it.engine.dispose() }
        tabs.clear()
        scope.cancel()
    }

    companion object {
        const val HOME_URL = "https://duckduckgo.com/"
        const val MAX_TABS = 8
    }
}

class BrowserDownload(val id: Long, val filename: String, val mimeType: String?) {
    var status by mutableStateOf("Downloading")
    var file: File? = null
}

enum class BrowserPanel { TABS, BOOKMARKS, HISTORY, DOWNLOADS, APPEARANCE, MENU }
