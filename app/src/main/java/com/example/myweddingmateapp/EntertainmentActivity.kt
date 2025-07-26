package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.EntertainmentAdapter
import com.example.myweddingmateapp.databinding.ActivityEntertainmentBinding
import com.example.myweddingmateapp.models.Entertainment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EntertainmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntertainmentBinding
    private lateinit var prefs: PrefsHelper
    private val entertainmentList = mutableListOf<Entertainment>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntertainmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchEntertainmentFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.entertainmentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.entertainmentRecyclerView.adapter = EntertainmentAdapter(
            entertainments = entertainmentList,
            onFavoriteClick = { entertainment ->
                entertainment.isFavorite = !entertainment.isFavorite
                if (entertainment.isFavorite) {
                    prefs.addFavorite(entertainment.id, "entertainment")
                } else {
                    prefs.removeFavorite(entertainment.id, "entertainment")
                }
                binding.entertainmentRecyclerView.adapter?.notifyItemChanged(
                    entertainmentList.indexOf(entertainment)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchEntertainmentFromFirestore() {
        db.collection("entertainment") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                entertainmentList.clear() // Clear existing hardcoded data
                val imageMap = createImageResourceMap() // Create map for image resources

                for (document in result) {
                    try {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val imageResIdName = document.getString("imageResId") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        val websiteUrl = document.getString("websiteUrl") ?: ""

                        // Get the actual drawable ID from the map, default to placeholder if not found
                        val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue // IMPORTANT: Add a placeholder image in res/drawable

                        val entertainment = Entertainment(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "entertainment")
                        )
                        entertainmentList.add(entertainment)
                    } catch (e: Exception) {
                        Log.e("EntertainmentActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.entertainmentRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("EntertainmentActivity", "Error getting entertainment documents: ", exception)
                Toast.makeText(this, "Error loading entertainment data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["dj_ash"] = R.drawable.dj_ash
        map["uma_dancing"] = R.drawable.uma_dancing
        map["budhawaththa"] = R.drawable.budhawaththa
        // Add all your entertainment image resources here
        // e.g., map["another_entertainer_image"] = R.drawable.another_entertainer_image
        return map
    }

    private fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening website", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}