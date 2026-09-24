package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val profiles by viewModel.allProfiles.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val verificationRequests by viewModel.verificationRequests.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val configuredTiers by viewModel.configuredMembershipTiers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Listings, 1: Verifications, 2: Gateways, 3: Reviews, 4: Audit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
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
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Admin Executive Control Panel", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Pink Passions South Africa • Multi-Module Management", color = Color(0xFFFFD700), fontSize = 10.sp)
                }
            }
        }

        // Metrics Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricTile(label = "Listings", value = "${profiles.size}", color = TextWhite, modifier = Modifier.weight(1f))
            MetricTile(label = "Pending Verify", value = "${verificationRequests.count { it.status == VerificationStatus.PENDING }}", color = Color(0xFF00E676), modifier = Modifier.weight(1f))
            MetricTile(label = "Gateways", value = "Active", color = PinkPrimary, modifier = Modifier.weight(1f))
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceVariant,
            contentColor = PinkPrimary,
            edgePadding = 12.dp
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Listings", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Verifications (${verificationRequests.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Gateways & Pricing", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Reviews (${allReviews.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Audit Trail", fontWeight = FontWeight.Bold) })
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tab 0: Listings Moderation
            if (selectedTab == 0) {
                items(profiles) { profile ->
                    AdminProfileModerationCard(
                        profile = profile,
                        onAction = { action ->
                            viewModel.adminModerateProfile(profile.id, action)
                        }
                    )
                }
            }

            // Tab 1: Verification Requests Queue
            if (selectedTab == 1) {
                items(verificationRequests) { req ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = req.userName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                    Text(text = req.userEmail, color = TextMuted, fontSize = 11.sp)
                                    Text(text = "Doc: ${req.documentType}", color = PinkLight, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (req.status == VerificationStatus.VERIFIED) Color(0x3300E676) else Color(0x33FFD700))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = req.status.name,
                                        color = if (req.status == VerificationStatus.VERIFIED) Color(0xFF00E676) else Color(0xFFFFD700),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biometric Match: ${req.matchScorePct}%", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Liveness: ${if (req.livenessPassed) "PASSED" else "PENDING"}", color = TextLight, fontSize = 12.sp)
                            }

                            if (!req.reviewerNotes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Audit Note: ${req.reviewerNotes}", color = TextMuted, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminApproveVerification(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Approve Badge", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.adminRejectVerification(req.id, "Image blurred or mismatched") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reject", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Gateways & Pricing Config
            if (selectedTab == 2) {
                item {
                    Text("Payment Gateways (ZAR Engine)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = PinkPrimary.copy(alpha = 0.5f)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PayFastLogo()
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PayFast Gateway", color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = true,
                                    onCheckedChange = { viewModel.showNotice("PayFast gateway configuration updated.") }
                                )
                            }
                            Text("Currency: ZAR (South African Rand) • Automated ITN Webhook Active", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = Color(0x6600D2FF)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PayPalLogo()
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PayPal Gateway", color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = true,
                                    onCheckedChange = { viewModel.showNotice("PayPal gateway configuration updated.") }
                                )
                            }
                            Text("Currency: ZAR / USD • 256-Bit TLS 1.3 Client Encryption Active", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Configured Membership Tiers Pricing (ZAR)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                items(configuredTiers) { tier ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tier.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(tier.billingPeriod, color = TextMuted, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (tier.priceZar == 0) "FREE" else "R${tier.priceZar}",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                TierBadge(tier)
                            }
                        }
                    }
                }
            }

            // Tab 3: Reviews Moderation
            if (selectedTab == 3) {
                items(allReviews) { review ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(review.reviewerName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Row {
                                    repeat(review.rating) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(review.comment, color = TextLight, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminModerateReview(review.id, "APPROVED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Approve", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.deleteReview(review.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 4: Audit Logs
            if (selectedTab == 4) {
                items(auditLogs) { log ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = BorderStroke(0.8.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                Text(log.timestamp, color = TextMuted, fontSize = 10.5.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Target: ${log.targetType} [${log.targetId}] • Admin: ${log.adminUser}", color = TextLight, fontSize = 11.sp)
                            Text(log.notes, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProfileModerationCard(
    profile: Profile,
    onAction: (String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(profile.displayName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${profile.city}, ${profile.province} • ${profile.category.title}", color = TextMuted, fontSize = 11.sp)
                }
                TierBadge(profile.membershipTier)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { onAction("FEATURE") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFEA00)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (profile.isFeatured) "Un-Feature" else "Boost Feature", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onAction("VERIFY_FULL") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Verify Badge", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onAction("SUSPEND") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Suspend", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
