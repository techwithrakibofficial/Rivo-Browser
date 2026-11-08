package com.rivo.browser.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypts local browsing metadata with an AES-256-GCM key held by Android Keystore.
 * Legacy plaintext values are readable for one-way migration and encrypted on next write.
 *
 * Encrypted payload failures are deliberately propagated. Returning an empty string here
 * would make callers treat unreadable saved data as an empty collection and overwrite it.
 */
internal class LocalDataCipher {
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        val payload = ByteBuffer.allocate(cipher.iv.size + encrypted.size)
            .put(cipher.iv)
            .put(encrypted)
            .array()
        return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    fun decryptOrLegacy(raw: String): String {
        if (raw.isBlank()) return ""
        if (!raw.startsWith(PREFIX)) return raw

        try {
            val payload = Base64.decode(raw.removePrefix(PREFIX), Base64.NO_WRAP)
            require(payload.size >= GCM_IV_BYTES + GCM_TAG_BYTES) {
                "Encrypted browser data is truncated"
            }
            val buffer = ByteBuffer.wrap(payload)
            val iv = ByteArray(GCM_IV_BYTES).also(buffer::get)
            val encrypted = ByteArray(buffer.remaining()).also(buffer::get)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            return String(cipher.doFinal(encrypted), StandardCharsets.UTF_8)
        } catch (failure: Exception) {
            throw EncryptedDataException(
                "Saved browser data could not be decrypted; the original record was preserved.",
                failure
            )
        }
    }

    @Synchronized
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val KEY_ALIAS = "rivo.local-data.v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val PREFIX = "enc:v1:"
        const val GCM_IV_BYTES = 12
        const val GCM_TAG_BITS = 128
        const val GCM_TAG_BYTES = GCM_TAG_BITS / 8
    }
}

internal class EncryptedDataException(message: String, cause: Throwable) :
    IllegalStateException(message, cause)
