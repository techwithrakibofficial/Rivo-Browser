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
            secondary = Color(0xFF567D9F),
            background = Color(0xFFF3F6FA),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF172333),
            error = Color(0xFFBA1A1A)
        )
        BrowserAppearance.DARK -> darkColorScheme(
            primary = Color(0xFF9CCBFF),
            secondary = Color(0xFFB8CDE2),
            background = Color(0xFF0D1118),
            surface = Color(0xFF171E28),
            onSurface = Color(0xFFE8EEF7),
            error = Color(0xFFFFB4AB)
        )
        BrowserAppearance.GLASS -> darkColorScheme(
            primary = Color(0xFFB7D5FF),
            secondary = Color(0xFFC4D8F0),
            background = Color(0xFF101827),
            surface = Color(0xCC26354B),
            onSurface = Color(0xFFF3F7FF),
            error = Color(0xFFFFB4AB)
        )
    }
}

/**
 * Premium Glass deliberately uses translucent semantic surfaces without
 * expensive real-time blur. This keeps a readable fallback on low-RAM devices.
 */
