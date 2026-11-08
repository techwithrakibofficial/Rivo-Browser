package com.rivo.browser

/** Select a restored tab by its saved record position, never by URL equality. */
internal fun restoredSelectedTabIndex(selectedFlags: List<Boolean>): Int =
    selectedFlags.indexOfFirst { it }.takeIf { it >= 0 } ?: 0
