package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityJewelleryBinding

class JewelleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJewelleryBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJewelleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("vogue", "raja", "mallika").forEach { jeweller ->
            favoriteMap[jeweller] = savedFavorites.contains(jeweller)
            updateHeartIcon(jeweller)
        }
    }

    private fun updateHeartIcon(jewellerKey: String) {
        val button = when(jewellerKey) {
            "vogue" -> binding.vogueFavorite
            "raja" -> binding.rajaFavorite
            "mallika" -> binding.mallikaFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[jewellerKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Vogue Jewellers
        binding.vogueButton.setOnClickListener {
            openWebsite("https://www.voguejewellers.lk/")
        }

        // Raja Jewellers
        binding.rajaButton.setOnClickListener {
            openWebsite("https://www.rajajewellers.com/")
        }

        // Mallika Hemachandra Jewellers
        binding.mallikaButton.setOnClickListener {
            openWebsite("https://mallikahemachandra.com/")
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
        binding.vogueFavorite.setOnClickListener {
            toggleFavorite("vogue", binding.vogueFavorite)
        }
        binding.rajaFavorite.setOnClickListener {
            toggleFavorite("raja", binding.rajaFavorite)
        }
        binding.mallikaFavorite.setOnClickListener {
            toggleFavorite("mallika", binding.mallikaFavorite)
        }
    }

    private fun toggleFavorite(jewellerKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[jewellerKey] ?: false
        favoriteMap[jewellerKey] = !isFavorite
        updateHeartIcon(jewellerKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}