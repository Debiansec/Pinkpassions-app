package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentMethod
import com.example.model.MembershipTier
import com.example.model.SubscriptionStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MembershipScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val configuredTiers by viewModel.configuredMembershipTiers.collectAsState()
    val activeSubscription by viewModel.userSubscription.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()

    var selectedTier by remember { mutableStateOf(MembershipTier.GOLD) }
    var selectedPayment by remember { mutableStateOf<PaymentMethod>(PaymentMethod.PayFast) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All Plans") }

    val categories = listOf("All Plans", "FWB & Chat (R99/m)", "Escorts & VIP", "Specialist & Dancers", "Agencies & Venues")

    val displayedTiers = remember(configuredTiers, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "FWB & Chat (R99/m)" -> configuredTiers.filter {
                it in listOf(
                    MembershipTier.FRIENDS_WITH_BENEFITS,
                    MembershipTier.BASIC_FREE
                )
            }
            "Escorts & VIP" -> configuredTiers.filter {
                it in listOf(
                    MembershipTier.BASIC_FREE,
                    MembershipTier.FRIENDS_WITH_BENEFITS,
                    MembershipTier.INDEPENDENT_ESCORT,
                    MembershipTier.PREMIUM,
                    MembershipTier.GOLD,
                    MembershipTier.VIP,
                    MembershipTier.UPMARKET_EXCLUSIVE
                )
            }
            "Specialist & Dancers" -> configuredTiers.filter {
                it in listOf(MembershipTier.STRIPPER_DANCER, MembershipTier.MASSEUSE)
            }
            "Agencies & Venues" -> configuredTiers.filter {
                it in listOf(MembershipTier.ESCORT_AGENCY, MembershipTier.NIGHTLIFE_VENUE)
            }
            else -> configuredTiers
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // App Bar
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("membership_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MEMBERSHIP PLANS",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PinkPrimary.copy(alpha = 0.2f))
                                .border(0.8.dp, PinkPrimary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("PACKAGES", color = PinkPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "South Africa's Premier Adult & Lifestyle Advertising Network",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Intro Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF2A0820),
                                    DarkSurfaceVariant
                                )
                            )
                        )
                        .border(1.dp, PinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "ELEVATE YOUR EXPOSURE",
                                    color = PinkLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Choose Your Membership Package",
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary.copy(alpha = 0.25f))
                                    .border(1.dp, PinkPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = PinkPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Join verified companions, elite escorts, spas, and nightlife venues across Gauteng, Western Cape, KZN & nationwide. Enjoy premium badges, high visibility, and direct WhatsApp contact.",
                            color = TextLight,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBackground.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Current Active Tier: ", color = TextMuted, fontSize = 11.sp)
                            TierBadge(activeSubscription.membershipTier)
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 11.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkPrimary,
                                selectedLabelColor = TextWhite,
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextMuted
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) PinkPrimary else DarkBorder
                            )
                        )
                    }
                }
            }

            // Premium Glossy Tier Cards
            items(displayedTiers) { tier ->
                val isSelected = selectedTier == tier
                val isCurrentPlan = activeSubscription.membershipTier == tier
                val tierColor = Color(tier.badgeColorHex)

                GlossyMembershipPackageCard(
                    tier = tier,
                    isSelected = isSelected,
                    isCurrentPlan = isCurrentPlan,
                    onSelect = { selectedTier = tier },
                    onSignUp = {
                        selectedTier = tier
                        if (tier.priceZar == 0) {
                            viewModel.purchaseMembership(tier)
                        } else {
                            viewModel.selectedPaymentMethod.value = selectedPayment
                            showCheckoutDialog = true
                        }
                    },
                    onRegisterWithTier = {
                        viewModel.startRegistrationFlow(initialStep = 1, tier = tier)
                    }
                )
            }

            // Payment Gateways Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                PaymentGatewaySelector(
                    selectedMethod = selectedPayment,
                    onSelect = { selectedPayment = it }
                )
            }

            // Technical Support Box
            item {
                Spacer(modifier = Modifier.height(6.dp))
                TechnicalSupportBox(
                    onNotice = { viewModel.showNotice(it) }
                )
            }
        }
    }

    if (showCheckoutDialog) {
        PaymentGatewayDialog(
            amountZar = selectedTier.priceZar,
            itemTitle = "${selectedTier.title} Membership Plan",
            selectedMethod = selectedPayment,
            isProcessing = isProcessingPayment,
            onConfirm = {
                viewModel.purchaseMembership(selectedTier)
                showCheckoutDialog = false
            },
            onDismiss = { showCheckoutDialog = false }
        )
    }
}

/**
 * Responsive Premium Glossy Membership Card
 * Designed with neon glow border, rich gradient depth, rounded corners,
 * package name, glowing badge, monthly price, short description, feature list,
 * current plan indicator, and prominent SIGN UP / UPGRADE button.
 */
@Composable
fun GlossyMembershipPackageCard(
    tier: MembershipTier,
    isSelected: Boolean,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit,
    onSignUp: () -> Unit,
    onRegisterWithTier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = Color(tier.badgeColorHex)
    val cardBorderBrush = if (isSelected || isCurrentPlan) {
        Brush.horizontalGradient(
            listOf(
                tierColor,
                tierColor.copy(alpha = 0.5f),
                PinkPrimary.copy(alpha = 0.8f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                DarkBorder,
                DarkBorder.copy(alpha = 0.6f)
            )
        )
    }

    val cardBackgroundBrush = Brush.verticalGradient(
        listOf(
            DarkSurfaceElevated.copy(alpha = 0.95f),
            DarkSurfaceVariant.copy(alpha = 0.98f),
            DarkBackground.copy(alpha = 0.9f)
        )
    )

    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(if (isSelected || isCurrentPlan) 1.8.dp else 1.dp, cardBorderBrush),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = tierColor.copy(alpha = 0.3f),
                spotColor = tierColor.copy(alpha = 0.4f)
            )
            .testTag("membership_card_${tier.name}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundBrush)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Row: Package Name, Current Indicator, Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tier.title,
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            if (isCurrentPlan) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x3300E676))
                                        .border(0.8.dp, Color(0xFF00E676), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CURRENT ACTIVE",
                                        color = Color(0xFF00E676),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = tier.billingPeriod,
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }

                    TierBadge(tier)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price Tag Bar with Neon Accent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(tierColor.copy(alpha = 0.12f))
                        .border(0.8.dp, tierColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY SUBSCRIPTION",
                            color = tierColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (tier.priceZar == 0) "FREE FOREVER" else "R${String.format(java.util.Locale.US, "%,d", tier.priceZar)}",
                            color = TextWhite,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (tier.priceZar > 0) {
                        Text(
                            text = "Billed monthly via PayFast/PayPal",
                            color = TextLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    } else {
                        Text(
                            text = "No credit card required",
                            color = Color(0xFF00E676),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Short Description
                if (tier.shortDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = tier.shortDescription,
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 16.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = DarkBorder.copy(alpha = 0.6f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Feature List
                Text(
                    text = "INCLUDED PACKAGE FEATURES",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                tier.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(tierColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = tierColor,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Direct "Register & Create Profile with this tier"
                    OutlinedButton(
                        onClick = onRegisterWithTier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                        border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text(
                            text = "Register Profile",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // Prominent Neon CTA Button
                    Button(
                        onClick = onSignUp,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentPlan) Color(0xFF1E293B) else tierColor
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                    ) {
                        Text(
                            text = if (isCurrentPlan) "Active Plan" else tier.ctaText,
                            color = if (isCurrentPlan) Color(0xFF00E676) else if (tier.badgeColorHex == 0xFFFFD700L || tier.badgeColorHex == 0xFFE2E8F0L) Color.Black else Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
