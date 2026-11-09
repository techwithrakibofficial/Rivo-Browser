package com.rivo.browser.engine.gecko

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WebNotificationPolicyTest {
    @Test fun allowsOnlySecureNormalModeNotifications() {
        assertTrue(WebNotificationPolicy.isAllowed(false, "https://example.com/path"))
        assertFalse(WebNotificationPolicy.isAllowed(true, "https://example.com/path"))
        assertFalse(WebNotificationPolicy.isAllowed(false, "http://example.com/path"))
        assertFalse(WebNotificationPolicy.isAllowed(false, "file:///tmp/page"))
        assertFalse(WebNotificationPolicy.isAllowed(false, "https://user:pass@example.com/path"))
        assertFalse(WebNotificationPolicy.isAllowed(false, null))
        assertFalse(WebNotificationPolicy.isAllowed(false, "not a URI"))
    }
}
