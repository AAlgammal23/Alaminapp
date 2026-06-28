package com.alamin.pharma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AlAminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = TealLight,
            secondary = TealDark,
            background = Color(0xFF1A1A1A),
            surface = Color(0xFF2C2C2C),
            onPrimary = White,
            onSecondary = White,
            onBackground = White,
            onSurface = White
        )
    } else {
        lightColorScheme(
            primary = TealLight,
            secondary = TealDark,
            background = Background,
            surface = White,
            onPrimary = White,
            onSecondary = White,
            onBackground = TextPrimary,
            onSurface = TextPrimary
        )
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
