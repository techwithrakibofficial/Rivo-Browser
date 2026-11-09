package com.rivo.browser

import java.net.IDN
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Pure URL/search parser: only HTTP and HTTPS navigation is accepted. */
object NavigationInputResolver {
    fun resolve(rawInput: String): String {
        val input = rawInput.trim()
        if (input.isEmpty()) return ""

        val candidate = if (
            input.startsWith("https://", ignoreCase = true) ||
            input.startsWith("http://", ignoreCase = true)
        ) input else "https://$input"

        val parsed = runCatching { URI(candidate) }.getOrNull()
        val normalized = parsed?.let(::normalizeInternationalHost)
        val scheme = normalized?.scheme?.lowercase()
        if (
            normalized != null &&
            scheme in setOf("http", "https") &&
            !normalized.host.isNullOrBlank() &&
            normalized.rawUserInfo == null &&
            (normalized.port == -1 || normalized.port in 0..65535) &&
            input.none(Char::isWhitespace)
        ) {
            return normalized.toASCIIString()
        }

        val query = URLEncoder.encode(input, StandardCharsets.UTF_8.name())
        return "https://duckduckgo.com/?q=$query"
    }

    /** URI leaves host null for Unicode domains; IDN converts only the host. */
    private fun normalizeInternationalHost(uri: URI): URI? {
        if (!uri.host.isNullOrBlank()) return uri
        val authority = uri.rawAuthority ?: return null
        val hostAndPort = authority.substringAfterLast('@')
        if (hostAndPort.startsWith("[")) return uri // IPv6 is parsed by URI itself.

        val separator = hostAndPort.lastIndexOf(':')
        val hasPort = separator >= 0
        val rawHost = if (hasPort) hostAndPort.substring(0, separator) else hostAndPort
        val rawPort = if (hasPort) hostAndPort.substring(separator + 1) else null
        if (rawHost.isBlank()) return null

        val port = when {
            rawPort == null -> uri.port
            rawPort.isEmpty() -> return null
            else -> rawPort.toIntOrNull()?.takeIf { it in 0..65535 } ?: return null
        }
        val asciiHost = runCatching {
            IDN.toASCII(rawHost, IDN.USE_STD3_ASCII_RULES)
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: return null

        val normalizedAuthority = buildString {
            uri.rawUserInfo?.let { append(it).append('@') }
            append(asciiHost)
            if (port >= 0) append(':').append(port)
        }
        val normalizedUri = buildString {
            append(uri.scheme).append("://").append(normalizedAuthority)
            append(uri.rawPath.orEmpty())
            uri.rawQuery?.let { append('?').append(it) }
            uri.rawFragment?.let { append('#').append(it) }
        }
        return runCatching { URI(normalizedUri) }.getOrNull()
    }
}
