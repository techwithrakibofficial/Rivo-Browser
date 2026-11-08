package com.rivo.browser

import java.io.File
import java.net.URI
import java.util.Locale

/**
 * Small, deterministic policy helpers for download boundaries.
 *
 * The response URL, filename, MIME type and restored metadata are untrusted input.
 * File contents are intentionally not described as encrypted by this policy.
 */
internal object DownloadSecurityPolicy {
    fun isAllowedUrl(rawUrl: String): Boolean {
        val uri = runCatching { URI(rawUrl) }.getOrNull() ?: return false
        return uri.scheme?.lowercase(Locale.ROOT) in setOf("http", "https") &&
            !uri.host.isNullOrBlank() &&
            uri.rawUserInfo == null
    }

    fun sanitizeFilename(rawName: String, fallback: String): String {
        val leafName = rawName.substringAfterLast('/').substringAfterLast('\\')
        val safeName = leafName
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .take(120)
        return safeName.takeUnless { it.isBlank() || it == "." || it == ".." } ?: fallback
    }

    /** Private-tab download bytes remain user-saved files; only their persistent browser metadata is omitted. */
    fun shouldPersistMetadata(privateMode: Boolean): Boolean = !privateMode

    fun isAllowedRestoredFile(
        file: File,
        expectedFilename: String,
        allowedRoots: List<File>,
        maxBytes: Long
    ): Boolean = runCatching {
        val canonicalFile = file.canonicalFile
        val canonicalRoots = allowedRoots.map { it.canonicalFile }
        canonicalFile.name == expectedFilename &&
            canonicalFile.isFile &&
            canonicalFile.length() in 1..maxBytes &&
            canonicalRoots.any { canonicalFile.parentFile == it }
    }.getOrDefault(false)
}
