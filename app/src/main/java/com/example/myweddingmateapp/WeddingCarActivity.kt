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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeddingCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeddingCarBinding
    private lateinit var prefs: PrefsHelper
    private val weddingCarList = mutableListOf<WeddingCar>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeddingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchWeddingCarsFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.weddingCarRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.weddingCarRecyclerView.adapter = WeddingCarAdapter(
            weddingCars = weddingCarList,
            onFavoriteClick = { weddingCar ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite wedding cars.", Toast.LENGTH_SHORT).show()
                    return@WeddingCarAdapter
                }

                weddingCar.isFavorite = !weddingCar.isFavorite
                if (weddingCar.isFavorite) {
                    prefs.addFavorite(userId, weddingCar.id, "weddingCar")
                } else {
                    prefs.removeFavorite(userId, weddingCar.id, "weddingCar")
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
        val currentUserId = auth.currentUser?.uid

        db.collection("wedding-car")
            .get()
            .addOnSuccessListener { result ->
                weddingCarList.clear()
                val imageMap = createImageResourceMap()

                for (document in result) {
                    try {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val imageResIdName = document.getString("imageResId") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        val websiteUrl = document.getString("websiteUrl") ?: ""

                        val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue

                        val weddingCar = WeddingCar(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = if (currentUserId != null) {
                                prefs.isFavorite(currentUserId, id, "weddingCar")
                            } else {
                                false
                            }
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

            }
    }

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["malkey_car"] = R.drawable.malkey_car
        map["cason_car"] = R.drawable.cason_car
        map["master_car"] = R.drawable.master_car
        map["premium_cards"] = R.drawable.premium_cards
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