package com.example.originos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OriginColorScheme = darkColorScheme(
    primary = Color(0xFF9BB8FF),
    onPrimary = Color(0xFF06121F),
    secondary = Color(0xFFB7A6FF),
    background = Color(0xFF050507),
    onBackground = Color(0xFFF4F5FA),
    surface = Color(0xFF111116),
    onSurface = Color(0xFFF4F5FA),
    surfaceVariant = Color(0xFF1A1A22),
    onSurfaceVariant = Color(0xFFB7B8C4),
    outline = Color(0xFF3A3B46),
)

@Composable
fun OriginTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OriginColorScheme,
        typography = Typography(),
        content = content,
    )
}
