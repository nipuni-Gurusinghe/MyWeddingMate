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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeauticianBrideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianBrideBinding
    private lateinit var prefs: PrefsHelper
    private val beauticianList = mutableListOf<BeauticianBride>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianBrideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore

        setupBackButton()
        setupRecyclerView()
        fetchBeauticiansFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.beauticianBrideRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beauticianBrideRecyclerView.adapter = BeauticianBrideAdapter(
            beauticians = beauticianList,
            onFavoriteClick = { beautician ->
                beautician.isFavorite = !beautician.isFavorite
                if (beautician.isFavorite) {
                    prefs.addFavorite(beautician.id, "beauticianBride")
                } else {
                    prefs.removeFavorite(beautician.id, "beauticianBride")
                }
                binding.beauticianBrideRecyclerView.adapter?.notifyItemChanged(beauticianList.indexOf(beautician))
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchBeauticiansFromFirestore() {
        db.collection("beautician-bride")
            .get()
            .addOnSuccessListener { result ->
                beauticianList.clear()
                for (document in result) {
                    val beautician = document.toObject(BeauticianBride::class.java).apply {
                        isFavorite = prefs.isFavorite(id, "beauticianBride")
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