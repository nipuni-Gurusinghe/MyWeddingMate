package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityPhotographyBinding

class PhotographyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotographyBinding
    private val heartStates = mutableMapOf<String, Boolean>().apply {
        put("prabath", false)
        put("harsha", false)
        put("adeesha", false)
        put("geeshan", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotographyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupFavoriteButtons()
        setupWebsiteButtons()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupFavoriteButtons() {
        binding.prabathFavorite.setOnClickListener {
            toggleHeart("prabath", binding.prabathFavorite)
        }
        binding.harshaFavorite.setOnClickListener {
            toggleHeart("harsha", binding.harshaFavorite)
        }
        binding.adeeshaFavorite.setOnClickListener {
            toggleHeart("adeesha", binding.adeeshaFavorite)
        }
        binding.geeshanFavorite.setOnClickListener {
            toggleHeart("geeshan", binding.geeshanFavorite)
        }
    }

    private fun toggleHeart(photographerKey: String, button: ImageButton) {
        val isFilled = heartStates[photographerKey] ?: false
        heartStates[photographerKey] = !isFilled
        button.setImageResource(
            if (isFilled) R.drawable.ic_heart_outline
            else R.drawable.ic_heart_filled
        )
    }

    private fun setupWebsiteButtons() {
        binding.prabathButton.setOnClickListener {
            openWebsite("https://www.prabathkanishkaphotography.com/")
        }
        binding.harshaButton.setOnClickListener {
            openWebsite("https://www.harshamaduranga.com/")
        }
        binding.adeeshaButton.setOnClickListener {
            openWebsite("https://www.adesharandulaphotography.com/")
        }
        binding.geeshanButton.setOnClickListener {
            openWebsite("https://geeshan.com/")
        }
    }

    private fun openWebsite(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(this, "Couldn't open website", Toast.LENGTH_SHORT).show()
        }
    }
}