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
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION,
                secureOrigin = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE,
                secureOrigin = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_INAUDIBLE,
                secureOrigin = true
            )
        )
    }

    @Test
    fun rejectsSensitiveAndUnsupportedCapabilitiesByDefault() {
        val unsupported = listOf(
            GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE,
            GeckoSession.PermissionDelegate.PERMISSION_XR,
            GeckoSession.PermissionDelegate.PERMISSION_MEDIA_KEY_SYSTEM_ACCESS,
            GeckoSession.PermissionDelegate.PERMISSION_TRACKING,
            GeckoSession.PermissionDelegate.PERMISSION_STORAGE_ACCESS,
            GeckoSession.PermissionDelegate.PERMISSION_LOCAL_DEVICE_ACCESS,
            GeckoSession.PermissionDelegate.PERMISSION_LOCAL_NETWORK_ACCESS
        )
        unsupported.forEach { permission ->
            assertFalse(GeckoSitePermissionPolicy.shouldPrompt(permission, secureOrigin = true))
        }
    }

    @Test
    fun geolocationRequiresHttpsAndIsSessionScopedInPrivateMode() {
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION,
                secureOrigin = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION,
                secureOrigin = true,
                privateMode = true
            )
        )
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION,
                secureOrigin = false
            )
        )
    }

    @Test
    fun privateBrowsingDoesNotAllowPersistentOsNotifications() {
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION,
                secureOrigin = true,
                privateMode = true
            )
        )
        assertTrue(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE,
                secureOrigin = true,
                privateMode = true
            )
        )
    }

    @Test
    fun rejectsAllPermissionPromptsForInsecureOrigins() {
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION,
                secureOrigin = false
            )
        )
        assertFalse(
            GeckoSitePermissionPolicy.shouldPrompt(
                GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE,
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
        assertFalse(
            GeckoSitePermissionPolicy.canRevoke(GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE)
        )
        assertTrue(
            GeckoSitePermissionPolicy.canRevoke(GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION)
        )
        assertTrue(
            GeckoSitePermissionPolicy.canRevoke(GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE)
        )
    }

    @Test
    fun labelsExistOnlyForRecognizedGeckoPermissionTypes() {
        assertEquals("Notifications", GeckoSitePermissionPolicy.label(GeckoSession.PermissionDelegate.PERMISSION_DESKTOP_NOTIFICATION))
        assertEquals("Location", GeckoSitePermissionPolicy.label(GeckoSession.PermissionDelegate.PERMISSION_GEOLOCATION))
        assertEquals("Persistent storage", GeckoSitePermissionPolicy.label(GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE))
        assertEquals(null, GeckoSitePermissionPolicy.label(Int.MAX_VALUE))
    }
}
