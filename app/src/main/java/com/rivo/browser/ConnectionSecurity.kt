package com.rivo.browser

/** Connection state shown in browser chrome. The same rules apply to regular and private tabs. */
enum class ConnectionSecurity {
    UNKNOWN,
    SECURE,
    NOT_SECURE,
    CERTIFICATE_EXCEPTION;

    companion object {
        fun from(known: Boolean, secure: Boolean, exception: Boolean): ConnectionSecurity = when {
            !known -> UNKNOWN
            exception -> CERTIFICATE_EXCEPTION
            secure -> SECURE
            else -> NOT_SECURE
        }
    }
}
