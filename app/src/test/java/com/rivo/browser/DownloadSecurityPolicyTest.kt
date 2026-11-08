package com.rivo.browser

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadSecurityPolicyTest {
    @Test
    fun acceptsOnlyHttpAndHttpsUrlsWithAHostAndWithoutUserInfo() {
        assertTrue(DownloadSecurityPolicy.isAllowedUrl("https://example.com/report.pdf"))
        assertTrue(DownloadSecurityPolicy.isAllowedUrl("http://example.com/report.pdf"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("file:///sdcard/report.pdf"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("content://provider/report.pdf"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("javascript:alert(1)"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("https://user:password@example.com/file"))
        assertFalse(DownloadSecurityPolicy.isAllowedUrl("https:///missing-host"))
    }

    @Test
    fun sanitizesPathSeparatorsControlCharactersAndTraversalNames() {
        assertEquals("report_.pdf", DownloadSecurityPolicy.sanitizeFilename("../report?.pdf", "fallback"))
        assertEquals("secret.txt", DownloadSecurityPolicy.sanitizeFilename("..\\secret.txt", "fallback"))
        assertEquals("fallback", DownloadSecurityPolicy.sanitizeFilename("..", "fallback"))
        assertEquals("fallback", DownloadSecurityPolicy.sanitizeFilename("   ", "fallback"))
        assertTrue(
            DownloadSecurityPolicy.sanitizeFilename("a".repeat(200), "fallback").length <= 120
        )
    }

    @Test
    fun privateDownloadsAreNotPersistedInBrowserDownloadMetadata() {
        assertFalse(DownloadSecurityPolicy.shouldPersistMetadata(privateMode = true))
        assertTrue(DownloadSecurityPolicy.shouldPersistMetadata(privateMode = false))
    }

    @Test
    fun restoredFilesMustBeDirectChildrenOfAnAllowedRootAndWithinSizeLimit() {
        val root = createTempDirectory("rivo-download-root").toFile()
        val outside = createTempDirectory("rivo-download-outside").toFile()
        try {
            val allowed = File(root, "report.pdf").apply { writeBytes(byteArrayOf(1, 2, 3)) }
            val empty = File(root, "empty.txt").apply { createNewFile() }
            val escaped = File(outside, "report.pdf").apply { writeBytes(byteArrayOf(1, 2, 3)) }
            assertTrue(
                DownloadSecurityPolicy.isAllowedRestoredFile(
                    allowed, "report.pdf", listOf(root), maxBytes = 10
                )
            )
            assertFalse(
                DownloadSecurityPolicy.isAllowedRestoredFile(
                    escaped, "report.pdf", listOf(root), maxBytes = 10
                )
            )
            assertTrue(
                DownloadSecurityPolicy.isAllowedRestoredFile(
                    empty, "empty.txt", listOf(root), maxBytes = 10
                )
            )
            assertFalse(
                DownloadSecurityPolicy.isAllowedRestoredFile(
                    allowed, "other.pdf", listOf(root), maxBytes = 10
                )
            )
            assertFalse(
                DownloadSecurityPolicy.isAllowedRestoredFile(
                    allowed, "report.pdf", listOf(root), maxBytes = 2
                )
            )
        } finally {
            root.deleteRecursively()
            outside.deleteRecursively()
        }
    }
}
