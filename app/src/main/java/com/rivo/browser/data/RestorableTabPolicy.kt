package com.rivo.browser.data

import java.net.URI

/** Allow only absolute web URLs to survive process restoration. */
object RestorableTabPolicy {
    fun isSafeUrl(rawUrl: String): Boolean = runCatching {
        val uri = URI(rawUrl)
        uri.isAbsolute &&
            uri.scheme?.lowercase() in setOf("http", "https") &&
            !uri.host.isNullOrBlank() &&
            uri.userInfo == null
    }.getOrDefault(false)

    /** Snapshot safe normal tabs only; retain order and duplicate URLs independently. */
    fun snapshot(
        tabs: List<RestorableTabCandidate>,
        selectedTabId: Long
    ): List<SavedTab> = tabs.asSequence()
        .filter { !it.privateMode && isSafeUrl(it.url) }
        .take(8)
        .map { SavedTab(it.url, it.title, it.id == selectedTabId) }
        .toList()
}

data class RestorableTabCandidate(
    val id: Long,
    val privateMode: Boolean,
    val url: String,
    val title: String
)
