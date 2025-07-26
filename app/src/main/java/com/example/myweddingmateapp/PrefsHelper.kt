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

        fun getInstance(context: Context): PrefsHelper {
            return instance ?: synchronized(this) {
                instance ?: PrefsHelper(context).also { instance = it }
            }
        }
    }

    // Add a favorite item with category
    fun addFavorite(itemId: String, category: String) {
        val normalizedCategory = normalizeCategory(category)
        val key = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        Log.d(TAG, "Adding favorite: $key")

        val current = getFavoritesRaw().toMutableSet()
        current.add(key)
        sharedPref.edit().putStringSet("favorites", current).apply()
    }

    // Remove a favorite item
    fun removeFavorite(itemId: String, category: String) {
        val normalizedCategory = normalizeCategory(category)
        val key = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        Log.d(TAG, "Removing favorite: $key")

        val current = getFavoritesRaw().toMutableSet()
        current.remove(key)
        sharedPref.edit().putStringSet("favorites", current).apply()
    }

    // Check if an item is favorite
    fun isFavorite(itemId: String, category: String): Boolean {
        val normalizedCategory = normalizeCategory(category)
        return getFavoritesRaw().contains("$normalizedCategory$CATEGORY_SEPARATOR$itemId")
    }

    // Get all favorites grouped by category
    fun getAllFavorites(): Map<String, Set<String>> {
        val favorites = getFavoritesRaw()
        Log.d(TAG, "All raw favorites: $favorites")

        return favorites.groupBy { item ->
            item.substringBefore(CATEGORY_SEPARATOR)
        }.mapValues { (_, items) ->
            items.map { item ->
                item.substringAfter(CATEGORY_SEPARATOR)
            }.toSet()
        }.also { groupedFavorites ->
            Log.d(TAG, "Grouped favorites: $groupedFavorites")
        }
    }

    // Get raw favorites set
    private fun getFavoritesRaw(): Set<String> {
        return sharedPref.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
    }

    // Normalize category names for consistency
    private fun normalizeCategory(category: String): String {
        return when (category.lowercase()) {
            "bridalwear", "bridal_wear", "bridalwears" -> "bridalWear"
            "beautician", "beautician_bride", "beauticianbride" -> "beauticianBride"
            "venue", "venues" -> "venue"
            "photography", "photographer" -> "photography"
            else -> category
        }
    }

    // Clear all favorites
    fun clearAllFavorites() {
        sharedPref.edit().remove("favorites").apply()
        Log.d(TAG, "Cleared all favorites")
    }

    // Migration from old format if needed
    fun migrateOldFavoritesIfNeeded() {
        val oldFavorites = sharedPref.getStringSet("old_favorites", null)
        oldFavorites?.let {
            Log.d(TAG, "Migrating old favorites format")
            val converted = it.map { id -> "venue$CATEGORY_SEPARATOR$id" }.toSet()
            sharedPref.edit()
                .putStringSet("favorites", converted)
                .remove("old_favorites")
                .apply()
        }
    }
}