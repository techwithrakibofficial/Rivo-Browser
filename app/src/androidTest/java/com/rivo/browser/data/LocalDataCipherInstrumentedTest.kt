package com.rivo.browser.data

import android.security.keystore.KeyInfo
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import java.security.KeyStore
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataCipherInstrumentedTest {
    private lateinit var keyAlias: String
    private lateinit var cipher: LocalDataCipher

    @Before
    fun setUp() {
        keyAlias = "rivo.test.${UUID.randomUUID()}"
        cipher = LocalDataCipher(keyAlias)
    }

    @After
    fun tearDown() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(keyAlias)) keyStore.deleteEntry(keyAlias)
    }

    @Test
    fun encryptsAndDecryptsWithAnAes256KeystoreKey() {
        val plainText = "private browsing metadata"
        val encrypted = cipher.encrypt(plainText)

        assertTrue(encrypted.startsWith("enc:v1:"))
        assertEquals(plainText, cipher.decryptOrLegacy(encrypted))

        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val key = keyStore.getKey(keyAlias, null) as SecretKey
        val keyInfo = SecretKeyFactory.getInstance("AES", "AndroidKeyStore")
            .getKeySpec(key, KeyInfo::class.java) as KeyInfo
        assertEquals(256, keyInfo.getKeySize())
    }

    @Test
    fun tamperedCiphertextFailsAuthentication() {
        val encrypted = cipher.encrypt("sensitive metadata")
        val payload = Base64.decode(encrypted.removePrefix("enc:v1:"), Base64.NO_WRAP)
        payload[payload.lastIndex] = (payload.last().toInt() xor 1).toByte()
        val tampered = "enc:v1:" + Base64.encodeToString(payload, Base64.NO_WRAP)

        assertThrows(EncryptedDataException::class.java) {
            cipher.decryptOrLegacy(tampered)
        }
    }

    @Test
    fun missingKeystoreKeyFailsClosedWithoutGeneratingReplacement() {
        val encrypted = cipher.encrypt("saved browser state")
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertTrue(keyStore.containsAlias(keyAlias))
        keyStore.deleteEntry(keyAlias)

        assertThrows(EncryptedDataException::class.java) {
            cipher.decryptOrLegacy(encrypted)
        }
        val afterDecrypt = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertFalse(afterDecrypt.containsAlias(keyAlias))
    }
}
