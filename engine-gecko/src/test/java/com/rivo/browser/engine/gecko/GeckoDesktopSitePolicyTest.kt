package com.rivo.browser.engine.gecko

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mozilla.geckoview.GeckoSessionSettings

class GeckoDesktopSitePolicyTest {
    @Test
    fun desktopModeSetsBothDesktopUserAgentAndViewport() {
        assertEquals(
            GeckoSessionSettings.USER_AGENT_MODE_DESKTOP,
            GeckoDesktopSitePolicy.userAgentMode(enabled = true)
        )
        assertEquals(
            GeckoSessionSettings.VIEWPORT_MODE_DESKTOP,
            GeckoDesktopSitePolicy.viewportMode(enabled = true)
        )
    }

    @Test
    fun mobileModeRestoresBothMobileUserAgentAndViewport() {
        assertEquals(
            GeckoSessionSettings.USER_AGENT_MODE_MOBILE,
            GeckoDesktopSitePolicy.userAgentMode(enabled = false)
        )
        assertEquals(
            GeckoSessionSettings.VIEWPORT_MODE_MOBILE,
            GeckoDesktopSitePolicy.viewportMode(enabled = false)
        )
    }
}
