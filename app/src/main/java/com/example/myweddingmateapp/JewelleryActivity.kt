package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.JewelleryAdapter
import com.example.myweddingmateapp.databinding.ActivityJewelleryBinding
import com.example.myweddingmateapp.models.Jewellery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JewelleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJewelleryBinding
    private lateinit var prefs: PrefsHelper
    private val jewelleryList = mutableListOf<Jewellery>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJewelleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()
        fetchJewelleryFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.jewelleryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.jewelleryRecyclerView.adapter = JewelleryAdapter(
            jewelleryList = jewelleryList,
            onFavoriteClick = { jewellery ->
                val userId = auth.currentUser?.uid

                if (userId == null) {
                    Toast.makeText(this, "Please log in to favorite jewellery.", Toast.LENGTH_SHORT).show()
                    return@JewelleryAdapter // Exit the lambda early
                }

                jewellery.isFavorite = !jewellery.isFavorite
                if (jewellery.isFavorite) {
                    prefs.addFavorite(userId, jewellery.id, "jewellery")
                } else {
                    prefs.removeFavorite(userId, jewellery.id, "jewellery")
                }
                binding.jewelleryRecyclerView.adapter?.notifyItemChanged(
                    jewelleryList.indexOf(jewellery)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchJewelleryFromFirestore() {
        val currentUserId = auth.currentUser?.uid

        db.collection("jewellery")
            .get()
            .addOnSuccessListener { result ->
                jewelleryList.clear()
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

                        val jewellery = Jewellery(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = if (currentUserId != null) {
                                prefs.isFavorite(currentUserId, id, "jewellery")
                            } else {
                                false
                            }
                        )
                        jewelleryList.add(jewellery)
                    } catch (e: Exception) {
                        Log.e("JewelleryActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.jewelleryRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("JewelleryActivity", "Error getting jewellery documents: ", exception)
                Toast.makeText(this, "Error loading jewellery data: ${exception.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["vogue"] = R.drawable.vogue
        map["raja"] = R.drawable.raja
        map["mallika"] = R.drawable.mallika
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