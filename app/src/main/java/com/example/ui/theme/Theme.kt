package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TitaniumDarkScheme = darkColorScheme(
    primary = TitaniumAccent,
    onPrimary = Color.Black,
    primaryContainer = Color(0x1F38BDF8),
    onPrimaryContainer = TitaniumAccent,

    secondary = AppleOrange,
    onSecondary = Color.Black,
    secondaryContainer = AppleOrangeSubtle,
    onSecondaryContainer = AppleOrange,

    background = TitaniumBackground,
    onBackground = AppleTextPrimary,

    surface = TitaniumSurface,
    onSurface = AppleTextPrimary,
    surfaceVariant = TitaniumSurfaceElevated,
    onSurfaceVariant = AppleTextSecondary,

    outline = TitaniumBorder,
    outlineVariant = TitaniumDivider,
    error = AppleRed
)

private val PearlLightScheme = lightColorScheme(
    primary = PearlAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0x1F0071E3),
    onPrimaryContainer = PearlAccent,

    secondary = AppleOrange,
    onSecondary = Color.White,
    secondaryContainer = AppleOrangeSubtle,
    onSecondaryContainer = AppleOrange,

    background = PearlBackground,
    onBackground = PearlTextPrimary,

    surface = PearlSurface,
    onSurface = PearlTextPrimary,
    surfaceVariant = PearlSurfaceElevated,
    onSurfaceVariant = PearlTextSecondary,

    outline = PearlBorder,
    outlineVariant = PearlDivider,
    error = AppleRed
)

@Composable
fun GateiAiTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.TITANIUM_LUXE,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeStyle.isLight) PearlLightScheme else TitaniumDarkScheme
    MaterialTheme(
        colorScheme = colorScheme,
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
    GateiAiTheme(themeStyle = AppThemeStyle.TITANIUM_LUXE, content = content)
}
