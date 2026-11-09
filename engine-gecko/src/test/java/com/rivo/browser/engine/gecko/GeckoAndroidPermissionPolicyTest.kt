package com.rivo.browser.engine.gecko

import android.Manifest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeckoAndroidPermissionPolicyTest {
    @Test
    fun allowsOnlyKnownDevicePermissionsFromSecureOrigins() {
        assertTrue(GeckoAndroidPermissionPolicy.shouldRequest(listOf(Manifest.permission.CAMERA), true))
        assertTrue(GeckoAndroidPermissionPolicy.shouldRequest(listOf(Manifest.permission.RECORD_AUDIO), true))
        assertTrue(GeckoAndroidPermissionPolicy.shouldRequest(listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), true))
        assertFalse(GeckoAndroidPermissionPolicy.shouldRequest(listOf(Manifest.permission.CAMERA), false))
        assertFalse(GeckoAndroidPermissionPolicy.shouldRequest(listOf("android.permission.WRITE_EXTERNAL_STORAGE"), true))
        assertFalse(GeckoAndroidPermissionPolicy.shouldRequest(emptyList(), true))
    }
}
