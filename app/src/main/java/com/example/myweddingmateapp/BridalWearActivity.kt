package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.BridalWearAdapter
import com.example.myweddingmateapp.databinding.ActivityBridalWearBinding
import com.example.myweddingmateapp.models.BridalWear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BridalWearActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBridalWearBinding
    private lateinit var prefs: PrefsHelper
    private val bridalWearList = mutableListOf<BridalWear>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBridalWearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchBridalWearFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.bridalWearRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bridalWearRecyclerView.adapter = BridalWearAdapter(
            bridalWearList = bridalWearList,
            onFavoriteClick = { bridalWear ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite bridal wear.", Toast.LENGTH_SHORT).show()
                    return@BridalWearAdapter
                }

                bridalWear.isFavorite = !bridalWear.isFavorite
                if (bridalWear.isFavorite) {
                    prefs.addFavorite(userId, bridalWear.id, "bridalWear")
                } else {
                    prefs.removeFavorite(userId, bridalWear.id, "bridalWear")
                }
                binding.bridalWearRecyclerView.adapter?.notifyItemChanged(
                    bridalWearList.indexOf(bridalWear)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchBridalWearFromFirestore() {
        val currentUserId = auth.currentUser?.uid

        db.collection("bridalWear")
            .get()
            .addOnSuccessListener { result ->
                bridalWearList.clear()
                for (document in result) {
                    val bridalWear = document.toObject(BridalWear::class.java).apply {
                        isFavorite = if (currentUserId != null) {
                            prefs.isFavorite(currentUserId, id, "bridalWear")
                        } else {
                            false
                        }
                    }
                    bridalWearList.add(bridalWear)
                }
                binding.bridalWearRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading bridal wear: ${exception.message}", Toast.LENGTH_SHORT).show()
                loadHardcodedData(currentUserId)
            }
    }

    private fun loadHardcodedData(currentUserId: String?) {

        bridalWearList.clear()
        bridalWearList.addAll(listOf(
            BridalWear(
                id = "amilani",
                name = "Amilani Perera",
                imageResId = "amilani",
                rating = 4.7f,
                reviewCount = 420,
                websiteUrl = "https://amilaniperera.com",
                isFavorite = if (currentUserId != null) {
                    prefs.isFavorite(currentUserId, "amilani", "bridalWear")
                } else {
                    false
                }
            ),
            BridalWear(
                id = "bridezone",
                name = "Bridezone",
                imageResId = "bridezone",
                rating = 4.5f,
                reviewCount = 380,
                websiteUrl = "https://bridezone.com",
                isFavorite = if (currentUserId != null) {
                    prefs.isFavorite(currentUserId, "bridezone", "bridalWear")
                } else {
                    false
                }
            ),
            BridalWear(
                id = "saree_mahal",
                name = "Saree Mahal",
                imageResId = "placeholder_venue",
                rating = 4.3f,
                reviewCount = 250,
                websiteUrl = "https://sareemahal.com",
                isFavorite = if (currentUserId != null) {
                    prefs.isFavorite(currentUserId, "saree_mahal", "bridalWear")
                } else {
                    false
                }
            )
        ))
        binding.bridalWearRecyclerView.adapter?.notifyDataSetChanged()
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