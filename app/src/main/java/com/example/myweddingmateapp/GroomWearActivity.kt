package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.GroomWearAdapter
import com.example.myweddingmateapp.databinding.ActivityGroomWearBinding
import com.example.myweddingmateapp.models.GroomWear
import com.google.firebase.firestore.FirebaseFirestore

class GroomWearActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroomWearBinding
    private lateinit var prefs: PrefsHelper
    private val groomWearList = mutableListOf<GroomWear>()
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroomWearBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        setupRecyclerView()
        loadGroomWearDataFromFirestore() // Load data from Firestore
    }

    private fun setupRecyclerView() {
        binding.groomWearRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groomWearRecyclerView.adapter = GroomWearAdapter(
            groomWearList = groomWearList,
            onFavoriteClick = { groomWear ->
                groomWear.isFavorite = !groomWear.isFavorite
                if (groomWear.isFavorite) {
                    prefs.addFavorite(groomWear.id, "groomWear")
                } else {
                    prefs.removeFavorite(groomWear.id, "groomWear")
                }
                binding.groomWearRecyclerView.adapter?.notifyItemChanged(
                    groomWearList.indexOf(groomWear)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun loadGroomWearDataFromFirestore() {
        db.collection("groomWear")
            .get()
            .addOnSuccessListener { result ->
                groomWearList.clear() // Clear existing data
                val imageMap = createImageResourceMap() // Create a map for image resources

                for (document in result) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val imageResIdName = document.getString("imageResId") ?: "" // Get image resource name
                    val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                    val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                    val websiteUrl = document.getString("websiteUrl") ?: ""

                    // Get the actual drawable ID from the map, default to a placeholder if not found
                    val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue // Replace with a suitable placeholder

                    val groomWear = GroomWear(
                        id = id,
                        name = name,
                        imageResId = imageDrawableId,
                        rating = rating,
                        reviewCount = reviewCount,
                        websiteUrl = websiteUrl,
                        isFavorite = prefs.isFavorite(id, "groomWear")
                    )
                    groomWearList.add(groomWear)
                }
                binding.groomWearRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("GroomWearActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading groom wear data.", Toast.LENGTH_SHORT).show()
            }
    }

    // Helper function to map image resource names to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["ramani_brothers"] = R.drawable.ramani_brothers
        map["dinesh_clothing"] = R.drawable.dinesh_clothing
        map["house_of_fashion"] = R.drawable.house_of_fashion
        map["british_dress"] = R.drawable.british_dress
        // Add all your groom wear image resources here
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