package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MembershipTier
import com.example.model.Review
import com.example.model.SubscriptionStatus
import com.example.model.VerificationLevel
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = DarkBorder,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else modifier

    Box(
        modifier = clickableModifier
            .clip(shape)
            .background(DarkSurfaceVariant.copy(alpha = 0.75f))
            .border(1.dp, borderColor, shape)
            .padding(12.dp),
        content = content
    )
}

@Composable
fun OnlineIndicator(isOnline: Boolean, modifier: Modifier = Modifier) {
    if (isOnline) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC111116))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676).copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "ONLINE",
                color = Color(0xFF00E676),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Neon Green Shield Verified Member Badge - NEVER PURCHASABLE
 */
@Composable
fun VerifiedMemberBadge(modifier: Modifier = Modifier, compact: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x2E00E676))
            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("badge_verified_member")
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Verified Member",
            tint = Color(0xFF00E676),
            modifier = Modifier.size(if (compact) 11.dp else 13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = if (compact) "VERIFIED" else "VERIFIED MEMBER",
            color = Color(0xFF00E676),
            fontSize = if (compact) 8.5.sp else 9.5.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.3.sp
        )
    }
}

/**
 * Dynamic Verified / Premium Unified Badge (Firestore Synced)
 * Dynamically displays based on membership status & verification level
 */
@Composable
fun VerifiedPremiumBadge(
    membershipTier: MembershipTier,
    verificationLevel: VerificationLevel = VerificationLevel.NONE,
    isGold: Boolean = false,
    isFeatured: Boolean = false,
    isSubscriptionActive: Boolean = false,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isVerified = verificationLevel != VerificationLevel.NONE
    val isPremiumTier = membershipTier != MembershipTier.BASIC_FREE || isGold || isSubscriptionActive

    if (!isVerified && !isPremiumTier && !isFeatured) return

    val (badgeBg, borderBrush, badgeText, badgeIcon, textColor) = when {
        // Active Friends with Benefits / Pro Hookup Radar Pass
        membershipTier == MembershipTier.FRIENDS_WITH_BENEFITS || (isSubscriptionActive && membershipTier == MembershipTier.BASIC_FREE) -> {
            val bg = Brush.horizontalGradient(listOf(Color(0x44FF1493), Color(0x337C4DFF)))
            val border = Brush.horizontalGradient(listOf(Color(0xFFFF1493), Color(0xFFB388FF)))
            val text = if (compact) "FWB VIP" else "FWB VIP PASS"
            BadgeTuple(bg, border, text, Icons.Default.Favorite, Color(0xFFFF80BF))
        }
        // Combined Verified + VIP / Elite
        isVerified && (membershipTier == MembershipTier.VIP || membershipTier == MembershipTier.UPMARKET_EXCLUSIVE) -> {
            val bg = Brush.horizontalGradient(listOf(Color(0x3300E676), Color(0x44E31999)))
            val border = Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFFE2E8F0)))
            val text = if (compact) "VERIFIED VIP" else "VERIFIED VIP PLATINUM"
            BadgeTuple(bg, border, text, Icons.Default.Diamond, Color(0xFFE2E8F0))
        }
        // Combined Verified + Gold / Premium
        isVerified && (membershipTier == MembershipTier.GOLD || membershipTier == MembershipTier.PREMIUM || isGold) -> {
            val bg = Brush.horizontalGradient(listOf(Color(0x3300E676), Color(0x44FFD700)))
            val border = Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFFFFD700)))
            val text = if (compact) "VERIFIED GOLD" else "VERIFIED GOLD MEMBER"
            BadgeTuple(bg, border, text, Icons.Default.WorkspacePremium, Color(0xFFFFD700))
        }
        // Combined Verified + Agency / Venue
        isVerified && (membershipTier == MembershipTier.ESCORT_AGENCY || membershipTier == MembershipTier.NIGHTLIFE_VENUE) -> {
            val bg = Brush.horizontalGradient(listOf(Color(0x3300E676), Color(0x3300E5FF)))
            val border = Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00E5FF)))
            val text = if (compact) "VERIFIED AGENCY" else "VERIFIED AGENCY / VENUE"
            BadgeTuple(bg, border, text, Icons.Default.Business, Color(0xFF00E5FF))
        }
        // Verified Only
        isVerified -> {
            val bg = Brush.linearGradient(listOf(Color(0x3300E676), Color(0x1A00E676)))
            val border = Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00C853)))
            val text = if (compact) "VERIFIED" else "18+ VERIFIED ID"
            BadgeTuple(bg, border, text, Icons.Default.VerifiedUser, Color(0xFF00E676))
        }
        // VIP Platinum Only
        membershipTier == MembershipTier.VIP || membershipTier == MembershipTier.UPMARKET_EXCLUSIVE -> {
            val bg = Brush.horizontalGradient(listOf(Color(0x33E2E8F0), Color(0x44E31999)))
            val border = Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFFF52AF)))
            val text = if (compact) "VIP" else "VIP PLATINUM"
            BadgeTuple(bg, border, text, Icons.Default.Diamond, Color(0xFFE2E8F0))
        }
        // Gold / Premium Only
        membershipTier == MembershipTier.GOLD || membershipTier == MembershipTier.PREMIUM || isGold -> {
            val bg = Brush.linearGradient(listOf(Color(0x33FFD700), Color(0x22FFA000)))
            val border = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFAB00)))
            val text = if (compact) "GOLD" else "GOLD VIP"
            BadgeTuple(bg, border, text, Icons.Default.WorkspacePremium, Color(0xFFFFD700))
        }
        // Featured Spotlight
        isFeatured -> {
            val bg = Brush.linearGradient(listOf(Color(0x33FFEA00), Color(0x22E31999)))
            val border = Brush.linearGradient(listOf(Color(0xFFFFEA00), Color(0xFFE31999)))
            val text = "FEATURED"
            BadgeTuple(bg, border, text, Icons.Default.LocalFireDepartment, Color(0xFFFFEA00))
        }
        // Default Tier
        else -> {
            val tierColor = Color(membershipTier.badgeColorHex)
            val bg = Brush.linearGradient(listOf(tierColor.copy(alpha = 0.25f), tierColor.copy(alpha = 0.1f)))
            val border = Brush.linearGradient(listOf(tierColor, tierColor.copy(alpha = 0.8f)))
            val text = membershipTier.badgeLabel
            BadgeTuple(bg, border, text, Icons.Default.Stars, tierColor)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .border(BorderStroke(1.dp, borderBrush), RoundedCornerShape(6.dp))
            .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 3.dp)
            .testTag("profile_verified_premium_badge")
    ) {
        Icon(
            imageVector = badgeIcon,
            contentDescription = badgeText,
            tint = textColor,
            modifier = Modifier.size(if (compact) 11.dp else 13.dp)
        )
        Spacer(modifier = Modifier.width(3.5.dp))
        Text(
            text = badgeText,
            color = textColor,
            fontSize = if (compact) 8.5.sp else 9.5.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.4.sp
        )
    }
}

/**
 * Visual "PREMIUM VIP" Badge for active Firestore subscribers
 */
@Composable
fun PremiumSubscriptionBadge(
    tier: MembershipTier,
    isActive: Boolean = true,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "premium_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val tierColor = when (tier) {
        MembershipTier.FRIENDS_WITH_BENEFITS -> PinkPrimary
        MembershipTier.VIP, MembershipTier.UPMARKET_EXCLUSIVE -> Color(0xFFE2E8F0)
        MembershipTier.GOLD, MembershipTier.PREMIUM -> Color(0xFFFFD700)
        MembershipTier.ESCORT_AGENCY -> Color(0xFF00E5FF)
        else -> PinkPrimary
    }

    Surface(
        color = tierColor.copy(alpha = 0.18f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.2.dp, tierColor.copy(alpha = glowAlpha)),
        modifier = modifier.testTag("premium_subscription_active_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 3.5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Active Premium Subscription",
                tint = tierColor,
                modifier = Modifier.size(if (compact) 12.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (compact) "PREMIUM" else "PREMIUM VIP",
                color = tierColor,
                fontSize = if (compact) 9.sp else 10.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Visual Radar Animation Overlay for Map Near Me & Hookup Radar views
 * Simulates real-time 360-degree rotating radar scan beam, pulsing sonar range rings, and range markers.
 */
@Composable
fun RadarScanOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true,
    rangeKm: Int = 50
) {
    if (!isScanning) return

    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    
    // Rotating sweep angle: 0 to 360 degrees
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Pulsing sonar wave expanding outwards
    val pulseRadiusFraction by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("radar_animation_overlay")
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = kotlin.math.min(size.width, size.height) * 0.48f

        // Concentric Distance Rings (10km, 25km, 50km)
        val ringFractions = listOf(0.33f, 0.66f, 1.0f)
        ringFractions.forEach { fraction ->
            drawCircle(
                color = Color(0x33E31999),
                radius = maxRadius * fraction,
                center = center,
                style = Stroke(width = 1.2f)
            )
        }

        // Crosshairs
        drawLine(
            color = Color(0x22E31999),
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 1f
        )
        drawLine(
            color = Color(0x22E31999),
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 1f
        )

        // Pulsing Sonar Ring Wave
        drawCircle(
            color = Color(0xFFFF1493).copy(alpha = pulseAlpha * 0.5f),
            radius = maxRadius * pulseRadiusFraction,
            center = center,
            style = Stroke(width = 2.5f)
        )

        // Rotating Radar Sweep Beam Line & Gradient Sector
        rotate(degrees = sweepAngle, pivot = center) {
            // Radar sweeping line
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0x00E31999), Color(0xFFFF1493)),
                    startX = center.x,
                    endX = center.x + maxRadius
                ),
                start = center,
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 2.5f
            )

            // Soft sweep trail arc (60 degree gradient fan)
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0x40E31999),
                    0.15f to Color(0x00E31999),
                    1.0f to Color(0x00E31999),
                    center = center
                ),
                startAngle = -60f,
                sweepAngle = 60f,
                useCenter = true,
                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
            )
        }

        // Center Blip Point
        drawCircle(
            color = Color(0xFFFF1493),
            radius = 5.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = center
        )
    }
}

private data class BadgeTuple(
    val background: Brush,
    val border: Brush,
    val text: String,
    val icon: ImageVector,
    val textColor: Color
)

/**
 * 5 Star Rating Badge - Neon Dark Yellow
 */
@Composable
fun FiveStarRatingBadge(rating: Float = 5.0f, reviewCount: Int? = null, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33FFD700))
            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "5 Star Rating",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = String.format("%.1f ★", rating),
            color = Color(0xFFFFD700),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                color = Color(0xCCFFD700),
                fontSize = 8.5.sp
            )
        }
    }
}

/**
 * Featured Spotlight Badge - Neon Yellow
 */
@Composable
fun FeaturedBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33FFEA00))
            .border(1.dp, Color(0xFFFFEA00), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Featured",
            tint = Color(0xFFFFEA00),
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "FEATURED",
            color = Color(0xFFFFEA00),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Sponsored Promotion Badge - Light Blue
 */
@Composable
fun SponsoredBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x3300D2FF))
            .border(1.dp, Color(0xFF00D2FF), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = "Sponsored",
            tint = Color(0xFF00D2FF),
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "SPONSORED",
            color = Color(0xFF00D2FF),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * VIP Platinum Badge
 */
@Composable
fun VipBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33E2E8F0))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = "VIP",
            tint = Color(0xFFE2E8F0),
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "VIP PLATINUM",
            color = Color(0xFFE2E8F0),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Membership Tier Badges
 */
@Composable
fun TierBadge(tier: MembershipTier, modifier: Modifier = Modifier) {
    val badgeColor = Color(tier.badgeColorHex)
    val icon = when (tier) {
        MembershipTier.VIP, MembershipTier.UPMARKET_EXCLUSIVE -> Icons.Default.Diamond
        MembershipTier.GOLD -> Icons.Default.WorkspacePremium
        MembershipTier.PREMIUM -> Icons.Default.Stars
        MembershipTier.ESCORT_AGENCY, MembershipTier.NIGHTLIFE_VENUE -> Icons.Default.Business
        else -> Icons.Default.Person
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor.copy(alpha = 0.2f))
            .border(0.9.dp, badgeColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tier.badgeLabel,
            tint = badgeColor,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = tier.badgeLabel,
            color = badgeColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VerificationBadge(level: VerificationLevel) {
    when (level) {
        VerificationLevel.FULL -> VerifiedMemberBadge(compact = true)
        VerificationLevel.PHOTO -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x2600D2FF))
                    .border(0.8.dp, Color(0xFF00D2FF), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Photo Verified",
                    tint = Color(0xFF00D2FF),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "PHOTO",
                    color = Color(0xFF00D2FF),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        VerificationLevel.BASIC -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x2600E676))
                    .border(0.8.dp, Color(0xFF00E676), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Basic Verified",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "BASIC",
                    color = Color(0xFF00E676),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        VerificationLevel.NONE -> {}
    }
}

@Composable
fun PinkGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0xFF2C2C3D)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .height(50.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = PinkPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) PinkGradient else Brush.linearGradient(listOf(Color(0xFF333344), Color(0xFF222233))))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun DarkOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderPink),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextWhite,
            containerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(label, color = TextMuted, fontSize = 9.5.sp)
        }
    }
}

@Composable
fun RatingBar(rating: Float, reviewCount: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ReviewCardItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (review.rating == 5) Color(0x44FFD700) else DarkBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PinkGlow)
                            .border(1.dp, PinkPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.reviewerName.take(1).uppercase(),
                            color = PinkPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.reviewerName,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (review.isVerifiedClient) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Client",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = review.createdAt,
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                color = TextLight,
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            if (!review.responseText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B1B26))
                        .border(0.8.dp, DarkBorderPink, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Creator Response:",
                            color = PinkPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = review.responseText,
                            color = TextLight,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }
    }
}
