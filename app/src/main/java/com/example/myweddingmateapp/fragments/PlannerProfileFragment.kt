package com.example.myweddingmateapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myweddingmateapp.LoginActivity
import com.example.myweddingmateapp.R

import com.example.myweddingmateapp.models.User
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PlannerProfileFragment : Fragment() {

    private lateinit var profilePicture: ImageView
    private lateinit var btnEditPicture: ImageView
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editLocation: EditText
    private lateinit var editCompany: EditText
    private lateinit var editYearsExperience: EditText
    private lateinit var editPriceRange: EditText
    private lateinit var editBio: EditText
    private lateinit var editInstagram: EditText
    private lateinit var editFacebook: EditText
    private lateinit var editWebsite: EditText
    private lateinit var specialtiesChipGroup: ChipGroup
    private lateinit var availabilityChipGroup: ChipGroup
    private lateinit var btnUpdate: Button

    private var currentUser: User? = null
    private var selectedImageBitmap: Bitmap? = null
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                profilePicture.setImageBitmap(selectedImageBitmap)
                saveProfileImageLocally(selectedImageBitmap)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_planner_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        loadUserData()
        setupButtons()
    }

    private fun setupViews(view: View) {
        profilePicture = view.findViewById(R.id.profilePicture)
        btnEditPicture = view.findViewById(R.id.btnEditPicture)
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
        specialtiesChipGroup = view.findViewById(R.id.specialtiesChipGroup)
        availabilityChipGroup = view.findViewById(R.id.availabilityChipGroup)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        view.findViewById<Button>(R.id.btnSignOut).setOnClickListener { signOut() }
        view.findViewById<Button>(R.id.btnAddReview).visibility = View.GONE
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                currentUser = document.toObject(User::class.java)
                if (currentUser != null) {
                    showUserData(currentUser!!)
                    loadProfileImage()
                    checkUserRole(document)
                }
            }
        }
    }

    private fun checkUserRole(document: DocumentSnapshot) {
        val role = document.getString("role") ?: "Planner"
        val isUser = role == "User"

        view?.findViewById<Button>(R.id.btnAddReview)?.visibility = if (isUser) View.VISIBLE else View.GONE

        val fields = listOf(
            editName, editEmail, editPhone, editLocation,
            editCompany, editYearsExperience, editPriceRange,
            editBio, editInstagram, editFacebook, editWebsite,
            btnUpdate, btnEditPicture
        )

        fields.forEach { it.isEnabled = !isUser }

        for (i in 0 until specialtiesChipGroup.childCount) {
            val chip = specialtiesChipGroup.getChildAt(i) as? Chip
            chip?.isCloseIconVisible = !isUser
            chip?.isClickable = !isUser
        }

        availabilityChipGroup.isEnabled = !isUser
        view?.findViewById<Button>(R.id.btnAddSpecialty)?.visibility = if (isUser) View.GONE else View.VISIBLE
    }

    private fun showUserData(user: User) {
        editName.setText(user.name ?: "")
        editEmail.setText(user.email ?: "")
        editPhone.setText(user.phoneNumber ?: "")
        editLocation.setText(user.location ?: "")
        editCompany.setText(user.company ?: "")
        editYearsExperience.setText(user.yearsExperience?.toString() ?: "")
        editPriceRange.setText(user.priceRange ?: "")
        editBio.setText(user.bio ?: "")
        editInstagram.setText(user.instagram ?: "")
        editFacebook.setText(user.facebook ?: "")
        editWebsite.setText(user.website ?: "")

        specialtiesChipGroup.removeAllViews()
        user.specialties?.forEach { specialty ->
            addSpecialtyChip(specialty)
        }

        when (user.availability) {
            "Available" -> availabilityChipGroup.check(R.id.chipAvailable)
            "Limited Availability" -> availabilityChipGroup.check(R.id.chipLimited)
            "Currently Unavailable" -> availabilityChipGroup.check(R.id.chipUnavailable)
        }
    }

    private fun addSpecialtyChip(text: String) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener { specialtiesChipGroup.removeView(chip) }
        chip.setChipBackgroundColorResource(R.color.chip_background)
        specialtiesChipGroup.addView(chip)
    }

    private fun loadProfileImage() {
        val imageFile = File(requireContext().filesDir, "profile_picture.jpg")
        if (imageFile.exists()) {
            profilePicture.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
        } else {
            profilePicture.setImageResource(R.drawable.profile_circle_background)
        }
    }

    private fun setupButtons() {
        btnEditPicture.setOnClickListener { pickImage() }
        btnUpdate.setOnClickListener { updateProfile() }
        view?.findViewById<Button>(R.id.btnAddSpecialty)?.setOnClickListener { addSpecialtyDialog() }
    }

    private fun pickImage() {
        galleryLauncher.launch("image/*")
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun saveProfileImageLocally(bitmap: Bitmap?) {
        if (bitmap == null) return

        try {
            val file = File(requireContext().filesDir, "profile_picture.jpg")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSpecialtyDialog() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Specialty")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    addSpecialtyChip(text)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProfile() {
        if (editName.text.toString().trim().isEmpty() || editEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Name and email are required", Toast.LENGTH_SHORT).show()
            return
        }

        val specialties = mutableListOf<String>()
        for (i in 0 until specialtiesChipGroup.childCount) {
            val chip = specialtiesChipGroup.getChildAt(i) as? Chip
            if (chip != null) {
                specialties.add(chip.text.toString())
            }
        }

        var availability = ""
        val checkedId = availabilityChipGroup.checkedChipId
        if (checkedId == R.id.chipAvailable) {
            availability = "Available"
        } else if (checkedId == R.id.chipLimited) {
            availability = "Limited Availability"
        } else if (checkedId == R.id.chipUnavailable) {
            availability = "Currently Unavailable"
        }

        val user = currentUser ?: User()
        user.name = editName.text.toString().trim()
        user.email = editEmail.text.toString().trim()
        user.phoneNumber = editPhone.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.location = editLocation.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.company = editCompany.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.yearsExperience = editYearsExperience.text.toString().trim().toIntOrNull()
        user.priceRange = editPriceRange.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.bio = editBio.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.instagram = editInstagram.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.facebook = editFacebook.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.website = editWebsite.text.toString().trim().takeIf { it.isNotEmpty() }.toString()
        user.specialties = specialties
        user.availability = availability

        if (selectedImageBitmap != null) {
            ByteArrayOutputStream().use { stream ->
                selectedImageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                user.profileImage = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT)
            }
        }

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).set(user)
                .addOnSuccessListener {
                    currentUser = user
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}