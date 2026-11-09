package com.rivo.browser

import java.io.File
import java.net.URI
import java.util.Locale

internal object DownloadSecurityPolicy {
    fun isAllowedUrl(rawUrl: String): Boolean {
        val uri = runCatching { URI(rawUrl) }.getOrNull() ?: return false
        return uri.scheme?.lowercase(Locale.ROOT) in setOf("http", "https") &&
            !uri.host.isNullOrBlank() && uri.rawUserInfo == null
    }
    fun shouldPersistMetadata(privateMode: Boolean): Boolean = !privateMode
    fun isAllowedRestoredFile(file: File, expectedFilename: String, allowedRoots: List<File>, maxBytes: Long): Boolean =
        runCatching {
            val canonical = file.canonicalFile
            canonical.name == expectedFilename && canonical.isFile && canonical.length() in 0L..maxBytes &&
                allowedRoots.any { canonical.parentFile == it.canonicalFile }
        }.getOrDefault(false)
}
