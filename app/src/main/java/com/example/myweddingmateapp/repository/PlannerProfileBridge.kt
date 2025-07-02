package com.example.myweddingmateapp.repository
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myweddingmateapp.models.WeddingPlanner
import com.example.myweddingmateapp.R
import com.example.weddingplan3.WeddingPlannersActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Bridge Activity to display detailed wedding planner profile
 * This bridges the gap between planner selection and the existing ProfileFragment structure
 */
class PlannerProfileBridge : AppCompatActivity() {

    companion object {
        private const val TAG = "PlannerProfileBridge"
        const val RESULT_PLANNER_SELECTED = 200
        const val EXTRA_SELECTED_PLANNER = "selected_planner_bridge"
    }

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var profileImage: ImageView
    private lateinit var plannerName: TextView
    private lateinit var plannerRating: TextView
    private lateinit var plannerLocation: TextView
    private lateinit var plannerExperience: TextView
    private lateinit var plannerPriceRange: TextView
    private lateinit var plannerBio: TextView
    private lateinit var plannerPhone: TextView
    private lateinit var plannerEmail: TextView
    private lateinit var availabilityBadge: TextView
    private lateinit var specialtiesChipGroup: ChipGroup
    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var portfolioRecyclerView: RecyclerView
    private lateinit var btnSelectPlanner: Button
    private lateinit var btnCallPlanner: Button
    private lateinit var btnEmailPlanner: Button

    // Data
    private lateinit var currentPlanner: WeddingPlanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner_profile_bridge)

        // Get planner data from intent
        currentPlanner = intent.getParcelableExtra(WeddingPlannersActivity.EXTRA_PLANNER_FOR_PROFILE)
            ?: run {
                Log.e(TAG, "No planner data received")
                finish()
                return
            }

        initializeViews()
        setupToolbar()
        setupPlannerData()
        setupClickListeners()
    }

    /**
     * Initialize all views
     */
    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        profileImage = findViewById(R.id.imgPlannerProfileLarge)
        plannerName = findViewById(R.id.tvPlannerNameLarge)
        plannerRating = findViewById(R.id.tvPlannerRatingLarge)
        plannerLocation = findViewById(R.id.tvPlannerLocationLarge)
        plannerExperience = findViewById(R.id.tvPlannerExperienceLarge)
        plannerPriceRange = findViewById(R.id.tvPlannerPriceRangeLarge)
        plannerBio = findViewById(R.id.tvPlannerBioLarge)
        plannerPhone = findViewById(R.id.tvPlannerPhone)
        plannerEmail = findViewById(R.id.tvPlannerEmail)
        availabilityBadge = findViewById(R.id.tvAvailabilityBadgeLarge)
        specialtiesChipGroup = findViewById(R.id.chipGroupSpecialties)
        servicesRecyclerView = findViewById(R.id.recyclerViewServices)
        portfolioRecyclerView = findViewById(R.id.recyclerViewPortfolio)
        btnSelectPlanner = findViewById(R.id.btnSelectPlannerLarge)
        btnCallPlanner = findViewById(R.id.btnCallPlanner)
        btnEmailPlanner = findViewById(R.id.btnEmailPlanner)
    }

    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Planner Profile"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Setup all planner data in views
     */
    private fun setupPlannerData() {
        // Basic info
        plannerName.text = currentPlanner.name
        plannerRating.text = "${currentPlanner.getRatingText()} ${currentPlanner.getReviewText()}"
        plannerLocation.text = currentPlanner.location.ifEmpty { "Location not specified" }
        plannerExperience.text = currentPlanner.getExperienceText()

        // Price range
        plannerPriceRange.text = if (currentPlanner.priceRange.isNotEmpty()) {
            "Budget Range: ${currentPlanner.priceRange}"
        } else {
            "Contact for pricing details"
        }

        // Bio
        plannerBio.text = if (currentPlanner.bio.isNotEmpty()) {
            currentPlanner.bio
        } else {
            "Professional wedding planner dedicated to making your special day perfect. With years of experience in creating memorable weddings, I work closely with couples to bring their vision to life."
        }

        // Contact info
        plannerPhone.text = if (currentPlanner.phone.isNotEmpty()) {
            "📞 ${currentPlanner.phone}"
        } else {
            "📞 Contact via platform"
        }

        plannerEmail.text = if (currentPlanner.email.isNotEmpty()) {
            "✉️ ${currentPlanner.email}"
        } else {
            "✉️ Contact via platform"
        }

        // Availability
        availabilityBadge.text = currentPlanner.getAvailabilityText()
        availabilityBadge.setBackgroundResource(
            if (currentPlanner.isAvailable) R.drawable.bg_available_badge
            else R.drawable.bg_unavailable_badge
        )

        // Load profile image
        loadProfileImage()

        // Setup specialties
        setupSpecialties()

        // Setup services
        setupServices()

        // Setup portfolio (if available)
        setupPortfolio()

        // Update button states
        updateButtonStates()
    }

    /**
     * Load planner profile image
     */
    private fun loadProfileImage() {
        if (currentPlanner.profileImageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(currentPlanner.profileImageUrl)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.ic_person)
        }
    }

    /**
     * Setup specialties chips
     */
    private fun setupSpecialties() {
        specialtiesChipGroup.removeAllViews()

        val specialties = if (currentPlanner.specialties.isNotEmpty()) {
            currentPlanner.specialties
        } else {
            listOf("Wedding Planning", "Event Coordination", "Venue Selection")
        }

        specialties.forEach { specialty ->
            val chip = Chip(this).apply {
                text = specialty
                isClickable = false
                setChipBackgroundColorResource(R.color.chip_background)
                setTextColor(getColor(R.color.chip_text))
            }
            specialtiesChipGroup.addView(chip)
        }
    }

    /**
     * Setup services list
     */
    private fun setupServices() {
        val services = if (currentPlanner.services.isNotEmpty()) {
            currentPlanner.services
        } else {
            listOf(
                "Full wedding planning and coordination",
                "Vendor selection and management",
                "Timeline and budget management",
                "Day-of coordination",
                "Venue scouting and selection"
            )
        }

        val servicesAdapter = SimpleListAdapter(services)
        servicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlannerProfileBridge)
            adapter = servicesAdapter
            isNestedScrollingEnabled = false
        }
    }

    /**
     * Setup portfolio images
     */
    private fun setupPortfolio() {
        if (currentPlanner.portfolioImages.isNotEmpty()) {
            val portfolioAdapter = PortfolioAdapter(currentPlanner.portfolioImages)
            portfolioRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@PlannerProfileBridge, LinearLayoutManager.HORIZONTAL, false)
                adapter = portfolioAdapter
                visibility = View.VISIBLE
            }
        } else {
            portfolioRecyclerView.visibility = View.GONE
        }
    }

    /**
     * Update button states based on availability
     */
    private fun updateButtonStates() {
        btnSelectPlanner.isEnabled = currentPlanner.isAvailable
        btnSelectPlanner.alpha = if (currentPlanner.isAvailable) 1.0f else 0.6f

        // Enable contact buttons only if contact info is available
        btnCallPlanner.isEnabled = currentPlanner.phone.isNotEmpty()
        btnEmailPlanner.isEnabled = currentPlanner.email.isNotEmpty()
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        // Select planner button
        btnSelectPlanner.setOnClickListener {
            if (currentPlanner.isAvailable) {
                selectPlanner()
            } else {
                Toast.makeText(this, "This planner is currently unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        // Call planner button
        btnCallPlanner.setOnClickListener {
            if (currentPlanner.phone.isNotEmpty()) {
                callPlanner()
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Email planner button
        btnEmailPlanner.setOnClickListener {
            if (currentPlanner.email.isNotEmpty()) {
                emailPlanner()
            } else {
                Toast.makeText(this, "Email address not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Select this planner and return to home
     */
    private fun selectPlanner() {
        Log.d(TAG, "Selecting planner: ${currentPlanner.name}")

        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_PLANNER, currentPlanner)
        }

        setResult(RESULT_PLANNER_SELECTED, resultIntent)

        // Show confirmation
        Toast.makeText(this, "${currentPlanner.name} selected as your wedding planner!", Toast.LENGTH_LONG).show()

        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /**
     * Call the planner
     */
    private fun callPlanner() {
        try {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:${currentPlanner.phone}")
            }
            startActivity(callIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error making phone call", e)
            Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Email the planner
     */
    private fun emailPlanner() {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:${currentPlanner.email}")
                putExtra(Intent.EXTRA_SUBJECT, "Wedding Planning Inquiry")
                putExtra(Intent.EXTRA_TEXT, "Hi ${currentPlanner.name},\n\nI'm interested in your wedding planning services. Could we schedule a consultation?\n\nBest regards")
            }
            startActivity(emailIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending email", e)
            Toast.makeText(this, "Unable to send email", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /**
     * Simple adapter for services list
     */
    private inner class SimpleListAdapter(private val items: List<String>) :
        RecyclerView.Adapter<SimpleListAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.tvServiceItem)
            val bulletPoint: TextView = itemView.findViewById(R.id.tvBulletPoint)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_service_simple, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = items[position]
            holder.bulletPoint.text = "•"
        }

        override fun getItemCount(): Int = items.size
    }

    /**
     * Portfolio adapter for image gallery
     */
    private inner class PortfolioAdapter(private val images: List<String>) :
        RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imgPortfolio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_portfolio_image, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(this@PlannerProfileBridge)
                .load(images[position])
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(holder.imageView)

            holder.imageView.setOnClickListener {
                // TODO: Open full-screen image viewer
                Toast.makeText(this@PlannerProfileBridge, "Portfolio image ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = images.size
    }
}