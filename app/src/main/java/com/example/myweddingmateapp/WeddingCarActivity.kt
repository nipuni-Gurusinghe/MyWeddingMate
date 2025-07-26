package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.WeddingCarAdapter
import com.example.myweddingmateapp.databinding.ActivityWeddingCarBinding
import com.example.myweddingmateapp.models.WeddingCar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeddingCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeddingCarBinding
    private lateinit var prefs: PrefsHelper
    private val weddingCarList = mutableListOf<WeddingCar>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeddingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchWeddingCarsFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.weddingCarRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.weddingCarRecyclerView.adapter = WeddingCarAdapter(
            weddingCars = weddingCarList,
            onFavoriteClick = { weddingCar ->
                weddingCar.isFavorite = !weddingCar.isFavorite
                if (weddingCar.isFavorite) {
                    prefs.addFavorite(weddingCar.id, "weddingCar")
                } else {
                    prefs.removeFavorite(weddingCar.id, "weddingCar")
                }
                binding.weddingCarRecyclerView.adapter?.notifyItemChanged(
                    weddingCarList.indexOf(weddingCar)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchWeddingCarsFromFirestore() {
        db.collection("wedding-car") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                weddingCarList.clear() // Clear existing hardcoded data
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

                        val weddingCar = WeddingCar(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "weddingCar")
                        )
                        weddingCarList.add(weddingCar)
                    } catch (e: Exception) {
                        Log.e("WeddingCarActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.weddingCarRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("WeddingCarActivity", "Error getting wedding car documents: ", exception)
                Toast.makeText(this, "Error loading wedding car data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["malkey_car"] = R.drawable.malkey_car
        map["cason_car"] = R.drawable.cason_car
        map["master_car"] = R.drawable.master_car
        map["premium_cards"] = R.drawable.premium_cards // Assuming premium_cards image is also used for cars
        // Add all your wedding car image resources here
        // e.g., map["another_car_image"] = R.drawable.another_car_image
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