package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Type-safe navigation destination route constants for Pink Passions South Africa.
 */
object AppRoutes {
    const val AGE_GATE = "age_gate"
    const val HOME = "home"
    const val DISCOVER = "discover"
    const val MAP = "map"
    const val MESSAGES = "messages"
    const val ACCOUNT = "account"
    const val PROFILE_DETAIL = "profile_detail/{profileId}"
    const val CHAT = "chat/{convoId}"
    const val CREATE_LISTING = "create_listing"
    const val SHOP = "shop"
    const val BUSINESS_DIRECTORY = "business_directory"
    const val EVENTS = "events"
    const val VERIFICATION = "verification"
    const val MEMBERSHIP = "membership"
    const val ADVERTISING = "advertising"
    const val ADMIN = "admin"
    const val LIVE = "live_entertainment"

    fun profileDetail(profileId: String): String = "profile_detail/$profileId"
    fun chat(convoId: String): String = "chat/$convoId"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onExitApp: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = modifier.fillMaxSize()
    ) {
        composable(AppRoutes.AGE_GATE) {
            AgeGateScreen(
                onEnter = {
                    viewModel.confirmAge()
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.AGE_GATE) { inclusive = true }
                    }
                },
                onExit = onExitApp
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(viewModel = viewModel)
        }

        composable(AppRoutes.DISCOVER) {
            DiscoverScreen(viewModel = viewModel)
        }

        composable(AppRoutes.MAP) {
            MapNearMeScreen(viewModel = viewModel)
        }

        composable(AppRoutes.MESSAGES) {
            MessagesScreen(viewModel = viewModel)
        }

        composable(AppRoutes.ACCOUNT) {
            AccountScreen(viewModel = viewModel)
        }

        composable(
            route = AppRoutes.PROFILE_DETAIL,
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
            route = AppRoutes.CHAT,
            arguments = listOf(navArgument("convoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val convoId = backStackEntry.arguments?.getString("convoId") ?: ""
            ChatScreen(
                convoId = convoId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.CREATE_LISTING) {
            CreateListingWizardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SHOP) {
            ShopScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.BUSINESS_DIRECTORY) {
            BusinessDirectoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.EVENTS) {
            EventsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.VERIFICATION) {
            VerificationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.MEMBERSHIP) {
            MembershipScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ADVERTISING) {
            AdvertisingScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ADMIN) {
            AdminPanelScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.LIVE) {
            LiveEntertainmentScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
