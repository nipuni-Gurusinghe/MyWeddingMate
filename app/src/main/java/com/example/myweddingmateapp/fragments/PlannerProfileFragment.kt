package com.example.myweddingmateapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.LoginActivity
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Review
import com.example.myweddingmateapp.models.Service
import com.example.myweddingmateapp.models.User
import com.example.myweddingmateapp.utils.DatabaseHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PlannerProfileFragment : Fragment() {
    private lateinit var btnAddReview: Button
    private lateinit var btnAddService: Button
    private lateinit var btnAddPortfolioItem: Button
    private lateinit var btnEditPicture: ImageButton
    private lateinit var btnUpdate: Button
    private lateinit var btnSignOut: Button
    private lateinit var btnAddSpecialty: Button
    private lateinit var specialtiesChipGroup: ChipGroup
    private lateinit var availabilityChipGroup: ChipGroup
    private lateinit var profilePicture: ImageView
    private lateinit var editName: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editLocation: TextInputEditText
    private lateinit var editCompany: TextInputEditText
    private lateinit var editYearsExperience: TextInputEditText
    private lateinit var editPriceRange: TextInputEditText
    private lateinit var editBio: TextInputEditText
    private lateinit var editInstagram: TextInputEditText
    private lateinit var editFacebook: TextInputEditText
    private lateinit var editWebsite: TextInputEditText
    private lateinit var portfolioContainer: LinearLayout
    private lateinit var reviewsContainer: LinearLayout
    private lateinit var servicesContainer: LinearLayout

    private var isPlanner: Boolean = false
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_planner_profile, container, false)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                profilePicture.setImageBitmap(selectedImageBitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            selectedImageBitmap = bitmap
            profilePicture.setImageBitmap(bitmap)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHelper = DatabaseHelper(requireContext())
        initViews(view)
        loadUserData()
        setupButtons()
    }

    private fun initViews(view: View) {
        btnAddReview = view.findViewById(R.id.btnAddReview)
        btnAddService = view.findViewById(R.id.btnAddService)
        btnAddPortfolioItem = view.findViewById(R.id.btnAddPortfolioItem)
        btnEditPicture = view.findViewById(R.id.btnEditPicture)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnAddSpecialty = view.findViewById(R.id.btnAddSpecialty)
        btnSignOut = view.findViewById(R.id.btnSignOut)
        specialtiesChipGroup = view.findViewById(R.id.specialtiesChipGroup)
        availabilityChipGroup = view.findViewById(R.id.availabilityChipGroup)
        profilePicture = view.findViewById(R.id.profilePicture)
        editName = view.findViewById(R.id.editName)
        editEmail = view.findViewById(R.id.editEmail)
        editPhone = view.findViewById(R.id.editPhone)
        editLocation = view.findViewById(R.id.editLocation)
        editCompany = view.findViewById(R.id.editCompany)
        editYearsExperience = view.findViewById(R.id.editYearsExperience)
        editPriceRange = view.findViewById(R.id.editPriceRange)
        editBio = view.findViewById(R.id.editBio)
        editInstagram = view.findViewById(R.id.editInstagram)
        editFacebook = view.findViewById(R.id.editFacebook)
        editWebsite = view.findViewById(R.id.editWebsite)
        portfolioContainer = view.findViewById(R.id.portfolioContainer)
        reviewsContainer = view.findViewById(R.id.reviewsContainer)
        servicesContainer = view.findViewById(R.id.servicesContainer)
    }

    private fun loadUserData() {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    isPlanner = document.getString("role") == "Wedding Planner"
                    updateUIForUserRole()
                    document.toObject(User::class.java)?.let { user ->
                        populateUserData(user)
                    }
                }
        }
    }

    private fun populateUserData(user: User) {
        editName.setText(user.name)
        editEmail.setText(user.email)
        editPhone.setText(user.phoneNumber)
        editLocation.setText(user.location)
        editCompany.setText(user.company)
        editYearsExperience.setText(user.yearsExperience?.toString())
        editPriceRange.setText(user.priceRange)
        editBio.setText(user.bio)
        editInstagram.setText(user.instagram)
        editFacebook.setText(user.facebook)
        editWebsite.setText(user.website)

        specialtiesChipGroup.removeAllViews()
        user.specialties?.forEach { addSpecialtyChip(it) }

        when (user.availability) {
            "Available" -> availabilityChipGroup.check(R.id.chipAvailable)
            "Limited Availability" -> availabilityChipGroup.check(R.id.chipLimited)
            "Currently Unavailable" -> availabilityChipGroup.check(R.id.chipUnavailable)
        }
    }

    private fun updateUIForUserRole() {
        btnAddReview.visibility = if (isPlanner) View.GONE else View.VISIBLE
        btnAddService.visibility = if (isPlanner) View.VISIBLE else View.GONE
        btnAddPortfolioItem.visibility = if (isPlanner) View.VISIBLE else View.GONE

        val editableViews = listOf(
            btnEditPicture, btnUpdate, btnAddSpecialty,
            specialtiesChipGroup, availabilityChipGroup,
            editName, editEmail, editPhone, editLocation,
            editCompany, editYearsExperience, editPriceRange,
            editBio, editInstagram, editFacebook, editWebsite
        )

        editableViews.forEach { it.isEnabled = isPlanner }

        for (i in 0 until specialtiesChipGroup.childCount) {
            (specialtiesChipGroup.getChildAt(i) as? Chip)?.isCloseIconVisible = isPlanner
        }
    }

    private fun setupButtons() {
        btnAddReview.setOnClickListener {
            if (!isPlanner) showAddReviewDialog()
            else Toast.makeText(context, "Only users can add reviews", Toast.LENGTH_SHORT).show()
        }

        btnAddService.setOnClickListener {
            if (isPlanner) showAddServiceDialog()
            else Toast.makeText(context, "Only planners can add services", Toast.LENGTH_SHORT).show()
        }

        btnAddPortfolioItem.setOnClickListener {
            if (isPlanner) showAddPortfolioDialog()
            else Toast.makeText(context, "Only planners can add portfolio items", Toast.LENGTH_SHORT).show()
        }

        btnEditPicture.setOnClickListener {
            if (isPlanner) pickImage()
            else Toast.makeText(context, "Only planners can edit profile", Toast.LENGTH_SHORT).show()
        }

        btnUpdate.setOnClickListener {
            if (isPlanner) updateProfile()
            else Toast.makeText(context, "Only planners can update profile", Toast.LENGTH_SHORT).show()
        }

        btnAddSpecialty.setOnClickListener {
            if (isPlanner) showAddSpecialtyDialog()
            else Toast.makeText(context, "Only planners can add specialties", Toast.LENGTH_SHORT).show()
        }

        btnSignOut.setOnClickListener {
            signOutUser()
        }
    }

    private fun signOutUser() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                performSignOut()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performSignOut() {
        FirebaseAuth.getInstance().signOut()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun updateProfile() {
        val uid = auth.currentUser?.uid ?: return

        val specialties = mutableListOf<String>().apply {
            for (i in 0 until specialtiesChipGroup.childCount) {
                (specialtiesChipGroup.getChildAt(i) as? Chip)?.text?.toString()?.let { add(it) }
            }
        }

        val availability = when (availabilityChipGroup.checkedChipId) {
            R.id.chipAvailable -> "Available"
            R.id.chipLimited -> "Limited Availability"
            R.id.chipUnavailable -> "Currently Unavailable"
            else -> ""
        }

        val user = User(
            uid = uid,
            name = editName.text.toString(),
            email = editEmail.text.toString(),
            phoneNumber = editPhone.text.toString(),
            location = editLocation.text.toString(),
            company = editCompany.text.toString(),
            yearsExperience = editYearsExperience.text.toString().toIntOrNull(),
            priceRange = editPriceRange.text.toString(),
            bio = editBio.text.toString(),
            instagram = editInstagram.text.toString(),
            facebook = editFacebook.text.toString(),
            website = editWebsite.text.toString(),
            specialties = specialties,
            availability = availability,
            role = if (isPlanner) "Wedding Planner" else "User"
        )

        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                selectedImageBitmap?.let { dbHelper.addPortfolioImage(it) }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pickImage() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                }
            }
            .show()
    }

    private fun takePhoto() {
        try {
            cameraLauncher.launch(null)
        } catch (e: Exception) {
            Toast.makeText(context, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseFromGallery() {
        try {
            galleryLauncher.launch("image/*")
        } catch (e: Exception) {
            Toast.makeText(context, "Gallery not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddReviewDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_review, null)
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<EditText>(R.id.etReviewTitle).text.toString()
                val comment = dialogView.findViewById<EditText>(R.id.etReviewComment).text.toString()
                val rating = dialogView.findViewById<RatingBar>(R.id.ratingBar).rating
                val userName = auth.currentUser?.displayName ?: "Anonymous"

                if (title.isNotEmpty() && comment.isNotEmpty()) {
                    dbHelper.addReview(Review(title, comment, rating, userName), auth.currentUser?.uid ?: "")
                    loadReviews()
                    Toast.makeText(context, "Review added", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showAddServiceDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_service, null)
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etServiceName).text.toString()
                val desc = dialogView.findViewById<EditText>(R.id.etServiceDescription).text.toString()
                val price = dialogView.findViewById<EditText>(R.id.etServicePrice).text.toString()

                if (name.isNotEmpty() && desc.isNotEmpty() && price.isNotEmpty()) {
                    dbHelper.addService(Service(0, name, desc, price))
                    loadServices()
                    Toast.makeText(context, "Service added", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showAddPortfolioDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Add Portfolio Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                }
            }
            .show()
    }

    private fun showAddSpecialtyDialog() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Add Specialty")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                input.text.toString().takeIf { it.isNotEmpty() }?.let { text ->
                    addSpecialtyChip(text)
                }
            }
            .show()
    }

    private fun addSpecialtyChip(text: String) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener { specialtiesChipGroup.removeView(this) }
            setChipBackgroundColorResource(R.color.chip_background)
        }
        specialtiesChipGroup.addView(chip)
    }

    private fun loadServices() {
        servicesContainer.removeAllViews()
        val services = dbHelper.getServicesForCurrentUser()
        services.forEach { service ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_service, servicesContainer, false)
            view.findViewById<TextView>(R.id.tvServiceName).text = service.name
            view.findViewById<TextView>(R.id.tvServicePrice).text = service.price
            servicesContainer.addView(view)
        }
    }

    private fun loadPortfolio() {
        portfolioContainer.removeAllViews()
        val portfolio = dbHelper.getPortfolioForCurrentUser()
        portfolio.forEach { bitmap ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_portfolio_image, portfolioContainer, false)
            view.findViewById<ImageView>(R.id.imgPortfolio).setImageBitmap(bitmap)
            portfolioContainer.addView(view)
        }
    }

    private fun loadReviews() {
        reviewsContainer.removeAllViews()
        val reviews = dbHelper.getReviewsForUser(auth.currentUser?.uid ?: "")
        reviews.forEach { review ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_review, reviewsContainer, false)
            view.findViewById<TextView>(R.id.reviewText).text = review.comment
            view.findViewById<RatingBar>(R.id.reviewRating).rating = review.rating
            view.findViewById<TextView>(R.id.reviewerName).text = review.userName
            reviewsContainer.addView(view)
        }
    }
}