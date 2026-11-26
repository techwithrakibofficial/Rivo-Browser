package com.rivo.browser.engine.gecko

import org.mozilla.geckoview.GeckoSessionSettings

/** Maps the user-facing Desktop Site toggle to GeckoView's independent UA and viewport modes. */
internal object GeckoDesktopSitePolicy {
    fun userAgentMode(enabled: Boolean): Int =
        if (enabled) GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
        else GeckoSessionSettings.USER_AGENT_MODE_MOBILE

    fun viewportMode(enabled: Boolean): Int =
        if (enabled) GeckoSessionSettings.VIEWPORT_MODE_DESKTOP
        else GeckoSessionSettings.VIEWPORT_MODE_MOBILE
}
