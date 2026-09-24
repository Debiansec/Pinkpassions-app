package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.AppThemeMode
import com.example.model.MembershipTier
import com.example.model.SubscriptionStatus
import com.example.model.VerificationStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Profile & Settings Screen
 * Displays complete user information, VIP membership status, editable credentials,
 * interactive theme preferences selector, storage/cache clearing management, and platform shortcuts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val verificationStatus by viewModel.userVerificationStatus.collectAsState()
    val membershipTier by viewModel.userMembershipTier.collectAsState()

    // User details from DataStore/ViewModel
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val userCity by viewModel.userCity.collectAsState()
    val userBio by viewModel.userBio.collectAsState()

    // Theme & preferences
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val currentAccentColor by viewModel.accentColor.collectAsState()
    val isDiscreetMode by viewModel.isDiscreetMode.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsState()

    // Subscriptions & Memberships
    val userSubscription by viewModel.userSubscription.collectAsState()
    val isSubscriptionActive = userSubscription.status == SubscriptionStatus.ACTIVE &&
            userSubscription.membershipTier != MembershipTier.BASIC_FREE

    // Cache management
    val cacheSizeBytes by viewModel.cacheSizeBytes.collectAsState()
    val isClearingCache by viewModel.isClearingCache.collectAsState()

    // Dialog & Sheet States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showClearCacheConfirmDialog by remember { mutableStateOf(false) }
    var showResetAgeDialog by remember { mutableStateOf(false) }

    // Refresh cache size on screen entry
    LaunchedEffect(Unit) {
        viewModel.refreshCacheSize()
    }

    val formattedCacheSize = remember(cacheSizeBytes) {
        when {
            cacheSizeBytes >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f MB", cacheSizeBytes.toDouble() / (1024 * 1024))
            cacheSizeBytes >= 1024 -> String.format(java.util.Locale.US, "%.1f KB", cacheSizeBytes.toDouble() / 1024)
            cacheSizeBytes > 0 -> "$cacheSizeBytes B"
            else -> "0.0 KB"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("screen_profile"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP SCREEN TITLE HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Profile & Settings",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Manage your account, theme preferences & app storage",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    color = PinkSubtle,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PinkPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BadgeVerifiedGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIP ACTIVE",
                            color = PinkLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 1. MAIN USER PROFILE INFORMATION HERO CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.2.dp, DarkBorderPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("profile_user_info_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar with gradient border
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PinkPrimary, PinkPassionsColors.WinePink, Color(0xFFFF70A6))
                                    )
                                )
                                .padding(2.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userDisplayName.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString("")
                                        .ifEmpty { "PP" },
                                    color = PinkLight,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = userDisplayName,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                IconButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("button_edit_profile")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = PinkLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = userCity,
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = BadgeVerifiedGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, BadgeVerifiedGreen)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = BadgeVerifiedGreen,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "18+ Verified ID",
                                            color = BadgeVerifiedGreen,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (isSubscriptionActive) {
                                    PremiumSubscriptionBadge(
                                        tier = userSubscription.membershipTier,
                                        isActive = true,
                                        compact = true
                                    )
                                } else {
                                    Surface(
                                        color = BadgeGold.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, BadgeGold)
                                    ) {
                                        Text(
                                            text = "${membershipTier.title} Plan",
                                            color = BadgeGold,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bio Quote
                    Surface(
                        color = Color(0xFF13131D),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.6.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"$userBio\"",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 STAT METRICS TILES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(label = "Views", value = "3,420", color = TextWhite, modifier = Modifier.weight(1f))
                        MetricTile(label = "Favourites", value = "184", color = PinkPrimary, modifier = Modifier.weight(1f))
                        MetricTile(label = "Inquiries", value = "92", color = BadgeGold, modifier = Modifier.weight(1f))
                        MetricTile(label = "Credits", value = "R2,500", color = Color(0xFF00E676), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 1.5. USER SETTINGS & PRIVACY CARD (PROFILE VISIBILITY, SUBSCRIPTIONS, 50KM RADAR)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.2.dp, PinkPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("card_open_user_settings")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.activeSubscreen.value = "user_settings" }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PinkPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Settings & Privacy Preferences",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                            Text(
                                text = "Profile visibility, R99/mo VIP status & 50km radar",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Settings",
                        tint = PinkLight
                    )
                }
            }
        }

        // 2. SETTINGS SECTION: THEME PREFERENCES (MANDATORY)
        item {
            ProfileSectionHeader(
                title = "Theme & Appearance Preferences",
                subtitle = "Toggle between deep pinks, OLED pure black & light styles"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("profile_theme_settings_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "App Theme Mode",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Choose your preferred viewing contrast and color scheme",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // QUICK THEME TOGGLE: PINK PASSIONS DARK VS SULTRY LIGHT
                    Surface(
                        color = Color(0xFF13131D),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("theme_toggle_quick_switch_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (currentThemeMode == AppThemeMode.LIGHT) Color(0xFFFAF9FC) else PinkPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (currentThemeMode == AppThemeMode.LIGHT) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = if (currentThemeMode == AppThemeMode.LIGHT) PinkPrimary else TextWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (currentThemeMode == AppThemeMode.LIGHT) "Sultry Light Theme" else "Pink Passions Dark Mode",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (currentThemeMode == AppThemeMode.LIGHT) "Light background with signature pink accents" else "Signature deep pink #E31999 & dark charcoal",
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            Switch(
                                checked = currentThemeMode != AppThemeMode.LIGHT,
                                onCheckedChange = { isDark ->
                                    viewModel.setThemeMode(if (isDark) AppThemeMode.DARK else AppThemeMode.LIGHT)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextWhite,
                                    checkedTrackColor = PinkPrimary,
                                    uncheckedThumbColor = PinkPrimary,
                                    uncheckedTrackColor = Color(0xFFE0E0EC)
                                ),
                                modifier = Modifier.testTag("theme_toggle_quick_switch")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "All Appearance Modes",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // 4 THEME CHOICES GRID/COLUMN
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = currentThemeMode == mode
                        Surface(
                            onClick = { viewModel.setThemeMode(mode) },
                            color = if (isSelected) PinkSubtle else Color(0xFF13131D),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.8.dp,
                                color = if (isSelected) PinkPrimary else DarkBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("theme_option_${mode.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Visual color dot preview
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (mode) {
                                                    AppThemeMode.DARK -> Color(0xFF0E0E14)
                                                    AppThemeMode.AMOLED -> Color(0xFF000000)
                                                    AppThemeMode.LIGHT -> Color(0xFFFAFAFC)
                                                    AppThemeMode.SYSTEM -> Color(0xFF1E1E2C)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (mode == AppThemeMode.LIGHT) Color.Gray else PinkPrimary,
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = mode.title,
                                            color = if (isSelected) PinkLight else TextWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = mode.description,
                                            color = TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PinkPrimary,
                                        unselectedColor = TextMuted
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Brand Accent Color Selector
                    Text(
                        text = "Brand Accent Shade",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccentColorChip(
                            title = "Hot Pink",
                            color = Color(0xFFFF2A85),
                            isSelected = currentAccentColor == "HOT_PINK",
                            onClick = { viewModel.setAccentColor("HOT_PINK") },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorChip(
                            title = "Velvet Rose",
                            color = Color(0xFFFF70A6),
                            isSelected = currentAccentColor == "VELVET_ROSE",
                            onClick = { viewModel.setAccentColor("VELVET_ROSE") },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorChip(
                            title = "Luxury Gold",
                            color = Color(0xFFFFC107),
                            isSelected = currentAccentColor == "GOLD",
                            onClick = { viewModel.setAccentColor("GOLD") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. SETTINGS SECTION: CACHE & STORAGE MANAGEMENT (MANDATORY)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileSectionHeader(
                title = "Data & Storage Management",
                subtitle = "Manage cached images, temporary files and data privacy"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("profile_cache_settings_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PinkGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = null,
                                    tint = PinkPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Cached Application Data",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Coil images, discovery feeds & temporary caches",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Display formatted cache size badge
                        Surface(
                            color = Color(0xFF13131D),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.8.dp, DarkBorderPink)
                        ) {
                            Text(
                                text = formattedCacheSize,
                                color = PinkLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.refreshCacheSize() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextWhite
                            ),
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_refresh_cache_size")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recalculate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { showClearCacheConfirmDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = TextWhite
                            ),
                            enabled = !isClearingCache,
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("button_clear_cache_data")
                        ) {
                            if (isClearingCache) {
                                CircularProgressIndicator(
                                    color = TextWhite,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isClearingCache) "Clearing..." else "Clear Cache",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. PRIVACY, SAFETY & NOTIFICATIONS SECTION
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileSectionHeader(
                title = "Privacy, Security & Age Compliance",
                subtitle = "RSA 18+ regulations, incognito browsing and alerts"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Discreet Privacy Mode Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Discreet Incognito Mode",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Blurs notification previews & disables app screenshot captures",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                        Switch(
                            checked = isDiscreetMode,
                            onCheckedChange = { viewModel.setDiscreetMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextWhite,
                                checkedTrackColor = PinkPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("switch_discreet_mode")
                        )
                    }

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Notifications Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Push Notifications",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Instant alerts for chat messages, bookings & boutique deliveries",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                        Switch(
                            checked = isNotificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextWhite,
                                checkedTrackColor = PinkPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("switch_push_notifications")
                        )
                    }

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Reset 18+ Age Confirmation Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "18+ Age Gate Verification",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Reset legal age consent dialog to re-prompt on next launch",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                        OutlinedButton(
                            onClick = { showResetAgeDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF70A6)),
                            border = BorderStroke(1.dp, DarkBorderPink),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("button_reset_age_gate")
                        ) {
                            Text("Reset Gate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. CREATOR & ADVERTISER TOOLS
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileSectionHeader(
                title = "Creator & Advertiser Tools",
                subtitle = "Publish listings, verify identity and grow your reach"
            )

            AccountMenuItem(
                title = "Create New Advertiser Listing",
                subtitle = "Publish your independent escort or massage profile",
                icon = Icons.Default.AddCircle,
                badge = "5 Steps",
                onClick = { viewModel.activeSubscreen.value = "create_listing" },
                testTag = "menu_create_listing"
            )
            AccountMenuItem(
                title = "Verification Center",
                subtitle = "Submit photo ID & selfies for verified trust badges",
                icon = Icons.Default.VerifiedUser,
                badge = verificationStatus.name,
                onClick = { viewModel.activeSubscreen.value = "verification" }
            )
            AccountMenuItem(
                title = "Membership & Upgrade Plans",
                subtitle = "Feature your profile on homepage & top search positions",
                icon = Icons.Default.Star,
                badge = membershipTier.title,
                onClick = { viewModel.activeSubscreen.value = "membership" }
            )
            AccountMenuItem(
                title = "Advertising & Sponsored Banners",
                subtitle = "Manage banner campaigns & city top placements",
                icon = Icons.Default.Campaign,
                onClick = { viewModel.activeSubscreen.value = "advertising" }
            )
        }

        // 6. ADULT BOUTIQUE & ENTERTAINMENT
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileSectionHeader(
                title = "Directory & Lifestyle E-Commerce",
                subtitle = "Discreet shopping and entertainment directory"
            )

            AccountMenuItem(
                title = "Adult Boutique E-Commerce",
                subtitle = "Shop intimate devices, lingerie & wellness goods",
                icon = Icons.Default.ShoppingBag,
                onClick = { viewModel.activeSubscreen.value = "shop" }
            )
            AccountMenuItem(
                title = "Gentlemen's Entertainment Directory",
                subtitle = "Clubs, lounges, VIP venues & spa massage listings",
                icon = Icons.Default.Nightlife,
                onClick = { viewModel.activeSubscreen.value = "business_directory" }
            )
            AccountMenuItem(
                title = "Upcoming Events & Parties",
                subtitle = "Masquerade galas, VIP launches & nightlife events",
                icon = Icons.Default.Event,
                onClick = { viewModel.activeSubscreen.value = "events" }
            )
        }

        // 7. SYSTEM, LEGAL & LOGOUT
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileSectionHeader(
                title = "System & Support",
                subtitle = "Administration, compliance and platform contact"
            )

            AccountMenuItem(
                title = "Admin Control & Moderation Panel",
                subtitle = "Platform audit logs, profile approvals & KPIs",
                icon = Icons.Default.AdminPanelSettings,
                badge = "Admin",
                onClick = { viewModel.activeSubscreen.value = "admin" }
            )
            AccountMenuItem(
                title = "Safety, Terms & 2257 Compliance",
                subtitle = "Legal disclaimers, RSA privacy policy & guidelines",
                icon = Icons.Default.Gavel,
                onClick = { viewModel.showNotice("Pink Passions complies with RSA 18+ advertising & safety regulations.") }
            )
            AccountMenuItem(
                title = "Logout / Reset Session",
                subtitle = "Sign out and clear local session state",
                icon = Icons.Default.Logout,
                onClick = {
                    viewModel.hasConfirmedAge.value = false
                    viewModel.showNotice("Logged out. Please verify age upon next launch.")
                },
                testTag = "menu_logout_session"
            )
        }

        // 8. TECHNICAL SUPPORT & CONTACT BOX
        item {
            Spacer(modifier = Modifier.height(10.dp))
            ProfileSectionHeader(
                title = "Official Help & Support",
                subtitle = "Direct encrypted support desk"
            )
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TechnicalSupportBox(
                    onNotice = { viewModel.showNotice(it) }
                )
            }
        }

        // 9. SECURE PAYMENTS GATEWAY BADGES
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Accepted RSA Payment Gateways",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PayPalLogo()
                            PayFastLogo()
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SecurePaymentsBadge()
                }
            }
        }
    }

    // DIALOG: EDIT USER PROFILE INFORMATION
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(userDisplayName) }
        var editCity by remember { mutableStateOf(userCity) }
        var editBio by remember { mutableStateOf(userBio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text(
                    text = "Edit Profile Information",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Update your public display name, location and headline.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Display Name", color = PinkLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_display_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("City / Province", color = PinkLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_city")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Headline / Bio", color = PinkLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_bio")
                    )
                }
            },
            confirmButton = {
                PinkGradientButton(
                    text = "Save Changes",
                    onClick = {
                        viewModel.updateUserProfile(editName, editCity, editBio)
                        showEditProfileDialog = false
                    },
                    modifier = Modifier.testTag("button_save_profile_edits")
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditProfileDialog = false }
                ) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // DIALOG: CONFIRM CLEAR CACHE
    if (showClearCacheConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheConfirmDialog = false },
            containerColor = DarkSurfaceElevated,
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Clear Cached Application Data?",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "This will remove $formattedCacheSize of cached images and temporary feed data. Your account information, saved favorites and orders will not be deleted.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearCacheConfirmDialog = false
                        viewModel.clearCachedData()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkPrimary,
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_clear_cache_button")
                ) {
                    Text("Clear Cache Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheConfirmDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // DIALOG: RESET AGE GATE CONFIRMATION
    if (showResetAgeDialog) {
        AlertDialog(
            onDismissRequest = { showResetAgeDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text("Reset 18+ Age Gate?", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "You will be presented with the mandatory 18+ legal age verification disclaimer upon re-launching the app.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetAgeDialog = false
                        viewModel.resetAgeConfirmation()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Reset Consent", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAgeDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun ProfileSectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = PinkLight,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.5.sp
            )
        }
    }
}

@Composable
fun AccentColorChip(
    title: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) PinkSubtle else Color(0xFF13131D),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.8.dp,
            color = if (isSelected) color else DarkBorder
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) TextWhite else TextMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
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
    Surface(
        color = Color(0xFF13131D),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.6.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AccountMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Surface(
        onClick = onClick,
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.8.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PinkSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = PinkPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, PinkPrimary)
                ) {
                    Text(
                        text = badge,
                        color = PinkLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
