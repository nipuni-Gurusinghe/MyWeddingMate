package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast // ADD THIS IMPORT
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.FavoritesAdapter
import com.example.myweddingmateapp.databinding.ActivityFavoritesBinding
import com.example.myweddingmateapp.models.FavoriteItem
import com.example.myweddingmateapp.utils.ResourceHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth // ADD THIS IMPORT

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var prefs: PrefsHelper
    private lateinit var adapter: FavoritesAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth // ADD THIS DECLARATION
    private val TAG = "FavoritesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        setupBackButton()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { favorite ->
                Toast.makeText(this, "Clicked ${favorite.name} (${favorite.category})", Toast.LENGTH_SHORT).show()
            },
            onRemoveClick = { favorite ->
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    prefs.removeFavorite(userId, favorite.id, favorite.category) // PASS USER ID
                    loadFavorites()
                } else {
                    Toast.makeText(this, "Not logged in to remove favorites.", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Attempted to remove favorite without being logged in.")
                }
            }
        )
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            adapter = this@FavoritesActivity.adapter
        }
    }

    private fun loadFavorites() {
        val userId = auth.currentUser?.uid // GET USER ID HERE
        if (userId == null) {
            Log.d(TAG, "No user logged in. Displaying 'Please log in' message.")
            binding.emptyStateTextView.text = getString(R.string.please_log_in_to_view_favorites)
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.favoritesRecyclerView.visibility = View.GONE
            adapter.submitList(emptyList())
            return
        }

        Log.d(TAG, "Starting to load favorites for user: $userId")
        val favoritesMap = prefs.getAllFavorites(userId)
        Log.d(TAG, "Favorites from Prefs for $userId: $favoritesMap")


        if (favoritesMap.isEmpty()) {
            Log.d(TAG, "No favorites found for user $userId in preferences.")
            binding.emptyStateTextView.text = getString(R.string.no_favorites_message)
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.favoritesRecyclerView.visibility = View.GONE
            adapter.submitList(emptyList())
            return
        }

        binding.emptyStateTextView.visibility = View.GONE
        binding.favoritesRecyclerView.visibility = View.VISIBLE

        val favoritesList = mutableListOf<FavoriteItem>()
        var completedFetches = 0
        val totalFavorites = favoritesMap.values.sumOf { it.size }
        Log.d(TAG, "Total favorites to load for user $userId: $totalFavorites")


        if (totalFavorites == 0) {
            Log.d(TAG, "All favorite lists are empty for user $userId.")
            binding.emptyStateTextView.text = getString(R.string.no_favorites_message)
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.favoritesRecyclerView.visibility = View.GONE
            adapter.submitList(emptyList())
            return
        }


        favoritesMap.forEach { (category, ids) ->
            ids.forEach { id ->
                Log.d(TAG, "Loading $category with id: $id for user $userId")
                val collection = when (category) {
                    "venue" -> "venues"
                    "photography" -> "photography"
                    "beauticianBride" -> "beautician-bride"
                    "beauticianGroom" -> "beautician-groom"
                    "bridalWear" -> "bridalWear"
                    "groomWear" -> "groomWear"
                    "jewellery" -> "jewellery"
                    "entertainment" -> "entertainment"
                    "floral" -> "floral"
                    "invitation" -> "invitation"
                    "weddingCar" -> "wedding-car"
                    else -> {
                        Log.w(TAG, "Unhandled category '$category'. Assuming collection name matches category.")
                        category
                    }
                }

                db.collection(collection).document(id).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val item = createFavoriteItem(document, category)
                            favoritesList.add(item)
                            Log.d(TAG, "Successfully loaded: ${item.name} for user $userId")
                        } else {
                            Log.w(TAG, "Document doesn't exist for $id in $collection. Removing from prefs for user $userId.")
                            prefs.removeFavorite(userId, id, category)
                        }
                        if (++completedFetches == totalFavorites) {
                            updateAdapter(favoritesList)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error loading $id from $collection for user $userId", e)
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
        val imageResIdName = document.getString("imageResId") ?: ""
        return FavoriteItem(
            id = document.id,
            name = document.getString("name") ?: "Unknown",
            category = category,
            imageRes = ResourceHelper.getDrawable(imageResIdName, category),
            websiteUrl = document.getString("websiteUrl")
        ).also {
            Log.d(TAG, "Created FavoriteItem: $it")
        }
    }

    private fun updateAdapter(items: List<FavoriteItem>) {
        Log.d(TAG, "Updating adapter with ${items.size} items")
        if (items.isEmpty()) {
            binding.emptyStateTextView.text = getString(R.string.no_favorites_message)
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
}