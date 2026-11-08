package com.rivo.browser.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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
    private val cipher = LocalDataCipher()
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

    suspend fun loadBookmarks(): List<SavedPage> = decode(readProtected(bookmarksKey))

    suspend fun loadHistory(): List<SavedPage> = decode(readProtected(historyKey))

    /** Restore only regular HTTP(S) tabs; private tabs are never serialized. */
    suspend fun loadOpenTabs(): List<SavedTab> = runCatching {
        val array = JSONArray(readProtected(openTabsKey))
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val url = item.optString("url")
            if (!RestorableTabPolicy.isSafeUrl(url)) null
            else SavedTab(url, item.optString("title", url), item.optBoolean("selected"))
        }.take(8)
    }.getOrDefault(emptyList())

    suspend fun loadDownloads(): List<SavedDownload> = runCatching {
        val array = JSONArray(readProtected(downloadsKey))
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val filename = item.optString("filename")
            val path = item.optString("path")
            if (filename.isBlank() || filename.contains('/') || filename.contains('\\') || path.isBlank()) null
            else SavedDownload(filename, item.optString("mimeType").takeIf { it.isNotBlank() }, path)
        }.take(100)
    }.getOrDefault(emptyList())

    suspend fun saveDownloads(downloads: List<SavedDownload>) {
        context.rivoDataStore.edit { prefs ->
            prefs[downloadsKey] = cipher.encrypt(JSONArray().apply {
                downloads.take(100).forEach { item ->
                    put(JSONObject().apply {
                        put("filename", item.filename)
                        put("mimeType", item.mimeType ?: "")
                        put("path", item.absolutePath)
                    })
                }
            }.toString())
        }
    }

    suspend fun saveOpenTabs(tabs: List<SavedTab>) {
        val safeTabs = tabs.filter { RestorableTabPolicy.isSafeUrl(it.url) }.take(8)
        context.rivoDataStore.edit { prefs ->
            prefs[openTabsKey] = cipher.encrypt(JSONArray().apply {
                safeTabs.forEach { tab ->
                    put(JSONObject().apply {
                        put("url", tab.url)
                        put("title", tab.title)
                        put("selected", tab.selected)
                    })
                }
            }.toString())
        }
    }

    suspend fun addBookmark(page: SavedPage) {
        context.rivoDataStore.edit { prefs ->
            val current = decode(cipher.decryptOrLegacy(prefs[bookmarksKey].orEmpty())).toMutableList()
            current.removeAll { it.url == page.url }
            current.add(0, page)
            prefs[bookmarksKey] = cipher.encrypt(encode(current.take(500)))
        }
    }

    suspend fun removeBookmark(url: String) {
        context.rivoDataStore.edit { prefs ->
            prefs[bookmarksKey] = cipher.encrypt(encode(decode(cipher.decryptOrLegacy(prefs[bookmarksKey].orEmpty())).filterNot { it.url == url }))
        }
    }

    suspend fun addHistory(page: SavedPage) {
        context.rivoDataStore.edit { prefs ->
            val current = decode(cipher.decryptOrLegacy(prefs[historyKey].orEmpty())).toMutableList()
            if (current.firstOrNull()?.url != page.url) current.add(0, page)
            prefs[historyKey] = cipher.encrypt(encode(current.take(300)))
        }
    }

    suspend fun clearHistory() {
        context.rivoDataStore.edit { it[historyKey] = cipher.encrypt("[]") }
    }

    private suspend fun readProtected(key: Preferences.Key<String>): String {
        val raw = context.rivoDataStore.data.first()[key].orEmpty()
        if (raw.isBlank()) return ""
        val plainText = cipher.decryptOrLegacy(raw)
        if (!raw.startsWith("enc:v1:")) {
            context.rivoDataStore.edit { prefs -> prefs[key] = cipher.encrypt(plainText) }
        }
        return plainText
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
