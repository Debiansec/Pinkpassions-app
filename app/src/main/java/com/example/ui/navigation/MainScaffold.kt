package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Bottom Navigation Items for the 4 primary tabs: Home, Directory, Shop, and Profile.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_bottom_home"
    )

    object Discovery : BottomNavItem(
        route = "discover",
        title = "Discovery",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        testTag = "nav_bottom_discovery"
    )

    object Directory : BottomNavItem(
        route = "directory",
        title = "Directory",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_bottom_directory"
    )

    object Shop : BottomNavItem(
        route = "shop",
        title = "Shop",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag,
        testTag = "nav_bottom_shop"
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.PersonOutline,
        testTag = "nav_bottom_profile"
    )
}

/**
 * Main Scaffold with Material3 NavigationBar and Navigation Compose NavHost.
 * Hosts Home, Directory, Shop, and Profile screens as well as nested detail destinations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onOpenDrawer: (() -> Unit)? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Discovery,
        BottomNavItem.Directory,
        BottomNavItem.Shop,
        BottomNavItem.Profile
    )

    val isTopLevelDestination = currentRoute in navItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Material 3 Bottom Navigation Bar
            if (isTopLevelDestination) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .testTag("main_bottom_navigation_bar")
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentRoute == item.route

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        viewModel.currentTab.value = item.route
                                    }
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (item == BottomNavItem.Shop && cartCount > 0) {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                                ) {
                                                    Text(
                                                        text = "$cartCount",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. HOME SCREEN / PLACEHOLDER
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.testTag("screen_home")
                )
            }

            // 2. DIRECTORY SCREEN / PLACEHOLDER
            composable(BottomNavItem.Directory.route) {
                MainDirectoryScreen(
                    viewModel = viewModel,
                    modifier = Modifier.testTag("screen_directory")
                )
            }

            // 3. SHOP SCREEN / PLACEHOLDER
            composable(BottomNavItem.Shop.route) {
                ShopScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // 4. PROFILE SCREEN
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    modifier = Modifier.testTag("screen_profile")
                )
            }

            // NESTED DESTINATIONS FOR COMPLETE WORKFLOWS
            composable(
                route = "profile_detail/{profileId}",
                arguments = listOf(navArgument("profileId") { type = NavType.StringType })
            ) { backStackEntry ->
                val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
                val profile = viewModel.allProfiles.value.find { it.id == profileId }
                    ?: viewModel.selectedProfile.value
                if (profile != null) {
                    ProfileDetailScreen(
                        profile = profile,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(
                route = "chat/{convoId}",
                arguments = listOf(navArgument("convoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val convoId = backStackEntry.arguments?.getString("convoId") ?: ""
                ChatScreen(
                    convoId = convoId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("create_listing") {
                CreateListingWizardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("verification") {
                VerificationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("membership") {
                MembershipScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("advertising") {
                AdvertisingScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin") {
                AdminPanelScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("live_entertainment") {
                LiveEntertainmentScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("local_chat_room") {
                LocalChatRoomsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("map") {
                MapNearMeScreen(viewModel = viewModel)
            }

            composable("discover") {
                DiscoverScreen(viewModel = viewModel)
            }

            composable("messages") {
                MessagesScreen(viewModel = viewModel)
            }
        }
    }
}
