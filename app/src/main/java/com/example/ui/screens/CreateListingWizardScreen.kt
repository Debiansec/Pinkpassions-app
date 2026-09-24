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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.MembershipTier
import com.example.model.ProfileCategory
import com.example.model.VerificationLevel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CreateListingWizardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val step by viewModel.createListingStep.collectAsState()
    val draft by viewModel.createListingDraft.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // TOP APP BAR
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
                    onClick = {
                        if (step > 1) {
                            viewModel.createListingStep.value = step - 1
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Create Advertiser Listing",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Step $step of 5 • South Africa",
                        color = PinkLight,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // STEP PROGRESS BAR
        LinearProgressIndicator(
            progress = { step / 5f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = PinkPrimary,
            trackColor = DarkBorder
        )

        // STEP CONTENT
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (step) {
                1 -> {
                    item {
                        Text("Step 1: Account Information", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Set up your secure Pink Passions advertiser credentials.", color = TextMuted, fontSize = 12.sp)
                    }

                    item {
                        OutlinedTextField(
                            value = draft.stageName,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(stageName = it) },
                            label = { Text("Stage / Display Name", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("listing_stage_name_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draft.email,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(email = it) },
                            label = { Text("Discreet Email Address", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draft.phone,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(phone = it) },
                            label = { Text("WhatsApp / Booking Phone Number", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                2 -> {
                    item {
                        Text("Step 2: Profile & Location Details", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Provide your location, category, rates, and services.", color = TextMuted, fontSize = 12.sp)
                    }

                    item {
                        Text("Province", color = PinkLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MockDataProvider.PROVINCES.forEach { prov ->
                                FilterChip(
                                    selected = draft.province == prov,
                                    onClick = {
                                        val firstCity = MockDataProvider.CITIES_BY_PROVINCE[prov]?.firstOrNull() ?: "City"
                                        viewModel.createListingDraft.value = draft.copy(province = prov, city = firstCity)
                                    },
                                    label = { Text(prov, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = TextWhite)
                                )
                            }
                        }
                    }

                    item {
                        Text("City in ${draft.province}", color = PinkLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        val cities = MockDataProvider.CITIES_BY_PROVINCE[draft.province] ?: listOf("Johannesburg")
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            cities.forEach { city ->
                                FilterChip(
                                    selected = draft.city == city,
                                    onClick = { viewModel.createListingDraft.value = draft.copy(city = city) },
                                    label = { Text(city, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = TextWhite)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = draft.area,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(area = it) },
                            label = { Text("Specific Suburb / Area (e.g. Sandton CBD, Menlyn)", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, unfocusedBorderColor = DarkBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draft.priceText,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(priceText = it) },
                            label = { Text("Rates & Pricing (e.g. R1,200 / hr)", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, unfocusedBorderColor = DarkBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draft.description,
                            onValueChange = { viewModel.createListingDraft.value = draft.copy(description = it) },
                            label = { Text("Profile Description / Bio", color = TextMuted) },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, unfocusedBorderColor = DarkBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                3 -> {
                    item {
                        Text("Step 3: Trust & Verification", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Select your desired verification tier. Higher badges receive 4x more bookings.", color = TextMuted, fontSize = 12.sp)
                    }

                    listOf(
                        Triple(VerificationLevel.PHOTO, "Photo Verified Badge", "Upload selfie holding a handwritten note with 'Pink Passions + Today's date'."),
                        Triple(VerificationLevel.FULL, "Fully Verified Gold Badge", "ID / Passport age confirmation + selfie audit for elite VIP status."),
                        Triple(VerificationLevel.BASIC, "Basic Verification", "Standard phone and SMS OTP check.")
                    ).forEach { (lvl, title, desc) ->
                        item {
                            Card(
                                onClick = { viewModel.createListingDraft.value = draft.copy(verificationLevel = lvl) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = BorderStroke(1.5.dp, if (draft.verificationLevel == lvl) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = draft.verificationLevel == lvl,
                                        onClick = { viewModel.createListingDraft.value = draft.copy(verificationLevel = lvl) },
                                        colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(desc, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    item {
                        Text("Step 4: Membership Tier", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Choose your promotion tier to boost your placement across South Africa.", color = TextMuted, fontSize = 12.sp)
                    }

                    listOf(
                        MembershipTier.FREE,
                        MembershipTier.FEATURED,
                        MembershipTier.VIP,
                        MembershipTier.GOLD
                    ).forEach { tier ->
                        item {
                            Card(
                                onClick = { viewModel.createListingDraft.value = draft.copy(membershipTier = tier) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = BorderStroke(1.5.dp, if (draft.membershipTier == tier) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = draft.membershipTier == tier,
                                            onClick = { viewModel.createListingDraft.value = draft.copy(membershipTier = tier) },
                                            colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(tier.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Includes badge & priority listing", color = TextMuted, fontSize = 11.sp)
                                        }
                                    }
                                    Text(
                                        text = if (tier.priceZar == 0) "Free" else "R${tier.priceZar} ${tier.billingPeriod}",
                                        color = PinkPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                5 -> {
                    item {
                        Text("Step 5: Review & Publish", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Review your listing before publishing to Pink Passions South Africa.", color = TextMuted, fontSize = 12.sp)
                    }

                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Listing Summary", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Display Name: ${draft.stageName.ifBlank { "My Stage Name" }}", color = TextWhite, fontSize = 13.sp)
                                Text("Location: ${draft.area}, ${draft.city}, ${draft.province}", color = TextWhite, fontSize = 13.sp)
                                Text("Category: ${draft.category.title}", color = TextWhite, fontSize = 13.sp)
                                Text("Rates: ${draft.priceText}", color = TextWhite, fontSize = 13.sp)
                                Text("Verification: ${draft.verificationLevel.label}", color = BadgeVerifiedGreen, fontSize = 13.sp)
                                Text("Membership: ${draft.membershipTier.title} (R${draft.membershipTier.priceZar})", color = PinkPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM ACTION BUTTONS
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (step > 1) {
                    DarkOutlineButton(
                        text = "Back",
                        onClick = { viewModel.createListingStep.value = step - 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                PinkGradientButton(
                    text = if (step == 5) "Publish Listing" else "Next Step",
                    onClick = {
                        if (step < 5) {
                            viewModel.createListingStep.value = step + 1
                        } else {
                            viewModel.submitListingCreation()
                        }
                    },
                    modifier = Modifier.weight(1.5f).testTag("wizard_next_button")
                )
            }
        }
    }
}
