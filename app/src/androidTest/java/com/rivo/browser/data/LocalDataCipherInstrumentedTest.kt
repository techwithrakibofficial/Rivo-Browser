package com.rivo.browser.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.security.keystore.KeyInfo
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rivo.browser.DownloadTempFileCleanup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.security.KeyStore
import java.util.UUID
import javax.crypto.SecretKeyFactory

@RunWith(AndroidJUnit4::class)
class LocalDataCipherInstrumentedTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var keyAlias: String
    private val fixtures = mutableListOf<PreferencesFixture>()
    private val temporaryFiles = mutableListOf<File>()

    @Before
    fun setUp() {
        keyAlias = "rivo.instrumented.${UUID.randomUUID()}"
    }

    @After
    fun tearDown() {
        fixtures.forEach { it.stop() }
        temporaryFiles.forEach { it.deleteRecursively() }
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(keyAlias)) keyStore.deleteEntry(keyAlias)
    }

    @Test
    fun aes256GcmRoundTripUsesFreshCiphertextForEachEncryption() {
        val cipher = LocalDataCipher(keyAlias)
        val plainText = """[{"url":"https://example.com","title":"private title"}]"""

        val first = cipher.encrypt(plainText)
        val second = cipher.encrypt(plainText)

        assertTrue(first.startsWith("enc:v1:"))
        val key = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            .getKey(keyAlias, null) as java.security.Key
        val secretKey = key as javax.crypto.SecretKey
        val keyInfo = SecretKeyFactory.getInstance("AES", "AndroidKeyStore")
            .getKeySpec(secretKey, KeyInfo::class.java) as KeyInfo
        assertEquals("Keystore must generate AES-256", 256, keyInfo.getKeySize())
        assertNotEquals("AES-GCM encryptions should use fresh randomized IVs", first, second)
        assertEquals(plainText, cipher.decryptOrLegacy(first))
        assertEquals(plainText, cipher.decryptOrLegacy(second))
    }

    @Test
    fun modifiedCiphertextFailsAuthentication() {
        val cipher = LocalDataCipher(keyAlias)
        val encrypted = cipher.encrypt("sensitive browser metadata")
        val payload = Base64.decode(encrypted.removePrefix("enc:v1:"), Base64.NO_WRAP)
        payload[payload.lastIndex] = (payload.last().toInt() xor 0x01).toByte()
        val tampered = "enc:v1:" + Base64.encodeToString(payload, Base64.NO_WRAP)

        assertThrows(EncryptedDataException::class.java) {
            cipher.decryptOrLegacy(tampered)
        }
    }

    @Test
    fun truncatedCiphertextFailsClosed() {
        val cipher = LocalDataCipher(keyAlias)
        val tooShortPayload = Base64.encodeToString(ByteArray(27), Base64.NO_WRAP)

        assertThrows(EncryptedDataException::class.java) {
            cipher.decryptOrLegacy("enc:v1:$tooShortPayload")
        }
    }

    @Test
    fun missingKeystoreAliasDoesNotCreateReplacementDuringDecryption() {
        val cipher = LocalDataCipher(keyAlias)
        val encrypted = cipher.encrypt("saved bookmarks")
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertTrue(keyStore.containsAlias(keyAlias))

        keyStore.deleteEntry(keyAlias)

        assertThrows(EncryptedDataException::class.java) {
            cipher.decryptOrLegacy(encrypted)
        }
        val afterDecrypt = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertFalse("Decrypt must never regenerate a missing key alias", afterDecrypt.containsAlias(keyAlias))
    }

    @Test
    fun malformedEncryptedBookmarkMetadataBlocksWritesAndPreservesOriginalCiphertext() = runBlocking {
        val fixture = newFixture()
        val cipher = LocalDataCipher(keyAlias)
        val original = cipher.encrypt("not valid JSON")
        val key = stringPreferencesKey("bookmarks_json")
        fixture.dataStore.edit { it[key] = original }
        val storage = BrowserStorage(fixture.dataStore, cipher, SensitiveWriteGuard())

        assertThrows(Exception::class.java) {
            runBlocking { storage.loadBookmarks() }
        }
        assertEquals(original, fixture.dataStore.data.first()[key])

        assertThrows(IllegalStateException::class.java) {
            runBlocking { storage.addBookmark(SavedPage("https://new.example", "new", 1L)) }
        }
        assertEquals(original, fixture.dataStore.data.first()[key])
    }

    @Test
    fun missingKeystoreKeyAfterStorageReopenCannotOverwriteSavedTabs() = runBlocking {
        val file = newPreferencesFile()
        val firstFixture = newFixture(file)
        val firstCipher = LocalDataCipher(keyAlias)
        val original = firstCipher.encrypt(
            """[{"url":"https://existing.example","title":"existing","selected":true}]"""
        )
        val key = stringPreferencesKey("open_tabs_json")
        firstFixture.dataStore.edit { it[key] = original }
        firstFixture.stop()

        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(keyAlias) }

        // Reopen the same persisted preferences file with a fresh guard, as after process restart.
        val relaunchedFixture = newFixture(file)
        val storage = BrowserStorage(
            relaunchedFixture.dataStore,
            LocalDataCipher(keyAlias),
            SensitiveWriteGuard()
        )
        assertThrows(EncryptedDataException::class.java) {
            runBlocking {
                storage.saveOpenTabs(
                    listOf(SavedTab("https://replacement.example", "replacement", true))
                )
            }
        }
        assertEquals(original, relaunchedFixture.dataStore.data.first()[key])
        assertThrows(IllegalStateException::class.java) {
            runBlocking { storage.saveOpenTabs(emptyList()) }
        }
        assertFalse(
            "Decrypt must not create a replacement alias",
            KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.containsAlias(keyAlias)
        )
    }

    @Test
    fun deniedAndroidPermissionsAreStoredEncryptedAndCanBeCleared() = runBlocking {
        val fixture = newFixture()
        val storage = BrowserStorage(fixture.dataStore, LocalDataCipher(keyAlias), SensitiveWriteGuard())
        val denied = setOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )

        storage.saveDeniedAndroidPermissions(denied)

        assertEquals(denied, storage.loadDeniedAndroidPermissions())
        val key = stringPreferencesKey("denied_android_permissions_json")
        assertTrue("Denied Android permissions must be stored encrypted", fixture.dataStore.data.first()[key].orEmpty().startsWith("enc:v1:"))

        storage.saveDeniedAndroidPermissions(emptySet())
        assertTrue(storage.loadDeniedAndroidPermissions().isEmpty())
        assertTrue(fixture.dataStore.data.first()[key].orEmpty().startsWith("enc:v1:"))
    }

    @Test
    fun legacyPlaintextMigratesToEncryptedStorageAndSurvivesStoreReopen() = runBlocking {
        val file = newPreferencesFile()
        val legacy = """[{"url":"https://legacy.example","title":"legacy page","visitedAt":42}]"""
        val key = stringPreferencesKey("bookmarks_json")
        val firstFixture = newFixture(file)
        firstFixture.dataStore.edit { it[key] = legacy }

        val firstStorage = BrowserStorage(firstFixture.dataStore, LocalDataCipher(keyAlias), SensitiveWriteGuard())
        val expected = listOf(SavedPage("https://legacy.example", "legacy page", 42L))
        assertEquals(expected, firstStorage.loadBookmarks())
        assertTrue(firstFixture.dataStore.data.first()[key].orEmpty().startsWith("enc:v1:"))
        firstFixture.stop()

        val relaunchedFixture = newFixture(file)
        val relaunchedStorage = BrowserStorage(
            relaunchedFixture.dataStore,
            LocalDataCipher(keyAlias),
            SensitiveWriteGuard()
        )
        assertEquals(expected, relaunchedStorage.loadBookmarks())
        assertTrue(relaunchedFixture.dataStore.data.first()[key].orEmpty().startsWith("enc:v1:"))
    }

    @Test
    fun datastoreCorruptionIsNotResetAcrossStoreReopen() = runBlocking {
        val file = newPreferencesFile()
        val corruptedBytes = byteArrayOf(0x0A, 0x7F, 0xFF.toByte(), 0x01)
        file.writeBytes(corruptedBytes)

        val firstFixture = newFixture(file)
        assertThrows(Exception::class.java) {
            runBlocking { firstFixture.dataStore.data.first() }
        }
        firstFixture.stop()
        assertArrayEquals(corruptedBytes, file.readBytes())

        val relaunchedFixture = newFixture(file)
        assertThrows(Exception::class.java) {
            runBlocking { relaunchedFixture.dataStore.data.first() }
        }
        assertArrayEquals(corruptedBytes, file.readBytes())
    }

    @Test
    fun bookmarkHistoryTabAndDownloadMetadataRoundTripThroughEncryptedStore() = runBlocking {
        val fixture = newFixture()
        val storage = BrowserStorage(fixture.dataStore, LocalDataCipher(keyAlias), SensitiveWriteGuard())
        val bookmark = SavedPage("https://bookmark.example", "bookmark", 1L)
        val history = SavedPage("https://history.example", "history", 2L)
        val tab = SavedTab("https://tab.example", "tab", true)
        val download = SavedDownload("report.pdf", "application/pdf", "/app/downloads/report.pdf")

        storage.addBookmark(bookmark)
        storage.addHistory(history)
        storage.saveOpenTabs(listOf(tab))
        storage.saveDownloads(listOf(download))

        assertEquals(listOf(bookmark), storage.loadBookmarks())
        assertEquals(listOf(history), storage.loadHistory())
        assertEquals(listOf(tab), storage.loadOpenTabs())
        assertEquals(listOf(download), storage.loadDownloads())
        val keys = setOf("bookmarks_json", "history_json", "open_tabs_json", "downloads_json")
        val savedValues = fixture.dataStore.data.first().asMap().filterKeys { it.name in keys }
        assertEquals(4, savedValues.size)
        assertTrue(savedValues.values.all { (it as? String).orEmpty().startsWith("enc:v1:") })
    }

    @Test
    fun ambiguousLegacyDownloadIndexIsClearedButFilesRemainAndNewWritesSurvive() = runBlocking {
        val fixture = newFixture()
        val cipher = LocalDataCipher(keyAlias)
        val key = stringPreferencesKey("downloads_json")
        val savedFile = File(context.cacheDir, "legacy-download-${UUID.randomUUID()}.bin")
            .apply { writeBytes(byteArrayOf(1, 2, 3, 4)) }
        temporaryFiles.add(savedFile)
        val legacy = cipher.encrypt(
            """[{"filename":"${savedFile.name}","mimeType":"application/octet-stream","path":"${savedFile.absolutePath}"}]"""
        )
        fixture.dataStore.edit { it[key] = legacy }
        val storage = BrowserStorage(fixture.dataStore, cipher, SensitiveWriteGuard())

        assertTrue(storage.loadDownloads().isEmpty())
        assertTrue("Migration must not delete user file bytes", savedFile.exists())
        val migratedPrefs = fixture.dataStore.data.first()
        assertNull("Ambiguous legacy download index must be removed", migratedPrefs[key])
        assertTrue(migratedPrefs[booleanPreferencesKey("private_download_metadata_v2")] == true)

        val newRecord = SavedDownload("new.bin", "application/octet-stream", "/app/downloads/new.bin")
        storage.saveDownloads(listOf(newRecord))
        assertEquals(listOf(newRecord), storage.loadDownloads())
        assertTrue(savedFile.exists())
    }

    @Test
    fun unsafeDownloadMetadataIsRejectedWithoutReplacingItsRecord() = runBlocking {
        val fixture = newFixture()
        val cipher = LocalDataCipher(keyAlias)
        val key = stringPreferencesKey("downloads_json")
        val original = cipher.encrypt(
            """[{"filename":"../outside.txt","mimeType":"text/plain","path":"/outside/outside.txt"}]"""
        )
        fixture.dataStore.edit { it[key] = original }
        val storage = BrowserStorage(fixture.dataStore, cipher, SensitiveWriteGuard())

        assertThrows(Exception::class.java) {
            runBlocking { storage.loadDownloads() }
        }
        assertEquals(original, fixture.dataStore.data.first()[key])
        assertThrows(IllegalStateException::class.java) {
            runBlocking { storage.saveDownloads(emptyList()) }
        }
        assertEquals(original, fixture.dataStore.data.first()[key])
    }

    @Test
    fun fileProviderIsNonExportedAndRejectsFilesOutsideConfiguredDownloadRoots() {
        val provider = context.packageManager.getProviderInfo(
            ComponentName(context.packageName, "androidx.core.content.FileProvider"),
            PackageManager.GET_META_DATA
        )
        assertFalse(provider.exported)
        assertTrue(provider.grantUriPermissions)

        val downloadRoot = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(context.filesDir, "downloads")
        assertTrue(downloadRoot.mkdirs() || downloadRoot.isDirectory)
        val allowedFile = File(downloadRoot, "rivo-security-test-${UUID.randomUUID()}.txt")
        allowedFile.writeText("shareable test data")
        val authority = context.packageName + ".fileprovider"
        try {
            val uri = FileProvider.getUriForFile(context, authority, allowedFile)
            assertEquals("content", uri.scheme)

            val outsideFile = File(context.cacheDir, "rivo-outside-${UUID.randomUUID()}.txt")
            outsideFile.writeText("must not be exposed")
            temporaryFiles.add(outsideFile)
            assertThrows(IllegalArgumentException::class.java) {
                FileProvider.getUriForFile(context, authority, outsideFile)
            }
        } finally {
            allowedFile.delete()
        }
    }

    private fun newPreferencesFile(): File {
        val directory = File(context.cacheDir, "rivo-security-data-store").apply { mkdirs() }
        return File(directory, "${UUID.randomUUID()}.preferences_pb").also {
            temporaryFiles.add(it)
        }
    }

    private fun newFixture(file: File = newPreferencesFile()): PreferencesFixture {
        val fixture = PreferencesFixture(file)
        fixtures.add(fixture)
        return fixture
    }

    private class PreferencesFixture(file: File) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { file }
        )
        fun stop() = scope.cancel()
    }
}
