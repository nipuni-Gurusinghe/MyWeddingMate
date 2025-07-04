package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityVenuesBinding

class VenuesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVenuesBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVenuesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()

    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        // Initialize all favorite states
        listOf("kingsbury", "cinnamon", "watersEdge", "araliya").forEach { hotel ->
            favoriteMap[hotel] = savedFavorites.contains(hotel)
            updateHeartIcon(hotel)
        }
    }

    private fun updateHeartIcon(hotelKey: String) {
        val button = when(hotelKey) {
            "kingsbury" -> binding.kingsburyFavorite
            "cinnamon" -> binding.cinnamonFavorite
            "watersEdge" -> binding.watersEdgeFavorite
            "araliya" -> binding.araliyaFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[hotelKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }




    private fun setupButtonClickListeners() {
        // Kingsbury Hotel
        binding.kingsburyButton.setOnClickListener {
            openWebsite("https://www.thekingsburyhotel.com/weddings/wedding-packages-and-menus")
        }

        // Cinnamon Hotel
        binding.cinnamonButton.setOnClickListener {
            openWebsite("https://www.cinnamonhotels.com/weddings-events/weddings-by-cinnamon")
        }

        // Waters Edge
        binding.watersEdgeButton.setOnClickListener {
            openWebsite("https://www.watersedge.lk/my-wedding/")
        }

        // Araliya Beach Resort
        binding.araliyaButton.setOnClickListener {
            openWebsite("https://www.araliyaresorts.com/araliya-beach-resort/weddings/")
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
            finish()  // Close current activity and return to Wishlist
        }
    }
    private fun setupFavoriteButtons() {
        // Initialize all favorite states to false
        val hotels = listOf("kingsbury", "cinnamon", "watersEdge", "araliya")
        hotels.forEach { hotel ->
            favoriteMap[hotel] = false
        }

        // Set click listeners for each favorite button
        binding.kingsburyFavorite.setOnClickListener {
            toggleFavorite("kingsbury", binding.kingsburyFavorite)
        }
        binding.cinnamonFavorite.setOnClickListener {
            toggleFavorite("cinnamon", binding.cinnamonFavorite)
        }
        // Set click listeners for each favorite button
        binding.watersEdgeFavorite.setOnClickListener {
            toggleFavorite("watersEdge", binding.watersEdgeFavorite)
        }
        // Set click listeners for each favorite button
        binding.araliyaFavorite.setOnClickListener {
            toggleFavorite("araliya", binding.araliyaFavorite)
        }


        // Add others similarly
    }

    private fun toggleFavorite(hotelKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[hotelKey] ?: false
        favoriteMap[hotelKey] = !isFavorite
        updateHeartIcon(hotelKey)
        // Update heart icon
        val newIcon = if (isFavorite) {
            R.drawable.ic_heart_outline  // Empty heart
        } else {
            R.drawable.ic_heart_filled  // Filled heart
        }
        button.setImageResource(newIcon)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)


    }



}