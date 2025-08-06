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

// select wedding planners
class WeddingPlannersActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WeddingPlannersActivity"
        const val RESULT_PLANNER_SELECTED = 100
        const val EXTRA_SELECTED_PLANNER = "selected_planner"
        const val EXTRA_PLANNER_FOR_PROFILE = "planner_for_profile"
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

    // Setup RecyclerView with adapter
    private fun setupRecyclerView() {
        plannerAdapter = WeddingPlannerAdapter(
            context = this,
            planners = plannersList,
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


    private fun loadWeddingPlanners() {
        showLoading(true)

        firestore.collection("wedding_planners")
            .orderBy("rating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Successfully loaded ${documents.size()} planners")

                plannersList.clear()

                for (document in documents) {
                    try {
                        val planner = document.toObject(WeddingPlanner::class.java)
                        planner.copy(id = document.id) // Ensure ID is set
                        plannersList.add(planner)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing planner document: ${document.id}", e)
                    }
                }

                updateUI()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading planners", exception)
                showError("Failed to load wedding planners. Please try again.")
                updateUI()
            }
    }


    private fun updateUI() {
        showLoading(false)

        if (plannersList.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            plannerAdapter.updatePlanners(plannersList)
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


    private fun selectPlanner(planner: WeddingPlanner) {
        Log.d(TAG, "Planner selected: ${planner.name}")


        val currentUser = auth.currentUser
        if (currentUser == null) {
            showError("Please login to select a planner")
            return
        }


        showLoading(true)

        //  check  user has a selected planner
        firestore.collection("selected_planners")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {

                    val existingDoc = documents.documents[0]
                    updateExistingSelection(existingDoc.id, planner)
                } else {

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
            "status" to "selected",
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

    // Handle successful selection
    private fun handleSelectionSuccess(planner: WeddingPlanner, selectionId: String) {
        showLoading(false)


        Toast.makeText(
            this,
            "Successfully selected ${planner.name}!",
            Toast.LENGTH_SHORT
        ).show()


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

    // View planner profile in detail
    private fun viewPlannerProfile(planner: WeddingPlanner) {
        Log.d(TAG, "Viewing profile for: ${planner.name}")

        try {
            val intent = Intent(this, PlannerProfileBridge::class.java).apply {
                putExtra(EXTRA_PLANNER_FOR_PROFILE, planner)
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