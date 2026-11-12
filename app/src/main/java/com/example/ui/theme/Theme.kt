package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChessDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = OnGold,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldLight,
    onSecondary = OnGold,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = MoveBrilliant,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = MoveBlunder,
    onError = Color.White
)

private val ChessLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldPrimary,
    onSecondary = OnGold,
    background = Color(0xFFF9F6F0),
    onBackground = Color(0xFF1E1A17),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1A17),
    surfaceVariant = Color(0xFFEDE6DF),
    onSurfaceVariant = Color(0xFF5E544C),
    outline = Color(0xFFD6CBC0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Chess Master uses a curated regal theme; dark theme is the primary aesthetic
    val colorScheme = if (darkTheme) ChessDarkColorScheme else ChessDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChessTypography,
        content = content
    )
}
