package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.Business
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Main Directory Screen using LazyVerticalGrid to display entertainment listings.
 * Each listing features:
 * - High-resolution Image
 * - Title
 * - Category Tag Badge
 * - Location, Rating, Specials, & Direct WhatsApp/Call actions
 */
@Composable
fun MainDirectoryScreen(
    viewModel: MainViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val businesses by viewModel.businesses.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val selectedCity = filterState.selectedCity ?: "All South Africa"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedBusinessForDetail by remember { mutableStateOf<Business?>(null) }
    var showSupportDialog by remember { mutableStateOf(false) }

    val categoryFilters = listOf(
        "All",
        "Gentlemen's Club",
        "Sensual Massage & Spa",
        "VIP Lounge",
        "Adult Boutique",
        "Escort Agency",
        "Fetish & BDSM"
    )

    // Filter businesses based on search, city, and category
    val filteredBusinesses = remember(businesses, searchQuery, selectedCategoryFilter, selectedCity) {
        businesses.filter { biz ->
            val matchesSearch = searchQuery.isBlank() ||
                    biz.name.contains(searchQuery, ignoreCase = true) ||
                    biz.category.contains(searchQuery, ignoreCase = true) ||
                    biz.description.contains(searchQuery, ignoreCase = true) ||
                    biz.city.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategoryFilter == "All" ||
                    biz.category.equals(selectedCategoryFilter, ignoreCase = true) ||
                    (selectedCategoryFilter == "Gentlemen's Club" && (biz.category.contains("Club", ignoreCase = true) || biz.category.contains("Venues", ignoreCase = true))) ||
                    (selectedCategoryFilter == "Sensual Massage & Spa" && (biz.category.contains("Massage", ignoreCase = true) || biz.category.contains("Spa", ignoreCase = true))) ||
                    (selectedCategoryFilter == "VIP Lounge" && biz.category.contains("Lounge", ignoreCase = true))

            val matchesCity = selectedCity == "All South Africa" ||
                    biz.city.contains(selectedCity, ignoreCase = true) ||
                    biz.province.contains(selectedCity, ignoreCase = true)

            matchesSearch && matchesCategory && matchesCity
        }
    }

    Column(
        modifier = modifier
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
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ENTERTAINMENT DIRECTORY",
                                color = PinkPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = BadgeGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, BadgeGold)
                            ) {
                                Text(
                                    text = "RSA 18+",
                                    color = BadgeGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Gentlemen's Clubs, Spas, VIP Lounges & Adult Boutiques",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.currentTab.value = "map" },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4285F4).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF4285F4), CircleShape)
                            .testTag("directory_open_map_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map View",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showSupportDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PinkGlow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Support",
                            tint = PinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // LAZY VERTICAL GRID (The core requirement)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("directory_vertical_grid")
        ) {
            // 1. Search Bar Header Item (Spans across both columns)
            item(span = { GridItemSpan(2) }) {
                Column {
                    DarkSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search clubs, sensual spas, lounges in $selectedCity..."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Tag Filters Strip
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(categoryFilters) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Surface(
                                onClick = { selectedCategoryFilter = cat },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PinkPrimary else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.testTag("category_filter_$cat")
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Results Counter & Location indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredBusinesses.size} Verified Entertainment Listings",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PinkLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = selectedCity, color = PinkLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 2. Directory Listings in Grid (Each with Image, Title, and Category Tag)
            if (filteredBusinesses.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No listings match your search criteria", color = TextMuted, fontSize = 14.sp)
                            TextButton(onClick = { searchQuery = ""; selectedCategoryFilter = "All" }) {
                                Text("Reset Filters", color = PinkPrimary)
                            }
                        }
                    }
                }
            } else {
                items(filteredBusinesses, key = { it.id }) { biz ->
                    EntertainmentListingGridCard(
                        business = biz,
                        onClick = { selectedBusinessForDetail = biz },
                        onCall = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${biz.phone}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showNotice("Calling ${biz.name} at ${biz.phone}")
                            }
                        },
                        onWhatsApp = {
                            viewModel.showNotice("Opening WhatsApp VIP booking for ${biz.name}")
                        }
                    )
                }
            }

            // 3. Technical Support Box (Full Width across 2 columns)
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(14.dp))
                TechnicalSupportBox(
                    onNotice = { viewModel.showNotice(it) }
                )
            }

            // 4. Secure Payment Gateway Footer Banner with PayPal & PayFast Logos
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
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
                                text = "Directory Advertiser Payments",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                PayPalLogo()
                                PayFastLogo()
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurePaymentsBadge()
                    }
                }
            }
        }
    }

    // Detail Dialog when a grid item is clicked
    selectedBusinessForDetail?.let { biz ->
        BusinessDetailDialog(
            business = biz,
            onDismiss = { selectedBusinessForDetail = null },
            onCall = {
                selectedBusinessForDetail = null
                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${biz.phone}"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    viewModel.showNotice("Calling ${biz.name}: ${biz.phone}")
                }
            },
            onGuestlist = {
                selectedBusinessForDetail = null
                viewModel.showNotice("VIP Reservation request confirmed for ${biz.name}!")
            }
        )
    }

    // Quick Support Dialog
    if (showSupportDialog) {
        Dialog(onDismissRequest = { showSupportDialog = false }) {
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                TechnicalSupportBox(
                    onNotice = { msg ->
                        showSupportDialog = false
                        viewModel.showNotice(msg)
                    }
                )
            }
        }
    }
}

/**
 * Grid Card Component for Entertainment Listings:
 * Displays Image, Title, Category Tag, Location, Rating, and Quick Action buttons.
 */
@Composable
fun EntertainmentListingGridCard(
    business: Business,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic Category Tag Color
    val tagColor = when {
        business.category.contains("Gentlemen", ignoreCase = true) -> Color(0xFFFF2A85)
        business.category.contains("Massage", ignoreCase = true) || business.category.contains("Spa", ignoreCase = true) -> Color(0xFF00E676)
        business.category.contains("VIP", ignoreCase = true) || business.category.contains("Lounge", ignoreCase = true) -> BadgeGold
        business.category.contains("Boutique", ignoreCase = true) -> Color(0xFF9C27B0)
        business.category.contains("Fetish", ignoreCase = true) || business.category.contains("BDSM", ignoreCase = true) -> Color(0xFFFF5252)
        else -> Color(0xFF00D2FF)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, if (business.isVip) DarkBorderPink else DarkBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("directory_grid_item_${business.id}")
    ) {
        Column {
            // 1. IMAGE CONTAINER WITH BADGES & GRADIENT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFF181824))
            ) {
                AsyncImage(
                    model = business.imageUrl,
                    contentDescription = business.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x660A0A0F), Color(0xCC0A0A0F))
                            )
                        )
                )

                // VIP Badge (Top Left)
                if (business.isVip) {
                    Surface(
                        color = Color(0xD90A0A0F),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, BadgeGold),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("VIP", color = BadgeGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Rating Badge (Top Right)
                Surface(
                    color = Color(0xD90A0A0F),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", business.rating),
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. CONTENT DETAILS (CATEGORY TAG, TITLE, LOCATION)
            Column(modifier = Modifier.padding(10.dp)) {
                // CATEGORY TAG (Prominent Badge)
                Surface(
                    color = tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.6.dp, tagColor.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = business.category,
                        color = tagColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // TITLE (Bold)
                Text(
                    text = business.name,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // LOCATION
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${business.city}, ${business.province.take(3)}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // DIRECT ACTION BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = onCall,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E1E2C),
                        border = BorderStroke(0.5.dp, DarkBorder),
                        modifier = Modifier.weight(1f).height(28.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = TextWhite, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        onClick = onWhatsApp,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0D251A),
                        border = BorderStroke(0.5.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f).height(28.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Chat", color = Color(0xFF25D366), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed View Dialog for a Business Directory Listing
 */
@Composable
fun BusinessDetailDialog(
    business: Business,
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onGuestlist: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkBorderPink),
            modifier = Modifier.fillMaxWidth().testTag("business_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = business.imageUrl,
                        contentDescription = business.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Category & VIP Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PinkPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.8.dp, PinkPrimary)
                        ) {
                            Text(
                                text = business.category,
                                color = PinkPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        RatingBar(rating = business.rating, reviewCount = business.reviewCount)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = business.name,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "${business.address} • ${business.city}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = business.description,
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Special Offer Banner
                    Surface(
                        color = Color(0xFF13131F),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.8.dp, DarkBorderPink.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = business.specials, color = PinkLight, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val ctx = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                try {
                                    val gmmIntentUri = android.net.Uri.parse("geo:${business.latitude},${business.longitude}?q=${android.net.Uri.encode("${business.name}, ${business.address}, ${business.city}")}")
                                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    if (mapIntent.resolveActivity(ctx.packageManager) != null) {
                                        ctx.startActivity(mapIntent)
                                    } else {
                                        val browserUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=${android.net.Uri.encode("${business.name} ${business.address} ${business.city}")}")
                                        ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, browserUri))
                                    }
                                } catch (e: Exception) {
                                    val browserUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=${android.net.Uri.encode("${business.name} ${business.city}")}")
                                    ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, browserUri))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("dialog_open_google_maps")
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open in Google Maps", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DarkOutlineButton(
                            text = "Call Venue",
                            icon = Icons.Default.Phone,
                            onClick = onCall,
                            modifier = Modifier.weight(1f)
                        )

                        PinkGradientButton(
                            text = "VIP Guestlist",
                            icon = Icons.Default.ConfirmationNumber,
                            onClick = onGuestlist,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
