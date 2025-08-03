package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Venue
import com.example.myweddingmateapp.utils.ResourceHelper

class VenuesAdapter(
    private val venues: List<Venue>,
    private val onFavoriteClick: (Venue) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.venueImage)
        private val nameView: TextView = itemView.findViewById(R.id.venueName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.venueRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.venueRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(venue: Venue) {
            imageView.setImageResource(ResourceHelper.getDrawable(venue.imageResId))
            nameView.text = venue.name
            ratingBar.rating = venue.rating
            ratingText.text = "Rating: ${venue.rating} (${venue.reviewCount}+ reviews)"

            favoriteButton.setImageResource(
                if (venue.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(venue) }
            websiteButton.setOnClickListener { onWebsiteClick(venue.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venue, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(venues[position])
    }

    override fun getItemCount() = venues.size
}