package com.rivo.browser

/** Keeps Gecko's initial empty document from replacing Rivo's native start-page marker. */
internal object HomeLocationPolicy {
    fun shouldApplyLocation(currentUrl: String, incomingUrl: String): Boolean =
        !(currentUrl == BrowserController.HOME_URL && incomingUrl.equals("about:blank", ignoreCase = true))
}
