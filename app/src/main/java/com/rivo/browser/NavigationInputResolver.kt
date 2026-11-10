package com.rivo.browser

import java.net.IDN
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Pure omnibox parser. Only HTTP and HTTPS navigation is accepted; anything else becomes a
 * search query using [searchTemplate] (a URL containing the `%s` placeholder).
 *
 * Input without an explicit scheme is treated as an address only when its host looks like
 * one: it contains a dot (domain or IPv4), is `localhost`, or is a bracketed IPv6 literal.
 * Dotless words such as `weather` are searches. International host names are converted to
 * ASCII (punycode) before navigation.
 */
object NavigationInputResolver {
    const val DEFAULT_SEARCH_TEMPLATE = "https://duckduckgo.com/?q=%s"

    private const val SEARCH_PLACEHOLDER = "%s"
    private val EXPLICIT_SCHEME = Regex("^(https?)://", RegexOption.IGNORE_CASE)
    private val HOST_NAME = Regex("^[a-z0-9_]([a-z0-9_-]*[a-z0-9_])?(\\.[a-z0-9_]([a-z0-9_-]*[a-z0-9_])?)*$")
    private val DIGITS_AND_DOTS = Regex("^[0-9.]+$")
    private val PORT_DIGITS = Regex("^[0-9]{1,5}$")

    fun resolve(rawInput: String, searchTemplate: String = DEFAULT_SEARCH_TEMPLATE): String {
        val input = rawInput.trim()
        if (input.isEmpty()) return ""
        return toWebUrl(input) ?: searchUrl(input, searchTemplate)
    }

    private fun searchUrl(query: String, template: String): String {
        val safeTemplate = if (template.contains(SEARCH_PLACEHOLDER)) template else DEFAULT_SEARCH_TEMPLATE
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        return safeTemplate.replace(SEARCH_PLACEHOLDER, encoded)
    }

    private fun toWebUrl(input: String): String? {
        if (input.any(Char::isWhitespace)) return null

        val schemeMatch = EXPLICIT_SCHEME.find(input)
        val explicitScheme = schemeMatch != null
        val scheme = schemeMatch?.groupValues?.get(1)?.lowercase() ?: "https"
        val afterScheme = if (schemeMatch != null) input.substring(schemeMatch.value.length) else input

        val authorityEnd = afterScheme.indexOfFirst { it == '/' || it == '?' || it == '#' }
            .let { if (it < 0) afterScheme.length else it }
        val authority = afterScheme.substring(0, authorityEnd)
        val tail = afterScheme.substring(authorityEnd)
        if (authority.isEmpty()) return null

        // "user@host" without a scheme is far more likely an e-mail address than a login URL.
        val userInfoEnd = authority.lastIndexOf('@')
        if (userInfoEnd >= 0 && !explicitScheme) return null
        val userInfo = authority.substring(0, userInfoEnd + 1)
        val hostPort = splitHostPort(authority.substring(userInfoEnd + 1)) ?: return null

        val host = normalizeHost(hostPort.host, explicitScheme) ?: return null
        val candidate = "$scheme://$userInfo$host${hostPort.port}$tail"
        val parsed = runCatching { URI(candidate) }.getOrNull() ?: return null
        if (parsed.host.isNullOrBlank()) return null
        return parsed.toASCIIString()
    }

    private class HostPort(val host: String, val port: String)

    private fun splitHostPort(hostPort: String): HostPort? {
        if (hostPort.startsWith("[")) {
            val close = hostPort.indexOf(']')
            if (close < 0) return null
            val literal = hostPort.substring(1, close)
            if (literal.isEmpty() || !literal.contains(':')) return null
            if (!literal.all { it.isLetterOrDigit() || it == ':' || it == '.' }) return null
            val port = hostPort.substring(close + 1)
            if (port.isNotEmpty() && !(port.startsWith(":") && isValidPort(port.substring(1)))) return null
            return HostPort(hostPort.substring(0, close + 1), port)
        }
        val colon = hostPort.lastIndexOf(':')
        if (colon < 0) return HostPort(hostPort, "")
        if (!isValidPort(hostPort.substring(colon + 1))) return null
        return HostPort(hostPort.substring(0, colon), hostPort.substring(colon))
    }

    private fun isValidPort(port: String): Boolean =
        PORT_DIGITS.matches(port) && port.toInt() in 1..65535

    private fun normalizeHost(host: String, explicitScheme: Boolean): String? {
        if (host.startsWith("[")) return host.lowercase()
        if (host.isEmpty() || host.startsWith(".") || host.endsWith(".") || host.contains("..")) return null

        val ascii = runCatching { IDN.toASCII(host) }.getOrNull()?.lowercase() ?: return null
        if (!HOST_NAME.matches(ascii)) return null

        if (!explicitScheme) {
            if (DIGITS_AND_DOTS.matches(ascii)) return if (isIpv4(ascii)) ascii else null
            if (!ascii.contains('.') && ascii != "localhost") return null
        }
        return ascii
    }

    private fun isIpv4(value: String): Boolean {
        val parts = value.split('.')
        return parts.size == 4 && parts.all { part ->
            part.isNotEmpty() && part.length <= 3 && part.toIntOrNull()?.let { it in 0..255 } == true
        }
    }
}
