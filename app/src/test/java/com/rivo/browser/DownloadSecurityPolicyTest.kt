package com.rivo.browser

import java.io.File
import java.nio.file.Files
import org.junit.Assert.*
import org.junit.Test

class DownloadSecurityPolicyTest {
    @Test fun onlyHttpAndHttpsWithHostAndNoUserInfoAreAllowed() {
        assertTrue(DownloadSecurityPolicy.isAllowedUrl("https://example.com/a"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("http://example.com/a"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("file:///sdcard/a"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("https://u:p@example.com/a"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("https:///missing"))
    }
    @Test fun mimeTypesAreNormalizedAndInvalidValuesAreRejected() {
        assertEquals("application/pdf", DownloadSecurityPolicy.normalizeMimeType("Application/PDF; charset=binary"))
        assertEquals("application/octet-stream", DownloadSecurityPolicy.normalizeMimeType("application/octet-stream"))
        assertNull(DownloadSecurityPolicy.normalizeMimeType("text/html" + 13.toChar() + 10.toChar() + "X-Evil: yes"))
        assertNull(DownloadSecurityPolicy.normalizeMimeType("not-a-mime-type"))
        assertNull(DownloadSecurityPolicy.normalizeMimeType(null))
    }
    @Test fun privateDownloadMetadataIsNotPersisted() {
        assertFalse(DownloadSecurityPolicy.shouldPersistMetadata(true))
        assertTrue(DownloadSecurityPolicy.shouldPersistMetadata(false))
    }
    @Test fun restoredFilesMustStayInsideCanonicalRootAndUnderSizeLimit() {
        val root=Files.createTempDirectory("rivo-root").toFile()
        val outside=Files.createTempDirectory("rivo-out").toFile()
        try {
            val allowed=File(root,"safe.bin").apply { writeBytes(byteArrayOf(1,2,3)) }
            val escaped=File(outside,"safe.bin").apply { writeBytes(byteArrayOf(1,2,3)) }
            assertTrue(DownloadSecurityPolicy.isAllowedRestoredFile(allowed,"safe.bin",listOf(root),4))
            assertFalse(DownloadSecurityPolicy.isAllowedRestoredFile(escaped,"safe.bin",listOf(root),4))
            assertFalse(DownloadSecurityPolicy.isAllowedRestoredFile(allowed,"safe.bin",listOf(root),2))
        } finally { root.deleteRecursively(); outside.deleteRecursively() }
    }
}
