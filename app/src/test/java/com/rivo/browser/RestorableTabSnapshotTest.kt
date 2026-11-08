package com.rivo.browser

import com.rivo.browser.data.RestorableTabCandidate
import com.rivo.browser.data.RestorableTabPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestorableTabSnapshotTest {
    @Test
    fun snapshotExcludesPrivateTabsAndRetainsDuplicateNormalUrlsIndependently() {
        val snapshot = RestorableTabPolicy.snapshot(
            listOf(
                RestorableTabCandidate(10L, false, "https://same.example", "first"),
                RestorableTabCandidate(11L, true, "https://private.example", "private"),
                RestorableTabCandidate(12L, false, "https://same.example", "second")
            ),
            selectedTabId = 12L
        )
        assertEquals(2, snapshot.size)
        assertEquals("first", snapshot[0].title)
        assertEquals("second", snapshot[1].title)
        assertFalse(snapshot[0].selected)
        assertTrue(snapshot[1].selected)
    }

    @Test
    fun snapshotRejectsUnsupportedAndCredentialBearingUrls() {
        val snapshot = RestorableTabPolicy.snapshot(
            listOf(
                RestorableTabCandidate(1L, false, "file:///private", "file"),
                RestorableTabCandidate(2L, false, "https://user:pass@example.com", "credentials"),
                RestorableTabCandidate(3L, false, "https://safe.example", "safe")
            ),
            selectedTabId = 3L
        )
        assertEquals(1, snapshot.size)
        assertEquals("safe", snapshot.single().title)
        assertTrue(snapshot.single().selected)
    }

    @Test
    fun snapshotCapsRestorableTabsAtEight() {
        val candidates = (1L..10L).map {
            RestorableTabCandidate(it, false, "https://tab$it.example", "tab$it")
        }
        assertEquals(8, RestorableTabPolicy.snapshot(candidates, 10L).size)
    }
}
