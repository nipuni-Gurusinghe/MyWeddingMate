package com.example.myweddingmateapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PrefsHelper private constructor(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("MyWeddingPrefs", Context.MODE_PRIVATE)
    private val CATEGORY_SEPARATOR = "::"
    private val TAG = "PrefsHelper"

    companion object {
        @Volatile private var instance: PrefsHelper? = null
        private const val BASE_FAVORITES_KEY = "favorites"

        fun getInstance(context: Context): PrefsHelper {
            return instance ?: synchronized(this) {
                instance ?: PrefsHelper(context).also { instance = it }
            }
        }
    }

    // Helper to get the user-specific SharedPreferences key
    private fun getUserFavoritesKey(userId: String): String {
        return "${userId}_$BASE_FAVORITES_KEY"
    }

    // Add a favorite item for a specific user and category
    fun addFavorite(userId: String, itemId: String, category: String) {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        val userPrefKey = getUserFavoritesKey(userId)

        val current = getUserFavoritesRaw(userId).toMutableSet()
        if (current.add(itemKey)) {
            sharedPref.edit().putStringSet(userPrefKey, current).apply()
            Log.d(TAG, "Added favorite for user $userId: $itemKey")
        } else {
            Log.d(TAG, "Favorite already exists for user $userId: $itemKey")
        }
    }

    // Remove a favorite item for a specific user
    fun removeFavorite(userId: String, itemId: String, category: String) {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        val userPrefKey = getUserFavoritesKey(userId)

        val current = getUserFavoritesRaw(userId).toMutableSet()
        if (current.remove(itemKey)) {
            sharedPref.edit().putStringSet(userPrefKey, current).apply()
            Log.d(TAG, "Removed favorite for user $userId: $itemKey")
        } else {
            Log.d(TAG, "Favorite not found for user $userId: $itemKey")
        }
    }

    // Check if an item is favorite for a specific user
    fun isFavorite(userId: String, itemId: String, category: String): Boolean {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        return getUserFavoritesRaw(userId).contains(itemKey)
    }


    fun getAllFavorites(userId: String): Map<String, Set<String>> {
        val favorites = getUserFavoritesRaw(userId)
        Log.d(TAG, "All raw favorites for user $userId: $favorites")

        return favorites.groupBy { item ->
            item.substringBefore(CATEGORY_SEPARATOR)
        }.mapValues { (_, items) ->
            items.map { item ->
                item.substringAfter(CATEGORY_SEPARATOR)
            }.toSet()
        }.also { groupedFavorites ->
            Log.d(TAG, "Grouped favorites for user $userId: $groupedFavorites")
        }
    }


    private fun getUserFavoritesRaw(userId: String): Set<String> {
        val userPrefKey = getUserFavoritesKey(userId)
        return sharedPref.getStringSet(userPrefKey, mutableSetOf()) ?: mutableSetOf()
    }


    private fun normalizeCategory(category: String): String {
        return when (category.lowercase()) {
            "bridalwear", "bridal_wear", "bridalwears" -> "bridalWear"
            "beautician", "beautician_bride", "beauticianbride" -> "beauticianBride"
            "venue", "venues" -> "venue"
            "photography", "photographer" -> "photography"
            "beauticiangroom" -> "beauticianGroom"
            "groomwear" -> "groomWear"
            "jewellery" -> "jewellery"
            "entertainment" -> "entertainment"
            "floral" -> "floral"
            "invitation" -> "invitation"
            "weddingcar" -> "weddingCar"
            else -> category
        }
    }


    fun clearAllFavoritesForUser(userId: String) {
        val userPrefKey = getUserFavoritesKey(userId)
        sharedPref.edit().remove(userPrefKey).apply()
        Log.d(TAG, "Cleared all favorites for user $userId")
    }


}