package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityWeddingCarBinding

class WeddingCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeddingCarBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeddingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("malkey", "cason", "master").forEach { carRenter ->
            favoriteMap[carRenter] = savedFavorites.contains(carRenter)
            updateHeartIcon(carRenter)
        }
    }

    private fun updateHeartIcon(carRenterKey: String) {
        val button = when(carRenterKey) {
            "malkey" -> binding.malkeyFavorite
            "cason" -> binding.casonFavorite
            "master" -> binding.masterFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[carRenterKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Malkey Rent a Car
        binding.malkeyButton.setOnClickListener {
            openWebsite("https://www.malkey.lk/rates/wedding-car-rates.html")
        }

        // Cason Rent a Car
        binding.casonButton.setOnClickListener {
            openWebsite("https://www.casons.lk/services/wedding-vip-hires")
        }

        // Master Wedding Cars
        binding.masterButton.setOnClickListener {
            openWebsite("https://www.facebook.com/MasterWeddingCarRentals/")
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
        binding.malkeyFavorite.setOnClickListener {
            toggleFavorite("malkey", binding.malkeyFavorite)
        }
        binding.casonFavorite.setOnClickListener {
            toggleFavorite("cason", binding.casonFavorite)
        }
        binding.masterFavorite.setOnClickListener {
            toggleFavorite("master", binding.masterFavorite)
        }
    }

    private fun toggleFavorite(carRenterKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[carRenterKey] ?: false
        favoriteMap[carRenterKey] = !isFavorite
        updateHeartIcon(carRenterKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}