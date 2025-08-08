package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BudgetOverviewActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BudgetOverviewActivity"
        private const val FAVORITES_COLLECTION = "userFavorites"
    }

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var budgetContainer: LinearLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Budget categories with estimated costs (you can modify these values)
    private val categoryBudgets = mapOf(
        "bridalWear" to 50000.0,
        "groomWear" to 30000.0,
        "venue" to 200000.0,
        "photography" to 75000.0,
        "beauticianBride" to 25000.0,
        "beauticianGroom" to 15000.0,
        "jewellery" to 100000.0,
        "entertainment" to 50000.0,
        "floral" to 30000.0,
        "invitation" to 20000.0,
        "weddingCar" to 40000.0
    )

    private val categoryDisplayNames = mapOf(
        "bridalWear" to "Bridal Wear",
        "groomWear" to "Groom Wear",
        "venue" to "Venue",
        "photography" to "Photography",
        "beauticianBride" to "Bridal Beauty",
        "beauticianGroom" to "Groom Beauty",
        "jewellery" to "Jewellery",
        "entertainment" to "Entertainment",
        "floral" to "Floral Arrangements",
        "invitation" to "Invitations",
        "weddingCar" to "Wedding Car"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_view)

        Log.d(TAG, "BudgetOverviewActivity created successfully")

        // Initialize Firebase
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        initializeViews()
        setupClickListeners()
        setupContent()
        loadBudgetData()
    }

    private fun initializeViews() {
        try {
            btnBack = findViewById(R.id.btn_back)
            tvTitle = findViewById(R.id.tv_title)
            budgetContainer = findViewById(R.id.budget)

            Log.d(TAG, "Views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            createFallbackLayout()
        }
    }

    private fun setupClickListeners() {
        try {
            btnBack.setOnClickListener {
                Log.d(TAG, "Back button clicked")
                onBackPressed()
            }

            Log.d(TAG, "Click listeners setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners", e)
        }
    }

    private fun setupContent() {
        try {
            tvTitle.text = "Budget Overview"
            Log.d(TAG, "Content setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up content", e)
        }
    }

    private fun loadBudgetData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated")
            showError("Please log in to view budget")
            return
        }

        val userId = currentUser.uid
        Log.d(TAG, "Loading budget data for user: $userId")

        // Show loading state
        showLoadingState()

        db.collection(FAVORITES_COLLECTION)
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Successfully loaded ${documents.size()} favorite items")

                // Group favorites by category
                val favoritesByCategory = mutableMapOf<String, Int>()

                for (document in documents) {
                    val category = document.getString("category") ?: continue
                    favoritesByCategory[category] = favoritesByCategory.getOrDefault(category, 0) + 1
                }

                displayBudgetData(favoritesByCategory)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading budget data", exception)
                showError("Failed to load budget data")
                showEmptyState()
            }
    }

    private fun showLoadingState() {
        budgetContainer.removeAllViews()
        val loadingView = TextView(this).apply {
            text = "Loading budget data..."
            textSize = 16f
            setPadding(16, 32, 16, 32)
            gravity = android.view.Gravity.CENTER
        }
        budgetContainer.addView(loadingView)
    }

    private fun displayBudgetData(favoritesByCategory: Map<String, Int>) {
        budgetContainer.removeAllViews()

        if (favoritesByCategory.isEmpty()) {
            showEmptyState()
            return
        }

        var totalBudget = 0.0

        // Create budget items for categories with favorites
        for ((category, itemCount) in favoritesByCategory) {
            val budgetAmount = categoryBudgets[category] ?: 0.0
            val categoryTotal = budgetAmount * itemCount
            totalBudget += categoryTotal

            val budgetItemView = createBudgetItemView(category, itemCount, categoryTotal)
            budgetContainer.addView(budgetItemView)
        }

        // Add total budget summary
        val totalView = createTotalBudgetView(totalBudget)
        budgetContainer.addView(totalView)

        Log.d(TAG, "Budget display completed. Total: $totalBudget")
    }

    private fun createBudgetItemView(category: String, itemCount: Int, amount: Double): View {
        val cardView = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            cardElevation = 2f
            radius = 8f
            setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        val containerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Category name and item count
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val categoryName = TextView(this).apply {
            text = categoryDisplayNames[category] ?: category.capitalize()
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val itemCountText = TextView(this).apply {
            text = "$itemCount item${if (itemCount > 1) "s" else ""}"
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }

        headerLayout.addView(categoryName)
        headerLayout.addView(itemCountText)

        // Budget amount
        val amountText = TextView(this).apply {
            text = "Rs. ${String.format("%,.2f", amount)}"
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.primary_brown))
            setPadding(0, 8, 0, 0)
        }

        containerLayout.addView(headerLayout)
        containerLayout.addView(amountText)
        cardView.addView(containerLayout)

        return cardView
    }

    private fun createTotalBudgetView(totalAmount: Double): View {
        val cardView = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            cardElevation = 4f
            radius = 12f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_brown))
        }

        val containerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }

        val titleText = TextView(this).apply {
            text = "Total Estimated Budget"
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            gravity = android.view.Gravity.CENTER
        }

        val amountText = TextView(this).apply {
            text = "Rs. ${String.format("%,.2f", totalAmount)}"
            textSize = 24f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 8, 0, 0)
        }

        containerLayout.addView(titleText)
        containerLayout.addView(amountText)
        cardView.addView(containerLayout)

        return cardView
    }

    private fun showEmptyState() {
        budgetContainer.removeAllViews()

        val emptyStateLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(32, 64, 32, 64)
        }

        val emptyIcon = TextView(this).apply {
            text = "💰"
            textSize = 48f
            gravity = android.view.Gravity.CENTER
        }

        val emptyTitle = TextView(this).apply {
            text = "No Budget Items"
            textSize = 20f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 16, 0, 8)
        }

        val emptyMessage = TextView(this).apply {
            text = "Add some wedding items to your favorites to see your estimated budget here!"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            gravity = android.view.Gravity.CENTER
        }

        emptyStateLayout.addView(emptyIcon)
        emptyStateLayout.addView(emptyTitle)
        emptyStateLayout.addView(emptyMessage)
        budgetContainer.addView(emptyStateLayout)
    }

    private fun createFallbackLayout() {
        try {
            val textView = TextView(this).apply {
                text = "Budget Overview\n\nThis feature is coming soon!"
                textSize = 18f
                setPadding(32, 32, 32, 32)
            }
            setContentView(textView)
            Log.d(TAG, "Fallback layout created")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating fallback layout", e)
        }
    }

    private fun showError(message: String) {
        try {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing error message", e)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        Log.d(TAG, "Back pressed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BudgetOverviewActivity destroyed")
    }
}