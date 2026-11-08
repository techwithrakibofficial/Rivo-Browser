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
}
