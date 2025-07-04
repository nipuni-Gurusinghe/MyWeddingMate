package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityGroomWearBinding

class GroomWearActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroomWearBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroomWearBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("ramani", "dinesh", "houseOfFashion", "britishDress").forEach { supplier ->
            favoriteMap[supplier] = savedFavorites.contains(supplier)
            updateHeartIcon(supplier)
        }
    }

    private fun updateHeartIcon(supplierKey: String) {
        val button = when(supplierKey) {
            "ramani" -> binding.ramaniFavorite
            "dinesh" -> binding.dineshFavorite
            "houseOfFashion" -> binding.houseOfFashionFavorite
            "britishDress" -> binding.britishDressFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[supplierKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Ramani Brothers
        binding.ramaniButton.setOnClickListener {
            openWebsite("https://ramanibros.com/")
        }

        // Dinesh Clothing
        binding.dineshButton.setOnClickListener {
            openWebsite("https://www.dineshclothing.com/")
        }

        // House of Fashion
        binding.houseOfFashionButton.setOnClickListener {
            openWebsite("https://www.hof.lk/")
        }

        // British Dress
        binding.britishDressButton.setOnClickListener {
            openWebsite("https://www.britishdress.com/")
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
        binding.ramaniFavorite.setOnClickListener {
            toggleFavorite("ramani", binding.ramaniFavorite)
        }
        binding.dineshFavorite.setOnClickListener {
            toggleFavorite("dinesh", binding.dineshFavorite)
        }
        binding.houseOfFashionFavorite.setOnClickListener {
            toggleFavorite("houseOfFashion", binding.houseOfFashionFavorite)
        }
        binding.britishDressFavorite.setOnClickListener {
            toggleFavorite("britishDress", binding.britishDressFavorite)
        }
    }

    private fun toggleFavorite(supplierKey: String, button: ImageButton) {
        val isFavorite = favoriteMap[supplierKey] ?: false
        favoriteMap[supplierKey] = !isFavorite
        updateHeartIcon(supplierKey)

        val favorites = favoriteMap.filter { it.value }.keys.toMutableSet()
        prefs.saveFavorites(favorites)
    }
}