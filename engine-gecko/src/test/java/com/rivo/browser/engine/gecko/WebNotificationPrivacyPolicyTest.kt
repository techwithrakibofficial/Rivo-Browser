package com.rivo.browser.engine.gecko

import org.junit.Assert.assertEquals
import org.junit.Test

class WebNotificationPrivacyPolicyTest {
    @Test
    fun publicVersionUsesGenericContentWithoutWebsiteData() {
        assertEquals("Rivo Browser", WebNotificationPrivacyPolicy.PUBLIC_TITLE)
        assertEquals("New website notification", WebNotificationPrivacyPolicy.PUBLIC_TEXT)
    }
}
