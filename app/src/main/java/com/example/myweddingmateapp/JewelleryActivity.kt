package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.JewelleryAdapter
import com.example.myweddingmateapp.databinding.ActivityJewelleryBinding
import com.example.myweddingmateapp.models.Jewellery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JewelleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJewelleryBinding
    private lateinit var prefs: PrefsHelper
    private val jewelleryList = mutableListOf<Jewellery>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJewelleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchJewelleryFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.jewelleryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.jewelleryRecyclerView.adapter = JewelleryAdapter(
            jewelleryList = jewelleryList,
            onFavoriteClick = { jewellery ->
                jewellery.isFavorite = !jewellery.isFavorite
                if (jewellery.isFavorite) {
                    prefs.addFavorite(jewellery.id, "jewellery")
                } else {
                    prefs.removeFavorite(jewellery.id, "jewellery")
                }
                binding.jewelleryRecyclerView.adapter?.notifyItemChanged(
                    jewelleryList.indexOf(jewellery)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchJewelleryFromFirestore() {
        db.collection("jewellery") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                jewelleryList.clear() // Clear existing hardcoded data
                val imageMap = createImageResourceMap() // Create map for image resources

                for (document in result) {
                    try {
                        // Use document.getString, getDouble, getLong for individual fields
                        // This gives more control over type conversion and error handling
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val imageResIdName = document.getString("imageResId") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        val websiteUrl = document.getString("websiteUrl") ?: ""

                        // Get the actual drawable ID from the map, default to placeholder if not found
                        val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue // IMPORTANT: Add a placeholder image in res/drawable

                        val jewellery = Jewellery(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "jewellery")
                        )
                        jewelleryList.add(jewellery)
                    } catch (e: Exception) {
                        Log.e("JewelleryActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.jewelleryRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("JewelleryActivity", "Error getting jewellery documents: ", exception)
                Toast.makeText(this, "Error loading jewellery data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["vogue"] = R.drawable.vogue
        map["raja"] = R.drawable.raja
        map["mallika"] = R.drawable.mallika
        // Add all your jewellery image resources here
        // e.g., map["another_jeweller_image"] = R.drawable.another_jeweller_image
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