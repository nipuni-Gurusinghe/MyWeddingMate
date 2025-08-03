package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.BeauticianGroomAdapter
import com.example.myweddingmateapp.databinding.ActivityBeauticianGroomBinding
import com.example.myweddingmateapp.models.BeauticianGroom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeauticianGroomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeauticianGroomBinding
    private lateinit var prefs: PrefsHelper
    private val beauticianGroomList = mutableListOf<BeauticianGroom>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeauticianGroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchBeauticiansGroomFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.beauticianGroomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beauticianGroomRecyclerView.adapter = BeauticianGroomAdapter(
            beauticians = beauticianGroomList,
            onFavoriteClick = { beautician ->
                val userId = auth.currentUser?.uid // GET THE CURRENT USER'S ID HERE

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite groom beauticians.", Toast.LENGTH_SHORT).show()
                    return@BeauticianGroomAdapter
                }

                beautician.isFavorite = !beautician.isFavorite
                if (beautician.isFavorite) {
                    prefs.addFavorite(userId, beautician.id, "beauticianGroom") // PASS USER ID
                } else {
                    prefs.removeFavorite(userId, beautician.id, "beauticianGroom") // PASS USER ID
                }
                binding.beauticianGroomRecyclerView.adapter?.notifyItemChanged(
                    beauticianGroomList.indexOf(beautician)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchBeauticiansGroomFromFirestore() {
        val currentUserId = auth.currentUser?.uid

        db.collection("beautician-groom")
            .get()
            .addOnSuccessListener { result ->
                beauticianGroomList.clear()
                val imageMap = createImageResourceMap()

                for (document in result) {
                    try {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val imageResIdName = document.getString("imageResId") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        val websiteUrl = document.getString("websiteUrl") ?: ""

                        val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue

                        val beautician = BeauticianGroom(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = if (currentUserId != null) {
                                prefs.isFavorite(currentUserId, id, "beauticianGroom") // PASS USER ID
                            } else {
                                false
                            }
                        )
                        beauticianGroomList.add(beautician)
                    } catch (e: Exception) {
                        Log.e("BeauticianGroomActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.beauticianGroomRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("BeauticianGroomActivity", "Error getting beautician groom documents: ", exception)
                Toast.makeText(this, "Error loading groom beautician data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["salon_zero_groom"] = R.drawable.salon_zero_groom
        map["naturals"] = R.drawable.naturals
        return map
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