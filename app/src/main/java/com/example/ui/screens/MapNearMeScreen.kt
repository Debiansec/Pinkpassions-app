package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Business
import com.example.model.Profile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Google Maps Visual Directory of Adult Entertainment Venues & Businesses
 * Features:
 * - Interactive Google Maps visual styling with custom venue pins
 * - Category filtering (Gentlemen's Clubs, Sensual Massage, VIP Lounges, Adult Boutiques, Escort Agencies)
 * - City Jump selector (Sandton, Cape Town, Durban, Pretoria)
 * - Map / List View toggle
 * - Selected Venue detail card with Google Maps navigation intent launch, WhatsApp VIP booking & call actions
 */
@Composable
fun MapNearMeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val businesses by viewModel.businesses.collectAsState()
    val profiles by viewModel.allProfiles.collectAsState()

    var directoryMode by remember { mutableStateOf("VENUES") } // VENUES or PROFILES
    var isMapView by remember { mutableStateOf(true) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedCity by remember { mutableStateOf("All RSA") }
    var selectedRadiusKm by remember { mutableStateOf(15f) }
    var zoomLevel by remember { mutableStateOf(1f) }

    var selectedBusiness by remember { mutableStateOf<Business?>(null) }
    var selectedProfile by remember { mutableStateOf<Profile?>(null) }
    var isRadarScanning by remember { mutableStateOf(true) }

    val venueCategories = listOf(
        "All",
        "Gentlemen's Club",
        "Sensual Massage & Spa",
        "VIP Lounge",
        "Adult Boutique",
        "Escort Agency",
        "Fetish & BDSM"
    )

    val cities = listOf(
        "All RSA",
        "Sandton & Joburg",
        "Cape Town",
        "Durban",
        "Pretoria"
    )

    // Filter businesses by Category & City
    val filteredBusinesses = remember(businesses, selectedCategoryFilter, selectedCity) {
        businesses.filter { biz ->
            val matchesCategory = selectedCategoryFilter == "All" ||
                    biz.category.equals(selectedCategoryFilter, ignoreCase = true) ||
                    (selectedCategoryFilter == "Gentlemen's Club" && (biz.category.contains("Club", ignoreCase = true) || biz.category.contains("Venues", ignoreCase = true))) ||
                    (selectedCategoryFilter == "Sensual Massage & Spa" && (biz.category.contains("Massage", ignoreCase = true) || biz.category.contains("Spa", ignoreCase = true))) ||
                    (selectedCategoryFilter == "VIP Lounge" && biz.category.contains("Lounge", ignoreCase = true))

            val matchesCity = when (selectedCity) {
                "Sandton & Joburg" -> biz.city.contains("Johannesburg", ignoreCase = true) || biz.city.contains("Sandton", ignoreCase = true) || biz.province.contains("Gauteng", ignoreCase = true)
                "Cape Town" -> biz.city.contains("Cape Town", ignoreCase = true) || biz.province.contains("Western Cape", ignoreCase = true)
                "Durban" -> biz.city.contains("Durban", ignoreCase = true) || biz.city.contains("Umhlanga", ignoreCase = true) || biz.province.contains("KwaZulu", ignoreCase = true)
                "Pretoria" -> biz.city.contains("Pretoria", ignoreCase = true) || biz.city.contains("Centurion", ignoreCase = true)
                else -> true
            }

            matchesCategory && matchesCity
        }
    }

    // Default select first business if none selected
    LaunchedEffect(filteredBusinesses) {
        if (selectedBusiness == null && filteredBusinesses.isNotEmpty()) {
            selectedBusiness = filteredBusinesses.firstOrNull()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP HEADER WITH TITLE & VIEW TOGGLES
            Surface(
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MAP & RADAR DIRECTORY",
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0x3300E676),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, Color(0xFF00E676))
                                ) {
                                    Text(
                                        text = "LIVE GPS",
                                        color = Color(0xFF00E676),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Adult entertainment venues & businesses on Google Maps",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Map / List toggle pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(modifier = Modifier.padding(3.dp)) {
                                IconButton(
                                    onClick = { isMapView = true },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (isMapView) PinkPrimary else Color.Transparent)
                                        .testTag("map_view_toggle_map")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Map View",
                                        tint = if (isMapView) TextWhite else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { isMapView = false },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (!isMapView) PinkPrimary else Color.Transparent)
                                        .testTag("map_view_toggle_list")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "List View",
                                        tint = if (!isMapView) TextWhite else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Switcher: Venues & Clubs vs Escorts vs FWB 50km Radar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                            .padding(2.dp)
                    ) {
                        Surface(
                            onClick = { directoryMode = "VENUES" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (directoryMode == "VENUES") PinkPrimary else Color.Transparent,
                            modifier = Modifier.weight(1f).testTag("mode_venues_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nightlife,
                                    contentDescription = null,
                                    tint = if (directoryMode == "VENUES") TextWhite else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Venues (${filteredBusinesses.size})",
                                    color = if (directoryMode == "VENUES") TextWhite else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = { directoryMode = "PROFILES" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (directoryMode == "PROFILES") PinkPrimary else Color.Transparent,
                            modifier = Modifier.weight(1f).testTag("mode_profiles_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonPinCircle,
                                    contentDescription = null,
                                    tint = if (directoryMode == "PROFILES") TextWhite else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Escorts (${profiles.size})",
                                    color = if (directoryMode == "PROFILES") TextWhite else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = { directoryMode = "FWB" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (directoryMode == "FWB") Color(0xFF4285F4) else Color.Transparent,
                            modifier = Modifier.weight(1f).testTag("mode_fwb_50km_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = if (directoryMode == "FWB") TextWhite else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Radar 50km",
                                    color = if (directoryMode == "FWB") TextWhite else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = { directoryMode = "MAPS_SDK" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (directoryMode == "MAPS_SDK") PinkPrimary else Color.Transparent,
                            modifier = Modifier.weight(1.1f).testTag("mode_maps_sdk_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = if (directoryMode == "MAPS_SDK") TextWhite else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Google Maps",
                                    color = if (directoryMode == "MAPS_SDK") TextWhite else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // CATEGORY & DISTANCE RADIUS FILTER STRIP (For Venues or FWB Mode)
            if (directoryMode == "VENUES") {
                Column(modifier = Modifier.background(DarkSurface)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(venueCategories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Surface(
                                onClick = { selectedCategoryFilter = cat },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) PinkPrimary else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.testTag("map_category_$cat")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    val icon = getCategoryIcon(cat)
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) TextWhite else PinkLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = cat,
                                        color = if (isSelected) TextWhite else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // City jump filters
                    LazyRow(
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(cities) { city ->
                            val isSelected = selectedCity == city
                            Surface(
                                onClick = { selectedCity = city },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DarkBorderPink else Color.Transparent,
                                border = BorderStroke(0.6.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.testTag("map_city_$city")
                            ) {
                                Text(
                                    text = city,
                                    color = if (isSelected) PinkLight else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            } else if (directoryMode == "FWB") {
                // 50KM RADAR DISTANCE AND CATEGORY STRIP
                Column(modifier = Modifier.background(DarkSurface).padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("50km Proximity Radar", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Radius: ${selectedRadiusKm.toInt()} km", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf(5f, 15f, 30f, 50f)) { km ->
                            val isSelected = selectedRadiusKm == km
                            Surface(
                                onClick = { selectedRadiusKm = km },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF4285F4) else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF4285F4) else DarkBorder),
                                modifier = Modifier.testTag("fwb_radius_${km.toInt()}km")
                            ) {
                                Text(
                                    text = if (km == 50f) "50 km (Max SA)" else "${km.toInt()} km",
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (directoryMode == "MAPS_SDK") {
                GoogleMapsDiscoveryScreen(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            } else if (isMapView) {
                // GOOGLE MAPS VISUAL CANVAS CONTAINER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F111A))
                        .border(1.2.dp, DarkBorderPink, RoundedCornerShape(20.dp))
                ) {
                    // Google Maps Styled Roadmap Grid Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSpacing = 44.dp.toPx()
                        for (x in 0..(size.width / gridSpacing).toInt()) {
                            drawLine(
                                color = Color(0xFF191C2B),
                                start = androidx.compose.ui.geometry.Offset(x * gridSpacing, 0f),
                                end = androidx.compose.ui.geometry.Offset(x * gridSpacing, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(size.height / gridSpacing).toInt()) {
                            drawLine(
                                color = Color(0xFF191C2B),
                                start = androidx.compose.ui.geometry.Offset(0f, y * gridSpacing),
                                end = androidx.compose.ui.geometry.Offset(size.width, y * gridSpacing),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // Visual Radar Animation Overlay (360-degree rotating radar scan & sonar pulse)
                    if (isRadarScanning) {
                        RadarScanOverlay(
                            modifier = Modifier.fillMaxSize(),
                            isScanning = true,
                            rangeKm = 50
                        )
                    }

                    // Center User Radar Pulse
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(PinkGlow.copy(alpha = 0.15f))
                            .border(1.5.dp, PinkPrimary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary)
                                .border(2.dp, TextWhite, CircleShape)
                        )
                    }

                    // Floating Radar Scanning Status Pill
                    Surface(
                        color = Color(0xDD13131D),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, PinkPrimary),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clickable { isRadarScanning = !isRadarScanning }
                            .testTag("toggle_radar_animation_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isRadarScanning) Color(0xFF00E676) else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRadarScanning) "50km Radar: SCANNING" else "50km Radar: PAUSED",
                                color = if (isRadarScanning) PinkLight else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // VENUE PINS ON MAP
                    if (directoryMode == "VENUES") {
                        val venuePinOffsets = listOf(
                            Pair(0.18f, 0.22f),
                            Pair(0.68f, 0.25f),
                            Pair(0.32f, 0.58f),
                            Pair(0.72f, 0.62f),
                            Pair(0.50f, 0.38f),
                            Pair(0.22f, 0.76f),
                            Pair(0.78f, 0.78f)
                        )

                        filteredBusinesses.forEachIndexed { index, biz ->
                            val offset = venuePinOffsets.getOrElse(index % venuePinOffsets.size) { Pair(0.5f, 0.5f) }
                            val isSelected = selectedBusiness?.id == biz.id
                            val pinColor = getCategoryColor(biz.category)

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (offset.first * 290 * zoomLevel).coerceIn(10f, 260f).dp,
                                        y = (offset.second * 340 * zoomLevel).coerceIn(10f, 260f).dp
                                    )
                                    .clickable { selectedBusiness = biz }
                                    .testTag("map_pin_venue_${biz.id}")
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) pinColor else DarkSurfaceElevated,
                                    border = BorderStroke(1.5.dp, if (isSelected) TextWhite else pinColor),
                                    shadowElevation = if (isSelected) 10.dp else 4.dp
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = getCategoryIcon(biz.category),
                                            contentDescription = null,
                                            tint = if (isSelected) TextWhite else pinColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = biz.name.take(12),
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                maxLines = 1
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = BadgeGold,
                                                    modifier = Modifier.size(9.dp)
                                                )
                                                Text(
                                                    text = "${biz.rating}",
                                                    color = if (isSelected) TextWhite else BadgeGold,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (directoryMode == "FWB") {
                        // 50KM FWB & HOOKUP RADAR PINS ON MAP
                        val fwbPinOffsets = listOf(
                            Triple(0.28f, 0.28f, "6.2 km"),
                            Triple(0.72f, 0.32f, "12.8 km"),
                            Triple(0.35f, 0.68f, "24.5 km"),
                            Triple(0.68f, 0.64f, "38.0 km"),
                            Triple(0.48f, 0.22f, "8.4 km"),
                            Triple(0.20f, 0.74f, "47.2 km")
                        )

                        val fwbProfiles = profiles.take(6)
                        fwbProfiles.forEachIndexed { index, profile ->
                            val item = fwbPinOffsets.getOrElse(index % fwbPinOffsets.size) { Triple(0.4f, 0.4f, "15.0 km") }
                            val isSelected = selectedProfile?.id == profile.id

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (item.first * 290 * zoomLevel).coerceIn(10f, 260f).dp,
                                        y = (item.second * 340 * zoomLevel).coerceIn(10f, 260f).dp
                                    )
                                    .clickable { selectedProfile = profile }
                                    .testTag("map_pin_fwb_${profile.id}")
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) Color(0xFF4285F4) else DarkSurfaceElevated,
                                    border = BorderStroke(1.2.dp, if (isSelected) TextWhite else Color(0xFF4285F4)),
                                    shadowElevation = 8.dp
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    ) {
                                        Text("🔥", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Column {
                                            Text(
                                                text = profile.displayName.take(8),
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
                                            )
                                            Text(
                                                text = item.third,
                                                color = if (isSelected) TextWhite else Color(0xFF00E5FF),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // INDEPENDENT ESCORT PROFILES PINS ON MAP
                        val profilePinOffsets = listOf(
                            Pair(0.24f, 0.30f),
                            Pair(0.70f, 0.28f),
                            Pair(0.38f, 0.65f),
                            Pair(0.66f, 0.60f),
                            Pair(0.52f, 0.18f),
                            Pair(0.18f, 0.70f)
                        )

                        profiles.take(6).forEachIndexed { index, profile ->
                            val offset = profilePinOffsets.getOrElse(index % profilePinOffsets.size) { Pair(0.4f, 0.4f) }
                            val isSelected = selectedProfile?.id == profile.id

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (offset.first * 290 * zoomLevel).coerceIn(10f, 260f).dp,
                                        y = (offset.second * 340 * zoomLevel).coerceIn(10f, 260f).dp
                                    )
                                    .clickable { selectedProfile = profile }
                                    .testTag("map_pin_profile_${profile.id}")
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) PinkPrimary else DarkSurfaceElevated,
                                    border = BorderStroke(1.2.dp, if (profile.isGold) BadgeGold else PinkPrimary),
                                    shadowElevation = 6.dp
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PersonPinCircle,
                                            contentDescription = null,
                                            tint = if (isSelected) TextWhite else PinkLight,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = profile.displayName.take(9),
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // GOOGLE MAPS CONTROLS (Zoom +/- & Google Maps API Indicator)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Surface(
                            color = Color(0xDD000000),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, DarkBorderPink)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Google Maps Live",
                                    color = TextWhite,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Zoom In/Out Buttons
                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Column {
                                IconButton(
                                    onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(1.6f) },
                                    modifier = Modifier.size(32.dp).testTag("map_zoom_in")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextWhite, modifier = Modifier.size(16.dp))
                                }
                                Divider(color = DarkBorder, thickness = 0.5.dp)
                                IconButton(
                                    onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) },
                                    modifier = Modifier.size(32.dp).testTag("map_zoom_out")
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextWhite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // BOTTOM SELECTED VENUE PREVIEW CARD
                    if (directoryMode == "VENUES" && selectedBusiness != null) {
                        val biz = selectedBusiness!!
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(1.2.dp, PinkPrimary),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .testTag("map_selected_venue_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF222233))
                                    ) {
                                        AsyncImage(
                                            model = biz.imageUrl,
                                            contentDescription = biz.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = biz.name,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            FiveStarRatingBadge(rating = biz.rating, reviewCount = biz.reviewCount)
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = getCategoryColor(biz.category).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(0.5.dp, getCategoryColor(biz.category))
                                            ) {
                                                Text(
                                                    text = biz.category,
                                                    color = getCategoryColor(biz.category),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${biz.city} • 1.4 km",
                                                color = TextMuted,
                                                fontSize = 10.5.sp
                                            )
                                        }

                                        Text(
                                            text = biz.specials,
                                            color = PinkLight,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = DarkBorder, thickness = 0.6.dp)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Action Buttons (Open in Google Maps & WhatsApp VIP)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Open in Google Maps navigation button
                                    Button(
                                        onClick = {
                                            openGoogleMapsDirections(context, biz)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4285F4),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .weight(1.1f)
                                            .height(36.dp)
                                            .testTag("button_open_google_maps")
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Direct WhatsApp VIP Booking
                                    Button(
                                        onClick = {
                                            openWhatsAppBooking(context, biz)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF25D366),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .testTag("button_whatsapp_venue")
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Call Venue Phone
                                    IconButton(
                                        onClick = {
                                            try {
                                                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${biz.phone}"))
                                                context.startActivity(dial)
                                            } catch (e: Exception) {
                                                viewModel.showNotice("Call ${biz.phone}")
                                            }
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSurfaceVariant)
                                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                            .testTag("button_call_venue")
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Call", tint = PinkLight, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    } else if (directoryMode == "FWB" && selectedProfile != null) {
                        val p = selectedProfile!!
                        Card(
                            onClick = { viewModel.openProfileDetail(p) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(1.2.dp, Color(0xFF4285F4)),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .testTag("map_selected_fwb_card")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF222233))
                                    ) {
                                        AsyncImage(
                                            model = p.avatarUrl,
                                            contentDescription = p.displayName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = p.displayName,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFF4285F4).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(0.5.dp, Color(0xFF4285F4))
                                            ) {
                                                Text(
                                                    text = "FWB / Hookup Match",
                                                    color = Color(0xFF00E5FF),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "📍 ${p.area}, ${p.city} • Within 50km Radar",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Looking for: Casual Hookups, FWB & Chemistry",
                                            color = PinkLight,
                                            fontSize = 10.5.sp,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.activeChatRoomId.value = "room_fwb_sa"
                                            viewModel.currentTab.value = "chat"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.weight(1f).height(36.dp).testTag("button_join_fwb_chat")
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("FWB Chat Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.openProfileDetail(p) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.weight(1f).height(36.dp).testTag("button_view_fwb_profile")
                                    ) {
                                        Text("View Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (directoryMode == "PROFILES" && selectedProfile != null) {
                        val p = selectedProfile!!
                        Card(
                            onClick = { viewModel.openProfileDetail(p) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(1.dp, PinkPrimary),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .testTag("map_selected_profile_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF222233))
                                ) {
                                    AsyncImage(
                                        model = p.avatarUrl,
                                        contentDescription = p.displayName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = p.displayName,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VerifiedPremiumBadge(
                                            membershipTier = p.membershipTier,
                                            verificationLevel = p.verifiedLevel,
                                            isGold = p.isGold,
                                            compact = true
                                        )
                                    }
                                    Text(
                                        text = "${p.area}, ${p.city} • 2.4 km away",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = p.priceText,
                                        color = PinkPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = { viewModel.openProfileDetail(p) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // LIST VIEW OF VENUES OR PROFILES
                LazyColumn(
                    contentPadding = PaddingValues(14.dp, 8.dp, 14.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (directoryMode == "VENUES") {
                        items(filteredBusinesses, key = { it.id }) { biz ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth().testTag("venue_list_item_${biz.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF222233))
                                    ) {
                                        AsyncImage(
                                            model = biz.imageUrl,
                                            contentDescription = biz.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = biz.name,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.5.sp
                                            )
                                            FiveStarRatingBadge(rating = biz.rating, reviewCount = biz.reviewCount)
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "${biz.category} • ${biz.city}",
                                            color = PinkLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = biz.address,
                                            color = TextMuted,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = { openGoogleMapsDirections(context, biz) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Directions", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { openWhatsAppBooking(context, biz) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(profiles, key = { it.id }) { profile ->
                            Card(
                                onClick = { viewModel.openProfileDetail(profile) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF222233))
                                    ) {
                                        AsyncImage(
                                            model = profile.avatarUrl,
                                            contentDescription = profile.displayName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = profile.displayName,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            VerifiedPremiumBadge(
                                                membershipTier = profile.membershipTier,
                                                verificationLevel = profile.verifiedLevel,
                                                isGold = profile.isGold,
                                                compact = true
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${profile.area}, ${profile.city} • 1.8 km away",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = profile.priceText,
                                            color = PinkLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Details",
                                        tint = TextMuted
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

private fun openGoogleMapsDirections(context: Context, business: Business) {
    try {
        val gmmIntentUri = Uri.parse("geo:${business.latitude},${business.longitude}?q=${Uri.encode("${business.name}, ${business.address}, ${business.city}")}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to web maps
            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode("${business.name} ${business.address} ${business.city}")}")
            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    } catch (e: Exception) {
        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode("${business.name} ${business.city}")}")
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}

private fun openWhatsAppBooking(context: Context, business: Business) {
    try {
        val cleanNumber = business.phone.replace("+", "").replace(" ", "")
        val uri = Uri.parse("https://wa.me/$cleanNumber?text=Hi%20${Uri.encode(business.name)},%20I%20would%20like%20to%20inquire%20about%20VIP%20table%20reservation%20from%20Pink%20Passions.")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: Exception) {
        // Fallback
    }
}

private fun getCategoryColor(category: String): Color {
    return when {
        category.contains("Club", ignoreCase = true) || category.contains("Venues", ignoreCase = true) -> Color(0xFF9C27B0) // Purple
        category.contains("Massage", ignoreCase = true) || category.contains("Spa", ignoreCase = true) -> Color(0xFF00E5FF) // Cyan
        category.contains("Lounge", ignoreCase = true) || category.contains("VIP", ignoreCase = true) -> Color(0xFFFFD700) // Gold
        category.contains("Boutique", ignoreCase = true) || category.contains("Shop", ignoreCase = true) -> Color(0xFFFF2A85) // Hot Pink
        category.contains("Agency", ignoreCase = true) -> Color(0xFF00D2FF) // Electric Blue
        else -> PinkPrimary
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when {
        category.contains("Club", ignoreCase = true) || category.contains("Venues", ignoreCase = true) -> Icons.Default.Nightlife
        category.contains("Massage", ignoreCase = true) || category.contains("Spa", ignoreCase = true) -> Icons.Default.Spa
        category.contains("Lounge", ignoreCase = true) || category.contains("VIP", ignoreCase = true) -> Icons.Default.Diamond
        category.contains("Boutique", ignoreCase = true) || category.contains("Shop", ignoreCase = true) -> Icons.Default.ShoppingBag
        category.contains("Agency", ignoreCase = true) -> Icons.Default.Business
        category.contains("Fetish", ignoreCase = true) -> Icons.Default.LocalFireDepartment
        else -> Icons.Default.Place
    }
}
