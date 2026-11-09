package com.rivo.browser

object QrCodePolicy {
    const val MAX_RESULT_CHARS = 2048

    fun sanitize(rawValue: String?): String? {
        val value = rawValue ?: return null
        if (value.length > MAX_RESULT_CHARS) return null
        return value
            .filter { it == '\n' || it == '\t' || it >= ' ' }
            .trim()
            .takeIf { it.isNotEmpty() }
    }
}
