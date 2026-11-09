package com.rivo.browser.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

/**
 * Local-only storage. Encrypted-data and malformed-record failures are propagated so
 * callers cannot mistake unreadable saved data for an empty collection and overwrite it.
 */
class BrowserStorage internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: LocalDataCipher = LocalDataCipher(),
    private val sensitiveWriteGuard: SensitiveWriteGuard = SensitiveWriteGuard.process
) {
    constructor(context: Context) : this(context.applicationContext.rivoDataStore)

    private fun ensureSensitiveWritesAllowed() = sensitiveWriteGuard.ensureAllowed()

    private fun integrityFailure(error: Exception): Nothing {
        sensitiveWriteGuard.block()
        throw error
    }
    private val themeKey = stringPreferencesKey("appearance")
    private val bookmarksKey = stringPreferencesKey("bookmarks_json")
    private val historyKey = stringPreferencesKey("history_json")
    private val openTabsKey = stringPreferencesKey("open_tabs_json")
    private val downloadsKey = stringPreferencesKey("downloads_json")
    private val deniedAndroidPermissionsKey = stringPreferencesKey("denied_android_permissions_json")
    private val privateDownloadMetadataV2Key = booleanPreferencesKey("private_download_metadata_v2")

    private companion object {
        val SUPPORTED_ANDROID_PERMISSION_DENIALS = setOf(
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"
        )
    }

    suspend fun loadTheme(): String =
        dataStore.data.first()[themeKey] ?: "dark"

    suspend fun saveTheme(theme: String) {
        dataStore.edit { it[themeKey] = theme }
    }

    suspend fun loadBookmarks(): List<SavedPage> = decodeProtected(readProtected(bookmarksKey))

    suspend fun loadHistory(): List<SavedPage> = decodeProtected(readProtected(historyKey))

    /** Restore only regular HTTP(S) tabs; private tabs are never serialized. */
    suspend fun loadOpenTabs(): List<SavedTab> =
        decodeOpenTabs(readProtected(openTabsKey))

    suspend fun loadDownloads(): List<SavedDownload> {
        // Old releases did not mark whether a download originated in a private tab.
        // Clear the ambiguous browser-history index once; never delete user-saved files.
        dataStore.edit { prefs ->
            if (prefs[privateDownloadMetadataV2Key] != true) {
                sensitiveWriteGuard.withAllowed {
                    sensitiveWriteGuard.validateExisting(
                        prefs[downloadsKey].orEmpty(),
                        cipher::decryptOrLegacy,
                        ::parseDownloads
                    )
                    prefs.remove(downloadsKey)
                    prefs[privateDownloadMetadataV2Key] = true
                }
            }
        }
        return decodeDownloads(readProtected(downloadsKey))
    }

    suspend fun saveDownloads(downloads: List<SavedDownload>) {
        ensureSensitiveWritesAllowed()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                sensitiveWriteGuard.validateExisting(
                    prefs[downloadsKey].orEmpty(),
                    cipher::decryptOrLegacy,
                    ::parseDownloads
                )
                prefs[downloadsKey] = cipher.encrypt(JSONArray().apply {
                    downloads.take(100).forEach { item ->
                        put(JSONObject().apply {
                            put("filename", item.filename)
                            put("mimeType", item.mimeType ?: "")
                            put("path", item.absolutePath)
                        })
                    }
                }.toString())
                // This transaction writes only the new format, so do not let the
                // first subsequent read mistake it for ambiguous legacy metadata.
                prefs[privateDownloadMetadataV2Key] = true
            }
        }
    }

    suspend fun loadDeniedAndroidPermissions(): Set<String> {
        val raw = readProtected(deniedAndroidPermissionsKey)
        return try {
            parseDeniedAndroidPermissions(raw)
        } catch (error: Exception) {
            integrityFailure(error)
        }
    }

    suspend fun saveDeniedAndroidPermissions(permissions: Set<String>) {
        ensureSensitiveWritesAllowed()
        val safePermissions = permissions.filter { it in SUPPORTED_ANDROID_PERMISSION_DENIALS }.toSortedSet()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                sensitiveWriteGuard.validateExisting(
                    prefs[deniedAndroidPermissionsKey].orEmpty(),
                    cipher::decryptOrLegacy,
                    ::parseDeniedAndroidPermissions
                )
                prefs[deniedAndroidPermissionsKey] = cipher.encrypt(JSONArray().apply {
                    safePermissions.forEach { put(it) }
                }.toString())
            }
        }
    }

    suspend fun saveOpenTabs(tabs: List<SavedTab>) {
        ensureSensitiveWritesAllowed()
        val safeTabs = tabs.filter { RestorableTabPolicy.isSafeUrl(it.url) }.take(8)
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                sensitiveWriteGuard.validateExisting(
                    prefs[openTabsKey].orEmpty(),
                    cipher::decryptOrLegacy,
                    ::parseOpenTabs
                )
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
    }

    suspend fun addBookmark(page: SavedPage) {
        ensureSensitiveWritesAllowed()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                val current = decodeProtected(decryptProtected(prefs[bookmarksKey].orEmpty())).toMutableList()
                current.removeAll { it.url == page.url }
                current.add(0, page)
                prefs[bookmarksKey] = cipher.encrypt(encode(current.take(500)))
            }
        }
    }

    suspend fun removeBookmark(url: String) {
        ensureSensitiveWritesAllowed()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                val current = decodeProtected(decryptProtected(prefs[bookmarksKey].orEmpty()))
                prefs[bookmarksKey] = cipher.encrypt(encode(current.filterNot { it.url == url }))
            }
        }
    }

    suspend fun addHistory(page: SavedPage) {
        ensureSensitiveWritesAllowed()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                val current = decodeProtected(decryptProtected(prefs[historyKey].orEmpty()))
                prefs[historyKey] = cipher.encrypt(encode(HistoryVisitPolicy.recordVisit(current, page)))
            }
        }
    }

    suspend fun clearHistory() {
        ensureSensitiveWritesAllowed()
        dataStore.edit { prefs ->
            sensitiveWriteGuard.withAllowed {
                sensitiveWriteGuard.validateExisting(
                    prefs[historyKey].orEmpty(),
                    cipher::decryptOrLegacy,
                    ::decode
                )
                prefs[historyKey] = cipher.encrypt("[]")
            }
        }
    }

    private suspend fun readProtected(key: Preferences.Key<String>): String {
        val raw = dataStore.data.first()[key].orEmpty()
        if (raw.isBlank()) return ""
        val plainText = decryptProtected(raw)
        if (!raw.startsWith("enc:v1:")) {
            // Migrate legacy plaintext only after successful read; encrypted write failure
            // leaves the original DataStore value intact because edit is transactional.
            dataStore.edit { prefs ->
                sensitiveWriteGuard.withAllowed {
                    if (prefs[key] == raw) prefs[key] = cipher.encrypt(plainText)
                }
            }
        }
        return plainText
    }

    private fun decodeOpenTabs(raw: String): List<SavedTab> = try {
        parseOpenTabs(raw)
    } catch (error: Exception) {
        integrityFailure(error)
    }

    private fun parseOpenTabs(raw: String): List<SavedTab> {
        if (raw.isBlank()) return emptyList()
        val array = JSONArray(raw)
        return (0 until array.length()).mapNotNull { index ->
            val item = requireNotNull(array.optJSONObject(index)) {
                "Saved tab entry is not an object"
            }
            val url = item.optString("url")
            require(url.isNotBlank()) { "Saved tab URL is missing" }
            if (!RestorableTabPolicy.isSafeUrl(url)) null
            else SavedTab(url, item.optString("title", url), item.optBoolean("selected"))
        }.take(8)
    }

    private fun decodeDownloads(raw: String): List<SavedDownload> = try {
        parseDownloads(raw)
    } catch (error: Exception) {
        integrityFailure(error)
    }

    private fun parseDownloads(raw: String): List<SavedDownload> {
        if (raw.isBlank()) return emptyList()
        val array = JSONArray(raw)
        return (0 until array.length()).map { index ->
            val item = requireNotNull(array.optJSONObject(index)) {
                "Saved download entry is not an object"
            }
            val filename = item.optString("filename")
            val path = item.optString("path")
            require(filename.isNotBlank() && !filename.contains('/') && !filename.contains(92.toChar())) {
                "Saved download filename is invalid"
            }
            require(path.isNotBlank()) { "Saved download path is missing" }
            SavedDownload(filename, item.optString("mimeType").takeIf { it.isNotBlank() }, path)
        }.take(100)
    }

    private fun parseDeniedAndroidPermissions(raw: String): Set<String> {
        if (raw.isBlank()) return emptySet()
        val array = JSONArray(raw)
        return (0 until array.length()).map { index ->
            val permission = array.getString(index)
            require(permission in SUPPORTED_ANDROID_PERMISSION_DENIALS) {
                "Saved Android permission denial is unsupported"
            }
            permission
        }.toSet()
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

    private fun decryptProtected(raw: String): String = try {
        cipher.decryptOrLegacy(raw)
    } catch (error: Exception) {
        integrityFailure(error)
    }

    private fun decodeProtected(raw: String): List<SavedPage> = try {
        decode(raw)
    } catch (error: Exception) {
        integrityFailure(error)
    }

    private fun decode(raw: String): List<SavedPage> {
        if (raw.isBlank()) return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val item = requireNotNull(array.optJSONObject(index)) {
                    "Saved page entry is not an object"
                }
                val url = item.optString("url")
                require(url.isNotBlank()) { "Saved page URL is missing" }
                SavedPage(
                    url = url,
                    title = item.optString("title", url),
                    visitedAt = item.optLong("visitedAt")
                )
            }
        } catch (error: Exception) {
            integrityFailure(error)
        }
    }
}

/** Thread-safe process-wide fail-closed gate for sensitive metadata mutations. */
internal class SensitiveWriteGuard {
    companion object {
        val process = SensitiveWriteGuard()
    }

    @Volatile private var blocked = false

    @Synchronized
    fun block() {
        blocked = true
    }

    fun ensureAllowed() {
        check(!blocked) {
            "Saved browser data is unreadable; sensitive writes are disabled to preserve existing records."
        }
    }

    /** Serialize each sensitive mutation with integrity-failure publication. */
    fun <T> withAllowed(action: () -> T): T = synchronized(this) {
        ensureAllowed()
        action()
    }

    /**
     * Authenticate and validate the current value before a direct replacement.
     * This protects records after process restart, when the in-memory block flag is reset.
     */
    fun validateExisting(
        raw: String,
        decrypt: (String) -> String,
        validate: (String) -> Any?
    ) {
        ensureAllowed()
        if (raw.isBlank()) return
        try {
            validate(decrypt(raw))
        } catch (error: Exception) {
            block()
            throw error
        }
    }
}

/**
 * Shared policy for persisted and in-memory history. Gecko can emit both location
 * and title callbacks for one visit; coalesce only consecutive callbacks for the
 * same URL, retaining a later revisit after a different URL.
 */
internal object HistoryVisitPolicy {
    fun recordVisit(
        existing: List<SavedPage>,
        page: SavedPage,
        maxEntries: Int = 300
    ): List<SavedPage> {
        require(maxEntries >= 0) { "History limit cannot be negative" }
        val bounded = existing.take(maxEntries)
        if (bounded.firstOrNull()?.url == page.url) return bounded
        return (listOf(page) + bounded).take(maxEntries)
    }
}
