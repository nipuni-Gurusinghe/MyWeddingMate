package com.example.myweddingmateapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myweddingmateapp.models.WeddingPlanner
import com.example.myweddingmateapp.R

class WeddingPlannerAdapter(
    private val context: Context,
    private val onPlannerClick: (WeddingPlanner) -> Unit,
    private val onViewProfileClick: (WeddingPlanner) -> Unit
) : RecyclerView.Adapter<WeddingPlannerAdapter.PlannerViewHolder>() {

    companion object {
        private const val TAG = "WeddingPlannerAdapter"
    }

    private var planners: MutableList<WeddingPlanner> = mutableListOf()

    /**
     * ViewHolder for planner items
     */
    inner class PlannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imgPlannerProfile)
        val plannerName: TextView = itemView.findViewById(R.id.tvPlannerName)
        val plannerRating: TextView = itemView.findViewById(R.id.tvPlannerRating)
        val plannerLocation: TextView = itemView.findViewById(R.id.tvPlannerLocation)
        val plannerExperience: TextView = itemView.findViewById(R.id.tvPlannerExperience)
       // val plannerSpecialties: TextView = itemView.findViewById(R.id.tvPlannerSpecialties)
        val plannerPriceRange: TextView = itemView.findViewById(R.id.tvPlannerPriceRange)
       // val plannerBio: TextView = itemView.findViewById(R.id.tvPlannerBio)
        val availabilityBadge: TextView = itemView.findViewById(R.id.tvAvailabilityBadge)
        val btnSelectPlanner: Button = itemView.findViewById(R.id.btnSelectPlanner)
        val btnViewProfile: Button = itemView.findViewById(R.id.btnViewProfile)
        val cardContainer: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardPlannerContainer)

        fun bind(planner: WeddingPlanner) {
            Log.d(TAG, "Binding planner: ${planner.name}")

            // Set planner name
            plannerName.text = planner.name

            // Set rating and reviews
            val ratingText = if (planner.reviewCount > 0) {
                "★ ${planner.rating} (${planner.reviewCount} reviews)"
            } else {
                "★ ${planner.rating} (No reviews yet)"
            }
            plannerRating.text = ratingText

            // Set location
            plannerLocation.text = if (planner.location.isNotEmpty()) {
                "📍 ${planner.location}"
            } else {
                "📍 Location not specified"
            }

            // Set experience
            plannerExperience.text = if (planner.experience > 0) {
                "${planner.experience} years experience"
            } else {
                "New to the field"
            }

//            // Set specialties
//            plannerSpecialties.text = if (planner.specialties.isNotEmpty()) {
//                planner.specialties.joinToString(", ")
//            } else {
//                "Wedding Planning"
//            }

            // Set price range
            plannerPriceRange.text = if (planner.priceRange.isNotEmpty()) {
                planner.priceRange
            } else {
                "Contact for quote"
            }

//            // Set bio (truncated)
//            plannerBio.text = if (planner.bio.isNotEmpty()) {
//                if (planner.bio.length > 120) {
//                    "${planner.bio.substring(0, 120)}..."
//                } else {
//                    planner.bio
//                }
//            } else {
//                "Professional wedding planner dedicated to making your special day perfect."
//            }

            // Set availability badge
            availabilityBadge.text = if (planner.isAvailable) "Available" else "Unavailable"
            availabilityBadge.setBackgroundResource(
                if (planner.isAvailable) R.drawable.bg_available_badge
                else R.drawable.bg_unavailable_badge
            )

            // Load profile image
            loadProfileImage(planner.profileImageUrl)

            // Set click listeners
            btnSelectPlanner.setOnClickListener {
                Log.d(TAG, "Select button clicked for: ${planner.name}")
                onPlannerClick(planner)
            }

            btnViewProfile.setOnClickListener {
                Log.d(TAG, "View profile button clicked for: ${planner.name}")
                onViewProfileClick(planner)
            }

            cardContainer.setOnClickListener {
                Log.d(TAG, "Card clicked for: ${planner.name}")
                onViewProfileClick(planner)
            }

            // Enable/disable select button based on availability
            btnSelectPlanner.isEnabled = planner.isAvailable
            btnSelectPlanner.alpha = if (planner.isAvailable) 1.0f else 0.6f
        }

        private fun loadProfileImage(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(profileImage)
            } else {
                // Set default profile image
                profileImage.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wedding_planner, parent, false)
        return PlannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlannerViewHolder, position: Int) {
        holder.bind(planners[position])
    }

    override fun getItemCount(): Int = planners.size


    fun updatePlanners(newPlanners: List<WeddingPlanner>) {
        Log.d(TAG, "updatePlanners called with ${newPlanners.size} planners")

        planners.clear()
        planners.addAll(newPlanners)

        Log.d(TAG, "Planners updated. Current size: ${planners.size}")
        for (planner in planners) {
            Log.d(TAG, "Planner: ${planner.name} - ${planner.email}")
        }

        notifyDataSetChanged()
    }


    fun getPlanners(): List<WeddingPlanner> = planners.toList()


    fun clearPlanners() {
        val size = planners.size
        planners.clear()
        notifyItemRangeRemoved(0, size)
    }
}