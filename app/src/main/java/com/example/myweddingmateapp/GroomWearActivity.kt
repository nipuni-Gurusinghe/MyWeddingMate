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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroomWearActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroomWearBinding
    private lateinit var prefs: PrefsHelper
    private val groomWearList = mutableListOf<GroomWear>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroomWearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        loadGroomWearDataFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.groomWearRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groomWearRecyclerView.adapter = GroomWearAdapter(
            groomWearList = groomWearList,
            onFavoriteClick = { groomWear ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite groom wear.", Toast.LENGTH_SHORT).show()
                    return@GroomWearAdapter
                }

                groomWear.isFavorite = !groomWear.isFavorite
                if (groomWear.isFavorite) {
                    prefs.addFavorite(userId, groomWear.id, "groomWear")
                } else {
                    prefs.removeFavorite(userId, groomWear.id, "groomWear")
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
        val currentUserId = auth.currentUser?.uid

        db.collection("groomWear")
            .get()
            .addOnSuccessListener { result ->
                groomWearList.clear()
                val imageMap = createImageResourceMap()

                for (document in result) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val imageResIdName = document.getString("imageResId") ?: ""
                    val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                    val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                    val websiteUrl = document.getString("websiteUrl") ?: ""

                    val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue

                    val groomWear = GroomWear(
                        id = id,
                        name = name,
                        imageResId = imageDrawableId,
                        rating = rating,
                        reviewCount = reviewCount,
                        websiteUrl = websiteUrl,
                        isFavorite = if (currentUserId != null) {
                            prefs.isFavorite(currentUserId, id, "groomWear")
                        } else {
                            false
                        }
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

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["ramani_brothers"] = R.drawable.ramani_brothers
        map["dinesh_clothing"] = R.drawable.dinesh_clothing
        map["house_of_fashion"] = R.drawable.house_of_fashion
        map["british_dress"] = R.drawable.british_dress
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