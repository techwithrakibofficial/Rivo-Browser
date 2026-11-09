package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QrCodePolicyTest {
    @Test
    fun trimsAndRemovesControlCharacters() {
        assertEquals("https://example.com", QrCodePolicy.sanitize("\u0000 https://example.com\r"))
    }

    @Test
    fun preservesOrdinaryWhitespaceInsidePayload() {
        assertEquals("hello\nworld", QrCodePolicy.sanitize(" hello\nworld "))
    }

    @Test
    fun rejectsBlankAndOversizedPayloads() {
        assertNull(QrCodePolicy.sanitize(" \u0000 "))
        assertNull(QrCodePolicy.sanitize("x".repeat(QrCodePolicy.MAX_RESULT_CHARS + 1)))
    }
}
