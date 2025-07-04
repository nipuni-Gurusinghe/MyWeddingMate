
package com.example.myweddingmateapp
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.ActivityBridalWearBinding

class BridalWearActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBridalWearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBridalWearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupBridalWearCards()
        loadDynamicRatings()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupBridalWearCards() {
        // Set click listeners for website buttons
        binding.amilaniButton.setOnClickListener {
            openWebsite("https://amilaniperera.com/pages/weddings")
        }

        binding.bridezoneButton.setOnClickListener {
            openWebsite("https://bridezone.lk/")
        }

        // Set click listeners for favorite buttons
        binding.amilaniFavorite.setOnClickListener {
            toggleFavorite(it as ImageButton, "amilani")
        }

        binding.bridezoneFavorite.setOnClickListener {
            toggleFavorite(it as ImageButton, "bridezone")
        }
    }

    private fun loadDynamicRatings() {
        // In a real app, you would fetch these from a database or API
        val bridalShops = listOf(
            BridalShop("amilani", 4.7f, 420),
            BridalShop("bridezone", 4.5f, 380)
        )

        bridalShops.forEach { shop ->
            when (shop.id) {
                "amilani" -> {
                    binding.amilaniRating.rating = shop.rating
                    binding.amilaniRatingText.text = "Rating: ${shop.rating} (${shop.reviewCount}+ reviews)"
                }
                "bridezone" -> {
                    binding.bridezoneRating.rating = shop.rating
                    binding.bridezoneRatingText.text = "Rating: ${shop.rating} (${shop.reviewCount}+ reviews)"
                }
            }
        }
    }

    private fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open website", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavorite(button: ImageButton, shopId: String) {
        // In a real app, you would save this state to SharedPreferences or database
        val isFavorite = button.tag as? Boolean ?: false
        button.setImageResource(
            if (isFavorite) R.drawable.ic_heart_outline else R.drawable.ic_heart_filled
        )
        button.tag = !isFavorite

        val message = if (isFavorite) "Removed from favorites" else "Added to favorites"
        Toast.makeText(this, "$message: ${shopId.replaceFirstChar { it.uppercase() }}", Toast.LENGTH_SHORT).show()
    }

    data class BridalShop(val id: String, val rating: Float, val reviewCount: Int)
}