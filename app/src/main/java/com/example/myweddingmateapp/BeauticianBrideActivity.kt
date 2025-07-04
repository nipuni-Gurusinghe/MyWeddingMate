package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityBeauticianBrideBinding

class BeauticianBrideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianBrideBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianBrideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("dhananjaya", "neeliya", "meylisha").forEach { beautician ->
            favoriteMap[beautician] = savedFavorites.contains(beautician)
            updateHeartIcon(beautician)
        }
    }

    private fun updateHeartIcon(beauticianKey: String) {
        val button = when(beauticianKey) {
            "dhananjaya" -> binding.dhananjayaFavorite
            "neeliya" -> binding.neeliyaFavorite
            "meylisha" -> binding.meylishaFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[beauticianKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Dhananjaya Bandara
        binding.dhananjayaButton.setOnClickListener {
            openWebsite("https://dhananjayabandara.lk/")
        }

        // Neeliya Mendis Salon
        binding.neeliyaButton.setOnClickListener {
            openWebsite("https://neeliyamendissalons.com/")
        }

        // A.Meylisha
        binding.meylishaButton.setOnClickListener {
            openWebsite("https://www.instagram.com/makeupobsessedbymeylisha/")
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
        binding.dhananjayaFavorite.setOnClickListener {
            toggleFavorite("dhananjaya", binding.dhananjayaFavorite)
        }
        binding.neeliyaFavorite.setOnClickListener {
            toggleFavorite("neeliya", binding.neeliyaFavorite)
        }
        binding.meylishaFavorite.setOnClickListener {
            toggleFavorite("meylisha", binding.meylishaFavorite)
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