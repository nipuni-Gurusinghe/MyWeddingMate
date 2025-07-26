package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.FloralAdapter
import com.example.myweddingmateapp.databinding.ActivityFloralBinding
import com.example.myweddingmateapp.models.Floral
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FloralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFloralBinding
    private lateinit var prefs: PrefsHelper
    private val floralList = mutableListOf<Floral>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFloralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchFloralFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.floralRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.floralRecyclerView.adapter = FloralAdapter(
            floralList = floralList,
            onFavoriteClick = { floral ->
                floral.isFavorite = !floral.isFavorite
                if (floral.isFavorite) {
                    prefs.addFavorite(floral.id, "floral")
                } else {
                    prefs.removeFavorite(floral.id, "floral")
                }
                binding.floralRecyclerView.adapter?.notifyItemChanged(
                    floralList.indexOf(floral)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchFloralFromFirestore() {
        db.collection("floral") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                floralList.clear() // Clear existing hardcoded data
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

                        val floral = Floral(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "floral")
                        )
                        floralList.add(floral)
                    } catch (e: Exception) {
                        Log.e("FloralActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.floralRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FloralActivity", "Error getting floral documents: ", exception)
                Toast.makeText(this, "Error loading floral data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["lassana_flora"] = R.drawable.lassana_flora
        map["araliya_flora"] = R.drawable.araliya_flora
        map["ninety_f_flora"] = R.drawable.ninety_f_flora
        // Add all your floral image resources here
        // e.g., map["another_floral_image"] = R.drawable.another_floral_image
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