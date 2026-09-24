package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.PaymentMethod

enum class VerificationLevel(val label: String, val badgeColorHex: Long) {
    NONE("Unverified", 0xFF6B6B80),
    BASIC("Basic Verified", 0xFF00E676),
    PHOTO("Photo Verified", 0xFF00D2FF),
    FULL("Fully Verified ID & Face", 0xFF39FF14)
}

enum class VerificationStatus {
    NOT_STARTED, PENDING, IN_REVIEW, VERIFIED, FAILED, EXPIRED, SUSPENDED
}

enum class VerificationMethodType {
    MOBILE_OTP, EMAIL, ID_PASSPORT, FACIAL_BIOMETRICS
}

enum class MembershipTier(
    val id: String,
    val title: String,
    val priceZar: Int,
    val billingPeriod: String,
    val badgeLabel: String,
    val badgeColorHex: Long,
    val shortDescription: String,
    val features: List<String>,
    val ctaText: String = if (priceZar > 0) "SIGN UP FOR R${String.format(java.util.Locale.US, "%,d", priceZar)} / MONTH" else "SIGN UP FOR FREE"
) {
    BASIC_FREE(
        id = "basic_free",
        title = "Basic Free Membership",
        priceZar = 0,
        billingPeriod = "Forever",
        badgeLabel = "Basic Member",
        badgeColorHex = 0xFF6B6B80,
        shortDescription = "Explore the directory, browse profiles, leave reviews, and submit inquiries.",
        features = listOf(
            "Create account & profile",
            "Basic profile listing",
            "Browse profiles & province search",
            "Favourites & client reviews",
            "Community & moderated chat access",
            "Verification application eligible"
        )
    ),
    FRIENDS_WITH_BENEFITS(
        id = "friends_with_benefits",
        title = "Friends with Benefits VIP Pass",
        priceZar = 99,
        billingPeriod = "/ month",
        badgeLabel = "FWB VIP",
        badgeColorHex = 0xFFFF1493, // Deep Pink
        shortDescription = "South African adult dating chat room access for friends with benefits, casual sex, local hookups, swingers & 50km Google Maps distance radar.",
        features = listOf(
            "Access to all Friends with Benefits & Hookup chat rooms",
            "Google Maps 50km distance viewer & local radar",
            "Local hookups & casual encounters instant messaging",
            "Swingers & couples private party lounges",
            "Unlimited direct messages with nearby members",
            "Discreet profile badge & 18+ verified status",
            "Instant photo sharing & meetup coordination"
        )
    ),
    INDEPENDENT_ESCORT(
        id = "independent_escort",
        title = "Independent Escort",
        priceZar = 250,
        billingPeriod = "/ month",
        badgeLabel = "Independent",
        badgeColorHex = 0xFFFF80BF, // Light Pink
        shortDescription = "Essential independent companion profile with full photo gallery, search exposure, and direct inquiries.",
        features = listOf(
            "Enhanced profile",
            "Profile gallery",
            "Profile search visibility",
            "Reviews",
            "Ratings",
            "Favourites",
            "Profile statistics",
            "Community access",
            "Chat",
            "Verification eligibility",
            "Upgrade options"
        )
    ),
    PREMIUM(
        id = "premium",
        title = "Premium Membership",
        priceZar = 380,
        billingPeriod = "/ month",
        badgeLabel = "Premium",
        badgeColorHex = 0xFFB388FF, // Purple
        shortDescription = "Increased visibility, priority search placement in your city, and featured profile eligibility.",
        features = listOf(
            "All Independent features included",
            "Increased profile visibility",
            "Priority search placement",
            "Enhanced gallery",
            "Profile analytics",
            "Featured profile eligibility",
            "Advanced profile controls",
            "Community access",
            "Chat",
            "Verification eligibility"
        )
    ),
    STRIPPER_DANCER(
        id = "stripper_dancer",
        title = "Stripper / Pole Dancer",
        priceZar = 350,
        billingPeriod = "/ month",
        badgeLabel = "Dancer VIP",
        badgeColorHex = 0xFFFF4081, // Pink
        shortDescription = "Professional performance profile, nightlife booking calendar, and client reviews.",
        features = listOf(
            "Professional performance profile & gallery",
            "Nightlife & event search placement",
            "Client reviews, ratings & booking links",
            "Community & live lounge access",
            "Verification eligibility & upgrade options"
        )
    ),
    MASSEUSE(
        id = "masseuse",
        title = "Masseuse / Massage Therapist",
        priceZar = 180,
        billingPeriod = "/ month",
        badgeLabel = "Massage Spa",
        badgeColorHex = 0xFFFF4081, // Pink
        shortDescription = "Professional therapy menu, studio address mapping, and 5-star ratings.",
        features = listOf(
            "Professional therapy profile & treatment menu",
            "Location & studio address mapping",
            "Client reviews & 5-star ratings",
            "Province & city spa search",
            "Verification eligibility"
        )
    ),
    GOLD(
        id = "gold",
        title = "Gold Membership",
        priceZar = 850,
        billingPeriod = "/ month",
        badgeLabel = "Gold Executive",
        badgeColorHex = 0xFFFFD700, // Gold
        shortDescription = "Higher search ranking, Gold membership badge distinction, and priority moderation queue.",
        features = listOf(
            "All Premium features included",
            "Higher search ranking",
            "Gold membership badge",
            "Priority visibility",
            "Enhanced profile presentation",
            "Advanced analytics",
            "Unlimited approved gallery images",
            "Priority moderation queue",
            "Verification eligibility",
            "Community and chat access"
        )
    ),
    VIP(
        id = "vip",
        title = "VIP Membership",
        priceZar = 1200,
        billingPeriod = "/ month",
        badgeLabel = "VIP Platinum",
        badgeColorHex = 0xFFE2E8F0, // Silver / Platinum
        shortDescription = "Maximum profile visibility, top VIP placement, and dedicated priority support.",
        features = listOf(
            "All Gold features included",
            "VIP badge",
            "Maximum profile visibility",
            "VIP placement",
            "Advanced analytics",
            "Priority support",
            "Enhanced profile customization",
            "Priority search placement",
            "Verification eligibility",
            "Community and chat access"
        )
    ),
    UPMARKET_EXCLUSIVE(
        id = "upmarket_exclusive",
        title = "Upmarket Exclusive",
        priceZar = 1600,
        billingPeriod = "/ month",
        badgeLabel = "Exclusive Elite",
        badgeColorHex = 0xFFFF6D00, // Neon Orange
        shortDescription = "Elite luxury profile presentation with maximum discovery placement nationwide.",
        features = listOf(
            "All VIP features included",
            "Exclusive membership badge",
            "Elite profile presentation",
            "Maximum visibility",
            "Priority discovery placement",
            "Premium profile styling",
            "Advanced analytics",
            "Priority support",
            "Verification eligibility"
        )
    ),
    ESCORT_AGENCY(
        id = "escort_agency",
        title = "Escort Agency",
        priceZar = 2500,
        billingPeriod = "/ month",
        badgeLabel = "Agency Verified",
        badgeColorHex = 0xFF00E5FF, // Agency Cyan
        shortDescription = "Multi-model agency dashboard to manage and promote a full roster of verified models.",
        features = listOf(
            "Multi-model agency dashboard & profiles",
            "Manage unlimited roster of verified models",
            "Agency analytics, inquiry logs & reviews",
            "Top agency directory search visibility",
            "Direct phone/WhatsApp agency contact box"
        )
    ),
    NIGHTLIFE_VENUE(
        id = "nightlife_venue",
        title = "Nightlife Venues Listing",
        priceZar = 150,
        billingPeriod = "/ month",
        badgeLabel = "Venue Verified",
        badgeColorHex = 0xFF7C4DFF, // Venue Purple
        shortDescription = "Venue profile with photos, capacity, location mapping, and event calendar.",
        features = listOf(
            "Venue profile with photo gallery & capacity",
            "Province/city location mapping & directions",
            "Event calendar, opening hours & specials",
            "Client reviews & ratings",
            "Promotional upgrade eligibility"
        )
    );

    companion object {
        val FREE = BASIC_FREE
        val FEATURED = PREMIUM
    }
}

enum class UpgradeType(
    val id: String,
    val title: String,
    val priceZar: Int,
    val durationText: String,
    val badgeLabel: String,
    val badgeColorHex: Long,
    val benefits: List<String>
) {
    FEATURED(
        id = "upgrade_featured",
        title = "Featured Spotlight",
        priceZar = 480,
        durationText = "7 Days",
        badgeLabel = "Featured",
        badgeColorHex = 0xFFFFEA00, // Neon Yellow
        benefits = listOf(
            "Featured homepage spotlight placement",
            "Glowing Neon Yellow Featured badge",
            "Search ranking boost across selected city",
            "Up to 3.8x more profile views and clicks"
        )
    ),
    SPONSORED(
        id = "upgrade_sponsored",
        title = "Sponsored Placement",
        priceZar = 280,
        durationText = "14 Days",
        badgeLabel = "Sponsored",
        badgeColorHex = 0xFF00D2FF, // Light Blue
        benefits = listOf(
            "Promoted search visibility in category lists",
            "Light Blue Sponsored Megaphone badge",
            "Promotional banner placement",
            "Up to 2.5x more discovery inquiries"
        )
    ),
    TOP_SLIDER(
        id = "upgrade_top_slider",
        title = "Homepage Top Slider",
        priceZar = 480,
        durationText = "30 Days (Month)",
        badgeLabel = "Top Slider",
        badgeColorHex = 0xFFFF2A85, // Hot Pink
        benefits = listOf(
            "Rotating luxury header banner on app launch",
            "Maximum promotional prominence in South Africa",
            "Direct link tap to your full profile",
            "Top Slider advertiser distinction"
        )
    )
}

enum class SubscriptionStatus {
    PENDING, ACTIVE, GRACE_PERIOD, PAYMENT_FAILED, CANCELLED, EXPIRED, SUSPENDED
}

enum class ListingStatus {
    DRAFT, PENDING_PAYMENT, PENDING_MODERATION, APPROVED, LIVE, REJECTED, SUSPENDED, EXPIRED
}

enum class ModerationStatus(val label: String, val badgeColorHex: Long) {
    DRAFT("Draft", 0xFF6B6B80),
    PENDING_REVIEW("Pending Review", 0xFFFFD700),
    APPROVED("Approved", 0xFF00E676),
    REJECTED("Rejected", 0xFFFF5252),
    SUSPENDED("Suspended", 0xFFFF1744)
}

enum class ProfileCategory(val title: String, val iconName: String) {
    INDEPENDENT("Independent Escorts", "person"),
    EXECUTIVE("Executive & Upmarket", "diamond"),
    VIP("VIP Members", "star"),
    MASSAGE("Masseuse & Spa Therapy", "spa"),
    DANCERS("Strippers & Pole Dancers", "nightlife"),
    AGENCIES("Escort Agencies", "business"),
    VENUES("Nightlife Clubs & Venues", "apartment"),
    EVENTS("VIP Events & Parties", "event"),
    LIVE("Live Cams & Sessions", "videocam")
}

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey val id: String,
    val userId: String,
    val displayName: String,
    val age: Int,
    val province: String,
    val city: String,
    val area: String,
    val category: ProfileCategory,
    val description: String,
    val rating: Float = 4.9f,
    val reviewCount: Int = 18,
    val status: ListingStatus = ListingStatus.LIVE,
    val moderationStatus: ModerationStatus = ModerationStatus.APPROVED,
    val verifiedLevel: VerificationLevel = VerificationLevel.BASIC,
    val membershipTier: MembershipTier = MembershipTier.BASIC_FREE,
    val isGold: Boolean = false,
    val isFeatured: Boolean = false,
    val isSponsored: Boolean = false,
    val isTopSlider: Boolean = false,
    val isOnline: Boolean = true,
    val lastActive: String = "Online now",
    val priceText: String = "From R800",
    val avatarUrl: String = "",
    val galleryUrls: String = "",
    val services: String = "Dinner dates, VIP Companionship, Social Events, Travel Companion",
    val incallOutcall: String = "Incall & Outcall",
    val phone: String = "+27 82 123 4567",
    val whatsapp: String = "+27 82 123 4567",
    val telegram: String = "@pinkpassions_za",
    val isFavourite: Boolean = false,
    val viewsCount: Int = 1420,
    val contactClicks: Int = 184,
    val isDemo: Boolean = true
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String,
    val reviewerId: String,
    val reviewerName: String,
    val profileId: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val createdAt: String,
    val moderationStatus: String = "APPROVED", // PENDING, APPROVED, FLAGGED, REMOVED
    val responseText: String? = null,
    val isVerifiedClient: Boolean = true
)

data class RatingBreakdown(
    val averageRating: Float,
    val totalReviews: Int,
    val fiveStarPct: Int = 82,
    val fourStarPct: Int = 12,
    val threeStarPct: Int = 4,
    val twoStarPct: Int = 1,
    val oneStarPct: Int = 1
)

@Entity(tableName = "businesses")
data class Business(
    @PrimaryKey val id: String,
    val ownerId: String,
    val name: String,
    val category: String,
    val description: String,
    val address: String,
    val province: String,
    val city: String,
    val latitude: Double = -26.2041,
    val longitude: Double = 28.0473,
    val phone: String = "+27 11 987 6543",
    val website: String = "https://pinkpassion.co.za",
    val openingHours: String = "Mon - Sun: 18:00 - 04:00",
    val rating: Float = 4.8f,
    val reviewCount: Int = 42,
    val isVip: Boolean = true,
    val specials: String = "VIP Lounge access with champagne package on Fridays",
    val imageUrl: String = "",
    val membershipFeeZar: Int = 280
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val city: String,
    val province: String,
    val ticketPriceZar: Int = 250,
    val isFeatured: Boolean = true,
    val imageUrl: String = ""
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val priceZar: Int,
    val category: String,
    val inventory: Int = 25,
    val rating: Float = 4.9f,
    val reviewCount: Int = 29,
    val imageUrl: String = "",
    val isDiscreetShipping: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: String,
    val name: String,
    val priceZar: Int,
    val quantity: Int = 1,
    val imageUrl: String = ""
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val isRead: Boolean = true
)

data class Conversation(
    val id: String,
    val recipientId: String,
    val recipientName: String,
    val recipientAvatar: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val verifiedLevel: VerificationLevel = VerificationLevel.PHOTO
)

data class AdvertisingCampaign(
    val id: String,
    val campaignName: String,
    val placement: String,
    val budgetZar: Int,
    val startDate: String,
    val endDate: String,
    val status: String,
    val impressions: Int,
    val clicks: Int
)

data class PaymentTransaction(
    val transactionId: String,
    val userId: String,
    val productTitle: String,
    val amountZar: Int,
    val currency: String = "ZAR",
    val provider: String, // "PayFast", "PayPal", "Ozow", "Card", "EFT"
    val status: String, // "COMPLETED", "PENDING", "FAILED", "REFUNDED"
    val timestamp: String,
    val invoiceNumber: String = "INV-${transactionId.takeLast(6)}"
)

data class Subscription(
    val id: String,
    val userId: String,
    val membershipTier: MembershipTier,
    val status: SubscriptionStatus,
    val amountZar: Int,
    val paymentProvider: String,
    val nextBillingDate: String,
    val expiresAt: String,
    val createdAt: String
)

data class PaymentProviderConfig(
    val providerId: String,
    val providerName: String,
    val providerEnabled: Boolean,
    val providerApproved: Boolean,
    val providerCurrency: String = "ZAR",
    val providerEnvironment: String = "SANDBOX", // "SANDBOX" or "PRODUCTION"
    val policyComplianceNotes: String,
    val supportsRecurringBilling: Boolean = true
)

data class VerificationRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userMobile: String,
    val method: VerificationMethodType,
    val status: VerificationStatus,
    val documentType: String? = null,
    val submittedAt: String,
    val matchScorePct: Int = 96,
    val livenessPassed: Boolean = true,
    val reviewerNotes: String = "Awaiting admin manual review."
)

data class AdminAuditLog(
    val id: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val adminUser: String,
    val timestamp: String,
    val notes: String
)

enum class ChatRoomCategory(val title: String, val icon: String, val description: String) {
    LOCAL_HOOKUPS("Local Hookups", "📍", "Discreet hookups, FWB dating & 50km radar chat"),
    LIFESTYLE_EVENTS("Lifestyle Events", "🥂", "Swinger parties, lifestyle meetups, spas & VIP events"),
    GENERAL_DISCUSSION("General Discussion", "💬", "Community introductions, dating tips & open discussions");

    companion object {
        val ALL = values().toList()

        fun fromString(value: String): ChatRoomCategory {
            return when {
                value.contains("Lifestyle", ignoreCase = true) || value.contains("Swinger", ignoreCase = true) || value.contains("Event", ignoreCase = true) || value.contains("Cam", ignoreCase = true) || value.contains("Spa", ignoreCase = true) -> LIFESTYLE_EVENTS
                value.contains("General", ignoreCase = true) || value.contains("Discussion", ignoreCase = true) || value.contains("Advice", ignoreCase = true) -> GENERAL_DISCUSSION
                else -> LOCAL_HOOKUPS
            }
        }
    }
}

data class ChatRoom(
    val id: String,
    val name: String,
    val province: String,
    val city: String,
    val description: String,
    val activeUsersCount: Int,
    val iconEmoji: String = "🍸",
    val isVipOnly: Boolean = false,
    val isWebcamLounge: Boolean = false,
    val category: String = "Local Hookups"
)

data class ChatRoomUser(
    val id: String,
    val displayName: String,
    val avatarUrl: String,
    val city: String,
    val verifiedLevel: VerificationLevel = VerificationLevel.PHOTO,
    val isVip: Boolean = false,
    val isModel: Boolean = false
)

data class ChatRoomMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val message: String,
    val timestamp: String,
    val isFromMe: Boolean = false,
    val isModel: Boolean = false,
    val verifiedLevel: VerificationLevel = VerificationLevel.PHOTO,
    val tipAmountZar: Int? = null
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val category: String,
    val isJoined: Boolean = false,
    val iconEmoji: String = "✨"
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "MEMBERSHIP", "PAYMENT", "VERIFICATION", "REVIEW", "CHAT", "PROMOTION"
    val timestamp: String,
    val isRead: Boolean = false
)

data class RegistrationFlowState(
    // Step 1: Register account
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val accountDisplayName: String = "",
    val agreedToTerms: Boolean = false,

    // Step 2: Verify email
    val isEmailVerified: Boolean = false,
    val emailVerificationPin: String = "",
    val emailOtpSent: Boolean = false,

    // Step 3: Verify mobile
    val mobileNumber: String = "+27 82 ",
    val isMobileVerified: Boolean = false,
    val mobileOtpCode: String = "",
    val mobileOtpSent: Boolean = false,

    // Step 4: Confirm legal age
    val confirmedAge18Plus: Boolean = false,
    val birthYear: Int = 2000,
    val statutoryDeclarationAccepted: Boolean = false,

    // Step 5: Select account/profile type
    val selectedProfileCategory: ProfileCategory = ProfileCategory.INDEPENDENT,

    // Step 6: Select membership
    val selectedMembershipTier: MembershipTier = MembershipTier.INDEPENDENT_ESCORT,

    // Step 7: Create profile info
    val stageName: String = "",
    val age: Int = 23,
    val province: String = "Gauteng",
    val city: String = "Sandton",
    val area: String = "Sandton CBD",
    val bioDescription: String = "",
    val services: String = "Dinner dates, VIP Companionship, Social Events",
    val hourlyRateZar: String = "R1,500 / hr",
    val incallOutcall: String = "Incall & Outcall",
    val whatsappContact: String = "+27 82 123 4567",
    val telegramContact: String = "@pinkpassions_za",

    // Step 8: Upload gallery
    val galleryPhotos: List<String> = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80"
    ),
    val coverPhotoUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",

    // Step 9: Verification eligibility
    val verificationDocType: String = "SA National Smart ID",
    val idDocumentAttached: Boolean = true,
    val biometricFacialMatchPassed: Boolean = true,
    val isBiometricVerified: Boolean = true,
    val biometricScorePct: Int = 98,

    // Step 10 & 11: Payment method & completion
    val paymentMethod: PaymentMethod = PaymentMethod.PayFast,
    val isPaymentProcessing: Boolean = false,
    val paymentCompleted: Boolean = false,
    val paymentTransactionId: String? = null,

    // Step 12: Server activation
    val membershipActivated: Boolean = false,

    // Step 13 & 14: Moderation submission
    val moderationStatus: ModerationStatus = ModerationStatus.PENDING_REVIEW,
    val submissionTimestamp: String = "",
    val createdProfileId: String? = null
)

data class CreateListingDraft(
    val stageName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val age: Int = 23,
    val province: String = "Gauteng",
    val city: String = "Sandton",
    val area: String = "Sandton CBD",
    val category: ProfileCategory = ProfileCategory.INDEPENDENT,
    val description: String = "",
    val services: String = "VIP Dates, Companionship",
    val priceText: String = "R1,000 / hr",
    val verificationLevel: VerificationLevel = VerificationLevel.PHOTO,
    val membershipTier: MembershipTier = MembershipTier.BASIC_FREE
)
