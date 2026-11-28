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
            primary = Color(0xFF9CCBFF),
            secondary = Color(0xFFB8CDE2),
            background = Color(0xFF0D1118),
            surface = Color(0xFF171E28),
            onSurface = Color(0xFFE8EEF7),
            surfaceVariant = Color(0xFF222D3B),
            onSurfaceVariant = Color(0xFFB9C7D8),
            error = Color(0xFFFFB4AB)
        )
        BrowserAppearance.GLASS -> darkColorScheme(
            primary = Color(0xFFE3EAF2),
            secondary = Color(0xFFC9D2DC),
            background = Color(0xFF151B22),
            surface = Color(0x667F8B98),
            onSurface = Color(0xFFF5F7FA),
            surfaceVariant = Color(0x557C8998),
            onSurfaceVariant = Color(0xFFD9E0E8),
            outline = Color(0x99D4DCE5),
            outlineVariant = Color(0x557D8997),
            error = Color(0xFFFFB4AB)
        )
    }
}

/**
 * Premium Glass deliberately uses translucent semantic surfaces without
 * expensive real-time blur. This keeps a readable fallback on low-RAM devices.
 */
