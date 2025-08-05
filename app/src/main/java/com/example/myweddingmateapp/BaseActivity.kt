package com.example.myweddingmateapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var navBar: LinearLayout

    protected abstract fun getCurrentNavId(): Int
    protected abstract fun getLayoutResourceId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        navBar = findViewById(R.id.navBar)
        setupNavigation()
        updateNavigationSelection(getCurrentNavId())
    }

    private fun setupNavigation() {
        navBar.findViewById<LinearLayout>(R.id.navHome)?.setOnClickListener {
            navigateToActivity(HomeActivity::class.java, R.id.navHome)
        }

        navBar.findViewById<LinearLayout>(R.id.navChat)?.setOnClickListener {
            navigateToActivity(ChatWithPlannerActivity::class.java, R.id.navChat)
        }

        navBar.findViewById<LinearLayout>(R.id.navProfile)?.setOnClickListener {
            navigateToActivity(CoupleProfileActivity::class.java, R.id.navProfile)
        }

        navBar.findViewById<LinearLayout>(R.id.navWishlist)?.setOnClickListener {
            navigateToActivity(WishlistActivity::class.java, R.id.navWishlist)
        }
    }

    private fun navigateToActivity(activityClass: Class<*>, navId: Int) {
        if (getCurrentNavId() != navId) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun updateNavigationSelection(selectedId: Int) {
        resetNavigationItems()
        highlightNavigationItem(selectedId)
    }

    private fun resetNavigationItems() {
        listOf(R.id.navHome, R.id.navChat, R.id.navProfile, R.id.navWishlist).forEach { itemId ->
            navBar.findViewById<LinearLayout>(itemId)?.let { setNavigationItemUnselected(it) }
        }
    }

    private fun highlightNavigationItem(itemId: Int) {
        navBar.findViewById<LinearLayout>(itemId)?.let { setNavigationItemSelected(it) }
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