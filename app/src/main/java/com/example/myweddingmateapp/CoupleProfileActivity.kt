package com.example.myweddingmateapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myweddingmateapp.models.CoupleProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class CoupleProfileActivity : BaseActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnChangePhoto: MaterialButton
    private lateinit var tilPartner1Name: TextInputLayout
    private lateinit var etPartner1Name: TextInputEditText
    private lateinit var tilPartner2Name: TextInputLayout
    private lateinit var etPartner2Name: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etPhone: TextInputEditText
    private lateinit var tilAddress: TextInputLayout
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSaveProfile: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var profilePhotoBitmap: Bitmap? = null
    private var isEditMode: Boolean = false
    private var coupleProfileData: CoupleProfile? = null

    // Activity Result Launchers
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    // Constants
    companion object {
        private const val TAG = "CoupleProfileActivity"
        private const val COLLECTION_COUPLE_PROFILES = "couple_profiles"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    // BaseActivity call
    override fun getCurrentNavId(): Int = R.id.navProfile

    override fun getLayoutResourceId(): Int = R.layout.activity_couple_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeViews()
        initializeFirestore()
        initializeActivityLaunchers()
        setupToolbar()
        setupTextWatchers()
        setupClickListeners()

        // Load data only if user is already authenticated
        if (auth.currentUser != null) {
            loadSavedData()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        ivProfilePhoto = findViewById(R.id.iv_profile_photo)
        btnChangePhoto = findViewById(R.id.btn_change_photo)
        tilPartner1Name = findViewById(R.id.til_partner1_name)
        etPartner1Name = findViewById(R.id.et_partner1_name)
        tilPartner2Name = findViewById(R.id.til_partner2_name)
        etPartner2Name = findViewById(R.id.et_partner2_name)
        tilEmail = findViewById(R.id.til_email)
        etEmail = findViewById(R.id.et_email)
        tilPhone = findViewById(R.id.til_phone)
        etPhone = findViewById(R.id.et_phone)
        tilAddress = findViewById(R.id.til_address)
        etAddress = findViewById(R.id.et_address)
        btnCancel = findViewById(R.id.btn_cancel)
        btnSaveProfile = findViewById(R.id.btn_save_profile)
    }

    private fun initializeFirestore() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            // Sign in anonymously if no user is authenticated
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "authentication successful")
                        loadSavedData()
                    } else {
                        Log.e(TAG, "authentication failed", task.exception)
                        showToast("Authentication failed. Please try again.")
                    }
                }
        }
    }

    private fun initializeActivityLaunchers() {
        // Camera launcher
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val extras = result.data?.extras
                val imageBitmap = extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    ivProfilePhoto.setImageBitmap(imageBitmap)
                    ivProfilePhoto.visibility = View.VISIBLE
                    profilePhotoBitmap = imageBitmap
                    showToast("Photo captured successfully")
                } else {
                    showToast("Failed to capture photo")
                }
            }
        }

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        ivProfilePhoto.setImageBitmap(bitmap)
                        ivProfilePhoto.visibility = View.VISIBLE
                        profilePhotoBitmap = bitmap
                        showToast("Photo selected successfully")
                    } catch (e: IOException) {
                        Log.e(TAG, "Error loading image from gallery", e)
                        showToast("Error loading image")
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                clearErrorMessages()
            }
        }

        etPartner1Name.addTextChangedListener(textWatcher)
        etPartner2Name.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)
        etAddress.addTextChangedListener(textWatcher)
    }

    private fun setupClickListeners() {
        btnChangePhoto.setOnClickListener {
            showPhotoSelectionDialog()
        }

        btnCancel.setOnClickListener {
            showCancelConfirmationDialog()
        }

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun showPhotoSelectionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        AlertDialog.Builder(this)
            .setTitle("Select Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkStoragePermissionAndOpenGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, start camera intent
            dispatchTakePictureIntent()
        } else {
            // Request the camera permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkStoragePermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, launch the camera
                    dispatchTakePictureIntent()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    showToast("Storage permission is required to access photos")
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Ensure there's a camera activity to handle the intent
            takePictureLauncher.launch(takePictureIntent)
        } else {
            showToast("No camera app available")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun saveProfile() {
        if (validateInputs()) {
            if (auth.currentUser == null) {
                showToast("Authenticating...")
                auth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            performSave()
                        } else {
                            showToast("Authentication failed. Please try again.")
                            Log.e(TAG, "Authentication failed", task.exception)
                        }
                    }
            } else {
                performSave()
            }
        }
    }

    private fun performSave() {
        lifecycleScope.launch {
            try {
                showToast("Saving profile...")
                val profile = createCoupleProfile()
                saveProfileToFirestore(profile)
                withContext(Dispatchers.Main) {
                    showToast("Profile saved successfully!")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile", e)
                withContext(Dispatchers.Main) {
                    showToast("Error saving profile. Please check your internet connection and try again.")
                }
            }
        }
    }

    private suspend fun saveProfileToFirestore(profile: CoupleProfile) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            throw Exception("User not authenticated")
        }

        val profileData = hashMapOf(
            "id" to profile.id,
            "partner1Name" to profile.partner1Name,
            "partner2Name" to profile.partner2Name,
            "email" to profile.email,
            "phone" to profile.phone,
            "address" to profile.address,
            "createdAt" to profile.createdAt,
            "updatedAt" to profile.updatedAt,
            "userId" to currentUser.uid,
            "hasProfilePhoto" to (profilePhotoBitmap != null)
        )

        firestore.collection(COLLECTION_COUPLE_PROFILES)
            .document(currentUser.uid)
            .set(profileData, SetOptions.merge())
            .await()
    }

    private fun validateInputs(): Boolean {
        clearErrorMessages()
        var isValid = true

        if (etPartner1Name.text.toString().trim().isEmpty()) {
            tilPartner1Name.error = "Partner 1 name is required"
            isValid = false
        } else if (etPartner1Name.text.toString().trim().length < 2) {
            tilPartner1Name.error = "Name must be at least 2 characters"
            isValid = false
        }

        if (etPartner2Name.text.toString().trim().isEmpty()) {
            tilPartner2Name.error = "Partner 2 name is required"
            isValid = false
        } else if (etPartner2Name.text.toString().trim().length < 2) {
            tilPartner2Name.error = "Name must be at least 2 characters"
            isValid = false
        }

        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            isValid = false
        }

        val phone = etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            tilPhone.error = "Phone number is required"
            isValid = false
        } else if (!isValidPhoneNumber(phone)) {
            tilPhone.error = "Please enter a valid phone number"
            isValid = false
        }

        return isValid
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^[+]?[0-9]{10,15}$"
        return Pattern.compile(phonePattern).matcher(phone).matches()
    }

    private fun clearErrorMessages() {
        tilPartner1Name.error = null
        tilPartner2Name.error = null
        tilEmail.error = null
        tilPhone.error = null
        tilAddress.error = null
    }

    private fun createCoupleProfile(): CoupleProfile {
        return CoupleProfile(
            id = coupleProfileData?.id ?: UUID.randomUUID().toString(),
            partner1Name = etPartner1Name.text.toString().trim(),
            partner2Name = etPartner2Name.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            address = etAddress.text.toString().trim(),
            profilePhotoPath = "", // No longer using file paths
            createdAt = coupleProfileData?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun loadSavedData() {
        lifecycleScope.launch {
            try {
                showToast("Loading profile...")
                val profile = loadProfileFromFirestore()
                withContext(Dispatchers.Main) {
                    if (profile != null) {
                        coupleProfileData = profile
                        populateFields()
                        isEditMode = true
                        showToast("Profile loaded successfully")
                    } else {
                        showToast("No saved profile found")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile", e)
                withContext(Dispatchers.Main) {
                    showToast("Error loading profile. Please check your internet connection.")
                }
            }
        }
    }

    private suspend fun loadProfileFromFirestore(): CoupleProfile? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "User not authenticated")
                return null
            }

            val document = firestore.collection(COLLECTION_COUPLE_PROFILES)
                .document(currentUser.uid)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data!!
                CoupleProfile(
                    id = data["id"] as? String ?: "",
                    partner1Name = data["partner1Name"] as? String ?: "",
                    partner2Name = data["partner2Name"] as? String ?: "",
                    email = data["email"] as? String ?: "",
                    phone = data["phone"] as? String ?: "",
                    address = data["address"] as? String ?: "",
                    profilePhotoPath = "", // No longer using file paths
                    createdAt = data["createdAt"] as? Long ?: 0L,
                    updatedAt = data["updatedAt"] as? Long ?: 0L
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile from Firestore", e)
            null
        }
    }

    private fun populateFields() {
        coupleProfileData?.let { profile ->
            etPartner1Name.setText(profile.partner1Name)
            etPartner2Name.setText(profile.partner2Name)
            etEmail.setText(profile.email)
            etPhone.setText(profile.phone)
            etAddress.setText(profile.address)

            // Note: Profile photo is not persisted, so it will show default image
            // If you want to persist photos, consider using Firebase Storage
        }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes")
            .setMessage("Are you sure you want to discard all changes?")
            .setPositiveButton("Discard") { _, _ ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .setNegativeButton("Continue Editing", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        showCancelConfirmationDialog()
    }
}