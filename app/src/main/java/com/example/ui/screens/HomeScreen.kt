package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.update
import com.example.data.MockDataProvider
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.filteredProfiles.collectAsState()
    val featuredProfiles by viewModel.featuredProfiles.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val isLoadingFeeds by viewModel.isLoadingFeeds.collectAsState()

    var showLocationBottomSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAuthModal by remember { mutableStateOf(false) }
    var authInitialMode by remember { mutableStateOf("REGISTER") }
    var selectedDirectoryChip by remember { mutableStateOf("Cams") }
    val configuredTiers by viewModel.configuredMembershipTiers.collectAsState()
    val context = LocalContext.current

    val displayCity = filterState.selectedCity ?: "All South Africa"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP APP BAR WITH LOGO, LIVE CHAT/CAM SHORTCUT & LOCATION SELECTOR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pink Passions Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.currentTab.value = "home" }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PinkGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PP", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pink",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Passions",
                                color = PinkPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "South Africa • 18+",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Actions: Location Button & Live Cam Shortcut Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Location selector trigger
                    Surface(
                        onClick = { showLocationBottomSheet = true },
                        shape = RoundedCornerShape(20.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("home_location_selector_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = PinkPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = displayCity,
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 110.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Live Webcams Header Shortcut Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E1024))
                            .border(1.dp, PinkPrimary, CircleShape)
                            .clickable { viewModel.activeSubscreen.value = "live_entertainment" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Live Cams",
                            tint = PinkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // PROMINENT AUTHENTICATION ACTIONS: REGISTER & SIGN IN BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // REGISTER BUTTON
                Button(
                    onClick = {
                        viewModel.startRegistrationFlow(initialStep = 1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("home_auth_register_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REGISTER", fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 0.5.sp)
                }

                // SIGN IN BUTTON
                OutlinedButton(
                    onClick = {
                        authInitialMode = "SIGN_IN"
                        showAuthModal = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("home_auth_signin_button")
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, tint = PinkLight, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SIGN IN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // PREMIUM SEARCH BAR COMPONENT
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                DarkSearchBar(
                    query = filterState.query,
                    onQueryChange = {
                        viewModel.setSearchQuery(it)
                        viewModel.triggerShimmerReload()
                    },
                    placeholder = "Search escorts, spas, massage, clubs...",
                    onFilterClick = { showFilterSheet = true },
                    hasActiveFilters = filterState.onlyVerified || filterState.onlyFeatured || filterState.onlyVip || filterState.selectedCategory != null
                )
            }
        }

        // HERO BANNER CAROUSEL
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF261026), Color(0xFF161226), Color(0xFF0F0F1A))
                        )
                    )
                    .border(1.dp, DarkBorderPink, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = PinkPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.8.dp, PinkPrimary)
                        ) {
                            Text(
                                text = "🇿🇦 PREMIER ADULT DIRECTORY",
                                color = PinkLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "South Africa's Luxury Companion Network",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Verified escorts, sensual massage, clubs, live webcam shows & adult boutique.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PinkGradientButton(
                                text = "Explore Live Cams",
                                onClick = { viewModel.activeSubscreen.value = "live_entertainment" },
                                modifier = Modifier.height(34.dp)
                            )

                            Button(
                                onClick = { viewModel.activeSubscreen.value = "local_chat_room" },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DarkBorderPink),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Forum, contentDescription = null, tint = PinkLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Lounge", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // LARGE PREMIUM CARD: REGISTER & CREATE YOUR PROFILE
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF330826),
                                Color(0xFF1E0A22),
                                DarkSurfaceElevated
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(
                                PinkPrimary,
                                Color(0xFFFF4081),
                                PinkPrimary.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PinkPrimary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.8.dp, PinkPrimary)
                        ) {
                            Text(
                                text = "✨ CREATOR & ADVERTISER NETWORK",
                                color = PinkLight,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "REGISTER & CREATE YOUR PROFILE",
                        color = TextWhite,
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Join South Africa's #1 verified platform for elite escorts, sensual masseuses, strip artists, luxury clubs & agencies.",
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 16.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Feature points
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00E676), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("14-Step Verified Setup", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00E676), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp Client Leads", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00E676), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified ID & Liveness", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Admin Moderation Safe", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PinkGradientButton(
                            text = "Register Profile",
                            onClick = {
                                viewModel.startRegistrationFlow(initialStep = 1)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                                .testTag("home_card_register_button")
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.activeSubscreen.value = "membership"
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("home_card_view_plans_button")
                        ) {
                            Text("View Plans", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // CATEGORY FILTER HORIZONTAL CHIP STRIP
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterState.selectedCategory == null,
                        onClick = {
                            viewModel.setCategoryFilter(null)
                            viewModel.triggerShimmerReload()
                        },
                        label = { Text("All Categories", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = TextWhite,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = PinkPrimary,
                            enabled = true,
                            selected = filterState.selectedCategory == null
                        )
                    )
                }
                items(ProfileCategory.values()) { cat ->
                    val isSelected = filterState.selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.setCategoryFilter(if (isSelected) null else cat)
                            viewModel.triggerShimmerReload()
                        },
                        label = { Text(cat.title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = TextWhite,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = PinkPrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // SKELETON OR CONTENT FEED
        if (isLoadingFeeds) {
            item {
                SkeletonFeaturedCarousel()
            }
            item {
                SkeletonFeedList()
            }
        } else {
            // FEATURED MEMBERS SECTION
            item {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BadgeGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Featured VIP Companions",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "View all",
                            color = PinkPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { viewModel.currentTab.value = "discover" }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(featuredProfiles) { profile ->
                            FeaturedProfileCard(
                                profile = profile,
                                onClick = { viewModel.openProfileDetail(profile) },
                                onFavouriteToggle = { viewModel.toggleFavourite(profile) }
                            )
                        }
                    }
                }
            }

            // DIRECTORY & SERVICES WITH HORIZONTAL SCROLLABLE CHIP FILTER
            item {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Directory & Services",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Explore All",
                            color = PinkLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { viewModel.currentTab.value = "discover" }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // HORIZONTAL SCROLLABLE CHIP FILTER: 'Cams', 'Events', 'Businesses', 'E-commerce'
                    val directoryChips = listOf(
                        Triple("Cams", Icons.Default.Videocam, "Live webcam models & VIP private chats"),
                        Triple("Events", Icons.Default.Event, "Masquerades, VIP nightlife & lifestyle parties"),
                        Triple("Businesses", Icons.Default.Storefront, "Gentlemen's clubs, sensual spas & agencies"),
                        Triple("E-commerce", Icons.Default.ShoppingBag, "Discreet boutique, intimate toys & fashion")
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("directory_filter_chips_row")
                    ) {
                        items(directoryChips) { (chipName, icon, _) ->
                            val isSelected = selectedDirectoryChip == chipName
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDirectoryChip = chipName },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) TextWhite else PinkPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = chipName,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    selectedLabelColor = TextWhite,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextWhite
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = DarkBorder,
                                    selectedBorderColor = PinkPrimary,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("chip_directory_${chipName.lowercase().replace("-", "_")}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // DYNAMIC DIRECTORY CONTENT BASED ON SELECTED CHIP FILTER
                    when (selectedDirectoryChip) {
                        "Cams" -> {
                            // CAMS PREVIEW CARD & SHORTCUTS
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                    border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
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
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFFF0055))
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("XCams Live Broadcasts", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                            }
                                            Text("28 Models Live", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Stream HD 1080p cams with South African and international models. Free group chats & private cam-to-cam shows.",
                                            color = TextMuted,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.activeSubscreen.value = "live_entertainment" },
                                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("button_open_cams_directory")
                                            ) {
                                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Enter Cam Gallery", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.activeSubscreen.value = "local_chat_room" },
                                                border = BorderStroke(1.dp, DarkBorder),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("Live Chat Rooms", fontSize = 11.5.sp, color = TextWhite)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Events" -> {
                            // EVENTS PREVIEW CARD & SHORTCUTS
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                    border = BorderStroke(1.dp, BadgeGold.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Event, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("VIP Parties & Galas", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                            }
                                            Text("Johannesburg & Cape Town", color = BadgeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Masquerade balls, luxury gentlemen's club theme nights, sensual wellness workshops, and private lifestyle socials.",
                                            color = TextMuted,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.activeSubscreen.value = "events" },
                                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("button_open_events_directory")
                                            ) {
                                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Browse All Events", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Businesses" -> {
                            // BUSINESSES PREVIEW CARD & SHORTCUTS
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                    border = BorderStroke(1.dp, DarkBorderPink),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Storefront, contentDescription = null, tint = PinkLight, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Verified Venues & Agencies", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                            }
                                            Text("4.9 ★ Rating", color = BadgeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Premier gentlemen's lounges, sensual Thai massage parlours, luxury hotels, and licensed adult agencies across South Africa.",
                                            color = TextMuted,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.activeSubscreen.value = "business_directory" },
                                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("button_open_business_directory")
                                            ) {
                                                Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Venues Directory", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.setCategoryFilter(ProfileCategory.MASSAGE) },
                                                border = BorderStroke(1.dp, DarkBorder),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("Sensual Spas", fontSize = 11.5.sp, color = TextWhite)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "E-commerce" -> {
                            // E-COMMERCE PREVIEW CARD & SHORTCUTS
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                    border = BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFFB388FF), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Adult Boutique & Lingerie", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                            }
                                            Text("Discreet Delivery", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Premium intimate toys, luxury satin robes, massage oils, and bedroom accessories delivered in 100% unbranded plain packaging.",
                                            color = TextMuted,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.activeSubscreen.value = "shop" },
                                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("button_open_shop_directory")
                                            ) {
                                                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Visit Adult Boutique", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // QUICK NAVIGATION TILES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryTile(
                            title = "Gentlemen's Clubs",
                            subtitle = "VIP Nightlife",
                            icon = Icons.Default.Nightlife,
                            color = Color(0xFFFF4081),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.activeSubscreen.value = "business_directory" }
                        )
                        CategoryTile(
                            title = "Sensual Spas",
                            subtitle = "Massage & Wellness",
                            icon = Icons.Default.Spa,
                            color = Color(0xFFE040FB),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setCategoryFilter(ProfileCategory.MASSAGE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryTile(
                            title = "Adult Boutique",
                            subtitle = "Discreet Shipping",
                            icon = Icons.Default.ShoppingBag,
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.activeSubscreen.value = "shop" }
                        )
                        CategoryTile(
                            title = "Local Chat Rooms",
                            subtitle = "Connect & Chat",
                            icon = Icons.Default.Forum,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.activeSubscreen.value = "local_chat_room" }
                        )
                    }
                }
            }

            // GENTLEMEN'S GUIDE & VENUES SECTION
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1026), Color(0xFF13131F))
                            )
                        )
                        .border(1.dp, DarkBorderPink, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = BadgeGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gentlemen's Venues & Galas",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Discover premier gentlemen's lounges, masquerade galas, and luxury nightlife venues across South Africa.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.activeSubscreen.value = "events" },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("View Events", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.activeSubscreen.value = "business_directory" },
                                border = BorderStroke(1.dp, DarkBorder),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Venues Directory", fontSize = 12.sp, color = TextWhite)
                            }
                        }
                    }
                }
            }

            // WEBCAMS GALLERY AFTER VENUES ON MAIN DASHBOARD
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0055))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Webcams Gallery",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Text(
                            text = "Open Full Screen",
                            color = PinkPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.activeSubscreen.value = "live_entertainment" }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Live webcam multi-room gallery powered by XCams network",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Embedded Webcams Gallery Iframe
                    LiveWebcamGalleryEmbed(
                        heightDp = 360
                    )
                }
            }

            // -------------------------------------------------------------
            // NEAR ME (MAP & RADAR) - PROXIMITY & GPS DISCOVERY SECTION
            // Positioned right after Webcams Gallery on Home Screen
            // -------------------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("section_near_me_map_radar")
                ) {
                    // Section Title Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PinkPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = PinkPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Near Me (Map & Radar)",
                                        color = TextWhite,
                                        fontSize = 16.5.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFF00E676).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.6.dp, Color(0xFF00E676))
                                    ) {
                                        Text(
                                            text = "50km LIVE",
                                            color = Color(0xFF00E676),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Nearby verified escorts, clubs & FWB hookup spots in $displayCity",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "Full Map",
                            color = PinkPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.activeSubscreen.value = "google_maps" }
                                .padding(4.dp)
                                .testTag("home_near_me_view_all_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // INTERACTIVE RADAR & MAP HERO CARD
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.2.dp, PinkPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_near_me_radar_banner")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF240E2C),
                                            Color(0xFF140D1E),
                                            DarkSurfaceElevated
                                        )
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            // Top radar info row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.MyLocation,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GPS Proximity: Within 50km",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    color = Color(0xFF1A237E).copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(0.8.dp, Color(0xFF4285F4))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            tint = Color(0xFF4285F4),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Google Maps SDK",
                                            color = Color.White,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Find Companions & Verified Venues Closest to You",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = "Scan 50km radius for active members, sensual massage parlours and gentlemen's clubs with instant WhatsApp & navigation.",
                                color = TextLight,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Three quick navigation triggers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.activeSubscreen.value = "google_maps" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(38.dp)
                                        .testTag("button_home_open_google_maps")
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = TextWhite, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open 50km Map", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.currentTab.value = "map" },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                    border = BorderStroke(1.dp, Color(0xFF4285F4)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("button_home_open_radar_tab")
                                ) {
                                    Icon(Icons.Default.Radar, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Radar Tab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.activeSubscreen.value = "local_chat_room" },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                    border = BorderStroke(1.dp, Color(0xFFFF5252)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("button_home_open_fwb_chat")
                                ) {
                                    Icon(Icons.Default.Forum, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FWB Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // HORIZONTAL LIST OF PROXIMITY CARDS (COMPANIONS & CLUBS WITHIN 50KM)
                    val nearbyProfiles = remember(profiles) {
                        profiles.take(6).mapIndexed { index, p ->
                            val dist = 2.4f + (index * 4.1f) % 46f
                            Pair(p, dist)
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(nearbyProfiles) { (profile, distanceKm) ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .width(180.dp)
                                    .clickable { viewModel.openProfileDetail(profile) }
                                    .testTag("card_near_me_${profile.id}")
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                    ) {
                                        AsyncImage(
                                            model = profile.avatarUrl,
                                            contentDescription = profile.displayName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Distance badge
                                        Surface(
                                            color = Color(0xCC000000),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(0.6.dp, Color(0xFF00E676)),
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .align(Alignment.TopStart)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.NearMe,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00E676),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "${String.format(java.util.Locale.US, "%.1f", distanceKm)} km",
                                                    color = Color(0xFF00E676),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        // Verified Badge
                                        if (profile.verifiedLevel != VerificationLevel.NONE) {
                                            Surface(
                                                color = PinkPrimary,
                                                shape = CircleShape,
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .size(20.dp)
                                                    .align(Alignment.TopEnd)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = "Verified",
                                                        tint = TextWhite,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = profile.displayName,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "${profile.category.title} • ${profile.city}",
                                            color = TextMuted,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = profile.priceText,
                                                color = BadgeGold,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Black
                                            )

                                            // WhatsApp Quick Action
                                            if (profile.whatsapp.isNotBlank()) {
                                                IconButton(
                                                    onClick = {
                                                        val clean = profile.whatsapp.replace(Regex("[^0-9+]"), "")
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse("https://wa.me/$clean?text=Hi%20${Uri.encode(profile.displayName)},%20I%20saw%20your%20listing%20on%20Near%20Me%20Radar.")
                                                        )
                                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                                    },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Chat,
                                                        contentDescription = "WhatsApp",
                                                        tint = Color(0xFF25D366),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MEMBERSHIP SUBSCRIPTION PACKAGES SECTION
            item {
                Column(modifier = Modifier.padding(top = 18.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Membership Packages",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "View All Plans",
                            color = PinkPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.activeSubscreen.value = "membership" }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(configuredTiers) { tier ->
                            val tierColor = Color(tier.badgeColorHex)
                            Card(
                                onClick = {
                                    viewModel.activeSubscreen.value = "membership"
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                border = BorderStroke(1.2.dp, tierColor.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(210.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tier.title,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f)
                                            )
                                            TierBadge(tier)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = if (tier.priceZar == 0) "FREE" else "R${String.format(java.util.Locale.US, "%,d", tier.priceZar)}",
                                            color = tierColor,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black
                                        )

                                        Text(
                                            text = tier.billingPeriod,
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = tier.shortDescription,
                                            color = TextLight,
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 14.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.startRegistrationFlow(initialStep = 1, tier = tier)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = tierColor),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                    ) {
                                        Text(
                                            text = tier.ctaText,
                                            color = if (tier.badgeColorHex == 0xFFFFD700L || tier.badgeColorHex == 0xFFE2E8F0L) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2-COLUMN PROFILE LISTINGS (Using reusable ProfileCard)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Verified Listings (${profiles.size})",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Showing profiles in $displayCity",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "Filter Options",
                        color = PinkLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showFilterSheet = true }
                    )
                }
            }

            val chunkedProfiles = profiles.chunked(2)
            items(chunkedProfiles) { rowPair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (profile in rowPair) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileCard(
                                profile = profile,
                                onClick = { viewModel.openProfileDetail(profile) },
                                onFavouriteToggle = { viewModel.toggleFavourite(profile) },
                                showQuickActions = true
                            )
                        }
                    }
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // LIVE WEBCAMS SHOWS AT THE BOTTOM OF THE DASHBOARD CONTENT
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.8f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LiveTv, contentDescription = null, tint = PinkPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "LIVE WEBCAMS SHOWS",
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Surface(
                                    color = Color(0xFF261026),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.5.dp, PinkPrimary)
                                ) {
                                    Text(
                                        text = "CamSpace Live",
                                        color = PinkLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Experience real-time interactive live shows, private one-on-one sessions, and tip your favourite performers.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Embedded CamSpace Live Widget
                            CamSpaceLiveWidgetEmbed(
                                heightDp = 300
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PinkGradientButton(
                                text = "Enter Live Webcams Stage & Chat",
                                onClick = { viewModel.activeSubscreen.value = "live_entertainment" },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // LOCATION SELECTOR BOTTOM SHEET
    if (showLocationBottomSheet) {
        LocationSelectorBottomSheet(
            selectedCity = filterState.selectedCity,
            selectedProvince = filterState.selectedProvince,
            onLocationSelected = { province, city ->
                viewModel.setLocationFilter(province, city)
                viewModel.triggerShimmerReload()
            },
            onDismissRequest = { showLocationBottomSheet = false }
        )
    }

    // ADVANCED FILTER BOTTOM SHEET
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = DarkSurfaceElevated,
            scrimColor = Color.Black.copy(alpha = 0.7f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search & Discovery Filters",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = {
                            viewModel.filterState.value = com.example.ui.viewmodel.SearchFilterState()
                            viewModel.triggerShimmerReload()
                        }
                    ) {
                        Text("Reset All", color = PinkLight, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Verification & Membership", color = PinkLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.filterState.update { it.copy(onlyVerified = !it.onlyVerified) }
                            viewModel.triggerShimmerReload()
                        }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = filterState.onlyVerified,
                        onCheckedChange = { checked ->
                            viewModel.filterState.update { it.copy(onlyVerified = checked) }
                            viewModel.triggerShimmerReload()
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PinkPrimary, checkmarkColor = TextWhite)
                    )
                    Text("Verified Profiles Only (Photo & Full)", color = TextWhite, fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.filterState.update { it.copy(onlyFeatured = !it.onlyFeatured) }
                            viewModel.triggerShimmerReload()
                        }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = filterState.onlyFeatured,
                        onCheckedChange = { checked ->
                            viewModel.filterState.update { it.copy(onlyFeatured = checked) }
                            viewModel.triggerShimmerReload()
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PinkPrimary, checkmarkColor = TextWhite)
                    )
                    Text("Featured Listings Only", color = TextWhite, fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.filterState.update { it.copy(onlyVip = !it.onlyVip) }
                            viewModel.triggerShimmerReload()
                        }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = filterState.onlyVip,
                        onCheckedChange = { checked ->
                            viewModel.filterState.update { it.copy(onlyVip = checked) }
                            viewModel.triggerShimmerReload()
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PinkPrimary, checkmarkColor = TextWhite)
                    )
                    Text("VIP & Platinum Members Only", color = TextWhite, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                PinkGradientButton(
                    text = "Apply Filters",
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // AUTH MODAL DIALOG (REGISTER / SIGN IN)
    AuthModalDialog(
        isOpen = showAuthModal,
        initialMode = authInitialMode,
        viewModel = viewModel,
        onDismiss = { showAuthModal = false }
    )
}

@Composable
fun FeaturedProfileCard(
    profile: Profile,
    onClick: () -> Unit,
    onFavouriteToggle: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, if (profile.isGold) BadgeGold.copy(alpha = 0.6f) else DarkBorderPink),
        modifier = Modifier
            .width(200.dp)
            .testTag("featured_profile_card_${profile.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFF1A1A24))
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = profile.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x660A0A0F), Color(0xE60A0A0F))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OnlineIndicator(isOnline = profile.isOnline)

                    IconButton(
                        onClick = onFavouriteToggle,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x990A0A0F))
                    ) {
                        Icon(
                            imageVector = if (profile.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = if (profile.isFavourite) PinkPrimary else TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VerificationBadge(profile.verifiedLevel)
                    if (profile.membershipTier == MembershipTier.VIP) {
                        VipBadge()
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = profile.displayName,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${profile.city} • ${profile.age}y",
                        color = TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    RatingBar(rating = profile.rating)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = profile.priceText,
                    color = PinkPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun CategoryTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.height(76.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
