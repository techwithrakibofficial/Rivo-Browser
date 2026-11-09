package com.rivo.browser

enum class TabUniverseFilter { ALL, NORMAL, PRIVATE, ACTIVE }
enum class TabUniverseSort { ACTIVE_FIRST, TITLE, TYPE }

data class TabUniverseItem(val id: Long, val title: String, val url: String, val privateMode: Boolean, val active: Boolean)

object TabUniversePolicy {
    fun visible(tabs: List<TabUniverseItem>, query: String, filter: TabUniverseFilter, sort: TabUniverseSort): List<TabUniverseItem> {
        val needle = query.trim().lowercase()
        val filtered = tabs.filter { tab ->
            val matchesFilter = when (filter) {
                TabUniverseFilter.ALL -> true
                TabUniverseFilter.NORMAL -> !tab.privateMode
                TabUniverseFilter.PRIVATE -> tab.privateMode
                TabUniverseFilter.ACTIVE -> tab.active
            }
            val matchesQuery = needle.isBlank() || tab.title.lowercase().contains(needle) || tab.url.lowercase().contains(needle)
            matchesFilter && matchesQuery
        }
        return when (sort) {
            TabUniverseSort.ACTIVE_FIRST -> filtered.sortedWith(compareByDescending<TabUniverseItem> { it.active }.thenBy { it.title.lowercase() })
            TabUniverseSort.TITLE -> filtered.sortedBy { it.title.lowercase() }
            TabUniverseSort.TYPE -> filtered.sortedWith(compareBy<TabUniverseItem> { it.privateMode }.thenByDescending { it.active }.thenBy { it.title.lowercase() })
        }
    }
}
