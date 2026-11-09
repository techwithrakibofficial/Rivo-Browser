package com.rivo.browser

import java.io.File

/**
 * Removes only incomplete Rivo-owned download staging files from app download
 * directories. Completed downloads and unrelated files are untouched.
 */
internal object DownloadTempFileCleanup {
    fun removeInterruptedFiles(directories: List<File>) {
        directories.forEach { directory ->
            directory.listFiles { file ->
                file.isFile &&
                    file.name.startsWith(".rivo-") &&
                    file.name.endsWith(".part")
            }?.forEach { file ->
                file.delete()
            }
        }
    }

    /** Private downloads are kept in a dedicated app-private directory and never restored after process death. */
    fun removePrivateDownloads(directories: List<File>) {
        directories.forEach { directory ->
            runCatching {
                val root = directory.canonicalFile
                val privateDirectory = File(directory, PRIVATE_DOWNLOAD_DIRECTORY).canonicalFile
                if (privateDirectory.name == PRIVATE_DOWNLOAD_DIRECTORY && privateDirectory.parentFile == root) {
                    privateDirectory.deleteRecursively()
                }
            }
        }
    }

    const val PRIVATE_DOWNLOAD_DIRECTORY = ".rivo-private"
}
