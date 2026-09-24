package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Signature Deep Pinks & Accents (#E31999)
val PinkPrimary = Color(0xFFE31999)
val PinkDeep = Color(0xFFE31999)
val PinkSecondary = Color(0xFFC71485)
val PinkLight = Color(0xFFFF52AF)
val PinkGlow = Color(0x33E31999)
val PinkSubtle = Color(0x1AE31999)
val PinkDark = Color(0xFF9E0B68)

// Dark Charcoal Luxury Neutrals & Surfaces
val CharcoalVoid = Color(0xFF0A0A0E)
val CharcoalBase = Color(0xFF101016)
val CharcoalSurface = Color(0xFF161620)
val CharcoalCard = Color(0xFF1E1E2A)
val CharcoalElevated = Color(0xFF262636)
val CharcoalBorder = Color(0xFF2E2E40)
val CharcoalBorderHighlight = Color(0xFF3E3E54)

// Standard Surface Aliases for Compatibility
val DarkBackground = Color(0xFF0D0D13)
val DarkSurface = Color(0xFF14141E)
val DarkSurfaceVariant = Color(0xFF1C1C28)
val DarkSurfaceElevated = Color(0xFF252536)
val DarkBorder = Color(0xFF2C2C3E)
val DarkBorderPink = Color(0x4DE31999)

// Text Neutrals
val TextWhite = Color(0xFFFFFFFF)
val TextLight = Color(0xFFF5F5FA)
val TextMuted = Color(0xFFA4A4BA)
val TextDark = Color(0xFF6E6E84)

// Verification & Status Badges
val BadgeVerifiedGreen = Color(0xFF00E676)
val BadgePhotoVerifiedBlue = Color(0xFF00D2FF)
val BadgeGold = Color(0xFFFFD700)
val BadgeVIPPlatinum = Color(0xFFE2E8F0)
val BadgeFeaturedPink = Color(0xFFE31999)

// Luxury Gradients
val PinkGradient = Brush.horizontalGradient(
    colors = listOf(PinkSecondary, PinkPrimary)
)
val PinkPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE31999), Color(0xFF9C27B0))
)
val CharcoalCardGradient = Brush.verticalGradient(
    colors = listOf(CharcoalCard.copy(alpha = 0.90f), CharcoalSurface.copy(alpha = 0.98f))
)
val CardGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF20202E).copy(alpha = 0.85f), Color(0xFF14141E).copy(alpha = 0.95f))
)
val HeroBannerGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0x990D0D13), Color(0xFF0D0D13))
)

