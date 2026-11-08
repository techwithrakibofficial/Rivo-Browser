package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class RestoredTabSelectionTest {
    @Test
    fun selectsTheMatchingRecordWhenUrlsMayBeDuplicated() {
        assertEquals(1, restoredSelectedTabIndex(listOf(false, true, false)))
    }

    @Test
    fun fallsBackToFirstTabWhenNoSelectionWasPersisted() {
        assertEquals(0, restoredSelectedTabIndex(listOf(false, false)))
    }

    @Test
    fun selectsFirstTabWhenItWasPersistedAsSelected() {
        assertEquals(0, restoredSelectedTabIndex(listOf(true, false)))
    }
}
