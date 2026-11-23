package com.rivo.browser

/** Human-readable transport/page-security state; unknown mixed-content state is never called fully secure. */
internal object BrowserSecurityStatusPolicy {
    fun label(
        secure: Boolean,
        certificateException: Boolean,
        activeMixedContentLoaded: Boolean,
        passiveMixedContentLoaded: Boolean,
        mixedContentStatusUnknown: Boolean
    ): String = when {
        certificateException -> "Certificate security exception"
        activeMixedContentLoaded -> "Active mixed content loaded — page security degraded"
        passiveMixedContentLoaded -> "Insecure passive content loaded"
        !secure -> "Connection is not secure"
        mixedContentStatusUnknown -> "Secure transport · mixed-content status unknown"
        else -> "Secure connection"
    }
}
