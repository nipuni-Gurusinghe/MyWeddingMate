package com.example.myweddingmateapp.adapters


import android.content.Context
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
    private var planners: MutableList<WeddingPlanner> = mutableListOf(),
    private val onPlannerClick: (WeddingPlanner) -> Unit,
    private val onViewProfileClick: (WeddingPlanner) -> Unit
) : RecyclerView.Adapter<WeddingPlannerAdapter.PlannerViewHolder>() {

    companion object {
        private const val TAG = "WeddingPlannerAdapter"
    }

//    viewholder
    inner class PlannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imgPlannerProfile)
        val plannerName: TextView = itemView.findViewById(R.id.tvPlannerName)
        val plannerRating: TextView = itemView.findViewById(R.id.tvPlannerRating)
        val plannerLocation: TextView = itemView.findViewById(R.id.tvPlannerLocation)
        val plannerExperience: TextView = itemView.findViewById(R.id.tvPlannerExperience)
        val plannerSpecialties: TextView = itemView.findViewById(R.id.tvPlannerSpecialties)
        val plannerPriceRange: TextView = itemView.findViewById(R.id.tvPlannerPriceRange)
        val plannerBio: TextView = itemView.findViewById(R.id.tvPlannerBio)
        val availabilityBadge: TextView = itemView.findViewById(R.id.tvAvailabilityBadge)
        val btnSelectPlanner: Button = itemView.findViewById(R.id.btnSelectPlanner)
        val btnViewProfile: Button = itemView.findViewById(R.id.btnViewProfile)
        val cardContainer: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardPlannerContainer)

        fun bind(planner: WeddingPlanner) {
            plannerName.text = planner.name


            plannerRating.text = "${planner.getRatingText()} ${planner.getReviewText()}"


            plannerLocation.text = planner.location.ifEmpty { "Location not specified" }


            plannerExperience.text = planner.getExperienceText()


            plannerSpecialties.text = planner.getSpecialtiesText()


            plannerPriceRange.text = if (planner.priceRange.isNotEmpty()) {
                "Budget: ${planner.priceRange}"
            } else {
                "Price: Contact for quote"
            }


            plannerBio.text = if (planner.bio.isNotEmpty()) {
                if (planner.bio.length > 120) {
                    "${planner.bio.substring(0, 120)}..."
                } else {
                    planner.bio
                }
            } else {
                "Professional wedding planner dedicated to making your special day perfect."
            }


            availabilityBadge.text = planner.getAvailabilityText()
            availabilityBadge.setBackgroundResource(
                if (planner.isAvailable) R.drawable.bg_available_badge
                else R.drawable.bg_unavailable_badge
            )


            loadProfileImage(planner.profileImageUrl)


            btnSelectPlanner.setOnClickListener {
                onPlannerClick(planner)
            }

            btnViewProfile.setOnClickListener {
                onViewProfileClick(planner)
            }

            cardContainer.setOnClickListener {
                onViewProfileClick(planner)
            }


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

//    update list
    fun updatePlanners(newPlanners: List<WeddingPlanner>) {
        planners.clear()
        planners.addAll(newPlanners)
        notifyDataSetChanged()
    }


     //Add 1 planner

    fun addPlanner(planner: WeddingPlanner) {
        planners.add(planner)
        notifyItemInserted(planners.size - 1)
    }


     //remove  planner

    fun removePlanner(plannerId: String) {
        val position = planners.indexOfFirst { it.id == plannerId }
        if (position != -1) {
            planners.removeAt(position)
            notifyItemRemoved(position)
        }
    }


     //update planner

    fun updatePlanner(updatedPlanner: WeddingPlanner) {
        val position = planners.indexOfFirst { it.id == updatedPlanner.id }
        if (position != -1) {
            planners[position] = updatedPlanner
            notifyItemChanged(position)
        }
    }


     //Get planner at position

    fun getPlannerAt(position: Int): WeddingPlanner? {
        return if (position in 0 until planners.size) planners[position] else null
    }


    fun clearPlanners() {
        val size = planners.size
        planners.clear()
        notifyItemRangeRemoved(0, size)
    }


    fun filterByAvailability(availableOnly: Boolean) {
        val filteredPlanners = if (availableOnly) {
            planners.filter { it.isAvailable }
        } else {
            planners
        }
        updatePlanners(filteredPlanners)
    }


    fun sortByRating() {
        planners.sortByDescending { it.rating }
        notifyDataSetChanged()
    }


    fun sortByExperience() {
        planners.sortByDescending { it.experience }
        notifyDataSetChanged()
    }
}