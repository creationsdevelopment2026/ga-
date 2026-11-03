package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(val label: String, val isLight: Boolean) {
    TITANIUM_LUXE("Titanium Luxe (Dark)", false),
    PEARL_MINIMAL("Pearl Ivory (Light)", true),
    STEALTH_MONO("Stealth Silver", false)
}

// ==========================================
// Titanium Luxe (Refined Apple Intelligence Dark)
// ==========================================
val TitaniumBackground = Color(0xFF0C0D10)
val TitaniumSurface = Color(0xFF16171D)
val TitaniumSurfaceElevated = Color(0xFF1F2027)
val TitaniumSurfaceHighlight = Color(0xFF2B2D38)
val TitaniumBorder = Color(0x1FFFFFFF)
val TitaniumDivider = Color(0x14FFFFFF)
val TitaniumAccent = Color(0xFF38BDF8)           // Sky / Apple Electric
val TitaniumAccentGradient = listOf(Color(0xFF6366F1), Color(0xFF38BDF8), Color(0xFFA855F7))

// ==========================================
// Pearl Ivory (Clean Apple Minimalist Light)
// ==========================================
val PearlBackground = Color(0xFFF6F7F9)
val PearlSurface = Color(0xFFFFFFFF)
val PearlSurfaceElevated = Color(0xFFFFFFFF)
val PearlSurfaceHighlight = Color(0xFFEAEBED)
val PearlBorder = Color(0x14000000)
val PearlDivider = Color(0x0F000000)
val PearlAccent = Color(0xFF0071E3)             // Apple Cupertino Blue
val PearlTextPrimary = Color(0xFF1D1D1F)
val PearlTextSecondary = Color(0xFF6E6E73)
val PearlTextTertiary = Color(0xFF86868B)

// ==========================================
// Universal Accents
// ==========================================
val AppleBlue = Color(0xFF0071E3)
val AppleBlueSubtle = Color(0x1F0071E3)
val AppleGreen = Color(0xFF34C759)
val AppleGreenSubtle = Color(0x1F34C759)
val AppleOrange = Color(0xFFFF9500)
val AppleOrangeSubtle = Color(0x1FFF9500)
val AppleRed = Color(0xFFFF3B30)
val AppleRedSubtle = Color(0x1FFF3B30)
val AppleIndigo = Color(0xFF5856D6)
val AppleIndigoSubtle = Color(0x1F5856D6)

// Text
val AppleTextPrimary = Color(0xFFFFFFFF)
val AppleTextSecondary = Color(0xFF98989F)
val AppleTextTertiary = Color(0xFF636366)
val AppleTextPlaceholder = Color(0xFF55555C)

// Aliases for compatibility
val AppleBackground = TitaniumBackground
val AppleSurface = TitaniumSurface
val AppleSurfaceElevated = TitaniumSurfaceElevated
val AppleSurfaceHighlight = TitaniumSurfaceHighlight
val AppleSurfaceBorder = TitaniumBorder
val AppleDivider = TitaniumDivider

val TacticalObsidian = AppleBackground
val TacticalSurface = AppleSurface
val TacticalSurfaceVariant = AppleSurfaceElevated
val TacticalCardBorder = AppleSurfaceBorder
val ArcCyan = AppleBlue
val ArcCyanContainer = AppleBlueSubtle
val TacticalAmber = AppleOrange
val TacticalAmberContainer = AppleOrangeSubtle
val TacticalEmerald = AppleGreen
val TacticalEmeraldContainer = AppleGreenSubtle
val TacticalCrimson = AppleRed
val TextPrimary = AppleTextPrimary
val TextSecondary = AppleTextSecondary
val TextMuted = AppleTextTertiary
