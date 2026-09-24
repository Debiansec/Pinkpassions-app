package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class PinkPassionsRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val profileDao = database.profileDao()
    private val businessDao = database.businessDao()
    private val eventDao = database.eventDao()
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val messageDao = database.messageDao()
    private val reviewDao = database.reviewDao()

    val paymentService = PaymentService()
    val firestoreService = FirestoreService()

    // Configurable Memberships State (Admin can modify prices/features without code rewrite)
    private val _configuredMembershipTiers = MutableStateFlow<List<MembershipTier>>(MembershipTier.values().toList())
    val configuredMembershipTiers: StateFlow<List<MembershipTier>> = _configuredMembershipTiers.asStateFlow()

    // Configurable Upgrade Products State
    private val _configuredUpgradeProducts = MutableStateFlow<List<UpgradeType>>(UpgradeType.values().toList())
    val configuredUpgradeProducts: StateFlow<List<UpgradeType>> = _configuredUpgradeProducts.asStateFlow()

    // Active User Subscription State
    private val _userSubscription = MutableStateFlow(
        Subscription(
            id = "sub_vip_01",
            userId = "usr_current",
            membershipTier = MembershipTier.VIP,
            status = SubscriptionStatus.ACTIVE,
            amountZar = 1200,
            paymentProvider = "PayFast",
            nextBillingDate = "1 October 2026",
            expiresAt = "30 September 2026",
            createdAt = "1 September 2026"
        )
    )
    val userSubscription: StateFlow<Subscription> = _userSubscription.asStateFlow()

    // User Verification Status State
    private val _userVerificationStatus = MutableStateFlow(VerificationStatus.VERIFIED)
    val userVerificationStatus: StateFlow<VerificationStatus> = _userVerificationStatus.asStateFlow()

    // User Verification Level
    private val _userVerificationLevel = MutableStateFlow(VerificationLevel.FULL)
    val userVerificationLevel: StateFlow<VerificationLevel> = _userVerificationLevel.asStateFlow()

    // Verification Requests Queue for Admin Review
    private val _verificationRequests = MutableStateFlow<List<VerificationRequest>>(emptyList())
    val verificationRequests: StateFlow<List<VerificationRequest>> = _verificationRequests.asStateFlow()

    // Conversations state
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Advertising campaigns state
    private val _campaigns = MutableStateFlow<List<AdvertisingCampaign>>(emptyList())
    val campaigns: StateFlow<List<AdvertisingCampaign>> = _campaigns.asStateFlow()

    // Payment transactions state
    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(emptyList())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    // Admin Audit Logs state
    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    // Community Groups State
    private val _communityGroups = MutableStateFlow<List<CommunityGroup>>(emptyList())
    val communityGroups: StateFlow<List<CommunityGroup>> = _communityGroups.asStateFlow()

    // Notifications State
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Active Profile Promotions (Featured, Sponsored, Top Slider)
    private val _activePromotions = MutableStateFlow<List<UpgradeType>>(listOf(UpgradeType.FEATURED))
    val activePromotions: StateFlow<List<UpgradeType>> = _activePromotions.asStateFlow()

    init {
        scope.launch {
            seedDatabaseIfEmpty()
            _conversations.value = MockDataProvider.getInitialConversations()
            _campaigns.value = MockDataProvider.getInitialCampaigns()
            _verificationRequests.value = MockDataProvider.getInitialVerificationRequests()
            _communityGroups.value = MockDataProvider.getInitialCommunityGroups()
            _notifications.value = MockDataProvider.getInitialNotifications()
            _transactions.value = listOf(
                PaymentTransaction("TXN-PF8912", "usr_current", "VIP Platinum Membership (Monthly)", 1200, "ZAR", "PayFast", "COMPLETED", "1 hour ago", "INV-8912"),
                PaymentTransaction("TXN-UP4801", "usr_current", "7-Day Featured Spotlight Boost", 480, "ZAR", "PayFast", "COMPLETED", "Yesterday", "INV-4801"),
                PaymentTransaction("TXN-PP2803", "usr_current", "14-Day Sponsored Placement", 280, "ZAR", "PayPal", "COMPLETED", "2 weeks ago", "INV-2803")
            )
            _auditLogs.value = listOf(
                AdminAuditLog("aud_1", "PROFILE_APPROVED", "Profile", "p1", "SuperAdmin", "10 mins ago", "ID & Facial Biometric Match passed."),
                AdminAuditLog("aud_2", "SUBSCRIPTION_ACTIVE", "Subscription", "sub_vip_01", "PayFast_ITN", "1 hour ago", "VIP tier activated for R1,200/mo."),
                AdminAuditLog("aud_3", "REVIEW_APPROVED", "Review", "rev_1", "ModTeam", "2 hours ago", "5-star client review verified and published.")
            )
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val initialProfiles = MockDataProvider.getInitialProfiles()
        val initialBusinesses = MockDataProvider.getInitialBusinesses()
        profileDao.insertProfiles(initialProfiles)
        businessDao.insertBusinesses(initialBusinesses)
        eventDao.insertEvents(MockDataProvider.getInitialEvents())
        productDao.insertProducts(MockDataProvider.getInitialProducts())
        reviewDao.insertReviews(MockDataProvider.getInitialReviews())

        try {
            firestoreService.syncInitialDataToCloud(initialProfiles, initialBusinesses)
        } catch (ignored: Exception) {}
    }

    // Profiles Flow
    fun getAllProfiles(): Flow<List<Profile>> = profileDao.getAllProfiles()

    fun getFavouriteProfiles(): Flow<List<Profile>> = profileDao.getFavouriteProfiles()

    suspend fun getProfileById(id: String): Profile? = profileDao.getProfileById(id)

    suspend fun toggleFavourite(id: String, isFav: Boolean) {
        profileDao.toggleFavourite(id, isFav)
    }

    suspend fun createOrUpdateProfile(profile: Profile) {
        profileDao.insertProfile(profile)
        firestoreService.saveDiscoveryProfile(profile)
        addAuditLog("PROFILE_SAVED", "Profile", profile.id, "Profile listing updated. Status: ${profile.status}")
    }

    // Reviews & 5-Star Rating System
    fun getReviewsForProfile(profileId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForProfile(profileId)
    }

    fun getAllReviews(): Flow<List<Review>> = reviewDao.getAllReviews()

    suspend fun submitReview(profileId: String, rating: Int, comment: String, reviewerName: String = "Verified Client"): Boolean {
        val newReview = Review(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            reviewerId = "usr_cli_${UUID.randomUUID().toString().take(4)}",
            reviewerName = reviewerName,
            profileId = profileId,
            rating = rating.coerceIn(1, 5),
            comment = comment,
            createdAt = "Just now",
            moderationStatus = "APPROVED",
            isVerifiedClient = true
        )
        reviewDao.insertReview(newReview)
        addAuditLog("REVIEW_SUBMITTED", "Review", newReview.id, "$rating stars for profile $profileId")
        addNotification("New Review Received", "A client left a $rating-star review on your profile.", "REVIEW")
        return true
    }

    suspend fun moderateReview(reviewId: String, status: String) {
        reviewDao.updateModerationStatus(reviewId, status)
        addAuditLog("REVIEW_MODERATED", "Review", reviewId, "Moderation status changed to $status")
    }

    suspend fun deleteReview(reviewId: String) {
        reviewDao.deleteReview(reviewId)
        addAuditLog("REVIEW_DELETED", "Review", reviewId, "Admin removed review from platform.")
    }

    // Subscription & Membership Lifecycle
    fun upgradeSubscription(tier: MembershipTier, provider: String) {
        val nextMonth = "1 October 2026"
        val expiry = "30 September 2026"
        _userSubscription.value = Subscription(
            id = "sub_${UUID.randomUUID().toString().take(6)}",
            userId = "usr_current",
            membershipTier = tier,
            status = SubscriptionStatus.ACTIVE,
            amountZar = tier.priceZar,
            paymentProvider = provider,
            nextBillingDate = if (tier.priceZar > 0) nextMonth else "Never (Free)",
            expiresAt = if (tier.priceZar > 0) expiry else "Lifetime",
            createdAt = "Today"
        )
        addAuditLog("SUBSCRIPTION_UPDATED", "Membership", tier.id, "Activated ${tier.title} via $provider (R${tier.priceZar})")
        addNotification("Membership Plan Activated", "You are now on the ${tier.title} (${tier.badgeLabel}).", "MEMBERSHIP")
    }

    fun cancelSubscription() {
        val current = _userSubscription.value
        _userSubscription.value = current.copy(
            status = SubscriptionStatus.CANCELLED,
            nextBillingDate = "Cancelled (Expires ${current.expiresAt})"
        )
        addAuditLog("SUBSCRIPTION_CANCELLED", "Membership", current.id, "User cancelled auto-renewal.")
        addNotification("Subscription Cancelled", "Your plan will remain active until ${current.expiresAt}.", "MEMBERSHIP")
    }

    fun resumeSubscription() {
        val current = _userSubscription.value
        _userSubscription.value = current.copy(
            status = SubscriptionStatus.ACTIVE,
            nextBillingDate = "1 October 2026"
        )
        addAuditLog("SUBSCRIPTION_RESUMED", "Membership", current.id, "User reactivated auto-renewal.")
        addNotification("Auto-Renewal Reactivated", "Your membership will auto-renew on 1 October 2026.", "MEMBERSHIP")
    }

    fun updatePaymentProvider(providerName: String) {
        val current = _userSubscription.value
        _userSubscription.value = current.copy(
            paymentProvider = providerName
        )
        addAuditLog("PAYMENT_METHOD_UPDATED", "Billing", current.id, "Updated default billing provider to $providerName")
        addNotification("Billing Method Updated", "Default payment method updated to $providerName.", "BILLING")
    }

    // Upgrades / Profile Promotions
    fun purchaseProfileUpgrade(upgrade: UpgradeType, provider: String) {
        _activePromotions.update { (it + upgrade).distinct() }
        val txn = PaymentTransaction(
            transactionId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = "usr_current",
            productTitle = "${upgrade.title} (${upgrade.durationText})",
            amountZar = upgrade.priceZar,
            currency = "ZAR",
            provider = provider,
            status = "COMPLETED",
            timestamp = "Just now"
        )
        recordTransaction(txn)
        addNotification("Profile Boost Active", "${upgrade.title} activated successfully. Your visibility is boosted!", "PROMOTION")
    }

    // Verification Workflow
    fun submitMobileVerification(mobileNumber: String, otpCode: String): Boolean {
        if (otpCode.length == 4 || otpCode == "1234") {
            _userVerificationLevel.value = VerificationLevel.BASIC
            _userVerificationStatus.value = VerificationStatus.VERIFIED
            addNotification("Mobile Number Verified", "Mobile $mobileNumber confirmed with OTP.", "VERIFICATION")
            addAuditLog("MOBILE_VERIFIED", "Verification", "usr_current", "Mobile $mobileNumber verified via OTP.")
            return true
        }
        return false
    }

    fun submitEmailVerification(email: String): Boolean {
        addNotification("Email Verified", "Email $email verified successfully.", "VERIFICATION")
        addAuditLog("EMAIL_VERIFIED", "Verification", "usr_current", "Email $email confirmed.")
        return true
    }

    fun submitFacialAndIdVerification(docType: String, matchScore: Int = 97) {
        val req = VerificationRequest(
            id = "vr_${UUID.randomUUID().toString().take(6)}",
            userId = "usr_current",
            userName = "Current Member",
            userEmail = "member@pinkpassions.co.za",
            userMobile = "+27 82 123 4567",
            method = VerificationMethodType.FACIAL_BIOMETRICS,
            status = VerificationStatus.VERIFIED,
            documentType = docType,
            submittedAt = "Just now",
            matchScorePct = matchScore,
            livenessPassed = true,
            reviewerNotes = "AI Biometric Similarity check: $matchScore% match. Liveness check: PASSED. Official Neon Green Shield issued."
        )
        _verificationRequests.update { listOf(req) + it }
        _userVerificationStatus.value = VerificationStatus.VERIFIED
        _userVerificationLevel.value = VerificationLevel.FULL
        addNotification("Verified Member Badge Issued", "Your ID and Facial Verification passed! The Neon Green shield is now live on your profile.", "VERIFICATION")
        addAuditLog("FACIAL_VERIFICATION_PASSED", "Verification", req.id, "$docType verified with $matchScore% confidence.")
    }

    fun adminApproveVerification(requestId: String) {
        _verificationRequests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = VerificationStatus.VERIFIED) else it }
        }
        addAuditLog("ADMIN_VERIFY_APPROVED", "Verification", requestId, "Admin manually approved verification.")
    }

    fun adminRejectVerification(requestId: String, reason: String) {
        _verificationRequests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = VerificationStatus.FAILED, reviewerNotes = reason) else it }
        }
        addAuditLog("ADMIN_VERIFY_REJECTED", "Verification", requestId, "Reason: $reason")
    }

    // Community Groups
    fun toggleJoinCommunityGroup(groupId: String) {
        _communityGroups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    val joined = !g.isJoined
                    g.copy(
                        isJoined = joined,
                        memberCount = if (joined) g.memberCount + 1 else g.memberCount - 1
                    )
                } else g
            }
        }
    }

    fun createCommunityGroup(name: String, description: String, category: String, emoji: String) {
        val newGroup = CommunityGroup(
            id = "grp_${UUID.randomUUID().toString().take(6)}",
            name = name,
            description = description,
            memberCount = 1,
            category = category,
            isJoined = true,
            iconEmoji = emoji
        )
        _communityGroups.update { listOf(newGroup) + it }
        addAuditLog("GROUP_CREATED", "Community", newGroup.id, "Group: $name ($category)")
    }

    // Notifications
    fun addNotification(title: String, message: String, type: String) {
        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            title = title,
            message = message,
            type = type,
            timestamp = "Just now",
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
    }

    fun markNotificationAsRead(notifId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notifId) it.copy(isRead = true) else it }
        }
    }

    // Businesses & Events
    fun getAllBusinesses(): Flow<List<Business>> = businessDao.getAllBusinesses()
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun createOrUpdateBusiness(business: Business) {
        businessDao.insertBusiness(business)
        firestoreService.saveBusinessListing(business)
        addAuditLog("BUSINESS_SAVED", "Business", business.id, "Business directory listing saved.")
    }

    // Products & Cart
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    fun getCartItems(): Flow<List<CartItem>> = cartDao.getCartItems()

    suspend fun addToCart(product: Product) {
        cartDao.insertCartItem(
            CartItem(
                productId = product.id,
                name = product.name,
                priceZar = product.priceZar,
                quantity = 1,
                imageUrl = product.imageUrl
            )
        )
    }

    suspend fun updateCartQuantity(productId: String, qty: Int) {
        if (qty <= 0) {
            cartDao.deleteCartItem(productId)
        } else {
            cartDao.updateQuantity(productId, qty)
        }
    }

    suspend fun clearCart() = cartDao.clearCart()

    // Messaging
    fun getMessagesForConversation(convoId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(convoId).map { dbList ->
            if (dbList.isEmpty()) {
                val initList = MockDataProvider.getInitialMessages(convoId)
                initList.forEach { messageDao.insertMessage(it) }
                initList
            } else {
                dbList
            }
        }
    }

    suspend fun sendMessage(convoId: String, text: String, senderName: String = "You") {
        val newMsg = Message(
            id = "msg_${System.currentTimeMillis()}",
            conversationId = convoId,
            senderId = "me",
            senderName = senderName,
            message = text,
            timestamp = "Just now",
            isFromMe = true,
            isRead = true
        )
        messageDao.insertMessage(newMsg)

        _conversations.update { list ->
            list.map { c ->
                if (c.id == convoId) {
                    c.copy(lastMessage = text, lastMessageTime = "Just now")
                } else c
            }
        }
    }

    // Advertising Campaigns
    fun createCampaign(name: String, placement: String, budget: Int, days: Int) {
        val newCamp = AdvertisingCampaign(
            id = "ad_${UUID.randomUUID().toString().take(6)}",
            campaignName = name,
            placement = placement,
            budgetZar = budget,
            startDate = "Today",
            endDate = "In $days days",
            status = "ACTIVE",
            impressions = 0,
            clicks = 0
        )
        _campaigns.update { listOf(newCamp) + it }
        addAuditLog("CAMPAIGN_CREATED", "Advertising", newCamp.id, "Budget: R$budget ($placement)")
    }

    // Admin Actions
    suspend fun adminModerateProfile(profileId: String, action: String) {
        val profile = profileDao.getProfileById(profileId) ?: return
        when (action) {
            "APPROVE" -> profileDao.updateProfile(profile.copy(status = ListingStatus.LIVE, moderationStatus = ModerationStatus.APPROVED))
            "REJECT" -> profileDao.updateProfile(profile.copy(status = ListingStatus.REJECTED, moderationStatus = ModerationStatus.REJECTED))
            "SUSPEND" -> profileDao.updateProfile(profile.copy(status = ListingStatus.SUSPENDED, moderationStatus = ModerationStatus.SUSPENDED))
            "PENDING_REVIEW" -> profileDao.updateProfile(profile.copy(status = ListingStatus.PENDING_MODERATION, moderationStatus = ModerationStatus.PENDING_REVIEW))
            "DRAFT" -> profileDao.updateProfile(profile.copy(status = ListingStatus.DRAFT, moderationStatus = ModerationStatus.DRAFT))
            "FEATURE" -> profileDao.updateProfile(profile.copy(isFeatured = !profile.isFeatured))
            "GOLD" -> profileDao.updateProfile(profile.copy(isGold = !profile.isGold))
            "VERIFY_FULL" -> profileDao.updateProfile(profile.copy(verifiedLevel = VerificationLevel.FULL))
            "DELETE" -> profileDao.deleteProfile(profileId)
        }
        addAuditLog("ADMIN_$action", "Profile", profileId, "Admin performed action: $action")
    }

    fun addAuditLog(action: String, targetType: String, targetId: String, notes: String) {
        val entry = AdminAuditLog(
            id = "aud_${UUID.randomUUID().toString().take(6)}",
            action = action,
            targetType = targetType,
            targetId = targetId,
            adminUser = "SuperAdmin",
            timestamp = "Just now",
            notes = notes
        )
        _auditLogs.update { listOf(entry) + it }
    }

    fun recordTransaction(transaction: PaymentTransaction) {
        _transactions.update { listOf(transaction) + it }
        addAuditLog("PAYMENT_RECORDED", "Transaction", transaction.transactionId, "${transaction.provider} - R${transaction.amountZar}")
    }

    // Chat Rooms State & Methods
    val chatRooms = MutableStateFlow(MockDataProvider.getInitialChatRooms())
    private val _chatRoomMessagesMap = MutableStateFlow<Map<String, List<ChatRoomMessage>>>(
        MockDataProvider.getInitialChatRooms().associate { room ->
            room.id to MockDataProvider.getInitialChatRoomMessages(room.id)
        }
    )

    init {
        // Sync initial categorized chat rooms to Firestore in background
        scope.launch {
            MockDataProvider.getInitialChatRooms().forEach { room ->
                firestoreService.saveChatRoom(room)
            }
        }
    }

    fun getChatRoomsByCategory(category: String? = null): Flow<List<ChatRoom>> {
        val firestoreFlow = firestoreService.observeCategorizedChatRooms(category)
        return combine(chatRooms, firestoreFlow) { localList, cloudList ->
            val baseList = if (cloudList.isNotEmpty()) {
                val nonOverlapping = localList.filterNot { local -> cloudList.any { it.id == local.id } }
                cloudList + nonOverlapping
            } else {
                localList
            }
            if (category == null || category == "All Rooms" || category == "All") {
                baseList
            } else {
                baseList.filter { it.category.equals(category, ignoreCase = true) }
            }
        }
    }

    fun getChatRoomMessages(roomId: String): Flow<List<ChatRoomMessage>> {
        val firestoreFlow = firestoreService.observeChatRoomMessages(roomId)
        val localFlow = _chatRoomMessagesMap.map { it[roomId] ?: emptyList() }
        return combine(firestoreFlow, localFlow) { cloudList, localList ->
            if (cloudList.isNotEmpty()) {
                val nonOverlappingLocal = localList.filterNot { local -> cloudList.any { it.id == local.id } }
                nonOverlappingLocal + cloudList
            } else {
                localList
            }
        }
    }

    fun sendChatRoomMessage(roomId: String, text: String, senderName: String = "You", tipAmount: Int? = null) {
        val newMsg = ChatRoomMessage(
            id = "crm_${UUID.randomUUID().toString().take(6)}",
            roomId = roomId,
            senderId = "me",
            senderName = senderName,
            senderAvatar = "",
            message = text,
            timestamp = "Just now",
            isFromMe = true,
            isModel = false,
            verifiedLevel = VerificationLevel.PHOTO,
            tipAmountZar = tipAmount
        )
        _chatRoomMessagesMap.update { currentMap ->
            val list = currentMap[roomId] ?: emptyList()
            currentMap + (roomId to (list + newMsg))
        }
        scope.launch {
            firestoreService.sendChatRoomMessage(newMsg)
        }
    }

    fun setSubscriptionTier(tier: MembershipTier, provider: String = "PayFast", isActive: Boolean = true) {
        _userSubscription.update {
            it.copy(
                membershipTier = if (isActive) tier else MembershipTier.BASIC_FREE,
                status = if (isActive) SubscriptionStatus.ACTIVE else SubscriptionStatus.CANCELLED,
                amountZar = if (isActive) tier.priceZar else 0,
                paymentProvider = provider,
                nextBillingDate = if (isActive) "In 30 days" else "None"
            )
        }
        scope.launch {
            firestoreService.saveUserSubscription("usr_current", tier, isActive, provider)
        }
    }
}
