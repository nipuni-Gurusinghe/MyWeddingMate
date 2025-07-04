package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import com.example.myweddingmateapp.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
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
}