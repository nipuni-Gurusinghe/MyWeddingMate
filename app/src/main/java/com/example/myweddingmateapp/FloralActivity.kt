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
import com.google.firebase.auth.FirebaseAuth // ADD THIS IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FloralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFloralBinding
    private lateinit var prefs: PrefsHelper
    private val floralList = mutableListOf<Floral>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFloralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchFloralFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.floralRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.floralRecyclerView.adapter = FloralAdapter(
            floralList = floralList,
            onFavoriteClick = { floral ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite floral options.", Toast.LENGTH_SHORT).show()
                    return@FloralAdapter
                }

                floral.isFavorite = !floral.isFavorite
                if (floral.isFavorite) {
                    prefs.addFavorite(userId, floral.id, "floral")
                } else {
                    prefs.removeFavorite(userId, floral.id, "floral")
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
        val currentUserId = auth.currentUser?.uid

        db.collection("floral")
            .get()
            .addOnSuccessListener { result ->
                floralList.clear()
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

                        val floral = Floral(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = if (currentUserId != null) {
                                prefs.isFavorite(currentUserId, id, "floral")
                            } else {
                                false
                            }
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

            }
    }

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["lassana_flora"] = R.drawable.lassana_flora
        map["araliya_flora"] = R.drawable.araliya_flora
        map["ninety_f_flora"] = R.drawable.ninety_f_flora
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