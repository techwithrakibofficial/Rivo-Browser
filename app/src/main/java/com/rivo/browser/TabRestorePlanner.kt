package com.rivo.browser

import com.rivo.browser.data.RestorableTabPolicy
import com.rivo.browser.data.SavedTab

/** Tabs to recreate after process death and which of them was selected. */
data class TabRestorePlan(val tabs: List<SavedTab>, val selectedIndex: Int)

object TabRestorePlanner {
    /**
     * Keeps only safe web URLs (at most [maxTabs]) and selects the saved selected tab by
     * position, so duplicate URLs cannot select the wrong tab. When no saved tab is marked
     * selected the last tab is selected, matching the previous restore behavior.
     */
    fun plan(saved: List<SavedTab>, maxTabs: Int): TabRestorePlan? {
        val usable = saved.filter { RestorableTabPolicy.isSafeUrl(it.url) }.take(maxTabs)
        if (usable.isEmpty()) return null
        val selected = usable.indexOfFirst { it.selected }
        return TabRestorePlan(usable, if (selected >= 0) selected else usable.lastIndex)
    }
}
