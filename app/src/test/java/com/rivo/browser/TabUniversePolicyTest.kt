package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class TabUniversePolicyTest {
    private val tabs = listOf(
        TabUniverseItem(1, "News", "https://news.example", false, true),
        TabUniverseItem(2, "Private research", "https://private.example", true, false),
        TabUniverseItem(3, "Mail", "https://mail.example", false, false)
    )

    @Test fun filtersNormalPrivateAndActiveStates() {
        assertEquals(listOf(1L, 3L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.NORMAL, TabUniverseSort.ACTIVE_FIRST).map { it.id })
        assertEquals(listOf(2L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.PRIVATE, TabUniverseSort.ACTIVE_FIRST).map { it.id })
        assertEquals(listOf(1L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.ACTIVE, TabUniverseSort.ACTIVE_FIRST).map { it.id })
    }

    @Test fun searchesTitleAndUrlCaseInsensitively() {
        assertEquals(listOf(2L), TabUniversePolicy.visible(tabs, "PRIVATE.EXAMPLE", TabUniverseFilter.ALL, TabUniverseSort.ACTIVE_FIRST).map { it.id })
        assertEquals(listOf(3L), TabUniversePolicy.visible(tabs, "mail", TabUniverseFilter.ALL, TabUniverseSort.ACTIVE_FIRST).map { it.id })
    }

    @Test fun sortsActiveFirstTitleAndType() {
        assertEquals(listOf(1L, 3L, 2L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.ALL, TabUniverseSort.ACTIVE_FIRST).map { it.id })
        assertEquals(listOf(3L, 1L, 2L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.ALL, TabUniverseSort.TITLE).map { it.id })
        assertEquals(listOf(1L, 3L, 2L), TabUniversePolicy.visible(tabs, "", TabUniverseFilter.ALL, TabUniverseSort.TYPE).map { it.id })
    }
}
