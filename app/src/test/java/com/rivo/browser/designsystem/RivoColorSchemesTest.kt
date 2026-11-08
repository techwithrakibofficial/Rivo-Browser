package com.rivo.browser.designsystem

import com.rivo.browser.BrowserAppearance
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RivoColorSchemesTest {
    @Test
    fun allAppearanceModesExposeDistinctSemanticSchemes() {
        val dark = RivoColorSchemes.forAppearance(BrowserAppearance.DARK)
        val light = RivoColorSchemes.forAppearance(BrowserAppearance.LIGHT)
        val glass = RivoColorSchemes.forAppearance(BrowserAppearance.GLASS)

        assertEquals(Color(0xFF0D1118), dark.background)
        assertEquals(Color(0xFFF3F6FA), light.background)
        assertEquals(Color(0xFF101827), glass.background)
        assertTrue(glass.surface.alpha < 1f)
        assertEquals(1f, dark.surface.alpha, 0f)
        assertEquals(1f, light.surface.alpha, 0f)
    }

    @Test
    fun lightSecondaryMeetsNormalTextWcagAaOnWhite() {
        val light = RivoColorSchemes.forAppearance(BrowserAppearance.LIGHT)
        assertTrue(contrastRatio(light.secondary, light.surface) >= 4.5)
    }

    private fun contrastRatio(foreground: androidx.compose.ui.graphics.Color, background: androidx.compose.ui.graphics.Color): Double {
        val a = relativeLuminance(foreground)
        val b = relativeLuminance(background)
        return (maxOf(a, b) + 0.05) / (minOf(a, b) + 0.05)
    }

    private fun relativeLuminance(color: androidx.compose.ui.graphics.Color): Double {
        fun linear(channel: Float): Double {
            val value = channel.toDouble()
            return if (value <= 0.04045) value / 12.92 else Math.pow((value + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)
    }
}
