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
    fun processGuardIsSharedAcrossStorageInstances() {
        SensitiveWriteGuard.process.block()

        assertThrows(IllegalStateException::class.java) {
            SensitiveWriteGuard.process.ensureAllowed()
        }
    }
}
