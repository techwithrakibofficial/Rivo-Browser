package com.rivo.browser

import com.rivo.browser.data.RestorableTabPolicy
import org.junit.Assert.assertEquals
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
    @Test
    fun processRestorationSnapshotExcludesPrivateTabsAndUnsafeUrls() {
        val snapshot = RestorableTabPolicy.snapshot(
            listOf(
                RestorableTabCandidate(1, false, "https://normal.example/", "Normal"),
                RestorableTabCandidate(2, true, "https://private.example/", "Private"),
                RestorableTabCandidate(3, false, "javascript:alert(1)", "Unsafe"),
                RestorableTabCandidate(4, false, "https://normal.example/", "Duplicate normal")
            ),
            selectedTabId = 2
        )

        assertEquals(2, snapshot.size)
        assertTrue(snapshot.all { it.url == "https://normal.example/" })
        assertTrue(snapshot.none { it.title == "Private" || it.url.contains("private") })
        assertFalse(snapshot.any { it.selected })
    }

}
