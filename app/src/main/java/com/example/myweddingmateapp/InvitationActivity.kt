package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.InvitationAdapter
import com.example.myweddingmateapp.databinding.ActivityInvitationBinding
import com.example.myweddingmateapp.models.Invitation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InvitationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvitationBinding
    private lateinit var prefs: PrefsHelper
    private val invitationList = mutableListOf<Invitation>()
    private lateinit var db: FirebaseFirestore // Declare Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)
        db = Firebase.firestore // Initialize Firestore

        setupBackButton()
        setupRecyclerView()
        fetchInvitationsFromFirestore() // Call function to fetch data from Firestore
    }

    private fun setupRecyclerView() {
        binding.invitationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.invitationRecyclerView.adapter = InvitationAdapter(
            invitations = invitationList,
            onFavoriteClick = { invitation ->
                invitation.isFavorite = !invitation.isFavorite
                if (invitation.isFavorite) {
                    prefs.addFavorite(invitation.id, "invitation")
                } else {
                    prefs.removeFavorite(invitation.id, "invitation")
                }
                binding.invitationRecyclerView.adapter?.notifyItemChanged(
                    invitationList.indexOf(invitation)
                )
            },
            onWebsiteClick = { url ->
                openWebsite(url)
            }
        )
    }

    private fun fetchInvitationsFromFirestore() {
        db.collection("invitation") // Refer to your new collection
            .get()
            .addOnSuccessListener { result ->
                invitationList.clear() // Clear existing hardcoded data
                val imageMap = createImageResourceMap() // Create map for image resources

                for (document in result) {
                    try {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val imageResIdName = document.getString("imageResId") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        val websiteUrl = document.getString("websiteUrl") ?: ""

                        // Get the actual drawable ID from the map, default to placeholder if not found
                        val imageDrawableId = imageMap[imageResIdName] ?: R.drawable.placeholder_venue // IMPORTANT: Add a placeholder image in res/drawable

                        val invitation = Invitation(
                            id = id,
                            name = name,
                            imageResId = imageDrawableId,
                            rating = rating,
                            reviewCount = reviewCount,
                            websiteUrl = websiteUrl,
                            isFavorite = prefs.isFavorite(id, "invitation")
                        )
                        invitationList.add(invitation)
                    } catch (e: Exception) {
                        Log.e("InvitationActivity", "Error parsing document ${document.id}: ${e.message}", e)
                        Toast.makeText(this, "Error parsing data for ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.invitationRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("InvitationActivity", "Error getting invitation documents: ", exception)
                Toast.makeText(this, "Error loading invitation data: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Optionally, you could load hardcoded data as a fallback here
                // loadInitialData() // Uncomment if you want fallback hardcoded data on Firestore failure
            }
    }

    // Helper function to map image resource names (from Firestore) to their R.drawable IDs
    private fun createImageResourceMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["card_craft"] = R.drawable.card_craft
        map["elegant_invites"] = R.drawable.elegant_invites
        map["wedding_card_co"] = R.drawable.wedding_card_co
        map["premium_cards"] = R.drawable.premium_cards
        // Add all your invitation image resources here
        // e.g., map["another_card_maker_image"] = R.drawable.another_card_maker_image
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