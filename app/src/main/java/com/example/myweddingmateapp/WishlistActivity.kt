package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupNavBar()
    }

    private fun setupClickListeners() {
        binding.venuesCard.setOnClickListener {
            startActivity(Intent(this, VenuesActivity::class.java))
        }
        binding.photographyCard.setOnClickListener {
            startActivity(Intent(this, PhotographyActivity::class.java))
        }
        binding.bridalWearCard.setOnClickListener {
            startActivity(Intent(this, BridalWearActivity::class.java))
        }
        binding.groomWearCard.setOnClickListener {
            startActivity(Intent(this, GroomWearActivity::class.java))
        }
        binding.beauticianBrideCard.setOnClickListener {
            startActivity(Intent(this, BeauticianBrideActivity::class.java))
        }
        binding.beauticianGroomCard.setOnClickListener {
            startActivity(Intent(this, BeauticianGroomActivity::class.java))
        }
        binding.jewelleryCard.setOnClickListener {
            startActivity(Intent(this, JewelleryActivity::class.java))
        }
        binding.entertainmentCard.setOnClickListener {
            startActivity(Intent(this, EntertainmentActivity::class.java))
        }
        binding.floralCard.setOnClickListener {
            startActivity(Intent(this, FloralActivity::class.java))
        }
        binding.invitationCard.setOnClickListener {
            startActivity(Intent(this, InvitationActivity::class.java))
        }
        binding.weddingCarCard.setOnClickListener {
            startActivity(Intent(this, WeddingCarActivity::class.java))
        }
    }

    private fun setupNavBar() {
        // Home button - reloads WishlistActivity
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
            finish()
        }

//        // To-do List button
//        findViewById<LinearLayout>(R.id.navTodo).setOnClickListener {
//            startActivity(Intent(this, ChecklistActivity::class.java))
//            finish()
//        }

//        // Profile button
//        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
//            startActivity(Intent(this, ProfileActivity::class.java))
//            finish()
//        }

        // Wishlist icon - goes to FavouritesActivity
        findViewById<LinearLayout>(R.id.navWishlist).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }
    }


}