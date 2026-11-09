package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class TransferStudioPolicyTest {
    private val transfers = listOf(
        TransferRecord(1, "report.pdf", "Saved in app Downloads", "application/pdf", 1024, true),
        TransferRecord(2, "video.mp4", "Downloading", "video/mp4", null, false),
        TransferRecord(3, "broken.zip", "Failed", "application/zip", null, false)
    )

    @Test
    fun filtersActiveCompletedAndFailedTransfers() {
        assertEquals(listOf(2L), TransferStudioPolicy.visible(transfers, "", TransferFilter.ACTIVE).map { it.id })
        assertEquals(listOf(1L), TransferStudioPolicy.visible(transfers, "", TransferFilter.COMPLETED).map { it.id })
        assertEquals(listOf(3L), TransferStudioPolicy.visible(transfers, "", TransferFilter.FAILED).map { it.id })
    }

    @Test
    fun searchesFilenameAndMimeTypeCaseInsensitively() {
        assertEquals(listOf(1L), TransferStudioPolicy.visible(transfers, "REPORT", TransferFilter.ALL).map { it.id })
        assertEquals(listOf(2L), TransferStudioPolicy.visible(transfers, "VIDEO/", TransferFilter.ALL).map { it.id })
    }

    @Test
    fun trimsQueryAndCombinesItWithTransferState() {
        assertEquals(listOf(1L), TransferStudioPolicy.visible(transfers, "  pdf  ", TransferFilter.COMPLETED).map { it.id })
        assertEquals(emptyList<Long>(), TransferStudioPolicy.visible(transfers, "pdf", TransferFilter.FAILED).map { it.id })
    }
}
