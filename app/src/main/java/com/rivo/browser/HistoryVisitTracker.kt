package com.rivo.browser

import com.rivo.browser.data.SavedPage

/**
 * Decides when a page visit is ready to be written to browsing history.
 *
 * Engines report the new location before the page title is known. Recording on the
 * location change stores the previous page's title, and local storage never updates an
 * entry that is already at the top of the history. This tracker therefore waits until the
 * load finishes (or, for in-page navigations, until a title arrives) and emits at most one
 * entry per location.
 *
 * Known limitation: an in-page route change that never updates the document title is not
 * recorded, and a title that only arrives after the load finished is not written back.
 */
class HistoryVisitTracker(private val clock: () -> Long = System::currentTimeMillis) {
    private var currentUrl: String? = null
    private var currentTitle: String? = null
    private var recorded = false
    private var loading = false

    fun onLocationChanged(url: String) {
        if (url == currentUrl) return
        currentUrl = url.takeIf { isWebUrl(it) }
        currentTitle = null
        recorded = false
    }

    fun onTitleChanged(title: String?): SavedPage? {
        if (title.isNullOrBlank() || currentUrl == null) return null
        currentTitle = title
        return if (!loading) recordIfNeeded() else null
    }

    fun onLoadingChanged(isLoading: Boolean): SavedPage? {
        val wasLoading = loading
        loading = isLoading
        return if (wasLoading && !isLoading) recordIfNeeded() else null
    }

    private fun recordIfNeeded(): SavedPage? {
        val url = currentUrl ?: return null
        if (recorded) return null
        recorded = true
        return SavedPage(url, currentTitle ?: url, clock())
    }

    companion object {
        fun isWebUrl(url: String): Boolean =
            url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)
    }
}

/** Keeps saved page lists safe to render with the URL as a unique list key. */
object PageListPolicy {
    /** Newest-first lists keep the first (most recent) occurrence of each URL. */
    fun uniqueByUrl(pages: List<SavedPage>, limit: Int): List<SavedPage> =
        pages.distinctBy { it.url }.take(limit)
}
