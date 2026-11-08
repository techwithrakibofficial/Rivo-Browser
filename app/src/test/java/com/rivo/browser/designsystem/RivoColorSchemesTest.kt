package com.rivo.browser.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.rivo.browser.BrowserAppearance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

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
    fun lightSemanticTextRolesMeetNormalTextAaOnWhiteSurface() {
        val scheme = RivoColorSchemes.forAppearance(BrowserAppearance.LIGHT)
        assertAaContrast(scheme.onSurface, scheme.surface, "Light onSurface")
        assertAaContrast(scheme.primary, scheme.surface, "Light primary")
        assertAaContrast(scheme.secondary, scheme.surface, "Light secondary")
        assertAaContrast(scheme.error, scheme.surface, "Light error")
    }

    @Test
    fun darkSemanticTextRolesMeetNormalTextAaOnSurface() {
        val scheme = RivoColorSchemes.forAppearance(BrowserAppearance.DARK)
        assertAaContrast(scheme.onSurface, scheme.surface, "Dark onSurface")
        assertAaContrast(scheme.primary, scheme.surface, "Dark primary")
        assertAaContrast(scheme.secondary, scheme.surface, "Dark secondary")
        assertAaContrast(scheme.error, scheme.surface, "Dark error")
    }

    @Test
    fun glassSemanticTextRolesMeetNormalTextAaOnCompositedSurface() {
        val scheme = RivoColorSchemes.forAppearance(BrowserAppearance.GLASS)
        val compositedSurface = scheme.surface.compositeOver(scheme.background)
        assertAaContrast(scheme.onSurface, compositedSurface, "Glass onSurface")
        assertAaContrast(scheme.primary, compositedSurface, "Glass primary")
        assertAaContrast(scheme.secondary, compositedSurface, "Glass secondary")
        assertAaContrast(scheme.error, compositedSurface, "Glass error")
    }

    private fun assertAaContrast(foreground: Color, background: Color, label: String) {
        val foregroundLuminance = relativeLuminance(foreground)
        val backgroundLuminance = relativeLuminance(background)
        val lighter = maxOf(foregroundLuminance, backgroundLuminance)
        val darker = minOf(foregroundLuminance, backgroundLuminance)
        val ratio = (lighter + 0.05) / (darker + 0.05)
        assertTrue("$label contrast was ${"%.2f".format(ratio)}:1; expected at least 4.5:1", ratio >= 4.5)
    }

    private fun relativeLuminance(color: Color): Double {
        fun linearize(channel: Float): Double {
            val value = channel.toDouble()
            return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * linearize(color.red) +
            0.7152 * linearize(color.green) +
            0.0722 * linearize(color.blue)
    }
}
