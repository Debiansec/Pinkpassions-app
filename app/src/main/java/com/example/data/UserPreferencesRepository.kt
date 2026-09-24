package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import coil.Coil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pink_passions_prefs")

enum class AppThemeMode(val title: String, val description: String) {
    DARK("Luxury Dark", "Deep luxury pinks on dark velvet canvas"),
    AMOLED("AMOLED Black", "Pitch black #000000 base for OLED battery saving"),
    LIGHT("Sultry Light", "Clean light neutral with magenta accents"),
    SYSTEM("System Default", "Follows device system dark mode settings")
}

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_AGE_CONFIRMED = booleanPreferencesKey("has_confirmed_age_18_plus")
        val KEY_TERMS_AGREED = booleanPreferencesKey("terms_and_conditions_agreed")
        val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
        val KEY_ACCENT_COLOR = stringPreferencesKey("app_accent_color")
        val KEY_DISCREET_MODE = booleanPreferencesKey("discreet_privacy_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("push_notifications_enabled")
        val KEY_USER_NAME = stringPreferencesKey("user_display_name")
        val KEY_USER_CITY = stringPreferencesKey("user_city")
        val KEY_USER_BIO = stringPreferencesKey("user_bio")

        // Profile Visibility & Location Privacy Keys
        val KEY_PROFILE_VISIBLE = booleanPreferencesKey("profile_visible")
        val KEY_GHOST_MODE = booleanPreferencesKey("ghost_mode_enabled")
        val KEY_SHOW_ONLINE_STATUS = booleanPreferencesKey("show_online_status")
        val KEY_SHOW_VERIFIED_BADGE = booleanPreferencesKey("show_verified_badge")
        val KEY_LOCATION_PRIVACY_MODE = stringPreferencesKey("location_privacy_mode")
        val KEY_SHARE_RADAR_50KM = booleanPreferencesKey("share_radar_50km")
        val KEY_AUTO_DETECT_GPS = booleanPreferencesKey("auto_detect_gps")
        val KEY_FWB_PREMIUM_ACTIVE = booleanPreferencesKey("fwb_premium_active")
    }

    val hasConfirmedAgeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_AGE_CONFIRMED] ?: false
        }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data
        .map { preferences ->
            val saved = preferences[KEY_THEME_MODE]
            try {
                if (saved != null) AppThemeMode.valueOf(saved) else AppThemeMode.DARK
            } catch (e: Exception) {
                AppThemeMode.DARK
            }
        }

    val accentColorFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ACCENT_COLOR] ?: "HOT_PINK"
        }

    val discreetModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_DISCREET_MODE] ?: true
        }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATIONS] ?: true
        }

    val userDisplayNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_NAME] ?: "Alexander Vance"
        }

    val userCityFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_CITY] ?: "Sandton, Johannesburg"
        }

    val userBioFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_BIO] ?: "VIP Member & Lifestyle Connoisseur in South Africa."
        }

    // Profile Visibility Flows
    val profileVisibleFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_PROFILE_VISIBLE] ?: true }

    val ghostModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_GHOST_MODE] ?: false }

    val showOnlineStatusFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_SHOW_ONLINE_STATUS] ?: true }

    val showVerifiedBadgeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_SHOW_VERIFIED_BADGE] ?: true }

    // Location Privacy Flows
    val locationPrivacyModeFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[KEY_LOCATION_PRIVACY_MODE] ?: "FUZZY_5KM" }

    val shareRadar50kmFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_SHARE_RADAR_50KM] ?: true }

    val autoDetectGpsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_AUTO_DETECT_GPS] ?: true }

    val fwbPremiumActiveFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_FWB_PREMIUM_ACTIVE] ?: false }

    suspend fun saveAgeConfirmation(confirmed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AGE_CONFIRMED] = confirmed
            preferences[KEY_TERMS_AGREED] = confirmed
        }
    }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode.name
        }
    }

    suspend fun setAccentColor(accentKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCENT_COLOR] = accentKey
        }
    }

    suspend fun setDiscreetMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DISCREET_MODE] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setProfileVisible(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PROFILE_VISIBLE] = visible
        }
    }

    suspend fun setGhostMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GHOST_MODE] = enabled
        }
    }

    suspend fun setShowOnlineStatus(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_ONLINE_STATUS] = enabled
        }
    }

    suspend fun setShowVerifiedBadge(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_VERIFIED_BADGE] = enabled
        }
    }

    suspend fun setLocationPrivacyMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOCATION_PRIVACY_MODE] = mode
        }
    }

    suspend fun setShareRadar50km(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHARE_RADAR_50KM] = enabled
        }
    }

    suspend fun setAutoDetectGps(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_DETECT_GPS] = enabled
        }
    }

    suspend fun setFwbPremiumActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FWB_PREMIUM_ACTIVE] = active
        }
    }

    suspend fun updateUserProfile(displayName: String, city: String, bio: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = displayName
            preferences[KEY_USER_CITY] = city
            preferences[KEY_USER_BIO] = bio
        }
    }

    /**
     * Calculates the size of cache directories in bytes.
     */
    suspend fun getCacheSizeBytes(): Long = withContext(Dispatchers.IO) {
        var totalBytes = 0L
        try {
            totalBytes += getFolderSize(context.cacheDir)
            context.externalCacheDir?.let {
                totalBytes += getFolderSize(it)
            }
            totalBytes += getFolderSize(File(context.filesDir, "image_cache"))
        } catch (e: Exception) {
            // Ignore
        }
        totalBytes
    }

    /**
     * Clears cached application data including Coil disk cache and temp files.
     */
    suspend fun clearCachedData(): Long = withContext(Dispatchers.IO) {
        val initialBytes = getCacheSizeBytes()
        try {
            // Clear Coil memory and disk caches if accessible
            try {
                val imageLoader = Coil.imageLoader(context)
                imageLoader.memoryCache?.clear()
                imageLoader.diskCache?.clear()
            } catch (e: Exception) {
                // Coil cache clearance fallback
            }

            deleteDir(context.cacheDir)
            context.externalCacheDir?.let { deleteDir(it) }
            deleteDir(File(context.filesDir, "image_cache"))
        } catch (e: Exception) {
            // Ignore
        }
        initialBytes
    }

    private fun getFolderSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        if (file.isFile) return file.length()
        var size = 0L
        val children = file.listFiles() ?: return 0L
        for (child in children) {
            size += getFolderSize(child)
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list() ?: return false
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) return false
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        }
        return false
    }
}
