package com.rivo.browser

/** Select a restored tab by its saved record position, never by URL equality. */
internal fun restoredSelectedTabIndex(selectedFlags: List<Boolean>): Int =
    selectedFlags.indexOfFirst { it }.takeIf { it >= 0 } ?: 0

/** Load a restored page only when its pending tab becomes selected. */
internal fun shouldLoadPendingRestoredTab(isPending: Boolean, isSelected: Boolean): Boolean =
    isPending && isSelected
