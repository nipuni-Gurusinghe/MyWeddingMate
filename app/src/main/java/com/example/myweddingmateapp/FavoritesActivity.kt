package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.FavoritesAdapter
import com.example.myweddingmateapp.databinding.ActivityFavoritesBinding
import com.example.myweddingmateapp.models.FavoriteItem
import com.example.myweddingmateapp.utils.ResourceHelper
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var prefs: PrefsHelper
    private lateinit var adapter: FavoritesAdapter
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FavoritesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupRecyclerView()
        loadFavorites()
        setupBackButton()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { favorite ->
                // Handle item click if needed
            },
            onRemoveClick = { favorite ->
                prefs.removeFavorite(favorite.id, favorite.category)
                loadFavorites()
            }
        )
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            adapter = this@FavoritesActivity.adapter
        }
    }

    private fun loadFavorites() {
        Log.d(TAG, "Starting to load favorites")
        val favoritesMap = prefs.getAllFavorites()
        Log.d(TAG, "Favorites from Prefs: $favoritesMap")

        // Show empty state initially
        binding.emptyStateTextView.visibility = View.VISIBLE
        binding.favoritesRecyclerView.visibility = View.GONE

        if (favoritesMap.isEmpty()) {
            Log.d(TAG, "No favorites found in preferences")
            return
        }

        val favoritesList = mutableListOf<FavoriteItem>()
        var completedFetches = 0
        val totalFavorites = favoritesMap.values.sumOf { it.size }
        Log.d(TAG, "Total favorites to load: $totalFavorites")

        // Process each category
        favoritesMap.forEach { (category, ids) ->
            ids.forEach { id ->
                Log.d(TAG, "Loading $category with id: $id")
                val collection = when (category) {
                    "venue" -> "venues"
                    "photography" -> "photography"
                    "beauticianBride" -> "beautician-bride" // Explicitly map 'beauticianBride'
                    "beauticianGroom" -> "beautician-groom" // Explicitly map 'beauticianGroom'
                    "bridalWear" -> "bridalWear"
                    "groomWear" -> "groomWear"
                    "jewellery" -> "jewellery"
                    "entertainment" -> "entertainment"
                    "floral" -> "floral"
                    "invitation" -> "invitation"
                    "weddingCar" -> "wedding-car"
                    else -> category
                }

                db.collection(collection).document(id).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val item = createFavoriteItem(document, category)
                            favoritesList.add(item)
                            Log.d(TAG, "Successfully loaded: ${item.name}")
                        } else {
                            Log.w(TAG, "Document doesn't exist for $id in $collection")
                        }
                        if (++completedFetches == totalFavorites) {
                            updateAdapter(favoritesList)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error loading $id from $collection", e)
                        if (++completedFetches == totalFavorites) {
                            updateAdapter(favoritesList)
                        }
                    }
            }
        }
    }

    private fun createFavoriteItem(
        document: com.google.firebase.firestore.DocumentSnapshot,
        category: String
    ): FavoriteItem {
        val imageResId = document.getString("imageResId") ?: ""
        return FavoriteItem(
            id = document.id,
            name = document.getString("name") ?: "Unknown",
            category = category,
            imageRes = ResourceHelper.getDrawable(imageResId, category),
            websiteUrl = document.getString("websiteUrl")
        ).also {
            Log.d(TAG, "Created FavoriteItem: $it")
        }
    }

    private fun updateAdapter(items: List<FavoriteItem>) {
        Log.d(TAG, "Updating adapter with ${items.size} items")
        if (items.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.favoritesRecyclerView.visibility = View.GONE
            Log.d(TAG, "Showing empty state")
        } else {
            adapter.submitList(items.sortedBy { it.name })
            binding.emptyStateTextView.visibility = View.GONE
            binding.favoritesRecyclerView.visibility = View.VISIBLE
            Log.d(TAG, "Showing ${items.size} favorites")
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }
}