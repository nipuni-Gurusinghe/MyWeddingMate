package com.example.myweddingmateapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myweddingmateapp.models.WeddingPlanner

class HomeActivity : BaseActivity() {

    private lateinit var btnProfile: Button
    private lateinit var btnPlanners: Button
    private lateinit var btnVendors: Button
    private lateinit var btnBudget: Button

    // Activity Result Launchers
    private val profileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            showToast("Profile updated successfully ")
            Log.d(TAG, "Profile activity completed successfully")
            refreshButtonStates()
        }
    }

    private val plannersLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                showToast("Wedding planner selection completed ")
                Log.d(TAG, "Wedding planners activity completed successfully")
            }
            WeddingPlannersActivity.RESULT_PLANNER_SELECTED -> {
                result.data?.getParcelableExtra<WeddingPlanner>(WeddingPlannersActivity.EXTRA_SELECTED_PLANNER)?.let { planner ->
                    onWeddingPlannerSelected(planner)
                }
            }
        }
    }

    private val vendorsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            showToast("Vendor selection completed")
            Log.d(TAG, "Vendor selection activity completed successfully")
        }
    }

    private val budgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            showToast("Budget updated successfully!")
            Log.d(TAG, "Budget overview activity completed successfully")
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }


    override fun getCurrentNavId(): Int = R.id.navHome

    override fun getLayoutResourceId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeViews()
        setupClickListeners()
        setupButtonAnimations()
        showWelcomeMessage()
    }

    private fun initializeViews() {
        try {
            btnProfile = findViewById(R.id.btn_profile)
            btnPlanners = findViewById(R.id.btn_planners)
            btnVendors = findViewById(R.id.btn_vendors)
            btnBudget = findViewById(R.id.btn_budget)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            showToast("Error loading interface")
        }
    }

    private fun setupClickListeners() {
        btnProfile.setOnClickListener {
            navigateToCoupleProfile()
        }

        btnPlanners.setOnClickListener {
            navigateToWeddingPlanners()
        }

        btnVendors.setOnClickListener {
            navigateToVendorSelection()
        }

        btnBudget.setOnClickListener {
            navigateToBudgetOverview()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtonAnimations() {
        val buttons = listOf(btnProfile, btnPlanners, btnVendors, btnBudget)

        buttons.forEach { button ->
            button.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }
    }

    private fun navigateToCoupleProfile() {
        try {
            val intent = Intent(this, CoupleProfileActivity::class.java)
            profileLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            Log.d(TAG, "Navigating to Couple Profile")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Couple Profile", e)
            showToast("Unable to open Couple Profile")
        }
    }

    private fun navigateToWeddingPlanners() {
        try {
            val intent = Intent(this, WeddingPlannersActivity::class.java)
            plannersLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            Log.d(TAG, "Navigating to Wedding Planners")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Wedding Planners", e)
            showToast("Wedding Planners feature coming soon!")
        }
    }

    private fun navigateToVendorSelection() {
        try {
            val intent = Intent(this, VendorSelectionActivity::class.java)
            vendorsLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            Log.d(TAG, "Navigating to Vendor Selection")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Vendor Selection", e)
            showToast("Vendor Selection feature coming soon!")
        }
    }

    private fun navigateToBudgetOverview() {
        try {
            val intent = Intent(this, BudgetOverviewActivity::class.java)
            budgetLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            Log.d(TAG, "Navigating to Budget Overview")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Budget Overview", e)
            showToast("Budget Overview feature coming soon!")
        }
    }

    //planner functions
    private fun onWeddingPlannerSelected(planner: WeddingPlanner) {
        Log.d(TAG, "Wedding planner selected: ${planner.name}")
        saveSelectedPlanner(planner)
        showToast("${planner.name} selected as your wedding planner!")
        updateUIForSelectedPlanner(planner)
    }

    private fun saveSelectedPlanner(planner: WeddingPlanner) {
        val sharedPref = getSharedPreferences("wedding_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_planner_id", planner.id)
            putString("selected_planner_name", planner.name)
            putString("selected_planner_email", planner.email)
            putString("selected_planner_phone", planner.phone)
            putString("selected_planner_bio", planner.bio)
            putFloat("selected_planner_rating", planner.rating)
            putString("selected_planner_location", planner.location)
            putBoolean("has_selected_planner", true)
            apply()
        }
    }

    private fun updateUIForSelectedPlanner(planner: WeddingPlanner) {

    }

    private fun showWelcomeMessage() {
        lifecycleScope.launch {
            delay(500)
            showToast("Welcome to your Wedding Planning Journey!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the Wedding Planner?")
            .setPositiveButton("Exit") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Home activity resumed")
    //    refreshButtonStates()
    }

    private fun refreshButtonStates() {
        val hasProfile = checkIfProfileExists()

        System.out.println("hasProfile" + hasProfile)
        btnPlanners.isEnabled = hasProfile
        btnVendors.isEnabled = hasProfile
        btnBudget.isEnabled = hasProfile

        if (!hasProfile) {
            btnPlanners.alpha = 0.6f
            btnVendors.alpha = 0.6f
            btnBudget.alpha = 0.6f

            btnPlanners.setOnClickListener {
                showToast("Please complete your couple profile first!")
            }
            btnVendors.setOnClickListener {
                showToast("Please complete your couple profile first!")
            }
            btnBudget.setOnClickListener {
                showToast("Please complete your couple profile first!")
            }
        } else {
            btnPlanners.alpha = 1.0f
            btnVendors.alpha = 1.0f
            btnBudget.alpha = 1.0f
            setupClickListeners()
        }
    }

    private fun checkIfProfileExists(): Boolean {
        val sharedPreferences = getSharedPreferences("couple_profile_prefs", MODE_PRIVATE)
        return !sharedPreferences.getString("profile_data", null).isNullOrEmpty()
    }
}