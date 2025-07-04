package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityBeauticianGroomBinding

class BeauticianGroomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianGroomBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianGroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("salonZero", "naturals").forEach { beautician ->
            favoriteMap[beautician] = savedFavorites.contains(beautician)
            updateHeartIcon(beautician)
        }
    }

    private fun updateHeartIcon(beauticianKey: String) {
        val button = when(beauticianKey) {
            "salonZero" -> binding.salonZeroFavorite
            "naturals" -> binding.naturalsFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[beauticianKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Grooms Art by Salon Zero
        binding.salonZeroButton.setOnClickListener {
            openWebsite("https://www.facebook.com/p/Grooms-Art-by-Salon-Zero-100054501548065/")
        }

        // Naturals Unisex Salon
        binding.naturalsButton.setOnClickListener {
            openWebsite("https://www.naturals.lk/")
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

    private fun setupFavoriteButtons() {
        binding.salonZeroFavorite.setOnClickListener {
            toggleFavorite("salonZero", binding.salonZeroFavorite)
        }
        binding.naturalsFavorite.setOnClickListener {
            toggleFavorite("naturals", binding.naturalsFavorite)
        }
    }

    private fun toggleFavorite(beauticianKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[beauticianKey] ?: false
        favoriteMap[beauticianKey] = !isFavorite
        updateHeartIcon(beauticianKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}