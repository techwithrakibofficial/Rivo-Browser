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
    fun preservesExplicitHttpsScheme() {
        assertEquals("https://example.com/secure", NavigationInputResolver.resolve("https://example.com/secure"))
    }

    @Test
    fun preservesValidPortOnInternationalizedDomain() {
        assertEquals(
            "https://xn--bcher-kva.de:8443/path",
            NavigationInputResolver.resolve("https://bücher.de:8443/path")
        )
    }

    @Test
    fun rejectsOutOfRangePortAsSearchInsteadOfNavigation() {
        assertEquals(
            "https://duckduckgo.com/?q=http%3A%2F%2Fexample.com%3A70000",
            NavigationInputResolver.resolve("http://example.com:70000")
        )
    }

    @Test
    fun sendsCredentialBearingUrlsToSearchInsteadOfPersistableNavigation() {
        assertEquals(
            "https://duckduckgo.com/?q=https%3A%2F%2Fuser%3Apass%40example.com",
            NavigationInputResolver.resolve("https://user:pass@example.com")
        )
    }

    @Test
    fun emptyInputStaysEmpty() {
        assertEquals("", NavigationInputResolver.resolve("  "))
    }
    @Test
    fun convertsInternationalizedDomainNamesToAscii() {
        assertEquals(
            "https://xn--bcher-kva.de/path",
            NavigationInputResolver.resolve("https://bücher.de/path")
        )
    }

    @Test
    fun addsHttpsForInternationalizedDomainNames() {
        assertEquals(
            "https://xn--r8jz45g.xn--zckzah",
            NavigationInputResolver.resolve("例え.テスト")
        )
    }

    @Test
    fun preservesEncodedPathQueryAndFragmentDuringIdnConversion() {
        assertEquals(
            "https://xn--bcher-kva.de/a%2Fb?q=a%2Fb#section",
            NavigationInputResolver.resolve("https://bücher.de/a%2Fb?q=a%2Fb#section")
        )
    }

}
