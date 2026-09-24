package com.example.data

import android.util.Log
import com.example.model.Business
import com.example.model.Profile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreService", "Firebase not initialized, running in local fallback mode: ${e.message}")
            null
        }
    }

    /**
     * Store or update a business directory listing in Firestore cloud storage
     */
    suspend fun saveBusinessListing(business: Business): Boolean {
        return try {
            val firestore = db ?: return false
            val data = hashMapOf(
                "id" to business.id,
                "ownerId" to business.ownerId,
                "name" to business.name,
                "category" to business.category,
                "description" to business.description,
                "address" to business.address,
                "province" to business.province,
                "city" to business.city,
                "latitude" to business.latitude,
                "longitude" to business.longitude,
                "phone" to business.phone,
                "website" to business.website,
                "openingHours" to business.openingHours,
                "rating" to business.rating,
                "reviewCount" to business.reviewCount,
                "isVip" to business.isVip,
                "specials" to business.specials,
                "imageUrl" to business.imageUrl,
                "membershipFeeZar" to business.membershipFeeZar,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("business_directory")
                .document(business.id)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving business listing to Firestore: ${e.message}")
            false
        }
    }

    /**
     * Store or update user discovery profile in Firestore cloud storage
     */
    suspend fun saveDiscoveryProfile(profile: Profile): Boolean {
        return try {
            val firestore = db ?: return false
            val data = hashMapOf(
                "id" to profile.id,
                "userId" to profile.userId,
                "displayName" to profile.displayName,
                "age" to profile.age,
                "province" to profile.province,
                "city" to profile.city,
                "area" to profile.area,
                "category" to profile.category.name,
                "description" to profile.description,
                "rating" to profile.rating,
                "reviewCount" to profile.reviewCount,
                "status" to profile.status.name,
                "verifiedLevel" to profile.verifiedLevel.name,
                "membershipTier" to profile.membershipTier.name,
                "isGold" to profile.isGold,
                "isFeatured" to profile.isFeatured,
                "isOnline" to profile.isOnline,
                "lastActive" to profile.lastActive,
                "priceText" to profile.priceText,
                "avatarUrl" to profile.avatarUrl,
                "services" to profile.services,
                "phone" to profile.phone,
                "whatsapp" to profile.whatsapp,
                "telegram" to profile.telegram,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("discovery_profiles")
                .document(profile.id)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving discovery profile to Firestore: ${e.message}")
            false
        }
    }

    /**
     * Observe realtime business listings from Firestore
     */
    fun observeBusinessDirectory(): Flow<List<Map<String, Any>>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("business_directory")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error observing business directory: ${error.message}")
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observe realtime featured discovery content from Firestore
     */
    fun observeFeaturedDiscoveryProfiles(): Flow<List<Map<String, Any>>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("discovery_profiles")
            .whereEqualTo("isFeatured", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error observing featured discovery profiles: ${error.message}")
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observe real-time chat messages for a specific room (e.g. Friends with Benefits / FWB)
     */
    fun observeChatRoomMessages(roomId: String): Flow<List<com.example.model.ChatRoomMessage>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("chat_rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestampEpoch", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error observing chat room $roomId: ${error.message}")
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        com.example.model.ChatRoomMessage(
                            id = doc.getString("id") ?: doc.id,
                            roomId = doc.getString("roomId") ?: roomId,
                            senderId = doc.getString("senderId") ?: "",
                            senderName = doc.getString("senderName") ?: "Anonymous",
                            senderAvatar = doc.getString("senderAvatar") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getString("timestamp") ?: "Just now",
                            isFromMe = doc.getBoolean("isFromMe") ?: false,
                            isModel = doc.getBoolean("isModel") ?: false,
                            verifiedLevel = try {
                                com.example.model.VerificationLevel.valueOf(doc.getString("verifiedLevel") ?: "PHOTO")
                            } catch (e: Exception) {
                                com.example.model.VerificationLevel.PHOTO
                            },
                            tipAmountZar = doc.getLong("tipAmountZar")?.toInt()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Send a new message to a Firestore chat room collection
     */
    suspend fun sendChatRoomMessage(message: com.example.model.ChatRoomMessage): Boolean {
        return try {
            val firestore = db ?: return false
            val data = hashMapOf(
                "id" to message.id,
                "roomId" to message.roomId,
                "senderId" to message.senderId,
                "senderName" to message.senderName,
                "senderAvatar" to message.senderAvatar,
                "message" to message.message,
                "timestamp" to message.timestamp,
                "isFromMe" to message.isFromMe,
                "isModel" to message.isModel,
                "verifiedLevel" to message.verifiedLevel.name,
                "tipAmountZar" to message.tipAmountZar,
                "timestampEpoch" to System.currentTimeMillis()
            )
            firestore.collection("chat_rooms")
                .document(message.roomId)
                .collection("messages")
                .document(message.id)
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error sending message to Firestore: ${e.message}")
            false
        }
    }

    /**
     * Observe categorized chat rooms in Firestore ('Local Hookups', 'Lifestyle Events', 'General Discussion')
     */
    fun observeCategorizedChatRooms(category: String? = null): Flow<List<com.example.model.ChatRoom>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = if (category != null && category != "All Rooms") {
            firestore.collection("chat_room_categories").whereEqualTo("category", category)
        } else {
            firestore.collection("chat_room_categories")
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreService", "Error observing chat rooms: ${error.message}")
                return@addSnapshotListener
            }
            val rooms = snapshot?.documents?.mapNotNull { doc ->
                try {
                    com.example.model.ChatRoom(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "Chat Room",
                        province = doc.getString("province") ?: "South Africa",
                        city = doc.getString("city") ?: "National",
                        description = doc.getString("description") ?: "",
                        activeUsersCount = (doc.getLong("activeUsersCount") ?: 50).toInt(),
                        iconEmoji = doc.getString("iconEmoji") ?: "🍸",
                        isVipOnly = doc.getBoolean("isVipOnly") ?: false,
                        isWebcamLounge = doc.getBoolean("isWebcamLounge") ?: false,
                        category = doc.getString("category") ?: "Local Hookups"
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            trySend(rooms)
        }

        awaitClose { listener.remove() }
    }

    /**
     * Save or sync Chat Room metadata with Category in Firestore
     */
    suspend fun saveChatRoom(room: com.example.model.ChatRoom): Boolean {
        return try {
            val firestore = db ?: return false
            val data = hashMapOf(
                "id" to room.id,
                "name" to room.name,
                "province" to room.province,
                "city" to room.city,
                "description" to room.description,
                "activeUsersCount" to room.activeUsersCount,
                "iconEmoji" to room.iconEmoji,
                "isVipOnly" to room.isVipOnly,
                "isWebcamLounge" to room.isWebcamLounge,
                "category" to room.category,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("chat_room_categories")
                .document(room.id)
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving chat room metadata: ${e.message}")
            false
        }
    }

    /**
     * Manage and sync R99 / Monthly VIP Subscriptions in Firestore
     */
    suspend fun saveUserSubscription(
        userId: String,
        tier: com.example.model.MembershipTier,
        isActive: Boolean,
        paymentProvider: String = "PayFast"
    ): Boolean {
        return try {
            val firestore = db ?: return false
            val data = hashMapOf(
                "userId" to userId,
                "tier" to tier.name,
                "tierTitle" to tier.title,
                "priceZar" to tier.priceZar,
                "monthlyFeeZar" to 99,
                "status" to if (isActive) "ACTIVE" else "CANCELLED",
                "paymentProvider" to paymentProvider,
                "isFwbVipUnlocked" to (tier == com.example.model.MembershipTier.FRIENDS_WITH_BENEFITS || tier == com.example.model.MembershipTier.VIP || tier == com.example.model.MembershipTier.GOLD),
                "isRadar50kmUnlocked" to true,
                "features" to listOf(
                    "Unlimited Friends with Benefits (FWB) SA Chat Access",
                    "50km Google Maps Proximity Radar & Hookup Discovery",
                    "Direct Client & Escort Contact Access",
                    "Verified 18+ Member Badge"
                ),
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("user_subscriptions")
                .document(userId)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating subscription in Firestore: ${e.message}")
            false
        }
    }

    /**
     * Observe User Subscription from Firestore
     */
    fun observeUserSubscription(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("user_subscriptions")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error observing subscription: ${error.message}")
                    return@addSnapshotListener
                }
                trySend(snapshot?.data)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Save user privacy preferences and visibility settings in Firestore
     */
    suspend fun saveUserSettings(userId: String, settings: Map<String, Any>): Boolean {
        return try {
            val firestore = db ?: return false
            firestore.collection("user_settings")
                .document(userId)
                .set(settings, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving user settings to Firestore: ${e.message}")
            false
        }
    }

    /**
     * Synchronize local seeds to Firestore
     */
    suspend fun syncInitialDataToCloud(profiles: List<Profile>, businesses: List<Business>) {
        try {
            businesses.forEach { saveBusinessListing(it) }
            profiles.forEach { saveDiscoveryProfile(it) }
        } catch (e: Exception) {
            Log.w("FirestoreService", "Cloud sync exception: ${e.message}")
        }
    }
}
