package com.rivo.browser

enum class TransferFilter { ALL, ACTIVE, COMPLETED, FAILED }

data class TransferRecord(
    val id: Long,
    val filename: String,
    val status: String,
    val mimeType: String?,
    val sizeBytes: Long?,
    val hasFile: Boolean,
    val bytesReceived: Long = 0L,
    val totalBytes: Long? = null
)

object TransferStudioPolicy {
    fun visible(
        transfers: List<TransferRecord>,
        query: String,
        filter: TransferFilter
    ): List<TransferRecord> {
        val normalizedQuery = query.trim().lowercase()
        return transfers.filter { item ->
            val matchesFilter = when (filter) {
                TransferFilter.ALL -> true
                TransferFilter.ACTIVE -> item.status.equals("Downloading", ignoreCase = true) || item.status.equals("Cancelling", ignoreCase = true)
                TransferFilter.COMPLETED -> item.hasFile
                TransferFilter.FAILED -> item.status.equals("Failed", ignoreCase = true) || item.status.equals("Cancelled", ignoreCase = true)
            }
            val matchesQuery = normalizedQuery.isEmpty() ||
                item.filename.lowercase().contains(normalizedQuery) ||
                item.mimeType.orEmpty().lowercase().contains(normalizedQuery)
            matchesFilter && matchesQuery
        }
    }
}
