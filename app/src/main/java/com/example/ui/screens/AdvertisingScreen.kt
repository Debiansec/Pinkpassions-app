package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdvertisingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val campaigns by viewModel.campaigns.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }

    var campaignName by remember { mutableStateOf("Gauteng Top Carousel Sponsor") }
    var placement by remember { mutableStateOf("Homepage Top Carousel") }
    var budget by remember { mutableStateOf(850) }
    var durationDays by remember { mutableStateOf(14) }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Advertising & Banners", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Campaign Performance & Placements", color = PinkLight, fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = { showCreateModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("+ New Ad", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricTile(label = "Total Impressions", value = "37,600", color = TextWhite, modifier = Modifier.weight(1f))
                    MetricTile(label = "Direct Clicks", value = "2,420", color = PinkPrimary, modifier = Modifier.weight(1f))
                    MetricTile(label = "CTR Avg", value = "6.4%", color = BadgeGold, modifier = Modifier.weight(1f))
                }
            }

            item {
                Text("Active Campaigns", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            items(campaigns) { camp ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(camp.campaignName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                color = BadgeVerifiedGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.5.dp, BadgeVerifiedGreen)
                            ) {
                                Text(camp.status, color = BadgeVerifiedGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Placement: ${camp.placement}", color = TextMuted, fontSize = 11.sp)
                        Text("Budget: R${camp.budgetZar} • ${camp.startDate} to ${camp.endDate}", color = TextMuted, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${camp.impressions} Views", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("${camp.clicks} Clicks", color = PinkPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TechnicalSupportBox(
                    onNotice = { viewModel.showNotice(it) }
                )
            }
        }
    }

    if (showCreateModal) {
        AlertDialog(
            onDismissRequest = { showCreateModal = false },
            containerColor = DarkSurfaceElevated,
            title = { Text("Launch Sponsored Ad Campaign", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = campaignName,
                        onValueChange = { campaignName = it },
                        label = { Text("Campaign Name", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, unfocusedBorderColor = DarkBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = placement,
                        onValueChange = { placement = it },
                        label = { Text("Placement Position", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, unfocusedBorderColor = DarkBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Budget: R$budget (14 Days)", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Accepted Gateways:", color = TextMuted, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            PayPalLogo()
                            PayFastLogo()
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createAdCampaign(campaignName, placement, budget, durationDays)
                        showCreateModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Pay & Launch Ad (R$budget)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateModal = false }) { Text("Cancel", color = TextMuted) }
            }
        )
    }
}
