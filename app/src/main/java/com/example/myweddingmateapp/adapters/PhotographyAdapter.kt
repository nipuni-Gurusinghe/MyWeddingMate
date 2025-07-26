package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Photography
import com.example.myweddingmateapp.utils.ResourceHelper

class PhotographyAdapter(
    private val photographyList: List<Photography>,
    private val onFavoriteClick: (Photography) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<PhotographyAdapter.PhotographyViewHolder>() {

    inner class PhotographyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photographyImage)
        private val nameView: TextView = itemView.findViewById(R.id.photographyName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.photographyRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.photographyRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(photography: Photography) {
            imageView.setImageResource(ResourceHelper.getDrawable(photography.imageResId))
            nameView.text = photography.name
            ratingBar.rating = photography.rating
            ratingText.text = "Rating: ${photography.rating} (${photography.reviewCount}+ reviews)"

            favoriteButton.setImageResource(
                if (photography.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(photography) }
            websiteButton.setOnClickListener { onWebsiteClick(photography.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotographyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photography, parent, false)
        return PhotographyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotographyViewHolder, position: Int) {
        holder.bind(photographyList[position])
    }

    override fun getItemCount() = photographyList.size
}