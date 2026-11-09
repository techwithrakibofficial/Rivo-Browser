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
}
