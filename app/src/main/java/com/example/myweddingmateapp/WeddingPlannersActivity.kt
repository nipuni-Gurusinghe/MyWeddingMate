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

// Select wedding planners from users collection
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

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerViewPlanners = findViewById(R.id.recyclerViewPlanners)
        progressIndicator = findViewById(R.id.progressIndicator)
        emptyView = findViewById(R.id.emptyView)
    }

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

    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    // Load wedding planners from users collection with role "Wedding Planner"
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

    // Convert user document to WeddingPlanner object
    private fun convertUserToWeddingPlanner(userId: String, userData: Map<String, Any>): WeddingPlanner {
        val planner = WeddingPlanner(
            id = userId,
            name = userData["name"] as? String ?: "Unknown Planner",
            email = userData["email"] as? String ?: "",
            phone = userData["phone"] as? String ?: "",
            location = userData["location"] as? String ?: "Location not specified",
            bio = userData["bio"] as? String ?: "Professional wedding planner dedicated to making your special day perfect.",
            experience = (userData["experience"] as? Long)?.toInt() ?: 0,
            specialties = userData["specialties"] as? List<String> ?: listOf("Wedding Planning"),
            priceRange = userData["priceRange"] as? String ?: "Contact for quote",
            rating = (userData["rating"] as? Double) ?: 4.0,
            reviewCount = (userData["reviewCount"] as? Long)?.toInt() ?: 0,
            isAvailable = userData["isAvailable"] as? Boolean ?: true,
            profileImageUrl = userData["profileImageUrl"] as? String ?: "",
            portfolioImages = userData["portfolioImages"] as? List<String> ?: emptyList(),
            services = userData["services"] as? List<String> ?: listOf("Full Wedding Planning"),
            website = userData["website"] as? String ?: "",
            socialMedia = userData["socialMedia"] as? Map<String, String> ?: emptyMap()
        )

        Log.d(TAG, "Converted user to planner: ${planner.name}, Available: ${planner.isAvailable}, Rating: ${planner.rating}")
        return planner
    }

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

    private fun showLoading(show: Boolean) {
        progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }

    // Select planner and store in user's document
    private fun selectPlanner(planner: WeddingPlanner) {
        Log.d(TAG, "Planner selected: ${planner.name} (ID: ${planner.id})")

        val currentUser = auth.currentUser
        if (currentUser == null) {
            showError("Please login to select a planner")
            return
        }

        // Check if planner is available
        if (!planner.isAvailable) {
            showError("${planner.name} is currently not available")
            return
        }

        showLoading(true)

        // Update the user's document with the selected planner ID
        val userDocRef = firestore.collection("users").document(currentUser.uid)

        val updateData = hashMapOf<String, Any>(
            "selectedPlannerId" to planner.id,
            "selectedPlannerName" to planner.name,
            "selectedPlannerEmail" to planner.email,
            "selectedPlannerUpdatedAt" to Date()
        )

        userDocRef.update(updateData)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully updated user with selected planner: ${planner.id}")
                handleSelectionSuccess(planner)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating user with selected planner", exception)
                showLoading(false)
                showError("Failed to select planner. Please try again.")
            }
    }

    // Handle successful planner selection (simplified version)
    private fun handleSelectionSuccess(planner: WeddingPlanner) {
        showLoading(false)

        Toast.makeText(
            this,
            "Successfully selected ${planner.name}!",
            Toast.LENGTH_SHORT
        ).show()

        // Return result to calling activity
        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_PLANNER, planner)
            putExtra("planner_id", planner.id)
        }

        setResult(RESULT_PLANNER_SELECTED, resultIntent)
        finish()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Check if user already has a selected planner (updated version)
    private fun getUserSelectedPlanner(callback: (WeddingPlanner?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(null)
            return
        }

        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val selectedPlannerId = document.getString("selectedPlannerId")
                    if (!selectedPlannerId.isNullOrEmpty()) {
                        // Load the planner details from users collection
                        firestore.collection("users").document(selectedPlannerId)
                            .get()
                            .addOnSuccessListener { plannerDoc ->
                                try {
                                    val planner = convertUserToWeddingPlanner(plannerDoc.id, plannerDoc.data!!)
                                    callback(planner)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error converting planner data", e)
                                    callback(null)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error loading selected planner details", exception)
                                callback(null)
                            }
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user's selected planner", exception)
                callback(null)
            }
    }

    // Create new planner selection
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
            "plannerData" to planner,
            // Additional metadata
            "createdAt" to Date(),
            "isActive" to true
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

    // Update existing planner selection
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
            "status" to "selected",
            "plannerData" to planner,
            "isActive" to true
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

    // Handle successful planner selection
    private fun handleSelectionSuccess(planner: WeddingPlanner, selectionId: String) {
        showLoading(false)

        Toast.makeText(
            this,
            "Successfully selected ${planner.name}!",
            Toast.LENGTH_SHORT
        ).show()

        // Return result to calling activity
        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_PLANNER, planner)
            putExtra("selection_id", selectionId)
            putExtra("planner_id", planner.id)
        }

        setResult(RESULT_PLANNER_SELECTED, resultIntent)
        finish()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Check if user already has a selected planner
    private fun getUserSelectedPlanner(callback: (WeddingPlanner?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(null, null)
            return
        }

        firestore.collection("selected_planners")
            .whereEqualTo("userId", currentUser.uid)
            .whereEqualTo("isActive", true)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val plannerData = document.get("plannerData")

                    // Try to convert back to WeddingPlanner object
                    val planner = try {
                        if (plannerData is HashMap<*, *>) {
                            convertHashMapToWeddingPlanner(plannerData)
                        } else {
                            plannerData as? WeddingPlanner
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting planner data", e)
                        null
                    }

                    callback(planner, document.id)
                } else {
                    callback(null, null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user's selected planner", exception)
                callback(null, null)
            }
    }

    // Convert HashMap back to WeddingPlanner (in case Firestore returns HashMap)
    private fun convertHashMapToWeddingPlanner(data: HashMap<*, *>): WeddingPlanner {
        return WeddingPlanner(
            id = data["id"] as? String ?: "",
            name = data["name"] as? String ?: "",
            email = data["email"] as? String ?: "",
            phone = data["phone"] as? String ?: "",
            location = data["location"] as? String ?: "",
            bio = data["bio"] as? String ?: "",
            experience = (data["experience"] as? Long)?.toInt() ?: 0,
            specialties = data["specialties"] as? List<String> ?: emptyList(),
            priceRange = data["priceRange"] as? String ?: "",
            rating = data["rating"] as? Double ?: 0.0,
            reviewCount = (data["reviewCount"] as? Long)?.toInt() ?: 0,
            isAvailable = data["isAvailable"] as? Boolean ?: true,
            profileImageUrl = data["profileImageUrl"] as? String ?: "",
            portfolioImages = data["portfolioImages"] as? List<String> ?: emptyList(),
            services = data["services"] as? List<String> ?: emptyList(),
            website = data["website"] as? String ?: "",
            socialMedia = data["socialMedia"] as? Map<String, String> ?: emptyMap()
        )
    }

    // View planner profile in detail .............
    private fun viewPlannerProfile(planner: WeddingPlanner) {
        Log.d(TAG, "Viewing profile for: ${planner.name} (ID: ${planner.id})")

        try {
            // Start PlannerProfileHostActivity to load PlannerProfileFragment
            val intent = Intent(this, PlannerProfileHostActivity::class.java).apply {
                putExtra(EXTRA_PLANNER_FOR_PROFILE, planner)
                putExtra("planner_user_id", planner.id)
                putExtra("planner_name", planner.name)
                putExtra("planner_email", planner.email)
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Utility methods for filtering and sorting
    private fun refreshPlanners() {
        loadWeddingPlanners()
    }

//    private fun filterAvailablePlanners() {
//        plannerAdapter.filterByAvailability(true)
//    }
//
//    private fun sortPlannersByRating() {
//        plannerAdapter.sortByRating()
//    }
//
//    private fun sortPlannersByExperience() {
//        plannerAdapter.sortByExperience()
//    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WeddingPlannersActivity destroyed")
    }
}