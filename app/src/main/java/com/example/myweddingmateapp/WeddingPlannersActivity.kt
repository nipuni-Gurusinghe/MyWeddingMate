package com.example.myweddingmateapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.adapters.WeddingPlannerAdapter
import com.example.myweddingmateapp.models.WeddingPlanner
import com.example.myweddingmateapp.repository.PlannerProfileBridge
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class WeddingPlannersActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WeddingPlannersActivity"
        const val RESULT_PLANNER_SELECTED = 100
        const val EXTRA_SELECTED_PLANNER = "selected_planner"
        const val EXTRA_PLANNER_FOR_PROFILE = "planner_for_profile"
        private const val ROLE_WEDDING_PLANNER = "Wedding Planner"
    }

    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerViewPlanners: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var emptyView: LinearLayout

    private lateinit var plannerAdapter: WeddingPlannerAdapter
    private val plannersList = mutableListOf<WeddingPlanner>()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wedding_planners)

        initializeViews()
        setupToolbar()
        setupRecyclerView()
        initializeFirebase()
        loadWeddingPlanners()
    }

    // Initialize view
    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerViewPlanners = findViewById(R.id.recyclerViewPlanners)
        progressIndicator = findViewById(R.id.progressIndicator)
        emptyView = findViewById(R.id.emptyView)
    }

    // Setup toolbar with back navigation
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Wedding Planners"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // Setup RecyclerView with adapter
    private fun setupRecyclerView() {
        plannerAdapter = WeddingPlannerAdapter(
            context = this,
            onPlannerClick = { planner ->
                selectPlanner(planner)
            },
            onViewProfileClick = { planner ->
                viewPlannerProfile(planner)
            }
        )

        recyclerViewPlanners.apply {
            layoutManager = LinearLayoutManager(this@WeddingPlannersActivity)
            adapter = plannerAdapter
            setHasFixedSize(true)
        }
    }

    // Initialize Firebase Firestore and Auth
    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    // Load wedding planners from Firebase
    private fun loadWeddingPlanners() {
        showLoading(true)

        firestore.collection("users")
            .whereEqualTo("role", ROLE_WEDDING_PLANNER)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Successfully loaded ${documents.size()} wedding planners from users collection")

                plannersList.clear()

                for (document in documents) {
                    try {
                        // Convert user document to WeddingPlanner object
                        val userData = document.data
                        val planner = convertUserToWeddingPlanner(document.id, userData)
                        plannersList.add(planner)

                        Log.d(TAG, "Added planner: ${planner.name} - ${planner.email}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing user document to planner: ${document.id}", e)
                    }
                }

                // Sort planners by rating (highest first) and availability
                try {
                    plannersList.sortWith(compareByDescending<WeddingPlanner> { it.isAvailable }
                        .thenByDescending { it.rating })
                    Log.d(TAG, "After sorting: ${plannersList.size} planners")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sorting planners", e)
                }

                updateUI()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading wedding planners from users collection", exception)
                showError("Failed to load wedding planners. Please try again.")
                updateUI()
            }
    }

    // Update UI based on data state
    private fun updateUI() {
        showLoading(false)

        Log.d(TAG, "updateUI called with ${plannersList.size} planners")

        Log.d(TAG, "Planners data: ${plannersList.map { "${it.name} - ${it.email} - Available: ${it.isAvailable}" }}")

        if (plannersList.isEmpty()) {
            showEmptyState(true)
            Log.d(TAG, "Showing empty state - no planners found")
        } else {
            showEmptyState(false)
            try {
                plannerAdapter.updatePlanners(plannersList)
                Log.d(TAG, "Successfully updated adapter with ${plannersList.size} planners")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating adapter", e)
            }
        }
    }

    // Show/hide loading indicator
    private fun showLoading(show: Boolean) {
        progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }

    // Show/hide empty state
    private fun showEmptyState(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }

    // Handle planner selection and store in Firestore (ONE planner per user)
    private fun selectPlanner(planner: WeddingPlanner) {
        Log.d(TAG, "Planner selected: ${planner.name} (ID: ${planner.id})")

        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showError("Please login to select a planner")
            return
        }

        // Show loading while saving
        showLoading(true)

        // First, check if user already has a selected planner
        firestore.collection("selected_planners")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // User already has a selection - UPDATE it
                    val existingDoc = documents.documents[0] // Get the first (should be only) document
                    updateExistingSelection(existingDoc.id, planner)
                } else {
                    // User has no selection - CREATE new one
                    createNewSelection(planner)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking existing selection", exception)
                showLoading(false)
                showError("Failed to check existing selection. Please try again.")
            }
    }

    // Create new selection for user
    private fun createNewSelection(planner: WeddingPlanner) {
        val currentUser = auth.currentUser ?: return

        val selectionData = hashMapOf(
            "userId" to currentUser.uid,
            "plannerId" to planner.id,
            "plannerName" to planner.name,
            "plannerEmail" to planner.email,
            "plannerPhone" to planner.phone,
            "plannerLocation" to planner.location,
            "plannerRating" to planner.rating,
            "plannerPriceRange" to planner.priceRange,
            "selectedAt" to Date(),
            "status" to "selected",
            "plannerData" to planner
        )

        firestore.collection("selected_planners")
            .add(selectionData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "New planner selection created with ID: ${documentReference.id}")
                handleSelectionSuccess(planner, documentReference.id)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error creating planner selection", exception)
                showLoading(false)
                showError("Failed to select planner. Please try again.")
            }
    }

    // Update existing selection with new planner
    private fun updateExistingSelection(documentId: String, planner: WeddingPlanner) {
        val updateData = hashMapOf<String, Any>(
            "plannerId" to planner.id,
            "plannerName" to planner.name,
            "plannerEmail" to planner.email,
            "plannerPhone" to planner.phone,
            "plannerLocation" to planner.location,
            "plannerRating" to planner.rating,
            "plannerPriceRange" to planner.priceRange,
            "updatedAt" to Date(),
            "status" to "selected", // Reset to selected if it was confirmed/cancelled
            "plannerData" to planner
        )

        firestore.collection("selected_planners")
            .document(documentId)
            .update(updateData)
            .addOnSuccessListener {
                Log.d(TAG, "Existing planner selection updated with ID: $documentId")
                handleSelectionSuccess(planner, documentId)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating planner selection", exception)
                showLoading(false)
                showError("Failed to update planner selection. Please try again.")
            }
    }

    // Handle successful selection (common for both create and update)
    private fun handleSelectionSuccess(planner: WeddingPlanner, selectionId: String) {
        showLoading(false)

        // Show success message
        Toast.makeText(
            this,
            "Successfully selected ${planner.name}!",
            Toast.LENGTH_SHORT
        ).show()

        // Create result intent
        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_PLANNER, planner)
            putExtra("selection_id", selectionId)
        }

        setResult(RESULT_PLANNER_SELECTED, resultIntent)
        finish()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Check if user already has a selected planner and get details
    private fun getUserSelectedPlanner(callback: (WeddingPlanner?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(null, null)
            return
        }

        firestore.collection("selected_planners")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val plannerData = document.get("plannerData") as? WeddingPlanner
                    callback(plannerData, document.id)
                } else {
                    callback(null, null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user's selected planner", exception)
                callback(null, null)
            }
    }

    // View  profile
    private fun viewPlannerProfile(planner: WeddingPlanner) {
        Log.d(TAG, "Viewing profile for: ${planner.name} (ID: ${planner.id})")

        try {
            val intent = Intent(this, PlannerProfileBridge::class.java).apply {
                putExtra(EXTRA_PLANNER_FOR_PROFILE, planner)
                putExtra("planner_user_id", planner.id) // Pass the user ID
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening planner profile", e)
            showError("Unable to view planner profile at the moment.")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // back btn
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun refreshPlanners() {
        loadWeddingPlanners()
    }


    private fun filterAvailablePlanners() {
        plannerAdapter.filterByAvailability(true)
    }


    private fun sortPlannersByRating() {
        plannerAdapter.sortByRating()
    }


    private fun sortPlannersByExperience() {
        plannerAdapter.sortByExperience()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WeddingPlannersActivity destroyed")
    }
}