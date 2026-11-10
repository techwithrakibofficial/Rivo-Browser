package com.rivo.browser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class DownloadTempFileCleanupTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun removesOnlyTheDirectPrivateDownloadDirectory() {
        val root = temporaryFolder.newFolder("downloads")
        val privateDirectory = File(root, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY)
            .apply { mkdirs() }
        val privateFile = File(privateDirectory, "private.bin").apply { writeText("private") }
        val normalFile = File(root, "normal.bin").apply { writeText("keep") }
        val nestedPrivateDirectory = File(root, "nested").apply {
            mkdirs()
        }.let { File(it, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY).apply { mkdirs() } }
        val nestedFile = File(nestedPrivateDirectory, "nested.bin").apply { writeText("keep") }

        DownloadTempFileCleanup.removePrivateDownloads(listOf(root))

        assertFalse(privateDirectory.exists())
        assertFalse(privateFile.exists())
        assertTrue(normalFile.exists())
        assertTrue(nestedPrivateDirectory.exists())
        assertTrue(nestedFile.exists())
    }

    @Test
    fun doesNotFollowPrivateDirectorySymlinkOutsideTheDownloadsRoot() {
        val root = temporaryFolder.newFolder("downloads")
        val outsideParent = temporaryFolder.newFolder("outside")
        val outsidePrivateDirectory = File(
            outsideParent,
            DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY
        ).apply { mkdirs() }
        val sentinel = File(outsidePrivateDirectory, "must-survive.bin").apply { writeText("keep") }
        val link = File(root, DownloadTempFileCleanup.PRIVATE_DOWNLOAD_DIRECTORY)
        Files.createSymbolicLink(link.toPath(), outsidePrivateDirectory.toPath())

        DownloadTempFileCleanup.removePrivateDownloads(listOf(root))

        assertTrue(Files.isSymbolicLink(link.toPath()))
        assertTrue(outsidePrivateDirectory.exists())
        assertTrue(sentinel.exists())
    }

    @Test
    fun missingPrivateDirectoryIsA noOp() {
        val root = temporaryFolder.newFolder("downloads")

        DownloadTempFileCleanup.removePrivateDownloads(listOf(root))

        assertTrue(root.isDirectory)
        assertTrue(root.listFiles().orEmpty().isEmpty())
    }
}
