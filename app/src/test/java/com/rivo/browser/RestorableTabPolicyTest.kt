package com.rivo.browser

import com.rivo.browser.data.RestorableTabPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestorableTabPolicyTest {
    @Test
    fun allowsOnlyAbsoluteHttpAndHttpsUrlsWithHosts() {
        assertTrue(RestorableTabPolicy.isSafeUrl("https://example.com/path"))
        assertTrue(RestorableTabPolicy.isSafeUrl("http://localhost:8080/"))
        assertFalse(RestorableTabPolicy.isSafeUrl("javascript:alert(1)"))
        assertFalse(RestorableTabPolicy.isSafeUrl("file:///etc/passwd"))
        assertFalse(RestorableTabPolicy.isSafeUrl("about:blank"))
        assertFalse(RestorableTabPolicy.isSafeUrl("example.com"))
        assertFalse(RestorableTabPolicy.isSafeUrl("https://user:pass@example.com"))
    }
}
