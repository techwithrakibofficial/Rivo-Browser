package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class DownloadFileWriterTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun sanitizesPathTraversalAndUnsafeHeaderCharacters() {
        val filename = DownloadFileWriter.sanitizeFilename(
            """attachment; filename="../../private report?.pdf"""",
            fallbackName = "fallback.pdf",
            nowMillis = 42L
        )
        assertEquals("private_report_.pdf", filename)
    }

    @Test
    fun supportsUtf8ExtendedContentDispositionFilename() {
        val filename = DownloadFileWriter.sanitizeFilename(
            "attachment; filename*=UTF-8''caf%C3%A9.pdf",
            fallbackName = "fallback.pdf",
            nowMillis = 42L
        )
        assertEquals("caf_.pdf", filename)
    }

    @Test
    fun protectsStartupCleanupTemporaryFilenameNamespace() {
        assertEquals(
            "download-.rivo-staged.part",
            DownloadFileWriter.sanitizeFilename(null, ".rivo-staged.part", 42L)
        )
    }

    @Test
    fun allocatesUniqueNamesWithoutOverwritingExistingFiles() {
        val directory = temporaryFolder.newFolder("downloads")
        val existing = File(directory, "report.pdf").apply { writeText("keep") }
        val first = DownloadFileWriter.save(ByteArrayInputStream("first".toByteArray()), directory, "report.pdf", 100L)
        val second = DownloadFileWriter.save(ByteArrayInputStream("second".toByteArray()), directory, "report.pdf", 100L)

        assertEquals("keep", existing.readText())
        assertEquals("report (1).pdf", first.name)
        assertEquals("report (2).pdf", second.name)
        assertEquals("first", first.readText())
        assertEquals("second", second.readText())
    }

    @Test
    fun closesInputAndRemovesStagingFileWhenSizeLimitIsExceeded() {
        val directory = temporaryFolder.newFolder("limited")
        val input = TrackingInputStream(ByteArray(16) { it.toByte() })
        val failure = runCatching {
            DownloadFileWriter.save(input, directory, "large.bin", maxBytes = 8L)
        }.exceptionOrNull()

        assertTrue(failure is IOException)
        assertTrue(input.closed)
        assertEquals(0, directory.listFiles().orEmpty().size)
    }

    @Test
    fun closesInputWhenDirectoryCreationFailsBeforeStaging() {
        val blockingFile = File(temporaryFolder.root, "not-a-directory").apply { writeText("x") }
        val input = TrackingInputStream(byteArrayOf(1, 2, 3))
        val failure = runCatching {
            DownloadFileWriter.save(input, blockingFile, "small.bin", maxBytes = 10L)
        }.exceptionOrNull()

        assertTrue(failure is IOException)
        assertTrue(input.closed)
        assertTrue(blockingFile.isFile)
    }

    @Test
    fun closesInputAfterSuccessfulFinalization() {
        val directory = temporaryFolder.newFolder("success")
        val input = TrackingInputStream("safe content".toByteArray())
        val file = DownloadFileWriter.save(input, directory, "safe.txt", maxBytes = 100L)

        assertTrue(input.closed)
        assertEquals("safe content", file.readText())
        assertFalse(directory.listFiles().orEmpty().any { it.name.endsWith(".part") })
    }

    private class TrackingInputStream(bytes: ByteArray) : ByteArrayInputStream(bytes) {
        var closed = false
        override fun close() {
            closed = true
            super.close()
        }
    }
}
