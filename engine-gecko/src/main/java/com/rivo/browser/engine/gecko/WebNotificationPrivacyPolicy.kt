package com.rivo.browser.engine.gecko

/**
 * Content intentionally safe to display in an insecure context such as the
 * lock screen or while screen sharing. Never include website-controlled text.
 */
internal object WebNotificationPrivacyPolicy {
    const val PUBLIC_TITLE = "Rivo Browser"
    const val PUBLIC_TEXT = "New website notification"
}
