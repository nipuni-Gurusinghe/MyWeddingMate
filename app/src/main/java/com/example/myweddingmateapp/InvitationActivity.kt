package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityInvitationBinding

class InvitationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvitationBinding
    private lateinit var prefs: PrefsHelper
    private val favoriteMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        loadInitialFavorites()
        setupFavoriteButtons()
        setupButtonClickListeners()
    }

    private fun loadInitialFavorites() {
        val savedFavorites = prefs.getFavorites()
        listOf("cardCraft", "elegantInvites", "weddingCardCo", "premiumCards").forEach { supplier ->
            favoriteMap[supplier] = savedFavorites.contains(supplier)
            updateHeartIcon(supplier)
        }
    }

    private fun updateHeartIcon(supplierKey: String) {
        val button = when(supplierKey) {
            "cardCraft" -> binding.cardCraftFavorite
            "elegantInvites" -> binding.elegantInvitesFavorite
            "weddingCardCo" -> binding.weddingCardCoFavorite
            "premiumCards" -> binding.premiumCardsFavorite
            else -> null
        }

        button?.setImageResource(
            if (favoriteMap[supplierKey] == true) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupButtonClickListeners() {
        // Card Craft
        binding.cardCraftButton.setOnClickListener {
            openWebsite("https://www.cardcraft.lk/")
        }

        // Elegant Invites
        binding.elegantInvitesButton.setOnClickListener {
            openWebsite("https://www.elegantinvites.com/")
        }

        // Wedding Card Co
        binding.weddingCardCoButton.setOnClickListener {
            openWebsite("https://www.weddingcardco.com/")
        }

        // Premium Cards
        binding.premiumCardsButton.setOnClickListener {
            openWebsite("https://www.premiumcards.lk/")
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
        binding.cardCraftFavorite.setOnClickListener {
            toggleFavorite("cardCraft", binding.cardCraftFavorite)
        }
        binding.elegantInvitesFavorite.setOnClickListener {
            toggleFavorite("elegantInvites", binding.elegantInvitesFavorite)
        }
        binding.weddingCardCoFavorite.setOnClickListener {
            toggleFavorite("weddingCardCo", binding.weddingCardCoFavorite)
        }
        binding.premiumCardsFavorite.setOnClickListener {
            toggleFavorite("premiumCards", binding.premiumCardsFavorite)
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