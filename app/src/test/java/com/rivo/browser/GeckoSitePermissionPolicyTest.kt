package com.rivo.browser

import com.rivo.browser.engine.gecko.GeckoSitePermissionPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mozilla.geckoview.GeckoSession

class GeckoSitePermissionPolicyTest {
    @Test
    fun promptsOnlyForMediatedGeckoCapabilitiesOnHttps() {
        val permissions = GeckoSession.PermissionDelegate
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                permissions.PERMISSION_DESKTOP_NOTIFICATION,
                secureOrigin = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                permissions.PERMISSION_AUTOPLAY_AUDIBLE,
                secureOrigin = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                permissions.PERMISSION_AUTOPLAY_INAUDIBLE,
                secureOrigin = true
            )
        )
    }

    @Test
    fun rejectsSensitiveAndUnsupportedCapabilitiesByDefault() {
        val permissions = GeckoSession.PermissionDelegate
        val unsupported = listOf(
            permissions.PERMISSION_GEOLOCATION,
            permissions.PERMISSION_PERSISTENT_STORAGE,
            permissions.PERMISSION_XR,
            permissions.PERMISSION_MEDIA_KEY_SYSTEM_ACCESS,
            permissions.PERMISSION_TRACKING,
            permissions.PERMISSION_STORAGE_ACCESS
        )
        unsupported.forEach { permission ->
            assertFalse(GeckoSitePermissionPolicy.shouldPrompt(permission, secureOrigin = true))
        }
    }

    @Test
    fun rejectsAllPermissionPromptsForInsecureOrigins() {
        val permissions = GeckoSession.PermissionDelegate
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                permissions.PERMISSION_DESKTOP_NOTIFICATION,
                secureOrigin = false
            )
        )
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                permissions.PERMISSION_AUTOPLAY_AUDIBLE,
                secureOrigin = false
            )
        )
    }

    @Test
    fun privateAndPromptStateRecordsAreNotListedAsPersistentSitePermissions() {
        val prompt = GeckoSession.PermissionDelegate.ContentPermission.VALUE_PROMPT
        val allowed = GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW
        val denied = GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY

        assertFalse(GeckoSitePermissionPolicy.shouldDisplay(privateMode = true, value = allowed))
        assertFalse(GeckoSitePermissionPolicy.shouldDisplay(privateMode = true, value = denied))
        assertFalse(GeckoSitePermissionPolicy.shouldDisplay(privateMode = false, value = prompt))
        assertTrue(GeckoSitePermissionPolicy.shouldDisplay(privateMode = false, value = allowed))
        assertTrue(GeckoSitePermissionPolicy.shouldDisplay(privateMode = false, value = denied))
    }

    @Test
    fun persistentStorageIsNotMarkedRevocable() {
        val permissions = GeckoSession.PermissionDelegate
        assertFalse(
            GeckoSitePermissionPolicy.canRevoke(permissions.PERMISSION_PERSISTENT_STORAGE)
        )
        assertTrue(
            GeckoSitePermissionPolicy.canRevoke(permissions.PERMISSION_DESKTOP_NOTIFICATION)
        )
        assertTrue(
            GeckoSitePermissionPolicy.canRevoke(permissions.PERMISSION_AUTOPLAY_AUDIBLE)
        )
    }

    @Test
    fun labelsExistOnlyForRecognizedGeckoPermissionTypes() {
        val permissions = GeckoSession.PermissionDelegate
        assertEquals("Notifications", GeckoSitePermissionPolicy.label(permissions.PERMISSION_DESKTOP_NOTIFICATION))
        assertEquals("Location", GeckoSitePermissionPolicy.label(permissions.PERMISSION_GEOLOCATION))
        assertEquals("Persistent storage", GeckoSitePermissionPolicy.label(permissions.PERMISSION_PERSISTENT_STORAGE))
        assertEquals(null, GeckoSitePermissionPolicy.label(Int.MAX_VALUE))
    }
}
