package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.model.VerificationLevel
import com.example.ui.components.RadarScanOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * Google Maps SDK Screen displaying verified nearby companions, hookup spots,
 * gentlemen's clubs, and adult entertainment venues within a 50km radius.
 */
data class MapListingItem(
    val id: String,
    val title: String,
    val category: String,
    val latLng: LatLng,
    val distanceKm: Float,
    val city: String,
    val priceText: String,
    val avatarUrl: String,
    val phone: String,
    val whatsapp: String,
    val isCompanion: Boolean,
    val profileRef: Profile? = null,
    val businessRef: Business? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapsDiscoveryScreen(
    viewModel: MainViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val profiles by viewModel.allProfiles.collectAsState()
    val businesses by viewModel.businesses.collectAsState()
    val userCity by viewModel.userCity.collectAsState()

    // South Africa Center Coordinates (Default: Sandton, Johannesburg)
    var userLocation by remember { mutableStateOf(LatLng(-26.1076, 28.0567)) }
    var selectedRadiusKm by remember { mutableStateOf(50f) }
    var selectedTypeFilter by remember { mutableStateOf("All") } // "All", "Companions", "Clubs & Lounges", "Spas"
    var selectedListing by remember { mutableStateOf<MapListingItem?>(null) }
    var isMapSatellite by remember { mutableStateOf(false) }

    // Map Camera State
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 11f)
    }

    // Convert profiles & businesses to MapListingItems with realistic South African GPS offsets
    val allListings = remember(profiles, businesses) {
        val list = mutableListOf<MapListingItem>()

        // Add companion profiles
        profiles.forEachIndexed { index, p ->
            val (baseLat, baseLng) = when {
                p.city.contains("Cape Town", ignoreCase = true) -> Pair(-33.9249, 18.4241)
                p.city.contains("Durban", ignoreCase = true) || p.city.contains("Umhlanga", ignoreCase = true) -> Pair(-29.8587, 31.0218)
                p.city.contains("Pretoria", ignoreCase = true) -> Pair(-25.7479, 28.2293)
                else -> Pair(-26.1076, 28.0567) // Sandton / Joburg
            }
            // Add subtle deterministic GPS jitter
            val latOffset = ((index % 7) - 3) * 0.025
            val lngOffset = ((index % 5) - 2) * 0.028
            val dist = 3.5f + (index * 2.1f) % 45f

            list.add(
                MapListingItem(
                    id = p.id,
                    title = p.displayName,
                    category = p.category.title,
                    latLng = LatLng(baseLat + latOffset, baseLng + lngOffset),
                    distanceKm = dist,
                    city = p.city,
                    priceText = p.priceText,
                    avatarUrl = p.avatarUrl,
                    phone = p.phone,
                    whatsapp = p.whatsapp,
                    isCompanion = true,
                    profileRef = p
                )
            )
        }

        // Add verified venues and clubs
        businesses.forEachIndexed { index, b ->
            val (baseLat, baseLng) = when {
                b.city.contains("Cape Town", ignoreCase = true) -> Pair(-33.9249, 18.4241)
                b.city.contains("Durban", ignoreCase = true) -> Pair(-29.8587, 31.0218)
                b.city.contains("Pretoria", ignoreCase = true) -> Pair(-25.7479, 28.2293)
                else -> Pair(-26.1200, 28.0400) // Sandton
            }
            val latOffset = ((index % 6) - 2.5) * 0.03
            val lngOffset = ((index % 8) - 4) * 0.022
            val dist = 2.0f + (index * 3.2f) % 48f

            list.add(
                MapListingItem(
                    id = b.id,
                    title = b.name,
                    category = b.category,
                    latLng = LatLng(baseLat + latOffset, baseLng + lngOffset),
                    distanceKm = dist,
                    city = b.city,
                    priceText = "Entry R${b.membershipFeeZar}",
                    avatarUrl = b.imageUrl,
                    phone = b.phone,
                    whatsapp = b.phone,
                    isCompanion = false,
                    businessRef = b
                )
            )
        }

        list
    }

    // Filter by radius (<= 50km) and Category
    val filteredListings = remember(allListings, selectedRadiusKm, selectedTypeFilter, userLocation) {
        allListings.filter { item ->
            val withinRadius = item.distanceKm <= selectedRadiusKm
            val matchesType = when (selectedTypeFilter) {
                "Companions" -> item.isCompanion
                "Clubs & Lounges" -> !item.isCompanion && (item.category.contains("Club", ignoreCase = true) || item.category.contains("Lounge", ignoreCase = true))
                "Spas" -> !item.isCompanion && (item.category.contains("Massage", ignoreCase = true) || item.category.contains("Spa", ignoreCase = true))
                else -> true
            }
            withinRadius && matchesType
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("screen_google_maps_sdk")
    ) {
        // 1. GOOGLE MAPS COMPOSE CANVAS
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("google_map_view"),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = if (isMapSatellite) MapType.HYBRID else MapType.NORMAL,
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                zoomGesturesEnabled = true
            )
        ) {
            // Draw 50km Proximity Circle around user GPS location
            Circle(
                center = userLocation,
                radius = (selectedRadiusKm * 1000).toDouble(), // in meters
                fillColor = androidx.compose.ui.graphics.Color(0x22E31999),
                strokeColor = androidx.compose.ui.graphics.Color(0xFFE31999),
                strokeWidth = 3.5f
            )

            // User Location GPS Pin Marker
            Marker(
                state = MarkerState(position = userLocation),
                title = "Your Location ($userCity)",
                snippet = "50km Hookup & Venue Discovery Radar Active",
                onClick = {
                    viewModel.showNotice("Your GPS Center: $userCity (50km Radar Active)")
                    false
                }
            )

            // Markers for nearby listings
            filteredListings.forEach { item ->
                Marker(
                    state = MarkerState(position = item.latLng),
                    title = item.title,
                    snippet = "${item.category} • ${String.format(java.util.Locale.US, "%.1f", item.distanceKm)} km away",
                    onClick = {
                        selectedListing = item
                        false
                    }
                )
            }
        }

        // Radar Scanning Animation Overlay on Google Maps
        RadarScanOverlay(
            modifier = Modifier.fillMaxSize(),
            isScanning = true,
            rangeKm = selectedRadiusKm.toInt()
        )

        // 2. TOP FLOATING CONTROLS (HEADER & CITY SELECTOR)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                color = DarkSurfaceElevated.copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onBack != null) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.size(32.dp).testTag("maps_back_button")
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(Icons.Default.Place, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Google Maps Radar (50km)",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${filteredListings.size} verified spots within ${selectedRadiusKm.toInt()}km of $userCity",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Map Layer Satellite Toggle
                        IconButton(
                            onClick = { isMapSatellite = !isMapSatellite },
                            modifier = Modifier.size(32.dp).testTag("button_toggle_satellite")
                        ) {
                            Icon(
                                if (isMapSatellite) Icons.Default.Map else Icons.Default.Satellite,
                                contentDescription = "Layer",
                                tint = if (isMapSatellite) PinkPrimary else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // City quick jump buttons
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val cities = listOf(
                            Pair("Sandton (JHB)", LatLng(-26.1076, 28.0567)),
                            Pair("Cape Town (WC)", LatLng(-33.9249, 18.4241)),
                            Pair("Durban (KZN)", LatLng(-29.8587, 31.0218)),
                            Pair("Pretoria (GP)", LatLng(-25.7479, 28.2293))
                        )
                        items(cities) { (name, coords) ->
                            val isSelected = (userLocation.latitude == coords.latitude)
                            Surface(
                                onClick = {
                                    userLocation = coords
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(coords, 11.5f),
                                            800
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PinkPrimary else DarkSurfaceVariant,
                                border = BorderStroke(0.8.dp, if (isSelected) PinkLight else DarkBorder),
                                modifier = Modifier.testTag("city_jump_${name.take(4).lowercase()}")
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Distance & Type Filter chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val types = listOf("All", "Companions", "Clubs & Lounges", "Spas")
                        items(types) { t ->
                            val isSelected = selectedTypeFilter == t
                            Surface(
                                onClick = { selectedTypeFilter = t },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PinkSubtle else DarkBackground,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder)
                            ) {
                                Text(
                                    text = t,
                                    color = if (isSelected) PinkLight else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        item {
                            // 50km toggle chip
                            Surface(
                                onClick = {
                                    selectedRadiusKm = if (selectedRadiusKm == 50f) 25f else if (selectedRadiusKm == 25f) 10f else 50f
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFF152642),
                                border = BorderStroke(1.dp, Color(0xFF4285F4))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Radar, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Radius: ${selectedRadiusKm.toInt()}km", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. FLOATING ACTION BUTTONS (RECENTER GPS & REFRESH)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (selectedListing != null) 220.dp else 90.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(userLocation, 12f),
                            600
                        )
                    }
                    viewModel.showNotice("Centered on 50km GPS radar")
                },
                containerColor = DarkSurfaceElevated,
                contentColor = PinkPrimary,
                shape = CircleShape,
                modifier = Modifier.size(48.dp).testTag("fab_recenter_maps_gps")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My GPS Location")
            }

            FloatingActionButton(
                onClick = {
                    viewModel.activeSubscreen.value = "local_chat_room"
                },
                containerColor = PinkPrimary,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier.size(48.dp).testTag("fab_maps_open_fwb_chat")
            ) {
                Icon(Icons.Default.Forum, contentDescription = "FWB Chat")
            }
        }

        // 4. SELECTED LISTING PREVIEW DETAIL CARD
        AnimatedVisibility(
            visible = selectedListing != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 85.dp)
        ) {
            selectedListing?.let { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.2.dp, PinkPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("card_map_listing_preview")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.avatarUrl,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { selectedListing = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Text(
                                    text = "${item.category} • ${item.city}",
                                    color = PinkLight,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.1f", item.distanceKm)} km away from you",
                                        color = Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.priceText,
                                        color = BadgeGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Open Google Maps App navigation & WhatsApp / Profile
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Launch external Google Maps Navigation Intent
                                    val gmmIntentUri = Uri.parse("google.navigation:q=${item.latLng.latitude},${item.latLng.longitude}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val webMap = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${item.latLng.latitude},${item.latLng.longitude}"))
                                        context.startActivity(webMap)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = BorderStroke(1.dp, Color(0xFF4285F4)),
                                modifier = Modifier.weight(1f).testTag("button_navigate_google_maps")
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Navigate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            if (item.whatsapp.isNotBlank()) {
                                Button(
                                    onClick = {
                                        val cleanNum = item.whatsapp.replace(Regex("[^0-9+]"), "")
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum?text=Hello%20${Uri.encode(item.title)},%20I%20saw%20your%20listing%20on%20Pink%20Passions%20Radar."))
                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    modifier = Modifier.weight(1f).testTag("button_whatsapp_map_listing")
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    if (item.isCompanion && item.profileRef != null) {
                                        viewModel.selectedProfile.value = item.profileRef
                                        viewModel.activeSubscreen.value = "profile_detail"
                                    } else if (item.businessRef != null) {
                                        viewModel.selectedBusiness.value = item.businessRef
                                        viewModel.activeSubscreen.value = "business_detail"
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                modifier = Modifier.weight(1.1f).testTag("button_view_full_map_profile")
                            ) {
                                Text("View Profile", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
