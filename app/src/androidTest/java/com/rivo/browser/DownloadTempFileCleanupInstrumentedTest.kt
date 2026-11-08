package com.rivo.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DownloadTempFileCleanupInstrumentedTest {
    @Test
    fun removesOnlyDirectRivoPartialFilesAndKeepsCompletedDownloads() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val directory = File(context.cacheDir, "rivo-temp-cleanup-test").apply {
            deleteRecursively()
            mkdirs()
        }
        val interrupted = File(directory, ".rivo-crash.part").apply { writeText("partial") }
        val completed = File(directory, "complete.pdf").apply { writeText("complete") }
        val unrelated = File(directory, "other.part").apply { writeText("other") }
        val nested = File(directory, "nested").apply { mkdirs() }
        val nestedPartial = File(nested, ".rivo-nested.part").apply { writeText("nested") }

        try {
            DownloadTempFileCleanup.removeInterruptedFiles(listOf(directory))
            assertFalse(interrupted.exists())
            assertTrue(completed.exists())
            assertTrue(unrelated.exists())
            assertTrue(nestedPartial.exists())
        } finally {
            directory.deleteRecursively()
        }
    }
}
