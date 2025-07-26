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
import com.example.myweddingmateapp.models.Jewellery

class JewelleryAdapter(
    private val jewelleryList: List<Jewellery>,
    private val onFavoriteClick: (Jewellery) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<JewelleryAdapter.JewelleryViewHolder>() {

    inner class JewelleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.jewelleryImage)
        private val nameView: TextView = itemView.findViewById(R.id.jewelleryName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.jewelleryRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.jewelleryRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(jewellery: Jewellery) {
            imageView.setImageResource(jewellery.imageResId)
            nameView.text = jewellery.name
            ratingBar.rating = jewellery.rating
            ratingText.text = "Rating: ${jewellery.rating} (${jewellery.reviewCount}+ reviews)"

            favoriteButton.setImageResource(
                if (jewellery.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(jewellery) }
            websiteButton.setOnClickListener { onWebsiteClick(jewellery.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JewelleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jewellery, parent, false)
        return JewelleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: JewelleryViewHolder, position: Int) {
        holder.bind(jewelleryList[position])
    }

    override fun getItemCount() = jewelleryList.size
}