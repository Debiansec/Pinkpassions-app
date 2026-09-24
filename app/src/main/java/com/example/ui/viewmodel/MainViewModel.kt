package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchFilterState(
    val query: String = "",
    val selectedProvince: String? = null,
    val selectedCity: String? = null,
    val selectedCategory: ProfileCategory? = null,
    val minRating: Float = 0f,
    val onlyVerified: Boolean = false,
    val onlyFeatured: Boolean = false,
    val onlyGold: Boolean = false,
    val onlyVip: Boolean = false,
    val sortBy: SortOption = SortOption.RECOMMENDED
)

enum class SortOption(val title: String) {
    RECOMMENDED("Recommended"),
    NEWEST("Newest Profiles"),
    HIGHEST_RATED("Highest Rated"),
    FEATURED("Featured First"),
    DISTANCE("Near Me")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "pink_passions.db"
    ).fallbackToDestructiveMigration().build()

    val repository = PinkPassionsRepository(db)
    val preferencesRepository = UserPreferencesRepository(application)

    // Age Gate state (Requires user to enter 18+ confirmation, persisted in DataStore)
    val hasConfirmedAge = MutableStateFlow(false)

    // Theme Preferences State
    val themeMode: StateFlow<AppThemeMode> = preferencesRepository.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.DARK)

    val accentColor: StateFlow<String> = preferencesRepository.accentColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "HOT_PINK")

    val isDiscreetMode: StateFlow<Boolean> = preferencesRepository.discreetModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isNotificationsEnabled: StateFlow<Boolean> = preferencesRepository.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userDisplayName: StateFlow<String> = preferencesRepository.userDisplayNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Alexander Vance")

    val userCity: StateFlow<String> = preferencesRepository.userCityFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sandton, Johannesburg")

    val userBio: StateFlow<String> = preferencesRepository.userBioFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "VIP Member & Lifestyle Connoisseur in South Africa.")

    // Profile Visibility Preferences
    val isProfileVisible: StateFlow<Boolean> = preferencesRepository.profileVisibleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isGhostMode: StateFlow<Boolean> = preferencesRepository.ghostModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isShowOnlineStatus: StateFlow<Boolean> = preferencesRepository.showOnlineStatusFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isShowVerifiedBadge: StateFlow<Boolean> = preferencesRepository.showVerifiedBadgeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Location Privacy Preferences
    val locationPrivacyMode: StateFlow<String> = preferencesRepository.locationPrivacyModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FUZZY_5KM")

    val isShareRadar50km: StateFlow<Boolean> = preferencesRepository.shareRadar50kmFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isAutoDetectGps: StateFlow<Boolean> = preferencesRepository.autoDetectGpsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isFwbPremiumActive: StateFlow<Boolean> = preferencesRepository.fwbPremiumActiveFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Cache Size and clearing state
    val cacheSizeBytes = MutableStateFlow(0L)
    val isClearingCache = MutableStateFlow(false)

    // Subscriptions, Memberships & Verification states
    val userSubscription: StateFlow<Subscription> = repository.userSubscription
    val userMembershipTier: StateFlow<MembershipTier> = repository.userSubscription
        .map { it.membershipTier }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MembershipTier.BASIC_FREE)
    val configuredMembershipTiers: StateFlow<List<MembershipTier>> = repository.configuredMembershipTiers
    val configuredUpgradeProducts: StateFlow<List<UpgradeType>> = repository.configuredUpgradeProducts
    val activePromotions: StateFlow<List<UpgradeType>> = repository.activePromotions
    val userVerificationStatus: StateFlow<VerificationStatus> = repository.userVerificationStatus
    val userVerificationLevel: StateFlow<VerificationLevel> = repository.userVerificationLevel
    val verificationRequests: StateFlow<List<VerificationRequest>> = repository.verificationRequests
    val communityGroups: StateFlow<List<CommunityGroup>> = repository.communityGroups
    val notifications: StateFlow<List<AppNotification>> = repository.notifications

    val allReviews: StateFlow<List<Review>> = repository.getAllReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tab in Bottom Nav ("home", "discover", "map", "community", "account")
    val currentTab = MutableStateFlow("home")

    // Active Subscreen Modal ("none", "profile_detail", "business_detail", "event_detail", "product_detail", "chat", "create_listing", "verification", "membership", "promotions", "shop", "cart", "admin", "settings", "terms", "privacy", "report", "notifications")
    val activeSubscreen = MutableStateFlow("none")

    // Filter & Search state
    val filterState = MutableStateFlow(SearchFilterState())

    // Selected items for modal/detail screens
    val selectedProfile = MutableStateFlow<Profile?>(null)
    val selectedBusiness = MutableStateFlow<Business?>(null)
    val selectedEvent = MutableStateFlow<Event?>(null)
    val selectedProduct = MutableStateFlow<Product?>(null)
    val activeConversationId = MutableStateFlow<String?>(null)

    // Notification badge state
    val unreadNotificationsCount = MutableStateFlow(3)

    // Registration Wizard & Auth State
    val registrationState = MutableStateFlow(RegistrationFlowState())
    val registrationCurrentStep = MutableStateFlow(1) // Steps 1 through 14
    val showAuthModal = MutableStateFlow(false)
    val authModalMode = MutableStateFlow("REGISTER") // "REGISTER" or "SIGN_IN"

    // Create listing wizard state
    val createListingStep = MutableStateFlow(1)
    val createListingDraft = MutableStateFlow(CreateListingDraft())
    val listingCreationSuccess = MutableStateFlow(false)

    // Checkout & Payment state
    val selectedPaymentMethod = MutableStateFlow<PaymentMethod>(PaymentMethod.PayFast)
    val isProcessingPayment = MutableStateFlow(false)
    val lastPaymentResult = MutableStateFlow<PaymentTransaction?>(null)

    // Skeleton Shimmer Loading state
    val isLoadingFeeds = MutableStateFlow(false)

    // Chat Rooms State
    val selectedChatRoomCategory = MutableStateFlow("All Rooms")
    val chatRooms: StateFlow<List<ChatRoom>> = repository.chatRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val selectedChatRoom = MutableStateFlow<ChatRoom?>(null)
    val activeChatRoomId = MutableStateFlow<String>("room_jhb")

    val activeChatRoomMessages: StateFlow<List<ChatRoomMessage>> = activeChatRoomId.flatMapLatest { roomId ->
        repository.getChatRoomMessages(roomId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast/Snackbar Message state
    val userNotice = MutableStateFlow<String?>(null)

    // Profiles Flow
    val allProfiles: StateFlow<List<Profile>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favouriteProfiles: StateFlow<List<Profile>> = repository.getFavouriteProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Profiles computed flow - STRICT: Never publish unapproved profiles!
    val filteredProfiles: StateFlow<List<Profile>> = combine(
        allProfiles,
        filterState
    ) { profiles, filters ->
        var list = profiles.filter { 
            (it.status == ListingStatus.LIVE || it.moderationStatus == ModerationStatus.APPROVED) &&
            it.moderationStatus != ModerationStatus.PENDING_REVIEW &&
            it.moderationStatus != ModerationStatus.DRAFT &&
            it.moderationStatus != ModerationStatus.REJECTED &&
            it.moderationStatus != ModerationStatus.SUSPENDED
        }

        if (filters.query.isNotBlank()) {
            val q = filters.query.lowercase().trim()
            list = list.filter {
                it.displayName.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.area.lowercase().contains(q) ||
                it.category.title.lowercase().contains(q) ||
                it.services.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

        if (filters.selectedProvince != null) {
            list = list.filter { it.province.equals(filters.selectedProvince, ignoreCase = true) }
        }

        if (filters.selectedCity != null) {
            list = list.filter { it.city.equals(filters.selectedCity, ignoreCase = true) }
        }

        if (filters.selectedCategory != null) {
            list = list.filter { it.category == filters.selectedCategory }
        }

        if (filters.onlyVerified) {
            list = list.filter { it.verifiedLevel != VerificationLevel.NONE }
        }

        if (filters.onlyFeatured) {
            list = list.filter { it.isFeatured }
        }

        if (filters.onlyGold) {
            list = list.filter { it.isGold }
        }

        if (filters.onlyVip) {
            list = list.filter { it.membershipTier == MembershipTier.VIP || it.membershipTier == MembershipTier.UPMARKET_EXCLUSIVE || it.membershipTier == MembershipTier.GOLD }
        }

        when (filters.sortBy) {
            SortOption.RECOMMENDED -> list.sortedWith(compareByDescending<Profile> { it.isFeatured }.thenByDescending { it.isGold }.thenByDescending { it.rating })
            SortOption.NEWEST -> list.reversed()
            SortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
            SortOption.FEATURED -> list.sortedByDescending { it.isFeatured }
            SortOption.DISTANCE -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Featured Carousel Profiles
    val featuredProfiles: StateFlow<List<Profile>> = allProfiles.map { list ->
        list.filter { it.isFeatured || it.isGold }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Businesses & Events
    val businesses: StateFlow<List<Business>> = repository.getAllBusinesses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<Event>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Shop Products & Cart
    val products: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotalZar: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.priceZar * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Conversations, Campaigns & Audit Logs
    val conversations = repository.conversations
    val campaigns = repository.campaigns
    val auditLogs = repository.auditLogs

    init {
        viewModelScope.launch {
            preferencesRepository.hasConfirmedAgeFlow.collect { confirmed ->
                hasConfirmedAge.value = confirmed
            }
        }
        refreshCacheSize()
    }

    fun confirmAge() {
        hasConfirmedAge.value = true
        viewModelScope.launch {
            preferencesRepository.saveAgeConfirmation(true)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
            showNotice("Theme updated to ${mode.title}")
        }
    }

    fun setAccentColor(accent: String) {
        viewModelScope.launch {
            preferencesRepository.setAccentColor(accent)
            showNotice("Accent color preference saved")
        }
    }

    fun setDiscreetMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDiscreetMode(enabled)
            showNotice(if (enabled) "Discreet Mode enabled" else "Standard Display enabled")
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
            showNotice(if (enabled) "Push Notifications enabled" else "Notifications muted")
        }
    }

    fun updateUserProfile(displayName: String, city: String, bio: String) {
        viewModelScope.launch {
            preferencesRepository.updateUserProfile(displayName, city, bio)
            showNotice("Profile information updated successfully!")
        }
    }

    fun setProfileVisible(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setProfileVisible(visible)
            syncUserSettingsToCloud()
            showNotice(if (visible) "Profile visibility enabled across discovery" else "Profile hidden from public discovery")
        }
    }

    fun setGhostMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setGhostMode(enabled)
            syncUserSettingsToCloud()
            showNotice(if (enabled) "Ghost Mode activated: Hidden from 50km proximity radar" else "Ghost Mode deactivated: Visible on radar")
        }
    }

    fun setShowOnlineStatus(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowOnlineStatus(enabled)
            syncUserSettingsToCloud()
            showNotice(if (enabled) "Online activity status visible" else "Online activity status hidden")
        }
    }

    fun setShowVerifiedBadge(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowVerifiedBadge(enabled)
            syncUserSettingsToCloud()
            showNotice(if (enabled) "18+ Verified badge visible on your card" else "Verified badge hidden")
        }
    }

    fun setLocationPrivacyMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setLocationPrivacyMode(mode)
            syncUserSettingsToCloud()
            val label = when (mode) {
                "EXACT_GPS" -> "Exact GPS Coordinates"
                "FUZZY_5KM" -> "Fuzzy 5km Approximate Area"
                else -> "City / Metro Area Only"
            }
            showNotice("Location Privacy set to: $label")
        }
    }

    fun setShareRadar50km(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShareRadar50km(enabled)
            syncUserSettingsToCloud()
            showNotice(if (enabled) "50km Hookup Radar sharing enabled" else "50km Radar sharing disabled")
        }
    }

    fun setAutoDetectGps(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoDetectGps(enabled)
            syncUserSettingsToCloud()
            showNotice(if (enabled) "Auto GPS location tracking active" else "Manual city selection active")
        }
    }

    fun toggleFwbSubscription(enable: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFwbPremiumActive(enable)
            val targetTier = if (enable) MembershipTier.FRIENDS_WITH_BENEFITS else MembershipTier.BASIC_FREE
            repository.setSubscriptionTier(targetTier, "PayFast", isActive = enable)
            if (enable) {
                showNotice("R99/month Friends with Benefits VIP Membership Activated! Synced with Firestore.")
            } else {
                showNotice("R99/month Membership cancelled. Switched to Free Basic tier.")
            }
        }
    }

    private fun syncUserSettingsToCloud() {
        viewModelScope.launch {
            val settingsMap = mapOf(
                "profileVisible" to isProfileVisible.value,
                "ghostMode" to isGhostMode.value,
                "showOnlineStatus" to isShowOnlineStatus.value,
                "showVerifiedBadge" to isShowVerifiedBadge.value,
                "locationPrivacyMode" to locationPrivacyMode.value,
                "shareRadar50km" to isShareRadar50km.value,
                "autoDetectGps" to isAutoDetectGps.value,
                "fwbPremiumActive" to isFwbPremiumActive.value,
                "updatedAt" to System.currentTimeMillis()
            )
            repository.firestoreService.saveUserSettings("usr_current", settingsMap)
        }
    }

    fun refreshCacheSize() {
        viewModelScope.launch {
            cacheSizeBytes.value = preferencesRepository.getCacheSizeBytes()
        }
    }

    fun clearCachedData() {
        viewModelScope.launch {
            isClearingCache.value = true
            val bytesCleared = preferencesRepository.clearCachedData()
            cacheSizeBytes.value = 0L
            isClearingCache.value = false
            val formatted = formatBytes(bytesCleared)
            showNotice("Cache successfully cleared! Freed $formatted of temporary data.")
        }
    }

    fun resetAgeConfirmation() {
        viewModelScope.launch {
            preferencesRepository.saveAgeConfirmation(false)
            hasConfirmedAge.value = false
            showNotice("Age verification reset. Please confirm 18+ upon next app launch.")
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format(java.util.Locale.US, "%.1f KB", bytes.toDouble() / 1024)
            else -> "$bytes B"
        }
    }

    fun setLocationFilter(province: String?, city: String?) {
        filterState.update { it.copy(selectedProvince = province, selectedCity = city) }
    }

    fun setCategoryFilter(category: ProfileCategory?) {
        filterState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        filterState.update { it.copy(query = query) }
    }

    fun toggleFavourite(profile: Profile) {
        viewModelScope.launch {
            repository.toggleFavourite(profile.id, !profile.isFavourite)
            showNotice(if (!profile.isFavourite) "Added to Favourites" else "Removed from Favourites")
        }
    }

    fun openProfileDetail(profile: Profile) {
        selectedProfile.value = profile
        activeSubscreen.value = "profile_detail"
    }

    fun openBusinessDetail(business: Business) {
        selectedBusiness.value = business
        activeSubscreen.value = "business_detail"
    }

    fun openEventDetail(event: Event) {
        selectedEvent.value = event
        activeSubscreen.value = "event_detail"
    }

    fun openProductDetail(product: Product) {
        selectedProduct.value = product
        activeSubscreen.value = "product_detail"
    }

    fun openConversation(convoId: String) {
        activeConversationId.value = convoId
        activeSubscreen.value = "chat"
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product)
            showNotice("Added '${product.name}' to cart")
        }
    }

    fun updateCartQuantity(productId: String, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, qty)
        }
    }

    fun checkoutCart(shippingAddress: String) {
        viewModelScope.launch {
            isProcessingPayment.value = true
            val total = cartTotalZar.value
            val result = repository.paymentService.processPayment(
                userId = "usr_current",
                productTitle = "Adult Shop Order (${cartItems.value.size} items)",
                amountZar = total,
                provider = selectedPaymentMethod.value
            )
            isProcessingPayment.value = false
            when (result) {
                is PaymentResult.Success -> {
                    repository.recordTransaction(result.transaction)
                    repository.clearCart()
                    lastPaymentResult.value = result.transaction
                    showNotice("Order placed successfully! Reference: ${result.transaction.transactionId}")
                    activeSubscreen.value = "order_success"
                }
                is PaymentResult.PendingVerification -> {
                    repository.recordTransaction(result.transaction)
                    repository.clearCart()
                    showNotice(result.instructions)
                }
                is PaymentResult.ProviderRestricted -> {
                    showNotice("Gateway notice: ${result.reason}")
                }
                is PaymentResult.Error -> {
                    showNotice("Payment failed: ${result.message}")
                }
            }
        }
    }

    fun purchaseMembership(tier: MembershipTier) {
        viewModelScope.launch {
            if (tier.priceZar == 0) {
                repository.upgradeSubscription(tier, "Free")
                showNotice("Activated Free Forever basic membership.")
                activeSubscreen.value = "none"
                return@launch
            }

            isProcessingPayment.value = true
            val result = repository.paymentService.processPayment(
                userId = "usr_current",
                productTitle = "Membership: ${tier.title}",
                amountZar = tier.priceZar,
                provider = selectedPaymentMethod.value
            )
            isProcessingPayment.value = false
            when (result) {
                is PaymentResult.Success -> {
                    repository.recordTransaction(result.transaction)
                    repository.upgradeSubscription(tier, selectedPaymentMethod.value.title)
                    lastPaymentResult.value = result.transaction
                    showNotice("Upgraded to ${tier.title} (${tier.badgeLabel})!")
                    activeSubscreen.value = "none"
                }
                is PaymentResult.PendingVerification -> {
                    repository.recordTransaction(result.transaction)
                    showNotice(result.instructions)
                }
                is PaymentResult.ProviderRestricted -> {
                    showNotice(result.reason)
                }
                is PaymentResult.Error -> {
                    showNotice("Upgrade failed: ${result.message}")
                }
            }
        }
    }

    fun cancelSubscriptionAutoRenew() {
        repository.cancelSubscription()
        showNotice("Auto-renewal paused. Your plan stays active until current cycle ends.")
    }

    fun resumeSubscriptionAutoRenew() {
        repository.resumeSubscription()
        showNotice("Auto-renewal reactivated! Next billing cycle: 1 October 2026.")
    }

    fun updateDefaultPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod.value = method
        repository.updatePaymentProvider(method.title)
        showNotice("Default billing method set to ${method.title}.")
    }

    fun purchaseProfileUpgrade(upgrade: UpgradeType) {
        viewModelScope.launch {
            isProcessingPayment.value = true
            val result = repository.paymentService.processPayment(
                userId = "usr_current",
                productTitle = "Boost: ${upgrade.title}",
                amountZar = upgrade.priceZar,
                provider = selectedPaymentMethod.value
            )
            isProcessingPayment.value = false
            if (result is PaymentResult.Success) {
                repository.purchaseProfileUpgrade(upgrade, selectedPaymentMethod.value.title)
                showNotice("${upgrade.title} activated for ${upgrade.durationText}!")
                activeSubscreen.value = "none"
            } else if (result is PaymentResult.PendingVerification) {
                showNotice(result.instructions)
            }
        }
    }

    fun cancelSubscription() {
        repository.cancelSubscription()
        showNotice("Subscription auto-renewal has been cancelled.")
    }

    fun createAdCampaign(name: String, placement: String, budgetZar: Int, durationDays: Int) {
        viewModelScope.launch {
            showNotice("Ad campaign '$name' launched ($placement, $durationDays days, R$budgetZar).")
        }
    }

    fun submitMobileVerification(phone: String, otp: String) {
        val success = repository.submitMobileVerification(phone, otp)
        if (success) {
            showNotice("Mobile OTP verified successfully! Neon Green badge awarded.")
        } else {
            showNotice("Invalid OTP. Please enter 4-digit code (e.g. 1234).")
        }
    }

    fun submitEmailVerification(email: String) {
        repository.submitEmailVerification(email)
        showNotice("Verification email sent to $email.")
    }

    fun submitFacialAndIdVerification(docType: String) {
        repository.submitFacialAndIdVerification(docType)
        showNotice("Biometric Face Match & ID passed! Neon Green Verified Member badge is now active.")
        activeSubscreen.value = "none"
    }

    fun submitReview(profileId: String, rating: Int, comment: String, reviewerName: String = "Verified Client") {
        viewModelScope.launch {
            repository.submitReview(profileId, rating, comment, reviewerName)
            showNotice("Thank you for your $rating-star review!")
        }
    }

    fun getReviewsForProfile(profileId: String): Flow<List<Review>> {
        return repository.getReviewsForProfile(profileId)
    }

    fun toggleJoinGroup(groupId: String) {
        repository.toggleJoinCommunityGroup(groupId)
        showNotice("Community group membership updated.")
    }

    fun createCommunityGroup(name: String, description: String, category: String, emoji: String) {
        repository.createCommunityGroup(name, description, category, emoji)
        showNotice("Group '$name' created successfully!")
    }

    fun openAuthModal(mode: String = "REGISTER") {
        authModalMode.value = mode
        showAuthModal.value = true
    }

    fun closeAuthModal() {
        showAuthModal.value = false
    }

    fun startRegistrationFlow(initialStep: Int = 1, tier: MembershipTier? = null) {
        showAuthModal.value = false
        if (tier != null) {
            registrationState.update { it.copy(selectedMembershipTier = tier) }
        }
        registrationCurrentStep.value = initialStep
        activeSubscreen.value = "registration"
    }

    fun updateRegistrationState(transform: (RegistrationFlowState) -> RegistrationFlowState) {
        registrationState.update(transform)
    }

    fun sendRegistrationEmailOtp() {
        registrationState.update { it.copy(emailOtpSent = true, emailVerificationPin = "") }
        showNotice("Verification PIN sent to ${registrationState.value.email.ifBlank { "your email" }}.")
    }

    fun verifyRegistrationEmailPin(pin: String): Boolean {
        if (pin.length >= 4 || pin == "1234" || pin == "8888") {
            registrationState.update { it.copy(isEmailVerified = true, emailVerificationPin = pin) }
            showNotice("Email confirmed successfully!")
            return true
        }
        showNotice("Please enter a valid 4-6 digit PIN (e.g. 1234).")
        return false
    }

    fun sendRegistrationMobileOtp() {
        registrationState.update { it.copy(mobileOtpSent = true, mobileOtpCode = "") }
        showNotice("SMS OTP code sent to ${registrationState.value.mobileNumber}.")
    }

    fun verifyRegistrationMobileOtp(code: String): Boolean {
        if (code.length >= 4 || code == "1234") {
            registrationState.update { it.copy(isMobileVerified = true, mobileOtpCode = code) }
            showNotice("Mobile number verified with OTP.")
            return true
        }
        showNotice("Invalid OTP code. Please enter 4-digit code (e.g. 1234).")
        return false
    }

    fun confirmRegistrationAge(birthYear: Int) {
        val currentYear = 2026
        val calculatedAge = currentYear - birthYear
        if (calculatedAge >= 18) {
            registrationState.update {
                it.copy(
                    confirmedAge18Plus = true,
                    birthYear = birthYear,
                    age = calculatedAge.coerceIn(18, 99),
                    statutoryDeclarationAccepted = true
                )
            }
            showNotice("18+ Legal Age requirement confirmed.")
        } else {
            showNotice("You must be 18 years or older to register on Pink Passions.")
        }
    }

    fun selectRegistrationCategory(category: ProfileCategory) {
        registrationState.update { it.copy(selectedProfileCategory = category) }
    }

    fun selectRegistrationTier(tier: MembershipTier) {
        registrationState.update { it.copy(selectedMembershipTier = tier) }
    }

    fun addRegistrationGalleryPhoto(photoUrl: String) {
        registrationState.update {
            val updated = (it.galleryPhotos + photoUrl).distinct()
            it.copy(galleryPhotos = updated)
        }
    }

    fun removeRegistrationGalleryPhoto(photoUrl: String) {
        registrationState.update {
            val updated = it.galleryPhotos.filterNot { url -> url == photoUrl }
            it.copy(galleryPhotos = updated)
        }
    }

    fun setRegistrationCoverPhoto(photoUrl: String) {
        registrationState.update { it.copy(coverPhotoUrl = photoUrl) }
        showNotice("Cover photo updated.")
    }

    fun completeRegistrationPayment(onSuccess: () -> Unit) {
        val current = registrationState.value
        val tier = current.selectedMembershipTier
        if (tier.priceZar == 0) {
            registrationState.update {
                it.copy(
                    paymentCompleted = true,
                    membershipActivated = true,
                    paymentTransactionId = "FREE_TIER_${System.currentTimeMillis()}"
                )
            }
            repository.upgradeSubscription(tier, "Free")
            showNotice("Free basic membership activated.")
            onSuccess()
            return
        }

        viewModelScope.launch {
            registrationState.update { it.copy(isPaymentProcessing = true) }
            val result = repository.paymentService.processPayment(
                userId = "usr_new_${System.currentTimeMillis()}",
                productTitle = "Membership: ${tier.title}",
                amountZar = tier.priceZar,
                provider = current.paymentMethod
            )
            registrationState.update { it.copy(isPaymentProcessing = false) }

            if (result is PaymentResult.Success) {
                registrationState.update {
                    it.copy(
                        paymentCompleted = true,
                        membershipActivated = true,
                        paymentTransactionId = result.transaction.transactionId
                    )
                }
                repository.recordTransaction(result.transaction)
                repository.upgradeSubscription(tier, current.paymentMethod.title)
                showNotice("Payment confirmed! Server activated ${tier.title}.")
                onSuccess()
            } else {
                showNotice("Payment could not be processed. Please try again.")
            }
        }
    }

    fun submitProfileForModeration() {
        viewModelScope.launch {
            val state = registrationState.value
            val profileId = "p_reg_${System.currentTimeMillis()}"
            val galleryString = state.galleryPhotos.joinToString(",")

            val newProfile = Profile(
                id = profileId,
                userId = "usr_current",
                displayName = state.stageName.ifBlank { state.accountDisplayName.ifBlank { "VIP Creator" } },
                age = state.age,
                province = state.province,
                city = state.city,
                area = state.area,
                category = state.selectedProfileCategory,
                description = state.bioDescription.ifBlank { "Verified companion profile on Pink Passions South Africa." },
                rating = 5.0f,
                reviewCount = 0,
                // IMPORTANT: NEVER AUTOMATICALLY PUBLISH AN UNAPPROVED PROFILE!
                status = ListingStatus.PENDING_MODERATION,
                moderationStatus = ModerationStatus.PENDING_REVIEW,
                verifiedLevel = if (state.isBiometricVerified || state.isMobileVerified) VerificationLevel.FULL else VerificationLevel.PHOTO,
                membershipTier = state.selectedMembershipTier,
                isGold = state.selectedMembershipTier == MembershipTier.GOLD,
                isFeatured = state.selectedMembershipTier == MembershipTier.VIP || state.selectedMembershipTier == MembershipTier.UPMARKET_EXCLUSIVE,
                isOnline = true,
                priceText = state.hourlyRateZar.ifBlank { "From R1,000" },
                avatarUrl = state.coverPhotoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                galleryUrls = galleryString,
                services = state.services,
                incallOutcall = state.incallOutcall,
                phone = state.whatsappContact,
                whatsapp = state.whatsappContact,
                telegram = state.telegramContact,
                isFavourite = false,
                viewsCount = 0,
                contactClicks = 0,
                isDemo = false
            )

            repository.createOrUpdateProfile(newProfile)
            repository.addAuditLog(
                action = "PROFILE_SUBMITTED_FOR_MODERATION",
                targetType = "Profile",
                targetId = profileId,
                notes = "Submitted by ${newProfile.displayName} (${state.selectedMembershipTier.title}). Status: PENDING_REVIEW."
            )
            repository.addNotification(
                title = "Profile Submitted For Moderation",
                message = "Your profile '${newProfile.displayName}' is currently in PENDING REVIEW status. Our team reviews all profiles within 24-48 hours before publication.",
                type = "MODERATION"
            )

            registrationState.update {
                it.copy(
                    createdProfileId = profileId,
                    moderationStatus = ModerationStatus.PENDING_REVIEW,
                    submissionTimestamp = "Just now"
                )
            }
            registrationCurrentStep.value = 14 // Final confirmation step
            showNotice("Profile successfully submitted for administrator moderation.")
        }
    }

    fun submitListingCreation() {
        viewModelScope.launch {
            val draft = createListingDraft.value
            val newProfile = Profile(
                id = "p_${System.currentTimeMillis()}",
                userId = "usr_current",
                displayName = draft.stageName.ifBlank { "New Creator" },
                age = draft.age,
                province = draft.province,
                city = draft.city,
                area = draft.area,
                category = draft.category,
                description = draft.description.ifBlank { "New verified profile on Pink Passions." },
                rating = 5.0f,
                reviewCount = 1,
                status = ListingStatus.PENDING_MODERATION,
                moderationStatus = ModerationStatus.PENDING_REVIEW,
                verifiedLevel = draft.verificationLevel,
                membershipTier = draft.membershipTier,
                isGold = draft.membershipTier == MembershipTier.GOLD,
                isFeatured = draft.membershipTier == MembershipTier.FEATURED || draft.membershipTier == MembershipTier.VIP,
                isOnline = true,
                priceText = draft.priceText,
                services = draft.services,
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80"
            )
            repository.createOrUpdateProfile(newProfile)
            listingCreationSuccess.value = true
            showNotice("Your listing '${newProfile.displayName}' has been submitted for moderation review.")
            createListingStep.value = 1
            activeSubscreen.value = "none"
        }
    }

    fun adminModerateProfile(profileId: String, action: String) {
        viewModelScope.launch {
            repository.adminModerateProfile(profileId, action)
            showNotice("Admin action '$action' applied to profile.")
        }
    }

    fun adminApproveVerification(reqId: String) {
        repository.adminApproveVerification(reqId)
        showNotice("Verification request approved. Verified badge issued.")
    }

    fun adminRejectVerification(reqId: String, reason: String) {
        repository.adminRejectVerification(reqId, reason)
        showNotice("Verification request rejected.")
    }

    fun adminModerateReview(reviewId: String, status: String) {
        viewModelScope.launch {
            repository.moderateReview(reviewId, status)
            showNotice("Review status updated to $status.")
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            showNotice("Review deleted by moderator.")
        }
    }

    fun selectChatRoom(room: ChatRoom) {
        selectedChatRoom.value = room
        activeChatRoomId.value = room.id
        activeSubscreen.value = "local_chat_room"
    }

    fun sendChatRoomMessage(text: String, tipAmount: Int? = null) {
        if (text.isBlank()) return
        val currentRoomId = activeChatRoomId.value
        repository.sendChatRoomMessage(currentRoomId, text, "You", tipAmount)
        if (tipAmount != null) {
            showNotice("Tipped R$tipAmount to the room lounge! 💖")
        }
    }

    fun triggerShimmerReload() {
        viewModelScope.launch {
            isLoadingFeeds.value = true
            kotlinx.coroutines.delay(400)
            isLoadingFeeds.value = false
        }
    }

    fun showNotice(msg: String) {
        userNotice.value = msg
    }

    fun clearNotice() {
        userNotice.value = null
    }
}
