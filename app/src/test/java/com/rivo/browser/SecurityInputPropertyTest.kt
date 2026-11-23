package com.rivo.browser

import java.net.URI
import java.util.Random
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Deterministic property-style regression coverage for hostile omnibox and
 * Content-Disposition inputs. This complements, but does not replace, fuzzing
 * against the browser engine and runtime URI handlers.
 */
class SecurityInputPropertyTest {
    @Test
    fun arbitraryOmniboxInputsResolveOnlyToHttpSearchOrWebUris() {
        val random = Random(0x5249564fL)
        val alphabet = charArrayOf(
            'a', 'Z', '0', '/', '\\', ':', '@', '?', '#', '%', '.', '-', '_',
            ' ', '\t', '\n', '\u0000', '<', '>', '\'', '"', '[', ']', '{', '}',
            'ঃ', '例', 'é'
        )
        val hostileSeeds = listOf(
            "javascript:alert(1)", "data:text/html,<script>alert(1)</script>",
            "file:///etc/passwd", "content://settings/system", "intent://evil",
            "https://user:pass@example.com", "https://example.com:99999",
            "https://%00.example", "http://[::1", "////evil.example",
            "\u0000https://example.com", "https://example.com\\@evil.example"
        )
        val inputs = hostileSeeds + List(512) {
            buildString {
                repeat(random.nextInt(96)) { append(alphabet[random.nextInt(alphabet.size)]) }
            }
        }

        inputs.forEach { input ->
            val resolved = NavigationInputResolver.resolve(input)
            assertFalse("Resolver returned an empty URL for input=$input", resolved.isBlank())
            val uri = URI(resolved)
            assertTrue("Unexpected scheme for input=$input -> $resolved",
                uri.scheme.equals("http", true) || uri.scheme.equals("https", true))
            assertNotNull("Missing host for input=$input -> $resolved", uri.host)
            assertFalse("Credential-bearing URL escaped resolver for input=$input -> $resolved",
                uri.rawUserInfo != null)
        }
    }

    @Test
    fun arbitraryDownloadNamesCannotEscapeManagedDirectoryOrCleanupNamespace() {
        val random = Random(0x444f574eL)
        val alphabet = charArrayOf(
            'a', 'Z', '0', '/', '\\', ':', ';', '%', '.', '-', '_',
            ' ', '\t', '\n', '\u0000', '<', '>', '\'', '"', 'ঃ', '例', 'é'
        )
        val hostileSeeds = listOf(
            "../../private.txt", "..", ".", "/", "\\", "C:\\Windows\\win.ini",
            ".rivo-evil.part", "filename*=UTF-8''../../escape.apk",
            "a".repeat(500), "\u0000/../../"
        )
        val inputs = hostileSeeds + List(512) {
            buildString {
                repeat(random.nextInt(160)) { append(alphabet[random.nextInt(alphabet.size)]) }
            }
        }

        inputs.forEachIndexed { index, input ->
            val safe = DownloadFileWriter.sanitizeFilename(null, input, index.toLong())
            assertTrue("Unsafe path separator survived: $input -> $safe",
                safe.none { it == '/' || it == '\\' })
            assertTrue("Filename is blank or too long: $input -> $safe",
                safe.isNotBlank() && safe.length <= 120)
            assertFalse("Dot path component survived: $input -> $safe", safe == "." || safe == "..")
            assertFalse("Cleanup staging namespace collision: $input -> $safe",
                safe.startsWith(".rivo-") && safe.endsWith(".part"))
        }
    }
}
