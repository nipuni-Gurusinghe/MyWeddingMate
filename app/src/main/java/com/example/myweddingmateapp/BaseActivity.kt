package com.example.myweddingmateapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseActivity : AppCompatActivity() {
    private var navBar: LinearLayout? = null

    protected abstract fun getCurrentNavId(): Int
    protected abstract fun getLayoutResourceId(): Int


    protected open fun hasNavBar(): Boolean = true


    protected open fun needsProgrammaticNavbar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        if (hasNavBar()) {
            if (needsProgrammaticNavbar()) {

                addNavbarProgrammatically()
            }


            setupNavbarAfterLayout()
        }
    }

    private fun addNavbarProgrammatically() {
        try {
            android.util.Log.d("BaseActivity", "Adding navbar programmatically...")


            val contentView = findViewById<ViewGroup>(android.R.id.content)
            val rootLayout = findRelativeLayoutInHierarchy(contentView)

            if (rootLayout == null) {
                android.util.Log.e("BaseActivity", "Could not find RelativeLayout for programmatic navbar")
                return
            }

            android.util.Log.d("BaseActivity", "Found RelativeLayout: ${rootLayout.javaClass.simpleName}")

            // Check if navbar  exists
            if (rootLayout.findViewById<View>(R.id.navBar) != null) {
                android.util.Log.d("BaseActivity", "Navbar already exists, skipping")
                return
            }

            // Inflate navbar
            val navbarView = LayoutInflater.from(this)
                .inflate(R.layout.nav_bar, rootLayout, false)

            // Set layout parameters at bottom
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }

            // Add navbar to layout
            rootLayout.addView(navbarView, params)

            // Adjust existing views to accommodate navbar
            adjustLayoutForProgrammaticNavbar(rootLayout)

            android.util.Log.d("BaseActivity", "Programmatic navbar added successfully")

        } catch (e: Exception) {
            android.util.Log.e("BaseActivity", "Error adding programmatic navbar: ${e.message}", e)
        }
    }

    private fun findRelativeLayoutInHierarchy(parent: ViewGroup): RelativeLayout? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (child is RelativeLayout) {
                android.util.Log.d("BaseActivity", "Found RelativeLayout at position $i")
                return child
            } else if (child is ViewGroup) {
                val found = findRelativeLayoutInHierarchy(child)
                if (found != null) return found
            }
        }
        return null
    }

    private fun adjustLayoutForProgrammaticNavbar(rootLayout: RelativeLayout) {
        try {
            // Adjust RecyclerView if it exists
            rootLayout.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerChats)?.let {
                val params = it.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ABOVE, R.id.navBar)
                it.layoutParams = params
                android.util.Log.d("BaseActivity", "RecyclerView adjusted for navbar")
            }



            // Adjust empty state if it exists
            rootLayout.findViewById<LinearLayout>(R.id.emptyState)?.let {
                val params = it.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ABOVE, R.id.navBar)
                it.layoutParams = params
                android.util.Log.d("BaseActivity", "Empty state adjusted for navbar")
            }

        } catch (e: Exception) {
            android.util.Log.e("BaseActivity", "Error adjusting layout for navbar: ${e.message}", e)
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