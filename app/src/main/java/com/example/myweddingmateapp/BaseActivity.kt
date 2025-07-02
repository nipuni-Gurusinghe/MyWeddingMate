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

        // Setup navigation
        setupNavigation()

        // Update navigation selection
        updateNavigationSelection(getCurrentNavId())
    }

    private fun setupNavigation() {
        navBar = findViewById(R.id.navBar)

        // Home Navigation
        val homeLayout = navBar.findViewById<LinearLayout>(R.id.navHome)
        homeLayout?.setOnClickListener {
            navigateToActivity(HomeActivity::class.java, R.id.navHome)
        }

        // Todo Navigation
        val todoLayout = navBar.findViewById<LinearLayout>(R.id.navTodo)
        todoLayout?.setOnClickListener {
            navigateToActivity(TodoActivity::class.java, R.id.navTodo)
        }

        // Profile Navigation
        val profileLayout = navBar.findViewById<LinearLayout>(R.id.navProfile)
        profileLayout?.setOnClickListener {
            navigateToActivity(CoupleProfileActivity::class.java, R.id.navProfile)
        }

        // Wishlist Navigation
        val wishlistLayout = navBar.findViewById<LinearLayout>(R.id.navWishlist)
        wishlistLayout?.setOnClickListener {
            navigateToActivity(VendorSelectionActivity::class.java, R.id.navWishlist)
        }
    }

    private fun navigateToActivity(activityClass: Class<*>, navId: Int) {
        if (getCurrentNavId() != navId) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            // Add transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish() // Close current activity to avoid stack buildup
        }
    }

    private fun updateNavigationSelection(selectedId: Int) {
        resetNavigationItems()
        highlightNavigationItem(selectedId)
    }

    private fun resetNavigationItems() {
        val navItems = listOf(R.id.navHome, R.id.navTodo, R.id.navProfile, R.id.navWishlist)
        navItems.forEach { itemId ->
            val layout = navBar.findViewById<LinearLayout>(itemId)
            layout?.let { setNavigationItemUnselected(it) }
        }
    }

    private fun highlightNavigationItem(itemId: Int) {
        val layout = navBar.findViewById<LinearLayout>(itemId)
        layout?.let { setNavigationItemSelected(it) }
    }

    private fun setNavigationItemSelected(layout: LinearLayout) {
        val icon = layout.getChildAt(0) as? ImageView
        val text = layout.getChildAt(1) as? TextView

        icon?.setColorFilter(getColor(R.color.nav_icon_selected))
        text?.setTextColor(getColor(R.color.nav_icon_selected))
        text?.setTypeface(null, Typeface.BOLD)
    }

    private fun setNavigationItemUnselected(layout: LinearLayout) {
        val icon = layout.getChildAt(0) as? ImageView
        val text = layout.getChildAt(1) as? TextView

        icon?.setColorFilter(getColor(R.color.nav_icon_unselected))
        text?.setTextColor(getColor(R.color.nav_icon_unselected))
        text?.setTypeface(null, Typeface.NORMAL)
    }
}