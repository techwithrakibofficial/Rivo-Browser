package com.rivo.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationInputResolverTest {
    @Test
    fun addsHttpsForDomain() {
        assertEquals("https://example.com", NavigationInputResolver.resolve("example.com"))
    }

    @Test
    fun preservesExplicitHttpScheme() {
        assertEquals("http://example.com/path", NavigationInputResolver.resolve("http://example.com/path"))
    }

    @Test
    fun searchesPlainText() {
        assertEquals(
            "https://duckduckgo.com/?q=hello+world",
            NavigationInputResolver.resolve("hello world")
        )
    }

    @Test
    fun doesNotOpenUnsupportedSchemes() {
        assertEquals(
            "https://duckduckgo.com/?q=javascript%3Aalert%281%29",
            NavigationInputResolver.resolve("javascript:alert(1)")
        )
    }

    @Test
    fun emptyInputStaysEmpty() {
        assertEquals("", NavigationInputResolver.resolve("  "))
    }
}
