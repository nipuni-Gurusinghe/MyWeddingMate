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
import com.google.firebase.auth.FirebaseAuth // ADD THIS IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EntertainmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntertainmentBinding
    private lateinit var prefs: PrefsHelper
    private val entertainmentList = mutableListOf<Entertainment>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntertainmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchEntertainmentFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.entertainmentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.entertainmentRecyclerView.adapter = EntertainmentAdapter(
            entertainments = entertainmentList,
            onFavoriteClick = { entertainment ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite entertainment options.", Toast.LENGTH_SHORT).show()
                    return@EntertainmentAdapter
                }

                entertainment.isFavorite = !entertainment.isFavorite
                if (entertainment.isFavorite) {
                    prefs.addFavorite(userId, entertainment.id, "entertainment")
                } else {
                    prefs.removeFavorite(userId, entertainment.id, "entertainment")
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
        val currentUserId = auth.currentUser?.uid

        db.collection("entertainment")
            .get()
            .addOnSuccessListener { result ->
                entertainmentList.clear()
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

                        val entertainment = Entertainment(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = if (currentUserId != null) {
                                prefs.isFavorite(currentUserId, id, "entertainment") // PASS USER ID
                            } else {
                                false
                            }
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

            }
    }

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["dj_ash"] = R.drawable.dj_ash
        map["uma_dancing"] = R.drawable.uma_dancing
        map["budhawaththa"] = R.drawable.budhawaththa
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