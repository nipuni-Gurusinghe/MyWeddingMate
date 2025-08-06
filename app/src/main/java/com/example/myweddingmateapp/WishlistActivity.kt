package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.WishlistAdapter
import com.example.myweddingmateapp.databinding.ActivityWishlistBinding
import com.example.myweddingmateapp.models.WishlistItem

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var adapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupNavBar()
    }

    private fun setupRecyclerView() {
        val wishlistItems = listOf(
            WishlistItem(
                id = "venues",
                title = "Venues",
                description = "Dream locations for your perfect day - from elegant ballrooms to scenic outdoor spaces",
                imageRes = R.drawable.venue,
                targetActivity = VenuesActivity::class.java
            ),
            WishlistItem(
                id = "photography",
                title = "Photography",
                description = "Capture every magical moment with our expert wedding photographers",
                imageRes = R.drawable.photography,
                targetActivity = PhotographyActivity::class.java
            ),
            WishlistItem(
                id = "bridal_wear",
                title = "Bridal Wear",
                description = "Stunning gowns to make you feel like royalty on your special day",
                imageRes = R.drawable.bridal_wear,
                targetActivity = BridalWearActivity::class.java
            ),
            WishlistItem(
                id = "groom_wear",
                title = "Groom Wear",
                description = "Elegant suits and traditional wear to complement your perfect look",
                imageRes = R.drawable.groom_wear,
                targetActivity = GroomWearActivity::class.java
            ),
            WishlistItem(
                id = "beautician_bride",
                title = "Bridal Beauty",
                description = "Professional makeup and hairstyling to enhance your natural beauty",
                imageRes = R.drawable.beautician_bride,
                targetActivity = BeauticianBrideActivity::class.java
            ),
            WishlistItem(
                id = "beautician_groom",
                title = "Groom Grooming",
                description = "Professional grooming services to look your sharpest on the big day",
                imageRes = R.drawable.beautician_groom,
                targetActivity = BeauticianGroomActivity::class.java
            ),
            WishlistItem(
                id = "jewellery",
                title = "Jewellery",
                description = "Exquisite pieces to add sparkle and elegance to your wedding attire",
                imageRes = R.drawable.jewellery,
                targetActivity = JewelleryActivity::class.java
            ),
            WishlistItem(
                id = "entertainment",
                title = "Entertainment",
                description = "Live music, DJs, and performers to keep your guests celebrating all night",
                imageRes = R.drawable.entertaintment,
                targetActivity = EntertainmentActivity::class.java
            ),
            WishlistItem(
                id = "floral",
                title = "Floral Decor",
                description = "Beautiful floral arrangements to transform your venue into a blooming paradise",
                imageRes = R.drawable.floral,
                targetActivity = FloralActivity::class.java
            ),
            WishlistItem(
                id = "invitation",
                title = "Invitations",
                description = "Elegant wedding invitations that make the perfect first impression",
                imageRes = R.drawable.invitation,
                targetActivity = InvitationActivity::class.java
            ),
            WishlistItem(
                id = "wedding_car",
                title = "Wedding Cars",
                description = "Luxurious vehicles for a grand entrance on your special day",
                imageRes = R.drawable.wedding_car,
                targetActivity = WeddingCarActivity::class.java
            )
        )

        adapter = WishlistAdapter(wishlistItems) { item ->
            startActivity(Intent(this, item.targetActivity))
        }

        binding.wishlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@WishlistActivity)
            adapter = this@WishlistActivity.adapter
            addItemDecoration(DividerItemDecoration(this@WishlistActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
    }

    private fun setupNavBar() {
        // Home button - reloads WishlistActivity
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // To-do List button
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatWithPlannerActivity::class.java))
            finish()
        }

        // Profile button
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, CoupleProfileActivity::class.java))
            finish()
        }

        // Wishlist icon - goes to FavoritesActivity
        findViewById<LinearLayout>(R.id.navWishlist).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }
    }
}