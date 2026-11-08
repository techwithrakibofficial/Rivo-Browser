package com.rivo.browser.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.rivoDataStore by preferencesDataStore(name = "rivo_browser")

data class SavedPage(
    val url: String,
    val title: String,
    val visitedAt: Long
)

data class SavedTab(
    val url: String,
    val title: String,
    val selected: Boolean
)

data class SavedDownload(
    val filename: String,
    val mimeType: String?,
    val absolutePath: String
)

/** Small local-only storage. History is deliberately bounded to 300 entries. */
class BrowserStorage(private val context: Context) {
    private val themeKey = stringPreferencesKey("appearance")
    private val bookmarksKey = stringPreferencesKey("bookmarks_json")
    private val historyKey = stringPreferencesKey("history_json")
    private val openTabsKey = stringPreferencesKey("open_tabs_json")
    private val downloadsKey = stringPreferencesKey("downloads_json")

    suspend fun loadTheme(): String =
        context.rivoDataStore.data.first()[themeKey] ?: "dark"

    suspend fun saveTheme(theme: String) {
        context.rivoDataStore.edit { it[themeKey] = theme }
    }

    suspend fun loadBookmarks(): List<SavedPage> =
        decode(context.rivoDataStore.data.first()[bookmarksKey].orEmpty())

    suspend fun loadHistory(): List<SavedPage> =
        decode(context.rivoDataStore.data.first()[historyKey].orEmpty())

    /** Restore only regular HTTP(S) tabs; private tabs are never serialized. */
    suspend fun loadOpenTabs(): List<SavedTab> = runCatching {
        val array = JSONArray(context.rivoDataStore.data.first()[openTabsKey].orEmpty())
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val url = item.optString("url")
            if (!RestorableTabPolicy.isSafeUrl(url)) null
            else SavedTab(url, item.optString("title", url), item.optBoolean("selected"))
        }.take(8)
    }.getOrDefault(emptyList())

    suspend fun loadDownloads(): List<SavedDownload> = runCatching {
        val array = JSONArray(context.rivoDataStore.data.first()[downloadsKey].orEmpty())
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val filename = item.optString("filename")
            val path = item.optString("path")
            if (filename.isBlank() || filename.contains('/') || filename.contains('\\\\') || path.isBlank()) null
            else SavedDownload(filename, item.optString("mimeType").takeIf { it.isNotBlank() }, path)
        }.take(100)
    }.getOrDefault(emptyList())

    suspend fun saveDownloads(downloads: List<SavedDownload>) {
        context.rivoDataStore.edit { prefs ->
            prefs[downloadsKey] = JSONArray().apply {
                downloads.take(100).forEach { item ->
                    put(JSONObject().apply {
                        put("filename", item.filename)
                        put("mimeType", item.mimeType ?: "")
                        put("path", item.absolutePath)
                    })
                }
            }.toString()
        }
    }

    suspend fun saveOpenTabs(tabs: List<SavedTab>) {
        val safeTabs = tabs.filter { RestorableTabPolicy.isSafeUrl(it.url) }.take(8)
        context.rivoDataStore.edit { prefs ->
            prefs[openTabsKey] = JSONArray().apply {
                safeTabs.forEach { tab ->
                    put(JSONObject().apply {
                        put("url", tab.url)
                        put("title", tab.title)
                        put("selected", tab.selected)
                    })
                }
            }.toString()
        }
    }

    suspend fun addBookmark(page: SavedPage) {
        context.rivoDataStore.edit { prefs ->
            val current = decode(prefs[bookmarksKey].orEmpty()).toMutableList()
            current.removeAll { it.url == page.url }
            current.add(0, page)
            prefs[bookmarksKey] = encode(current.take(500))
        }
    }

    suspend fun removeBookmark(url: String) {
        context.rivoDataStore.edit { prefs ->
            prefs[bookmarksKey] = encode(decode(prefs[bookmarksKey].orEmpty()).filterNot { it.url == url })
        }
    }

    suspend fun addHistory(page: SavedPage) {
        context.rivoDataStore.edit { prefs ->
            val current = decode(prefs[historyKey].orEmpty()).toMutableList()
            if (current.firstOrNull()?.url != page.url) current.add(0, page)
            prefs[historyKey] = encode(current.take(300))
        }
    }

    suspend fun clearHistory() {
        context.rivoDataStore.edit { it[historyKey] = "[]" }
    }

    private fun encode(items: List<SavedPage>): String = JSONArray().apply {
        items.forEach { page ->
            put(JSONObject().apply {
                put("url", page.url)
                put("title", page.title)
                put("visitedAt", page.visitedAt)
            })
        }
    }.toString()

    private fun decode(raw: String): List<SavedPage> = runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val url = item.optString("url")
            if (url.isBlank()) null else SavedPage(
                url = url,
                title = item.optString("title", url),
                visitedAt = item.optLong("visitedAt")
            )
        }
    }.getOrDefault(emptyList())
}
