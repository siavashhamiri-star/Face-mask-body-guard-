package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberTeal,
    onPrimary = Color.Black,
    primaryContainer = CyberTealGlow.copy(alpha = 0.2f),
    onPrimaryContainer = CyberTeal,
    secondary = SecurityGreen,
    onSecondary = Color.Black,
    secondaryContainer = SecurityGreenGlow.copy(alpha = 0.2f),
    onSecondaryContainer = SecurityGreen,
    tertiary = CyberViolet,
    onTertiary = Color.White,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    error = RecordingRed,
    onError = Color.White
)

@Composable
fun FaceGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FaceGuardTheme(content = content)
}
