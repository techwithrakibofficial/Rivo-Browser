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

/** Small local-only storage. History is deliberately bounded to 300 entries. */
class BrowserStorage(private val context: Context) {
    private val themeKey = stringPreferencesKey("appearance")
    private val bookmarksKey = stringPreferencesKey("bookmarks_json")
    private val historyKey = stringPreferencesKey("history_json")

    suspend fun loadTheme(): String =
        context.rivoDataStore.data.first()[themeKey] ?: "dark"

    suspend fun saveTheme(theme: String) {
        context.rivoDataStore.edit { it[themeKey] = theme }
    }

    suspend fun loadBookmarks(): List<SavedPage> =
        decode(context.rivoDataStore.data.first()[bookmarksKey].orEmpty())

    suspend fun loadHistory(): List<SavedPage> =
        decode(context.rivoDataStore.data.first()[historyKey].orEmpty())

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
