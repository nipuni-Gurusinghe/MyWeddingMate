package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.BeauticianGroomAdapter
import com.example.myweddingmateapp.databinding.ActivityBeauticianGroomBinding
import com.example.myweddingmateapp.models.BeauticianGroom // Ensure this matches your model class name
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeauticianGroomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianGroomBinding
    private lateinit var prefs: PrefsHelper
    private val beauticianGroomList = mutableListOf<BeauticianGroom>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianGroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchBeauticiansGroomFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.beauticianGroomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beauticianGroomRecyclerView.adapter = BeauticianGroomAdapter(
            beauticians = beauticianGroomList,
            onFavoriteClick = { beautician ->
                beautician.isFavorite = !beautician.isFavorite
                if (beautician.isFavorite) {
                    prefs.addFavorite(beautician.id, "beauticianGroom")
                } else {
                    prefs.removeFavorite(beautician.id, "beauticianGroom")
                }
                binding.beauticianGroomRecyclerView.adapter?.notifyItemChanged(
                    beauticianGroomList.indexOf(beautician)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchBeauticiansGroomFromFirestore() {
        db.collection("beautician-groom") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                beauticianGroomList.clear() // Clear existing hardcoded data
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

                        val beautician = BeauticianGroom(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "beauticianGroom")
                        )
                        beauticianGroomList.add(beautician)
                    } catch (e: Exception) {
                        Log.e("BeauticianGroomActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.beauticianGroomRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("BeauticianGroomActivity", "Error getting beautician groom documents: ", exception)
                Toast.makeText(this, "Error loading groom beautician data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["salon_zero_groom"] = R.drawable.salon_zero_groom
        map["naturals"] = R.drawable.naturals
        // Add all your groom beautician image resources here
        // e.g., map["another_beautician_image"] = R.drawable.another_beautician_image
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