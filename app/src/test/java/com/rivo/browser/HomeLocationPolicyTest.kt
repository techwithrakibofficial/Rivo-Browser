package com.rivo.browser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLocationPolicyTest {
    @Test
    fun ignoresInitialAboutBlankForNativeHomeTab() {
        assertFalse(HomeLocationPolicy.shouldApplyLocation(BrowserController.HOME_URL, "about:blank"))
    }

    @Test
    fun ignoresCaseVariantAboutBlankForNativeHomeTab() {
        assertFalse(HomeLocationPolicy.shouldApplyLocation(BrowserController.HOME_URL, "ABOUT:BLANK"))
    }

    @Test
    fun acceptsRealNavigationFromHome() {
        assertTrue(HomeLocationPolicy.shouldApplyLocation(BrowserController.HOME_URL, "https://example.com"))
    }

    @Test
    fun doesNotSuppressAboutBlankOutsideHome() {
        assertTrue(HomeLocationPolicy.shouldApplyLocation("https://example.com", "about:blank"))
    }
}
