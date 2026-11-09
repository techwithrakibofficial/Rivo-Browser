package com.rivo.browser

import com.rivo.browser.data.HistoryVisitPolicy
import com.rivo.browser.data.SavedPage
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryVisitPolicyTest {
    @Test
    fun consecutiveCallbacksForCurrentUrlAreCoalesced() {
        val first = SavedPage("https://one.example", "first", 1L)
        val callback = SavedPage("https://one.example", "updated title", 2L)
        assertEquals(listOf(first), HistoryVisitPolicy.recordVisit(listOf(first), callback))
    }

    @Test
    fun revisitingUrlAfterAnotherPageCreatesANewHistoryEntry() {
        val oldVisit = SavedPage("https://one.example", "first visit", 1L)
        val middle = SavedPage("https://two.example", "middle", 2L)
        val revisit = SavedPage("https://one.example", "revisit", 3L)
        assertEquals(
            listOf(revisit, middle, oldVisit),
            HistoryVisitPolicy.recordVisit(listOf(middle, oldVisit), revisit)
        )
    }

    @Test
    fun historyRemainsBoundedToConfiguredMaximum() {
        val pages = (1..300).map {
            SavedPage("https://$it.example", "page $it", it.toLong())
        }
        val next = SavedPage("https://new.example", "new", 301L)
        val result = HistoryVisitPolicy.recordVisit(pages, next)
        assertEquals(300, result.size)
        assertEquals(next, result.first())
        assertEquals(pages[298], result.last())
    }

    @Test
    fun zeroMaximumProducesEmptyHistory() {
        assertEquals(
            emptyList<SavedPage>(),
            HistoryVisitPolicy.recordVisit(
                listOf(SavedPage("https://one.example", "one", 1L)),
                SavedPage("https://two.example", "two", 2L),
                maxEntries = 0
            )
        )
    }
}
