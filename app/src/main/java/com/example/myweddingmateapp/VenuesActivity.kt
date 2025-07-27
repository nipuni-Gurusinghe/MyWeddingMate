package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.VenuesAdapter
import com.example.myweddingmateapp.databinding.ActivityVenuesBinding
import com.example.myweddingmateapp.models.Venue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VenuesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVenuesBinding
    private lateinit var prefs: PrefsHelper
    private val venuesList = mutableListOf<Venue>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVenuesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchVenuesFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.venuesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.venuesRecyclerView.adapter = VenuesAdapter(
            venues = venuesList,
            onFavoriteClick = { venue ->
                val userId = auth.currentUser?.uid

                if (userId == null) {

                    Toast.makeText(this, "Please log in to favorite venues.", Toast.LENGTH_SHORT).show()
                    return@VenuesAdapter // Exit the lambda
                }

                venue.isFavorite = !venue.isFavorite
                if (venue.isFavorite) {
                    prefs.addFavorite(userId, venue.id, "venue")
                } else {
                    prefs.removeFavorite(userId, venue.id, "venue")
                }
                binding.venuesRecyclerView.adapter?.notifyItemChanged(venuesList.indexOf(venue))
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchVenuesFromFirestore() {
        val currentUserId = auth.currentUser?.uid

        db.collection("venues")
            .get()
            .addOnSuccessListener { result ->
                venuesList.clear()
                for (document in result) {
                    val venue = document.toObject(Venue::class.java).apply {
                        // Check favorite status using the currentUserId
                        isFavorite = if (currentUserId != null) {
                            prefs.isFavorite(currentUserId, id, "venue")
                        } else {
                            false
                        }
                    }
                    venuesList.add(venue)
                }
                binding.venuesRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading venues: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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