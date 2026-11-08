package com.rivo.browser.data

import org.junit.Assert.assertThrows
import org.junit.Test

class SensitiveWriteGuardTest {
    @Test
    fun allowsWritesUntilIntegrityFailure() {
        val guard = SensitiveWriteGuard()

        guard.ensureAllowed()
        guard.block()

        assertThrows(IllegalStateException::class.java) {
            guard.ensureAllowed()
        }
    }

    @Test
    fun remainsBlockedAfterRepeatedIntegrityFailures() {
        val guard = SensitiveWriteGuard()

        guard.block()
        guard.block()

        assertThrows(IllegalStateException::class.java) {
            guard.ensureAllowed()
        }
    }

    @Test
    fun rejectsReplacementWhenExistingCiphertextCannotBeAuthenticated() {
        val guard = SensitiveWriteGuard()
        var replacementWritten = false

        assertThrows(IllegalStateException::class.java) {
            guard.validateExisting(
                raw = "enc:v1:unreadable",
                decrypt = { throw IllegalStateException("authentication failed") },
                validate = {}
            )
            replacementWritten = true
        }

        assertThrows(IllegalStateException::class.java) {
            guard.ensureAllowed()
        }
        org.junit.Assert.assertFalse(replacementWritten)
    }

    @Test
    fun rejectsReplacementWhenExistingMetadataIsMalformed() {
        val guard = SensitiveWriteGuard()
        var replacementWritten = false

        assertThrows(IllegalArgumentException::class.java) {
            guard.validateExisting(
                raw = "legacy-json",
                decrypt = { it },
                validate = { throw IllegalArgumentException("malformed metadata") }
            )
            replacementWritten = true
        }

        assertThrows(IllegalStateException::class.java) {
            guard.ensureAllowed()
        }
        org.junit.Assert.assertFalse(replacementWritten)
    }

    @Test
    fun allowsReplacementOnlyAfterExistingMetadataPassesValidation() {
        val guard = SensitiveWriteGuard()
        var replacementWritten = false

        guard.validateExisting(
            raw = "legacy-json",
            decrypt = { it },
            validate = { assertEquals("legacy-json", it) }
        )
        replacementWritten = true

        org.junit.Assert.assertTrue(replacementWritten)
        guard.ensureAllowed()
    }
}
