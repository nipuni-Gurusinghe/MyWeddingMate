// BridalWearAdapter.kt
package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.BridalWear
import com.example.myweddingmateapp.utils.ResourceHelper

class BridalWearAdapter(
    private val bridalWearList: List<BridalWear>,
    private val onFavoriteClick: (BridalWear) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<BridalWearAdapter.BridalWearViewHolder>() {

    inner class BridalWearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.bridalWearImage)
        private val nameView: TextView = itemView.findViewById(R.id.bridalWearName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.bridalWearRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.bridalWearRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(bridalWear: BridalWear) {
            imageView.setImageResource(ResourceHelper.getDrawable(bridalWear.imageResId, "bridalwear"))
            nameView.text = bridalWear.name
            ratingBar.rating = bridalWear.rating
            ratingText.text = "Rating: ${bridalWear.rating} (${bridalWear.reviewCount}+ reviews)"

            favoriteButton.setImageResource(
                if (bridalWear.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(bridalWear) }
            websiteButton.setOnClickListener { onWebsiteClick(bridalWear.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BridalWearViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bridal_wear, parent, false)
        return BridalWearViewHolder(view)
    }

    override fun onBindViewHolder(holder: BridalWearViewHolder, position: Int) {
        holder.bind(bridalWearList[position])
    }

    override fun getItemCount() = bridalWearList.size
}