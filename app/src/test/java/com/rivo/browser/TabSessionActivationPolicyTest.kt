package com.rivo.browser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TabSessionActivationPolicyTest {
    @Test
    fun onlySelectedTabIsActiveWhileHostIsStarted() {
        assertTrue(TabSessionActivationPolicy.shouldActivate(2L, 2L, hostStarted = true))
        assertFalse(TabSessionActivationPolicy.shouldActivate(1L, 2L, hostStarted = true))
    }

    @Test
    fun noTabIsActiveWhileHostIsStopped() {
        assertFalse(TabSessionActivationPolicy.shouldActivate(2L, 2L, hostStarted = false))
        assertFalse(TabSessionActivationPolicy.shouldActivate(1L, 2L, hostStarted = false))
    }
}
