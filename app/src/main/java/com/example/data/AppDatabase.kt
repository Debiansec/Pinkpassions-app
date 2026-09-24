package com.example.data

import androidx.room.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY isFeatured DESC, isGold DESC, rating DESC")
    fun getAllProfiles(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profile?

    @Query("SELECT * FROM profiles WHERE isFavourite = 1")
    fun getFavouriteProfiles(): Flow<List<Profile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<Profile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Query("UPDATE profiles SET isFavourite = :isFav WHERE id = :id")
    suspend fun toggleFavourite(id: String, isFav: Boolean)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)
}

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY isVip DESC, rating DESC")
    fun getAllBusinesses(): Flow<List<Business>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<Business>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY isFeatured DESC")
    fun getAllEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :convoId ORDER BY id ASC")
    fun getMessagesForConversation(convoId: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE profileId = :profileId ORDER BY createdAt DESC")
    fun getReviewsForProfile(profileId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("UPDATE reviews SET moderationStatus = :status WHERE id = :id")
    suspend fun updateModerationStatus(id: String, status: String)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: String)
}

@Database(
    entities = [
        Profile::class,
        Business::class,
        Event::class,
        Product::class,
        CartItem::class,
        Message::class,
        Review::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun businessDao(): BusinessDao
    abstract fun eventDao(): EventDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun messageDao(): MessageDao
    abstract fun reviewDao(): ReviewDao
}
