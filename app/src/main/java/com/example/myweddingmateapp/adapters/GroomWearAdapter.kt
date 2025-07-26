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
import com.example.myweddingmateapp.models.GroomWear

class GroomWearAdapter(
    private val groomWearList: List<GroomWear>,
    private val onFavoriteClick: (GroomWear) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<GroomWearAdapter.GroomWearViewHolder>() {

    inner class GroomWearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.groomWearImage)
        private val nameView: TextView = itemView.findViewById(R.id.groomWearName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.groomWearRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.groomWearRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(groomWear: GroomWear) {
            imageView.setImageResource(groomWear.imageResId)
            nameView.text = groomWear.name
            ratingBar.rating = groomWear.rating
            ratingText.text = "Rating: ${groomWear.rating} (${groomWear.reviewCount}+ grooms)"

            favoriteButton.setImageResource(
                if (groomWear.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(groomWear) }
            websiteButton.setOnClickListener { onWebsiteClick(groomWear.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroomWearViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_groom_wear, parent, false)
        return GroomWearViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroomWearViewHolder, position: Int) {
        holder.bind(groomWearList[position])
    }

    override fun getItemCount() = groomWearList.size
}