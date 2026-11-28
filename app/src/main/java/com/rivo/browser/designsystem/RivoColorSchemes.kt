package com.rivo.browser.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.rivo.browser.BrowserAppearance

/** Central semantic palettes for all browser chrome surfaces and controls. */
object RivoColorSchemes {
    fun forAppearance(appearance: BrowserAppearance): ColorScheme = when (appearance) {
        BrowserAppearance.LIGHT -> lightColorScheme(
            primary = Color(0xFF315B85),
            secondary = Color(0xFF4D7192),
            background = Color(0xFFF3F6FA),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF172333),
            surfaceVariant = Color(0xFFE5EDF5),
            onSurfaceVariant = Color(0xFF46586B),
            error = Color(0xFFBA1A1A)
        )
        BrowserAppearance.DARK -> darkColorScheme(
            primary = Color(0xFFB69CFF),
            secondary = Color(0xFF73B8FF),
            background = Color(0xFF090B18),
            surface = Color(0xFF15152A),
            onSurface = Color(0xFFF1EEFF),
            surfaceVariant = Color(0xFF242442),
            onSurfaceVariant = Color(0xFFC9C6E6),
            error = Color(0xFFFFB4AB)
        )
        BrowserAppearance.GLASS -> darkColorScheme(
            primary = Color(0xFFD3BFFF),
            secondary = Color(0xFF8CCBFF),
            background = Color(0xFF100D25),
            surface = Color(0xE61D2340),
            onSurface = Color(0xFFF8F5FF),
            surfaceVariant = Color(0xD52D3654),
            onSurfaceVariant = Color(0xFFD9E0E8),
            outline = Color(0x99D4DCE5),
            outlineVariant = Color(0x997D8997),
            error = Color(0xFFFFB4AB)
        )
    }
}

/**
 * Premium Glass deliberately uses translucent semantic surfaces without
 * expensive real-time blur. This keeps a readable fallback on low-RAM devices.
 */
