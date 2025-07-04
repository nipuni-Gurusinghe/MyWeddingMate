package com.example.myweddingmateapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.myweddingmateapp.models.CoupleProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
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


    private lateinit var sharedPreferences: SharedPreferences
    private var currentPhotoPath: String = ""
    private var isEditMode: Boolean = false
    private var coupleProfileData: CoupleProfile? = null

    // Activity Result Launchers
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    // Constants
    companion object {
        private const val TAG = "CoupleProfileActivity"
        private const val PREFS_NAME = "couple_profile_prefs"
        private const val KEY_PROFILE_DATA = "profile_data"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_GALLERY = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    // BaseActivity abstract methods
    override fun getCurrentNavId(): Int = R.id.navProfile

    override fun getLayoutResourceId(): Int = R.layout.activity_couple_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeViews()
        initializePreferences()
        initializeActivityLaunchers()
        setupToolbar()
        setupTextWatchers()
        setupClickListeners()
        loadSavedData()
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

    private fun initializePreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun initializeActivityLaunchers() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleCameraResult(result)
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleGalleryResult(result)
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionResult(permissions)
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
        val options = arrayOf("Take Photo", "Choose from Gallery", "Remove Photo")

        AlertDialog.Builder(this)
            .setTitle("Select Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndTakePhoto()
                    1 -> checkStoragePermissionAndOpenGallery()
                    2 -> removePhoto()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun checkStoragePermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun takePhoto() {
        try {
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        } catch (ex: IOException) {
            Log.e(TAG, "Error creating image file", ex)
            showToast("Error creating image file")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun removePhoto() {
        ivProfilePhoto.setImageResource(R.drawable.ic_couple_profile)
        currentPhotoPath = ""
        showToast("Photo removed")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("COUPLE_${timeStamp}_", ".jpg", storageDir)
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (currentPhotoPath.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                ivProfilePhoto.setImageBitmap(bitmap)
                showToast("Photo captured successfully")
            }
        }
    }

    private fun handleGalleryResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    ivProfilePhoto.setImageBitmap(bitmap)
                    saveImageToInternalStorage(bitmap)
                    showToast("Photo selected successfully")
                } catch (e: IOException) {
                    Log.e(TAG, "Error loading image from gallery", e)
                    showToast("Error loading image")
                }
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        try {
            val filename = "couple_profile_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
            currentPhotoPath = file.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "Error saving image to internal storage", e)
        }
    }

    private fun handlePermissionResult(permissions: Map<String, Boolean>) {
        permissions.forEach { (permission, granted) ->
            when (permission) {
                Manifest.permission.CAMERA -> {
                    if (granted) {
                        takePhoto()
                    } else {
                        showToast("Camera permission is required to take photos")
                    }
                }
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    if (granted) {
                        openGallery()
                    } else {
                        showToast("Storage permission is required to access photos")
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        if (validateInputs()) {
            lifecycleScope.launch {
                try {
                    val profile = createCoupleProfile()
                    saveProfileData(profile)
                    withContext(Dispatchers.Main) {
                        showToast("Profile saved successfully!")
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving profile", e)
                    withContext(Dispatchers.Main) {
                        showToast("Error saving profile. Please try again.")
                    }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        clearErrorMessages()
        var isValid = true

        // Validate Partner 1 Name
        if (etPartner1Name.text.toString().trim().isEmpty()) {
            tilPartner1Name.error = "Partner 1 name is required"
            isValid = false
        } else if (etPartner1Name.text.toString().trim().length < 2) {
            tilPartner1Name.error = "Name must be at least 2 characters"
            isValid = false
        }

        // Validate Partner 2 Name
        if (etPartner2Name.text.toString().trim().isEmpty()) {
            tilPartner2Name.error = "Partner 2 name is required"
            isValid = false
        } else if (etPartner2Name.text.toString().trim().length < 2) {
            tilPartner2Name.error = "Name must be at least 2 characters"
            isValid = false
        }

        // Validate Email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate Phone
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
            profilePhotoPath = currentPhotoPath,
            createdAt = coupleProfileData?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun saveProfileData(profile: CoupleProfile) {

        val profileJson = """
            {
                "id": "${profile.id}",
                "partner1Name": "${profile.partner1Name}",
                "partner2Name": "${profile.partner2Name}",
                "email": "${profile.email}",
                "phone": "${profile.phone}",
                "address": "${profile.address.replace("\"", "\\\"")}",
                "profilePhotoPath": "${profile.profilePhotoPath}",
                "createdAt": ${profile.createdAt},
                "updatedAt": ${profile.updatedAt}
            }
        """.trimIndent()

        sharedPreferences.edit()
            .putString(KEY_PROFILE_DATA, profileJson)
            .apply()
    }

    private fun loadSavedData() {
        val profileJson = sharedPreferences.getString(KEY_PROFILE_DATA, null)
        if (profileJson != null) {
            try {

                coupleProfileData = parseProfileFromJson(profileJson)
                populateFields()
                isEditMode = true
            } catch (e: Exception) {
                Log.e(TAG, "Error loading saved data", e)
            }
        }
    }

    private fun parseProfileFromJson(json: String): CoupleProfile {
        val id = extractJsonValue(json, "id")
        val partner1Name = extractJsonValue(json, "partner1Name")
        val partner2Name = extractJsonValue(json, "partner2Name")
        val email = extractJsonValue(json, "email")
        val phone = extractJsonValue(json, "phone")
        val address = extractJsonValue(json, "address")
        val profilePhotoPath = extractJsonValue(json, "profilePhotoPath")
        val createdAt = extractJsonValue(json, "createdAt").toLongOrNull() ?: 0L
        val updatedAt = extractJsonValue(json, "updatedAt").toLongOrNull() ?: 0L

        return CoupleProfile(
            id = id,
            partner1Name = partner1Name,
            partner2Name = partner2Name,
            email = email,
            phone = phone,
            address = address,
            profilePhotoPath = profilePhotoPath,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun extractJsonValue(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\""
        val regex = Regex(pattern)
        val matchResult = regex.find(json)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    private fun populateFields() {
        coupleProfileData?.let { profile ->
            etPartner1Name.setText(profile.partner1Name)
            etPartner2Name.setText(profile.partner2Name)
            etEmail.setText(profile.email)
            etPhone.setText(profile.phone)
            etAddress.setText(profile.address)

            if (profile.profilePhotoPath.isNotEmpty()) {
                currentPhotoPath = profile.profilePhotoPath
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                if (bitmap != null) {
                    ivProfilePhoto.setImageBitmap(bitmap)
                }
            }
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