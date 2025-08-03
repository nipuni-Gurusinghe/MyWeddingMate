package com.example.myweddingmateapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class PrefsHelper private constructor(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("MyWeddingPrefs", Context.MODE_PRIVATE)
    private val db: FirebaseFirestore = Firebase.firestore
    private val CATEGORY_SEPARATOR = "::"
    private val TAG = "PrefsHelper"
    private val FAVORITES_COLLECTION = "userFavorites"

    companion object {
        @Volatile private var instance: PrefsHelper? = null
        private const val BASE_FAVORITES_KEY = "favorites"

        fun getInstance(context: Context): PrefsHelper {
            return instance ?: synchronized(this) {
                instance ?: PrefsHelper(context).also { instance = it }
            }
        }
    }

    private fun getUserFavoritesKey(userId: String): String {
        return "${userId}_$BASE_FAVORITES_KEY"
    }



    fun addFavorite(userId: String, itemId: String, category: String, callback: (Boolean) -> Unit = {}) {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        val userPrefKey = getUserFavoritesKey(userId)
        val favoriteId = UUID.randomUUID().toString()


        val currentLocal = getUserFavoritesRaw(userId).toMutableSet().apply { add(itemKey) }
        sharedPref.edit().putStringSet(userPrefKey, currentLocal).apply()


        val favoriteData = hashMapOf(
            "favoriteId" to favoriteId,
            "itemId" to itemId,
            "category" to normalizedCategory,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .collection("items")
            .document(favoriteId)
            .set(favoriteData)
            .addOnSuccessListener {
                Log.d(TAG, "Added favorite: $userId/$itemId with favoriteId: $favoriteId")
                callback(true)
            }
            .addOnFailureListener { e ->
                currentLocal.remove(itemKey)
                sharedPref.edit().putStringSet(userPrefKey, currentLocal).apply()
                Log.e(TAG, "Failed to add favorite", e)
                callback(false)
            }
    }

    fun removeFavorite(userId: String, itemId: String, category: String, callback: (Boolean) -> Unit = {}) {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        val userPrefKey = getUserFavoritesKey(userId)


        val currentLocal = getUserFavoritesRaw(userId).toMutableSet().apply { remove(itemKey) }
        sharedPref.edit().putStringSet(userPrefKey, currentLocal).apply()


        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .collection("items")
            .whereEqualTo("itemId", itemId)
            .whereEqualTo("category", normalizedCategory)
            .get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                for (document in documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "Removed favorite: $userId/$itemId")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        currentLocal.add(itemKey)
                        sharedPref.edit().putStringSet(userPrefKey, currentLocal).apply()
                        Log.e(TAG, "Failed to remove favorite in batch", e)
                        callback(false)
                    }
            }
            .addOnFailureListener { e ->
                currentLocal.add(itemKey)
                sharedPref.edit().putStringSet(userPrefKey, currentLocal).apply()
                Log.e(TAG, "Failed to query favorite for removal", e)
                callback(false)
            }
    }


    fun isFavorite(userId: String, itemId: String, category: String): Boolean {
        val normalizedCategory = normalizeCategory(category)
        val itemKey = "$normalizedCategory$CATEGORY_SEPARATOR$itemId"
        return getUserFavoritesRaw(userId).contains(itemKey)
    }

    fun getAllFavorites(userId: String): Map<String, Set<String>> {
        return getUserFavoritesRaw(userId)
            .groupBy { it.substringBefore(CATEGORY_SEPARATOR) }
            .mapValues { (_, values) ->
                values.map { it.substringAfter(CATEGORY_SEPARATOR) }.toSet()
            }
    }

    fun getFavoritesByCategory(userId: String, category: String): Set<String> {
        val normalizedCategory = normalizeCategory(category)
        return getUserFavoritesRaw(userId)
            .filter { it.startsWith("$normalizedCategory$CATEGORY_SEPARATOR") }
            .map { it.substringAfter(CATEGORY_SEPARATOR) }
            .toSet()
    }



    fun syncFavoritesFromFirestore(userId: String, callback: (Boolean) -> Unit = {}) {
        val favorites = mutableSetOf<String>()

        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { items ->
                items.forEach { itemDoc ->
                    val category = itemDoc.getString("category") ?: ""
                    val itemId = itemDoc.getString("itemId") ?: ""
                    favorites.add("$category$CATEGORY_SEPARATOR$itemId")
                }

                sharedPref.edit()
                    .putStringSet(getUserFavoritesKey(userId), favorites)
                    .apply()
                Log.d(TAG, "Synced ${favorites.size} favorites")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Sync failed", e)
                callback(false)
            }
    }



    fun clearAllFavoritesForUser(userId: String, callback: (Boolean) -> Unit = {}) {

        sharedPref.edit()
            .remove(getUserFavoritesKey(userId))
            .apply()

        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { items ->
                val batch = db.batch()
                items.forEach { item ->
                    batch.delete(item.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "Cleared all favorites for $userId")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Batch delete failed", e)
                        callback(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to load items for deletion", e)
                callback(false)
            }
    }

    private fun initializeUserFavorites(userId: String) {
        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .set(mapOf("initialized" to true))
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to initialize user favorites", e)
            }
    }

    private fun getUserFavoritesRaw(userId: String): Set<String> {
        return sharedPref.getStringSet(getUserFavoritesKey(userId), mutableSetOf()) ?: mutableSetOf()
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
}