package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.ui.components.AgeVerificationDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            PinkPassionsTheme(themeMode = themeMode) {
                PinkPassionsApp(
                    viewModel = viewModel,
                    onExitApp = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinkPassionsApp(
    viewModel: MainViewModel,
    onExitApp: () -> Unit
) {
    val hasConfirmedAge by viewModel.hasConfirmedAge.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val activeSubscreen by viewModel.activeSubscreen.collectAsState()
    val userNotice by viewModel.userNotice.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userNotice) {
        userNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotice()
        }
    }

    // MANDATORY 18+ AGE VERIFICATION DIALOG GATE
    if (!hasConfirmedAge) {
        AgeGateScreen(
            onEnter = { viewModel.confirmAge() },
            onExit = onExitApp
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = activeSubscreen == "none",
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = DarkSurfaceElevated,
                    drawerContentColor = TextWhite,
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // DRAWER HEADER
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PinkGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("PP", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Pink Passions", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("South Africa • 18+ Directory", color = PinkLight, fontSize = 11.sp)
                            }
                        }

                        Divider(color = DarkBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // SPECIAL: LIVE WEBCAMS SHOWS (HOT PINK HIGHLIGHT)
                        Surface(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.activeSubscreen.value = "live_entertainment"
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2B1024),
                            border = BorderStroke(1.dp, PinkPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("drawer_live_webcams")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("LIVE WEBCAMS SHOWS", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF0055))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // SPECIAL: LOCAL CHAT ROOMS
                        Surface(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.activeSubscreen.value = "local_chat_room"
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("drawer_local_chat_rooms")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Forum, contentDescription = null, tint = PinkLight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Local Chat Rooms", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Surface(color = Color(0xFF00E676).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                    Text("LIVE", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("MAIN NAVIGATION", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        DrawerItem(icon = Icons.Default.Home, label = "Home Feed", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.currentTab.value = "home"
                            viewModel.activeSubscreen.value = "none"
                        })
                        DrawerItem(icon = Icons.Default.Explore, label = "Discover Categories", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.currentTab.value = "discover"
                            viewModel.activeSubscreen.value = "none"
                        })
                        DrawerItem(icon = Icons.Default.LocationOn, label = "Near Me (Map & Radar)", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.currentTab.value = "map"
                            viewModel.activeSubscreen.value = "none"
                        })
                        DrawerItem(icon = Icons.Default.ChatBubble, label = "Direct Messages", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.currentTab.value = "messages"
                            viewModel.activeSubscreen.value = "none"
                        })
                        DrawerItem(icon = Icons.Default.Person, label = "My Account & Profile", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.currentTab.value = "account"
                            viewModel.activeSubscreen.value = "none"
                        })

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("SERVICES & ENTERTAINMENT", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        DrawerItem(icon = Icons.Default.Nightlife, label = "Gentlemen's Clubs & Venues", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "business_directory"
                        })
                        DrawerItem(icon = Icons.Default.Event, label = "Events & Masquerade Galas", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "events"
                        })
                        DrawerItem(icon = Icons.Default.ShoppingBag, label = "Adult Boutique (Shop)", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "shop"
                        })

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("MEMBERSHIP & REGISTRATION", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // HIGHLIGHT: REGISTER & CREATE PROFILE
                        Surface(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.startRegistrationFlow(initialStep = 1)
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = PinkPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().testTag("drawer_register_create_profile")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Register & Create Profile", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                }
                                Surface(color = PinkPrimary, shape = RoundedCornerShape(4.dp)) {
                                    Text("NEW", color = TextWhite, fontSize = 8.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // HIGHLIGHT: MEMBERSHIP PACKAGES
                        Surface(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.activeSubscreen.value = "membership"
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("drawer_membership_plans")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Membership Plans", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Surface(color = Color(0x33FFD700), shape = RoundedCornerShape(4.dp)) {
                                    Text("PLANS", color = Color(0xFFFFD700), fontSize = 8.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        DrawerItem(icon = Icons.Default.Campaign, label = "Advertise & Create Listing", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "create_listing"
                        })
                        DrawerItem(icon = Icons.Default.LocationOn, label = "Google Maps Radar (50km)", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "google_maps"
                        })
                        DrawerItem(icon = Icons.Default.Settings, label = "Settings & Privacy Preferences", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "user_settings"
                        })
                        DrawerItem(icon = Icons.Default.VerifiedUser, label = "Photo & ID Verification", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "verification"
                        })
                        DrawerItem(icon = Icons.Default.AdminPanelSettings, label = "Admin Moderation Console", onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.activeSubscreen.value = "admin"
                        })

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { data ->
                            Snackbar(
                                containerColor = DarkSurfaceElevated,
                                contentColor = TextWhite,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .border(1.dp, PinkPrimary, RoundedCornerShape(12.dp))
                            ) {
                                Text(data.visuals.message, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                },
                bottomBar = {
                    if (activeSubscreen == "none") {
                        PinkPassionsBottomNavigation(
                            currentTab = currentTab,
                            onTabSelected = { tab -> viewModel.currentTab.value = tab },
                            onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        )
                    }
                },
                containerColor = DarkBackground,
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(DarkBackground)
                ) {
                    // SUBSCREEN ROUTING
                    when (activeSubscreen) {
                        "profile_detail" -> {
                            selectedProfile?.let { profile ->
                                ProfileDetailScreen(
                                    profile = profile,
                                    viewModel = viewModel,
                                    onBack = { viewModel.activeSubscreen.value = "none" }
                                )
                            } ?: run {
                                viewModel.activeSubscreen.value = "none"
                            }
                        }
                        "chat" -> {
                            activeConversationId?.let { convoId ->
                                ChatScreen(
                                    convoId = convoId,
                                    viewModel = viewModel,
                                    onBack = { viewModel.activeSubscreen.value = "none" }
                                )
                            } ?: run {
                                viewModel.activeSubscreen.value = "none"
                            }
                        }
                        "registration" -> {
                            RegistrationWizardScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" },
                                onFinish = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "create_listing" -> {
                            CreateListingWizardScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "verification" -> {
                            VerificationScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "membership" -> {
                            MembershipScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "advertising" -> {
                            AdvertisingScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "shop" -> {
                            ShopScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "business_directory" -> {
                            BusinessDirectoryScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "events" -> {
                            EventsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "admin" -> {
                            AdminPanelScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "live_entertainment" -> {
                            LiveEntertainmentScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "local_chat_room" -> {
                            LocalChatRoomsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "user_settings", "settings" -> {
                            UserSettingsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        "google_maps" -> {
                            GoogleMapsDiscoveryScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.activeSubscreen.value = "none" }
                            )
                        }
                        else -> {
                            // MAIN TAB NAVIGATION
                            when (currentTab) {
                                "home" -> HomeScreen(viewModel = viewModel)
                                "directory" -> MainDirectoryScreen(viewModel = viewModel)
                                "shop" -> ShopScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.currentTab.value = "home" }
                                )
                                "profile", "account", "settings" -> ProfileScreen(viewModel = viewModel)
                                "discover" -> DiscoverScreen(viewModel = viewModel)
                                "map" -> MapNearMeScreen(viewModel = viewModel)
                                "messages" -> MessagesScreen(viewModel = viewModel)
                                else -> HomeScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PinkPassionsBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated.copy(alpha = 0.98f),
        border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sidebar Menu Trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onMenuClick)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .testTag("nav_tab_menu")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1B1B2A))
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = PinkLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text("Menu", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            // 1. HOME TAB
            BottomNavItem(
                label = "Home",
                filledIcon = Icons.Filled.Home,
                outlinedIcon = Icons.Outlined.Home,
                isSelected = currentTab == "home",
                onClick = { onTabSelected("home") },
                testTag = "nav_tab_home"
            )

            // 2. DISCOVERY TAB (Featured & Cloud sync)
            BottomNavItem(
                label = "Discovery",
                filledIcon = Icons.Filled.AutoAwesome,
                outlinedIcon = Icons.Outlined.AutoAwesome,
                isSelected = currentTab == "discover",
                onClick = { onTabSelected("discover") },
                testTag = "nav_tab_discover"
            )

            // 3. DIRECTORY TAB
            BottomNavItem(
                label = "Directory",
                filledIcon = Icons.Filled.Explore,
                outlinedIcon = Icons.Outlined.Explore,
                isSelected = currentTab == "directory",
                onClick = { onTabSelected("directory") },
                testTag = "nav_tab_directory"
            )

            // 4. SHOP TAB
            BottomNavItem(
                label = "Shop",
                filledIcon = Icons.Filled.ShoppingBag,
                outlinedIcon = Icons.Outlined.ShoppingBag,
                isSelected = currentTab == "shop",
                onClick = { onTabSelected("shop") },
                testTag = "nav_tab_shop"
            )

            // 5. PROFILE TAB
            BottomNavItem(
                label = "Profile",
                filledIcon = Icons.Filled.Person,
                outlinedIcon = Icons.Outlined.PersonOutline,
                isSelected = currentTab == "profile" || currentTab == "account",
                onClick = { onTabSelected("profile") },
                testTag = "nav_tab_profile"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    filledIcon: ImageVector,
    outlinedIcon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = PinkPrimary,
                        contentColor = TextWhite
                    ) {
                        Text("$badgeCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) {
            // Pill Active Indicator with deep pink glow and dark neutral container
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) PinkGlow else Color.Transparent
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) PinkPrimary.copy(alpha = 0.6f) else Color.Transparent,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) filledIcon else outlinedIcon,
                    contentDescription = label,
                    tint = if (isSelected) PinkPrimary else TextMuted,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = if (isSelected) PinkLight else TextMuted,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}
