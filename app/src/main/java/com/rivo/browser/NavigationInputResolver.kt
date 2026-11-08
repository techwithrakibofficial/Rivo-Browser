package com.rivo.browser

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
        val scheme = parsed?.scheme?.lowercase()
        if (
            parsed != null &&
            scheme in setOf("http", "https") &&
            !parsed.host.isNullOrBlank() &&
            input.none(Char::isWhitespace)
        ) {
            return parsed.toASCIIString()
        }

        val query = URLEncoder.encode(input, StandardCharsets.UTF_8.name())
        return "https://duckduckgo.com/?q=$query"
    }
}
