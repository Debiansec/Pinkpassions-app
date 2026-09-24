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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeMode
import com.example.data.PaymentMethod
import com.example.model.MembershipTier
import com.example.model.SubscriptionStatus
import com.example.ui.components.PremiumSubscriptionBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * User Settings & Privacy Screen
 * Features:
 * 1. Material3 Dark Theme Toggle (Adult-focused luxury Pink Passions aesthetic)
 * 2. Dedicated 'Premium Membership' Management (Billing Cycle, Payment Methods, Auto-Renewal, Invoices)
 * 3. Profile Visibility & Ghost Mode
 * 4. 50km Radar Location Privacy Preferences
 * 5. App Data & Cache Management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val isProfileVisible by viewModel.isProfileVisible.collectAsState()
    val isGhostMode by viewModel.isGhostMode.collectAsState()
    val isShowOnlineStatus by viewModel.isShowOnlineStatus.collectAsState()
    val isShowVerifiedBadge by viewModel.isShowVerifiedBadge.collectAsState()

    val locationPrivacyMode by viewModel.locationPrivacyMode.collectAsState()
    val isShareRadar50km by viewModel.isShareRadar50km.collectAsState()
    val isAutoDetectGps by viewModel.isAutoDetectGps.collectAsState()

    val userSubscription by viewModel.userSubscription.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val isFwbPremiumActive by viewModel.isFwbPremiumActive.collectAsState()

    val cacheSizeBytes by viewModel.cacheSizeBytes.collectAsState()
    val isClearingCache by viewModel.isClearingCache.collectAsState()

    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showInvoicesDialog by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    val isSubActive = userSubscription.status == SubscriptionStatus.ACTIVE &&
            (userSubscription.membershipTier != MembershipTier.BASIC_FREE || isFwbPremiumActive)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings & Privacy",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Aesthetics, Premium Billing & 50km Radar",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_button_back")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurfaceElevated
                )
            )
        },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize().testTag("screen_user_settings")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 1. MATERIAL 3 DARK THEME & AESTHETIC STYLING
            // =========================================================================
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Palette,
                    title = "App Theme & Visual Aesthetic",
                    subtitle = "Switch between signature luxury dark mode and light theme"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("card_theme_settings")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick Dark Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(end = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (currentThemeMode != AppThemeMode.LIGHT) PinkPrimary.copy(alpha = 0.2f) else Color(0x33FFB300)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (currentThemeMode != AppThemeMode.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (currentThemeMode != AppThemeMode.LIGHT) PinkPrimary else Color(0xFFFFB300),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (currentThemeMode != AppThemeMode.LIGHT) "Pink Passions Dark Mode" else "Sultry Light Theme",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (currentThemeMode != AppThemeMode.LIGHT)
                                            "Deep velvet canvas with electric #E31999 hot pink"
                                        else
                                            "Crisp light neutral with signature pink highlights",
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
                                    uncheckedThumbColor = TextDark,
                                    uncheckedTrackColor = DarkSurface
                                ),
                                modifier = Modifier.testTag("toggle_dark_theme_switch")
                            )
                        }

                        HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)

                        Text(
                            text = "SELECT COLOR PALETTE PRESET",
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )

                        // 4 Aesthetic Mode Option Cards
                        val themePresets = listOf(
                            Triple(AppThemeMode.DARK, "Luxury Dark", "Deep charcoal velvet #0E0E14"),
                            Triple(AppThemeMode.AMOLED, "AMOLED Black", "Pure black #000000 (OLED saver)"),
                            Triple(AppThemeMode.LIGHT, "Sultry Light", "Off-white with pink accents"),
                            Triple(AppThemeMode.SYSTEM, "System Default", "Follows device dark/light setting")
                        )

                        themePresets.forEach { (mode, title, desc) ->
                            val isSelected = currentThemeMode == mode
                            Surface(
                                onClick = { viewModel.setThemeMode(mode) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PinkSubtle else DarkBackground,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth().testTag("theme_preset_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Visual Color preview dots
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (mode) {
                                                        AppThemeMode.DARK -> Color(0xFF0E0E14)
                                                        AppThemeMode.AMOLED -> Color(0xFF000000)
                                                        AppThemeMode.LIGHT -> Color(0xFFFAFAFC)
                                                        AppThemeMode.SYSTEM -> Color(0xFF1F1F2E)
                                                    }
                                                )
                                                .border(
                                                    1.dp,
                                                    if (mode == AppThemeMode.LIGHT) Color.Gray else PinkPrimary,
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = title,
                                                color = TextWhite,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.5.sp
                                            )
                                            Text(
                                                text = desc,
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = PinkPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. DEDICATED 'PREMIUM MEMBERSHIP' MANAGEMENT SECTION
            // =========================================================================
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.WorkspacePremium,
                    title = "Premium Membership & Billing",
                    subtitle = "Manage your subscription cycle, payment methods and billing invoices"
                )
            }

            // Current Billing Cycle Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.2.dp, if (isSubActive) PinkPrimary else DarkBorder),
                    modifier = Modifier.testTag("card_billing_cycle_management")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Membership Header & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isSubActive) userSubscription.membershipTier.title else "Free Basic Account",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = if (isSubActive) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF9100).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, if (isSubActive) Color(0xFF00E676) else Color(0xFFFF9100))
                                    ) {
                                        Text(
                                            text = if (isSubActive) "ACTIVE" else "FREE TIER",
                                            color = if (isSubActive) Color(0xFF00E676) else Color(0xFFFF9100),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isSubActive) "Firestore Cloud Synced • Auto-Renewal On" else "Upgrade to unlock unlimited 50km radar & FWB chat",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            if (isSubActive) {
                                PremiumSubscriptionBadge(
                                    tier = userSubscription.membershipTier,
                                    isActive = true,
                                    compact = true
                                )
                            }
                        }

                        // Detailed Billing Breakdown Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkBackground,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "CURRENT BILLING CYCLE DETAILS",
                                    color = PinkLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    letterSpacing = 0.5.sp
                                )

                                BillingDetailRow(
                                    label = "Current Rate",
                                    value = if (isSubActive) "R${userSubscription.amountZar}.00 ZAR / Month" else "R0.00 (Free Forever)"
                                )
                                BillingDetailRow(
                                    label = "Billing Interval",
                                    value = "Monthly recurring (every 30 days)"
                                )
                                BillingDetailRow(
                                    label = "Next Billing Date",
                                    value = if (isSubActive) userSubscription.nextBillingDate else "N/A (Free Plan)"
                                )
                                BillingDetailRow(
                                    label = "Bank Descriptor",
                                    value = "PP-MEMBER-SA (100% Discreet)"
                                )
                                BillingDetailRow(
                                    label = "VAT & Compliance",
                                    value = "Includes 15% South African VAT"
                                )
                            }
                        }

                        // Auto-Renewal Toggle & Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "Automatic Renewal",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Text(
                                    text = if (isSubActive) "Renews automatically on ${userSubscription.nextBillingDate}" else "Auto-renewal is paused",
                                    color = TextMuted,
                                    fontSize = 10.5.sp
                                )
                            }

                            Switch(
                                checked = isSubActive,
                                onCheckedChange = { enable ->
                                    if (enable) {
                                        viewModel.resumeSubscriptionAutoRenew()
                                    } else {
                                        viewModel.cancelSubscriptionAutoRenew()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextWhite,
                                    checkedTrackColor = PinkPrimary,
                                    uncheckedThumbColor = TextDark,
                                    uncheckedTrackColor = DarkSurface
                                ),
                                modifier = Modifier.testTag("switch_toggle_auto_renewal")
                            )
                        }

                        // Upgrade & Invoices Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showUpgradeDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp).testTag("button_change_membership_plan")
                            ) {
                                Icon(Icons.Default.Upgrade, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Plan", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showInvoicesDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = BorderStroke(1.dp, DarkBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp).testTag("button_view_billing_invoices")
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PinkLight, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View Invoices", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Payment Methods Management Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("card_payment_methods_management")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Saved Payment Methods",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Active default: ${selectedPaymentMethod.title}",
                                    color = PinkLight,
                                    fontSize = 11.sp
                                )
                            }

                            IconButton(
                                onClick = { showAddPaymentDialog = true },
                                modifier = Modifier.size(32.dp).testTag("button_add_payment_method")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = "Add Payment Method",
                                    tint = PinkPrimary
                                )
                            }
                        }

                        // List of Supported / Configured Payment Methods
                        val paymentOptions = listOf(
                            PaymentMethodItemData(
                                method = PaymentMethod.PayFast,
                                name = "PayFast SA Gateway (Visa / MC / Capitec Pay)",
                                subtitle = "Active Default Gateway • 3D Secure Protection",
                                icon = Icons.Default.Lock
                            ),
                            PaymentMethodItemData(
                                method = PaymentMethod.Ozow,
                                name = "Ozow Instant EFT (All SA Banks)",
                                subtitle = "Capitec, FNB, Standard Bank, Absa, Nedbank",
                                icon = Icons.Default.AccountBalance
                            ),
                            PaymentMethodItemData(
                                method = PaymentMethod.Card,
                                name = "Credit / Debit Card (•••• 4821)",
                                subtitle = "Visa ending in 4821 • Exp 08/28",
                                icon = Icons.Default.CreditCard
                            ),
                            PaymentMethodItemData(
                                method = PaymentMethod.ManualEFT,
                                name = "Discreet Bank Transfer & Crypto",
                                subtitle = "Manual EFT Reference / USDT TRC20",
                                icon = Icons.Default.CurrencyBitcoin
                            )
                        )

                        paymentOptions.forEach { pItem ->
                            val isDefault = selectedPaymentMethod == pItem.method
                            Surface(
                                onClick = { viewModel.updateDefaultPaymentMethod(pItem.method) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDefault) PinkSubtle else DarkBackground,
                                border = BorderStroke(1.dp, if (isDefault) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth().testTag("payment_method_${pItem.method.title.lowercase().replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isDefault) PinkPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = pItem.icon,
                                                contentDescription = null,
                                                tint = if (isDefault) PinkPrimary else TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = pItem.name,
                                                color = TextWhite,
                                                fontWeight = if (isDefault) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = pItem.subtitle,
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (isDefault) {
                                        Surface(
                                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(0.8.dp, Color(0xFF00E676))
                                        ) {
                                            Text(
                                                text = "DEFAULT",
                                                color = Color(0xFF00E676),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Set Default",
                                            color = PinkPrimary,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Add Payment Method Button
                        OutlinedButton(
                            onClick = { showAddPaymentDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkLight),
                            border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("button_add_new_card")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Card or EFT Bank Account", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // =========================================================================
            // 3. PROFILE VISIBILITY & DISCRETION SECTION
            // =========================================================================
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Visibility,
                    title = "Profile Visibility & Discretion",
                    subtitle = "Control who sees your profile, online status, and verification badges"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("card_profile_visibility_settings")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsToggleRow(
                            title = "Public Profile Visibility",
                            description = "Allow nearby members to discover your profile in listings and search",
                            checked = isProfileVisible,
                            onCheckedChange = { viewModel.setProfileVisible(it) },
                            testTag = "toggle_profile_visibility"
                        )

                        HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)

                        SettingsToggleRow(
                            title = "Ghost Mode (Incognito Browsing)",
                            description = "Browse profiles and cam rooms anonymously without leaving view receipts",
                            checked = isGhostMode,
                            onCheckedChange = { viewModel.setGhostMode(it) },
                            testTag = "toggle_ghost_mode"
                        )

                        HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)

                        SettingsToggleRow(
                            title = "Show Online Status Indicator",
                            description = "Display glowing green active indicator when you are using the app",
                            checked = isShowOnlineStatus,
                            onCheckedChange = { viewModel.setShowOnlineStatus(it) },
                            testTag = "toggle_online_status"
                        )

                        HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)

                        SettingsToggleRow(
                            title = "Display Verified & VIP Badges",
                            description = "Show ID-verified checkmark and Gold/VIP membership badges on your avatar",
                            checked = isShowVerifiedBadge,
                            onCheckedChange = { viewModel.setShowVerifiedBadge(it) },
                            testTag = "toggle_verified_badge"
                        )
                    }
                }
            }

            // =========================================================================
            // 4. 50KM RADAR & LOCATION PRIVACY
            // =========================================================================
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Radar,
                    title = "50km Radar & Location Privacy",
                    subtitle = "Manage proximity radar scanning and South African city detection"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("card_location_privacy_settings")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsToggleRow(
                            title = "50km Radar Scanning Overlay",
                            description = "Show interactive radar pulse animation on Near Me map and listings",
                            checked = isShareRadar50km,
                            onCheckedChange = { viewModel.setShareRadar50km(it) },
                            testTag = "toggle_radar_animation"
                        )

                        HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)

                        SettingsToggleRow(
                            title = "Automatic GPS Location Detection",
                            description = "Automatically select Johannesburg, Cape Town, Durban, or Pretoria based on device location",
                            checked = isAutoDetectGps,
                            onCheckedChange = { viewModel.setAutoDetectGps(it) },
                            testTag = "toggle_auto_gps"
                        )
                    }
                }
            }

            // =========================================================================
            // 5. STORAGE & APP CACHE
            // =========================================================================
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Storage,
                    title = "Storage & App Cache",
                    subtitle = "Manage media cache and locally saved profile data"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("card_storage_cache_settings")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cached Images & Media", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${cacheSizeBytes / (1024 * 1024)} MB used", color = TextMuted, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.clearCachedData() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isClearingCache,
                                modifier = Modifier.height(34.dp).testTag("button_clear_cache")
                            ) {
                                if (isClearingCache) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PinkPrimary, strokeWidth = 2.dp)
                                } else {
                                    Text("Clear Cache", color = PinkLight, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // =========================================================================
    // DIALOG: ADD PAYMENT METHOD
    // =========================================================================
    if (showAddPaymentDialog) {
        var cardHolder by remember { mutableStateOf("") }
        var cardNumber by remember { mutableStateOf("") }
        var cardExpiry by remember { mutableStateOf("") }
        var cardCvv by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPaymentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = PinkPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Payment Method", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your South African Credit / Debit Card or Instant EFT bank details. Processed securely via 256-bit TLS.",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Cardholder Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { if (it.length <= 19) cardNumber = it },
                        label = { Text("Card Number (Visa / Mastercard)") },
                        placeholder = { Text("4000 1234 5678 9010") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { if (it.length <= 5) cardExpiry = it },
                            label = { Text("MM/YY") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = { if (it.length <= 4) cardCvv = it },
                            label = { Text("CVV") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("3D Secure 2.0 & PayFast Tokenization Enabled", color = Color(0xFF00E676), fontSize = 10.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDefaultPaymentMethod(PaymentMethod.Card)
                        showAddPaymentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Save Card", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPaymentDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // =========================================================================
    // DIALOG: VIEW INVOICES & RECEIPTS
    // =========================================================================
    if (showInvoicesDialog) {
        AlertDialog(
            onDismissRequest = { showInvoicesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PinkPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Billing Invoices & History", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val invoiceList = listOf(
                        Triple("INV-98241", "01 Sep 2026", "R99.00 • Paid via PayFast"),
                        Triple("INV-87312", "01 Aug 2026", "R99.00 • Paid via PayFast"),
                        Triple("INV-76409", "01 Jul 2026", "R99.00 • Paid via Ozow Instant EFT")
                    )

                    invoiceList.forEach { (invNum, date, desc) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkBackground,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(invNum, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(date, color = TextMuted, fontSize = 10.sp)
                                    Text(desc, color = Color(0xFF00E676), fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.FileDownload, contentDescription = "Download Receipt", tint = PinkPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInvoicesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // =========================================================================
    // DIALOG: CHANGE / UPGRADE PLAN
    // =========================================================================
    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = {
                Text("Select Membership Tier", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val tiers = listOf(
                        MembershipTier.FRIENDS_WITH_BENEFITS,
                        MembershipTier.GOLD,
                        MembershipTier.VIP
                    )

                    tiers.forEach { tier ->
                        val isCurrent = userSubscription.membershipTier == tier
                        Surface(
                            onClick = {
                                viewModel.purchaseMembership(tier)
                                showUpgradeDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) PinkSubtle else DarkBackground,
                            border = BorderStroke(1.dp, if (isCurrent) PinkPrimary else DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tier.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("R${tier.priceZar}.00 ${tier.billingPeriod}", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                    Text(tier.badgeLabel, color = TextMuted, fontSize = 10.sp)
                                }
                                if (isCurrent) {
                                    Text("CURRENT", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 10.sp)
                                } else {
                                    Text("Select", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("Close", color = TextMuted)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

private data class PaymentMethodItemData(
    val method: PaymentMethod,
    val name: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
private fun BillingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextMuted, fontSize = 11.sp)
        Text(value, color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PinkPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconColor: Color = PinkPrimary,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = description, color = TextMuted, fontSize = 10.5.sp, lineHeight = 14.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = iconColor,
                uncheckedThumbColor = TextDark,
                uncheckedTrackColor = DarkSurface
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

