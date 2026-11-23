package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class BrowserSecurityStatusPolicyTest {
    @Test
    fun certificateExceptionTakesPrecedence() {
        assertEquals(
            "Certificate security exception",
            BrowserSecurityStatusPolicy.label(true, true, true, true, false)
        )
    }

    @Test
    fun loadedActiveMixedContentIsNeverReportedAsSecure() {
        assertEquals(
            "Active mixed content loaded — page security degraded",
            BrowserSecurityStatusPolicy.label(true, false, true, false, false)
        )
    }

    @Test
    fun loadedPassiveMixedContentIsDisclosed() {
        assertEquals(
            "Insecure passive content loaded",
            BrowserSecurityStatusPolicy.label(true, false, false, true, false)
        )
    }

    @Test
    fun unknownMixedContentStateIsNotOverstatedAsFullySecure() {
        assertEquals(
            "Secure transport · mixed-content status unknown",
            BrowserSecurityStatusPolicy.label(true, false, false, false, true)
        )
    }

    @Test
    fun ordinarySecureConnectionKeepsExistingPositiveLabel() {
        assertEquals(
            "Secure connection",
            BrowserSecurityStatusPolicy.label(true, false, false, false, false)
        )
    }

    @Test
    fun insecureConnectionIsNotReportedAsSecure() {
        assertEquals(
            "Connection is not secure",
            BrowserSecurityStatusPolicy.label(false, false, false, false, false)
        )
    }
}
