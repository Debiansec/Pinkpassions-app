package com.example.ui.screens

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import coil.request.ImageRequest
import kotlinx.coroutines.flow.update
import com.example.data.MockDataProvider
import com.example.model.Profile
import com.example.model.ProfileCategory
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SortOption

/**
 * Discovery Screen Component
 * Fetches and displays featured content from Firebase Firestore,
 * styled with Pink Passions deep pinks and dark neutrals brand colors.
 */
@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filterState by viewModel.filterState.collectAsState()
    val profiles by viewModel.filteredProfiles.collectAsState()
    val isLoadingFeeds by viewModel.isLoadingFeeds.collectAsState()

    // Firestore Live Featured Content State
    val firestoreService = remember { viewModel.repository.firestoreService }
    val featuredCloudProfiles by firestoreService.observeFeaturedDiscoveryProfiles()
        .collectAsState(initial = emptyList())

    // Merge or fallback to featured local profiles if firestore cloud is empty / offline
    val featuredList = remember(featuredCloudProfiles, profiles) {
        if (featuredCloudProfiles.isNotEmpty()) {
            featuredCloudProfiles.map { map ->
                Profile(
                    id = map["id"] as? String ?: "feat_cloud",
                    userId = map["userId"] as? String ?: "user_c",
                    displayName = map["displayName"] as? String ?: "Featured Escort",
                    age = (map["age"] as? Long)?.toInt() ?: 24,
                    province = map["province"] as? String ?: "Gauteng",
                    city = map["city"] as? String ?: "Sandton",
                    area = map["area"] as? String ?: "Sandhurst",
                    category = ProfileCategory.valueOf(map["category"] as? String ?: ProfileCategory.INDEPENDENT.name),
                    description = map["description"] as? String ?: "Exclusive verified companion",
                    rating = (map["rating"] as? Double)?.toFloat() ?: 4.9f,
                    reviewCount = (map["reviewCount"] as? Long)?.toInt() ?: 28,
                    isFeatured = true,
                    isGold = true,
                    isOnline = true,
                    priceText = map["priceText"] as? String ?: "R1,500 / hr",
                    avatarUrl = map["avatarUrl"] as? String ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                    services = map["services"] as? String ?: "Dinner Dates, VIP Travel",
                    phone = map["phone"] as? String ?: "+27 82 555 0199",
                    whatsapp = map["whatsapp"] as? String ?: "+27825550199"
                )
            }
        } else {
            profiles.filter { it.isFeatured || it.isGold }
        }
    }

    var selectedProvinceExpanded by remember { mutableStateOf<String?>("Gauteng") }
    var selectedTabSection by remember { mutableStateOf("featured") } // "featured", "categories", "locations", "live"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("screen_discovery"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP HEADER WITH BRAND STYLING & CLOUD BADGE
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Discovery & Featured",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Curated VIP companions & entertainment in South Africa",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    // Firestore Live Cloud Badge
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
                                text = "Firestore Live",
                                color = PinkLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                DarkSearchBar(
                    query = filterState.query,
                    onQueryChange = {
                        viewModel.setSearchQuery(it)
                        viewModel.triggerShimmerReload()
                    },
                    placeholder = "Search featured VIPs, cities, services...",
                    hasActiveFilters = filterState.selectedCategory != null || filterState.onlyVerified
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Segment Tabs
                TabRow(
                    selectedTabIndex = when (selectedTabSection) {
                        "featured" -> 0
                        "categories" -> 1
                        "locations" -> 2
                        "live" -> 3
                        else -> 0
                    },
                    containerColor = DarkSurfaceVariant,
                    contentColor = PinkPrimary,
                    indicator = { tabPositions ->
                        val index = when (selectedTabSection) {
                            "featured" -> 0
                            "categories" -> 1
                            "locations" -> 2
                            "live" -> 3
                            else -> 0
                        }
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = PinkPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabSection == "featured",
                        onClick = { selectedTabSection = "featured" },
                        text = { Text("Featured VIPs", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("discover_tab_featured")
                    )
                    Tab(
                        selected = selectedTabSection == "categories",
                        onClick = { selectedTabSection = "categories" },
                        text = { Text("Categories", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("discover_tab_categories")
                    )
                    Tab(
                        selected = selectedTabSection == "locations",
                        onClick = { selectedTabSection = "locations" },
                        text = { Text("Provinces", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("discover_tab_locations")
                    )
                    Tab(
                        selected = selectedTabSection == "live",
                        onClick = { selectedTabSection = "live" },
                        text = { Text("Live Cams", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("discover_tab_live")
                    )
                }
            }
        }

        // SECTION 0: FEATURED CONTENT FROM FIRESTORE CAROUSEL & CARDS
        if (selectedTabSection == "featured") {
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // HERO FEATURED BANNER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF260D1E), Color(0xFF13131E), Color(0xFF380824))
                                )
                            )
                            .border(1.dp, DarkBorderPink, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = PinkPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = TextWhite,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "FIRESTORE SPOTLIGHT",
                                            color = TextWhite,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                Text(
                                    text = "${featuredList.size} Top Rated",
                                    color = BadgeGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = "Hand-Picked VIP Companions",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Synchronized in real-time from Cloud Firestore with photo verification.",
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Johannesburg • Cape Town • Durban",
                                    color = PinkLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "100% Discretion",
                                        color = BadgeVerifiedGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // HORIZONTAL FEATURED COMPANIONS STRIP
                    Text(
                        text = "Featured Spotlight Feed",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredList) { profile ->
                            FirestoreFeaturedCard(
                                profile = profile,
                                onClick = { viewModel.openProfileDetail(profile) }
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: CATEGORIES CHIP SELECTOR & PROFILES
        if (selectedTabSection == "categories") {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterState.selectedCategory == null,
                            onClick = {
                                viewModel.setCategoryFilter(null)
                                viewModel.triggerShimmerReload()
                            },
                            label = { Text("All Listings", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
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
        }

        // SECTION 2: PROVINCES & CITIES ACCORDION
        if (selectedTabSection == "locations") {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Browse by South African Province",
                        color = PinkLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MockDataProvider.PROVINCES.forEach { province ->
                        val isExpanded = selectedProvinceExpanded == province
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = BorderStroke(1.dp, if (filterState.selectedProvince == province) PinkPrimary else DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedProvinceExpanded = if (isExpanded) null else province
                                        }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationCity,
                                            contentDescription = null,
                                            tint = if (filterState.selectedProvince == province) PinkPrimary else TextMuted
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = province,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }

                                if (isExpanded) {
                                    Divider(color = DarkBorder)
                                    val cities = MockDataProvider.CITIES_BY_PROVINCE[province] ?: emptyList()
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.setLocationFilter(province, null)
                                                    viewModel.triggerShimmerReload()
                                                }
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "• All $province Listings",
                                                color = if (filterState.selectedProvince == province && filterState.selectedCity == null) PinkPrimary else PinkLight,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        cities.forEach { city ->
                                            val isCitySelected = filterState.selectedProvince == province && filterState.selectedCity == city
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.setLocationFilter(province, city)
                                                        viewModel.triggerShimmerReload()
                                                    }
                                                    .padding(vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Place,
                                                    contentDescription = null,
                                                    tint = if (isCitySelected) PinkPrimary else TextMuted,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = city,
                                                    color = if (isCitySelected) PinkPrimary else TextWhite,
                                                    fontWeight = if (isCitySelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 13.sp
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

        // SECTION 3: LIVE WEBCAMS
        if (selectedTabSection == "live") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFF260D1E), Color(0xFF13131E)))
                            )
                            .border(1.dp, DarkBorderPink, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
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
                                        text = "LIVE WEBCAMS SHOWS",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }

                                Surface(color = Color(0xFF261224), shape = RoundedCornerShape(6.dp), border = BorderStroke(0.5.dp, PinkPrimary)) {
                                    Text("XCams Network", color = PinkLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Interactive Live Model Streaming & Private Shows",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            LiveWebcamGalleryEmbed(
                                heightDp = 380
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PinkGradientButton(
                                text = "Enter Full Live Lounge Stage",
                                onClick = { viewModel.activeSubscreen.value = "live_entertainment" },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // SKELETON LOADING OR RESULTS FEED
        if (isLoadingFeeds) {
            item {
                SkeletonFeedList()
            }
        } else {
            // RESULTS COUNT & SORT
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${profiles.size} Listings Found" + (filterState.selectedCity?.let { " in $it" } ?: ""),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    var showSortMenu by remember { mutableStateOf(false) }
                    Box {
                        TextButton(
                            onClick = { showSortMenu = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = PinkPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(filterState.sortBy.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            containerColor = DarkSurfaceElevated
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title, color = TextWhite) },
                                    onClick = {
                                        viewModel.filterState.update { it.copy(sortBy = option) }
                                        viewModel.triggerShimmerReload()
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // RESULTS GRID
            val chunked = profiles.chunked(2)
            if (chunked.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No listings match your filter criteria",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try clearing filters or selecting another South African city.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PinkGradientButton(
                                text = "Reset Filters",
                                onClick = {
                                    viewModel.filterState.value = com.example.ui.viewmodel.SearchFilterState()
                                    viewModel.triggerShimmerReload()
                                },
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }
            } else {
                items(chunked) { rowPair ->
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
            }
        }
    }
}

/**
 * Card specifically displaying Featured Content from Firebase Firestore
 * using brand colors (deep pinks and dark neutrals).
 */
@Composable
fun FirestoreFeaturedCard(
    profile: Profile,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.5.dp, PinkPrimary),
        modifier = Modifier
            .width(170.dp)
            .clickable(onClick = onClick)
            .testTag("firestore_featured_card_${profile.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF161622))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(profile.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = profile.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top featured tag
                Surface(
                    color = PinkPrimary,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "FEATURED",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Price badge
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = profile.priceText,
                        color = PinkLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "${profile.displayName}, ${profile.age}",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${profile.city}, ${profile.province}",
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, DarkBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BadgeVerifiedGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Available Now",
                            color = BadgeVerifiedGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
