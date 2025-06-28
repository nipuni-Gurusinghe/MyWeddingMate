package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityFloralBinding

class FloralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFloralBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFloralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("lassana", "araliya", "ninetyF").forEach { florist ->
            favoriteMap[florist] = savedFavorites.contains(florist)
            updateHeartIcon(florist)
        }
    }

    private fun updateHeartIcon(floristKey: String) {
        val button = when(floristKey) {
            "lassana" -> binding.lassanaFavorite
            "araliya" -> binding.araliyaFavorite
            "ninetyF" -> binding.ninetyFFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[floristKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Lassana Weddings
        binding.lassanaButton.setOnClickListener {
            openWebsite("https://lassanaweddings.com/packages.html")
        }

        // Araliya Flora
        binding.araliyaButton.setOnClickListener {
            openWebsite("https://araliyaflora.com/")
        }

        // 90F Wedding
        binding.ninetyFButton.setOnClickListener {
            openWebsite("https://www.90fweddings.com/")
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
        binding.lassanaFavorite.setOnClickListener {
            toggleFavorite("lassana", binding.lassanaFavorite)
        }
        binding.araliyaFavorite.setOnClickListener {
            toggleFavorite("araliya", binding.araliyaFavorite)
        }
        binding.ninetyFFavorite.setOnClickListener {
            toggleFavorite("ninetyF", binding.ninetyFFavorite)
        }
    }

    private fun toggleFavorite(floristKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[floristKey] ?: false
        favoriteMap[floristKey] = !isFavorite
        updateHeartIcon(floristKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}