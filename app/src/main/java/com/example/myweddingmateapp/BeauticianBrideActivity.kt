package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.BeauticianBrideAdapter
import com.example.myweddingmateapp.databinding.ActivityBeauticianBrideBinding
import com.example.myweddingmateapp.models.BeauticianBride
import com.google.firebase.auth.FirebaseAuth // ADD THIS IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeauticianBrideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianBrideBinding
    private lateinit var prefs: PrefsHelper
    private val beauticianList = mutableListOf<BeauticianBride>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth // ADD THIS DECLARATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianBrideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchBeauticiansFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.beauticianBrideRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beauticianBrideRecyclerView.adapter = BeauticianBrideAdapter(
            beauticians = beauticianList,
            onFavoriteClick = { beautician ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    // User is not logged in, show a toast message
                    Toast.makeText(this, "Please log in to favorite beauticians.", Toast.LENGTH_SHORT).show()
                    return@BeauticianBrideAdapter
                }

                beautician.isFavorite = !beautician.isFavorite
                if (beautician.isFavorite) {
                    prefs.addFavorite(userId, beautician.id, "beauticianBride") // PASS USER ID
                } else {
                    prefs.removeFavorite(userId, beautician.id, "beauticianBride")
                }
                binding.beauticianBrideRecyclerView.adapter?.notifyItemChanged(beauticianList.indexOf(beautician))
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchBeauticiansFromFirestore() {
        val currentUserId = auth.currentUser?.uid

        db.collection("beautician-bride")
            .get()
            .addOnSuccessListener { result ->
                beauticianList.clear()
                for (document in result) {
                    val beautician = document.toObject(BeauticianBride::class.java).apply {
                        // Check favorite status using the currentUserId
                        isFavorite = if (currentUserId != null) {
                            prefs.isFavorite(currentUserId, id, "beauticianBride") // PASS USER ID
                        } else {
                            false
                        }
                    }
                    beauticianList.add(beautician)
                }
                binding.beauticianBrideRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading beauticians: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening website", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}