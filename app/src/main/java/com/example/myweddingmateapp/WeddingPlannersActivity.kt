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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


// select wedding planners

class WeddingPlannersActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WeddingPlannersActivity"
        const val RESULT_PLANNER_SELECTED = 100
        const val EXTRA_SELECTED_PLANNER = "selected_planner"
        const val EXTRA_PLANNER_FOR_PROFILE = "planner_for_profile"
    }

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerViewPlanners: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var emptyView: LinearLayout

    // Adapter and data
    private lateinit var plannerAdapter: WeddingPlannerAdapter
    private val plannersList = mutableListOf<WeddingPlanner>()

    // Firebase
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wedding_planners)

        initializeViews()
        setupToolbar()
        setupRecyclerView()
        initializeFirebase()
        loadWeddingPlanners()
    }


//     Initialize view

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerViewPlanners = findViewById(R.id.recyclerViewPlanners)
        progressIndicator = findViewById(R.id.progressIndicator)
        emptyView = findViewById(R.id.emptyView)
    }


//     Setup toolbar with back navigation

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


//      Setup RecyclerView with adapter

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


//     Initialize Firebase Firestore

    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
    }


//      Load wedding planners from Firebase

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


//      Update UI based on data state

    private fun updateUI() {
        showLoading(false)

        if (plannersList.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            plannerAdapter.updatePlanners(plannersList)
        }
    }


//      Show/hide loading indicator

    private fun showLoading(show: Boolean) {
        progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }


//      Show/hide empty state

    private fun showEmptyState(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewPlanners.visibility = if (show) View.GONE else View.VISIBLE
    }


//      Handle planner selection

    private fun selectPlanner(planner: WeddingPlanner) {
        Log.d(TAG, "Planner selected: ${planner.name}")

        // Create result intent
        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_PLANNER, planner)
        }

        // Set result and finish activity
        setResult(RESULT_PLANNER_SELECTED, resultIntent)
        finish()

        // Add exit animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


//     View planner profile in detail

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


//      back btn press

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


//      Refresh planners list

    private fun refreshPlanners() {
        loadWeddingPlanners()
    }


//      Filter planners by availability

    private fun filterAvailablePlanners() {
        plannerAdapter.filterByAvailability(true)
    }


//      Sort planners by rating

    private fun sortPlannersByRating() {
        plannerAdapter.sortByRating()
    }


//     by experience

    private fun sortPlannersByExperience() {
        plannerAdapter.sortByExperience()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WeddingPlannersActivity destroyed")
    }
}