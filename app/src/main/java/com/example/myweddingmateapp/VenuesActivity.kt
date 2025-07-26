package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.VenuesAdapter
import com.example.myweddingmateapp.databinding.ActivityVenuesBinding
import com.example.myweddingmateapp.models.Venue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VenuesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVenuesBinding
    private lateinit var prefs: PrefsHelper
    private val venuesList = mutableListOf<Venue>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVenuesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore

        setupBackButton()
        setupRecyclerView()
        fetchVenuesFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.venuesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.venuesRecyclerView.adapter = VenuesAdapter(
            venues = venuesList,
            onFavoriteClick = { venue ->
                venue.isFavorite = !venue.isFavorite
                if (venue.isFavorite) {
                    prefs.addFavorite(venue.id, "venue")
                } else {
                    prefs.removeFavorite(venue.id, "venue")
                }
                binding.venuesRecyclerView.adapter?.notifyItemChanged(venuesList.indexOf(venue))
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchVenuesFromFirestore() {
        db.collection("venues")
            .get()
            .addOnSuccessListener { result ->
                venuesList.clear()
                for (document in result) {
                    val venue = document.toObject(Venue::class.java).apply {
                        isFavorite = prefs.isFavorite(id, "venue")
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