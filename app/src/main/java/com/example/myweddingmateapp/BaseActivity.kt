package com.example.myweddingmateapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    private var navBar: LinearLayout? = null

    protected abstract fun getCurrentNavId(): Int
    protected abstract fun getLayoutResourceId(): Int
    protected open fun hasNavBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        if (hasNavBar()) {
            setupNavbarAfterLayout()
        }
    }

    private fun setupNavbarAfterLayout() {
        // Use post to ensure layout is complete before setup
        window.decorView.post {
            try {
                navBar = findViewById(R.id.navBar)
                navBar?.let {
                    android.util.Log.d("BaseActivity", "Setting up navbar functionality")
                    setupNavigation()
                    updateNavigationSelection(getCurrentNavId())
                } ?: run {
                    if (hasNavBar()) {
                        android.util.Log.w("BaseActivity", "NavBar not found after layout completion")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("BaseActivity", "NavBar setup failed: ${e.message}")
            }
        }
    }

    private fun setupNavigation() {
        navBar?.let { nav ->
            nav.findViewById<LinearLayout>(R.id.navHome)?.setOnClickListener {
                android.util.Log.d("BaseActivity", "Home navigation clicked")
                navigateToActivity(HomeActivity::class.java, R.id.navHome)
            }

            nav.findViewById<LinearLayout>(R.id.navChat)?.setOnClickListener {
                android.util.Log.d("BaseActivity", "Chat navigation clicked")
                navigateToActivity(ChatWithPlannerActivity::class.java, R.id.navChat)
            }

            nav.findViewById<LinearLayout>(R.id.navProfile)?.setOnClickListener {
                android.util.Log.d("BaseActivity", "Profile navigation clicked")
                navigateToActivity(CoupleProfileActivity::class.java, R.id.navProfile)
            }

            nav.findViewById<LinearLayout>(R.id.navWishlist)?.setOnClickListener {
                android.util.Log.d("BaseActivity", "Wishlist navigation clicked")
                navigateToActivity(WishlistActivity::class.java, R.id.navWishlist)
            }

            android.util.Log.d("BaseActivity", "Navigation setup complete")
        }
    }

    private fun navigateToActivity(activityClass: Class<*>, navId: Int) {
        if (getCurrentNavId() != navId) {
            android.util.Log.d("BaseActivity", "Navigating to ${activityClass.simpleName}")
            val intent = Intent(this, activityClass)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            android.util.Log.d("BaseActivity", "Already on ${activityClass.simpleName}, no navigation needed")
        }
    }

    private fun updateNavigationSelection(selectedId: Int) {
        navBar?.let {
            resetNavigationItems()
            highlightNavigationItem(selectedId)
            android.util.Log.d("BaseActivity", "Navigation selection updated for ID: $selectedId")
        }
    }

    private fun resetNavigationItems() {
        navBar?.let { nav ->
            listOf(R.id.navHome, R.id.navChat, R.id.navProfile, R.id.navWishlist).forEach { itemId ->
                nav.findViewById<LinearLayout>(itemId)?.let { setNavigationItemUnselected(it) }
            }
        }
    }

    private fun highlightNavigationItem(itemId: Int) {
        navBar?.findViewById<LinearLayout>(itemId)?.let { setNavigationItemSelected(it) }
    }

    private fun setNavigationItemSelected(layout: LinearLayout) {
        (layout.getChildAt(0) as? ImageView)?.setColorFilter(getColor(R.color.black))
        (layout.getChildAt(1) as? TextView)?.apply {
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun setNavigationItemUnselected(layout: LinearLayout) {
        (layout.getChildAt(0) as? ImageView)?.setColorFilter(getColor(R.color.card_background))
        (layout.getChildAt(1) as? TextView)?.apply {
            setTextColor(getColor(R.color.card_background))
            setTypeface(null, Typeface.NORMAL)
        }
    }
}
