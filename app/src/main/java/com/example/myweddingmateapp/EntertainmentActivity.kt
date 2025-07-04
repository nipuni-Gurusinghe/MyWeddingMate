package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityEntertainmentBinding

class EntertainmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntertainmentBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntertainmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("djAsh", "umaDancing", "budhawaththa").forEach { entertainer ->
            favoriteMap[entertainer] = savedFavorites.contains(entertainer)
            updateHeartIcon(entertainer)
        }
    }

    private fun updateHeartIcon(entertainerKey: String) {
        val button = when(entertainerKey) {
            "djAsh" -> binding.djAshFavorite
            "umaDancing" -> binding.umaFavorite
            "budhawaththa" -> binding.budhawaththaFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[entertainerKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // DJ Ash
        binding.djAshButton.setOnClickListener {
            openWebsite("https://djash.lk/")
        }

        // Uma Dancing
        binding.umaButton.setOnClickListener {
            openWebsite("http://www.umadancing.com/")
        }

        // Budhawaththa Dancing Academy
        binding.budhawaththaButton.setOnClickListener {
            openWebsite("https://budawattadancetroupe.com/")
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
        binding.djAshFavorite.setOnClickListener {
            toggleFavorite("djAsh", binding.djAshFavorite)
        }
        binding.umaFavorite.setOnClickListener {
            toggleFavorite("umaDancing", binding.umaFavorite)
        }
        binding.budhawaththaFavorite.setOnClickListener {
            toggleFavorite("budhawaththa", binding.budhawaththaFavorite)
        }
    }

    private fun toggleFavorite(entertainerKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[entertainerKey] ?: false
        favoriteMap[entertainerKey] = !isFavorite
        updateHeartIcon(entertainerKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}