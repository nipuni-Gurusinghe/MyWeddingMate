package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Entertainment

class EntertainmentAdapter(
    private val entertainments: List<Entertainment>,
    private val onFavoriteClick: (Entertainment) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<EntertainmentAdapter.EntertainmentViewHolder>() {

    inner class EntertainmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.entertainmentImage)
        private val nameView: TextView = itemView.findViewById(R.id.entertainmentName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.entertainmentRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.entertainmentRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(entertainment: Entertainment) {
            imageView.setImageResource(entertainment.imageResId)
            nameView.text = entertainment.name
            ratingBar.rating = entertainment.rating
            ratingText.text = "Rating: ${entertainment.rating} (${entertainment.reviewCount}+ bookings)"

            favoriteButton.setImageResource(
                if (entertainment.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(entertainment) }
            websiteButton.setOnClickListener { onWebsiteClick(entertainment.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntertainmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entertainment, parent, false)
        return EntertainmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntertainmentViewHolder, position: Int) {
        holder.bind(entertainments[position])
    }

    override fun getItemCount() = entertainments.size
}