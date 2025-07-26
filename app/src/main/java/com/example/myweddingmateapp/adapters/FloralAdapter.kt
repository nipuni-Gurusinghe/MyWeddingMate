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
import com.example.myweddingmateapp.models.Floral

class FloralAdapter(
    private val floralList: List<Floral>,
    private val onFavoriteClick: (Floral) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<FloralAdapter.FloralViewHolder>() {

    inner class FloralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.floralImage)
        private val nameView: TextView = itemView.findViewById(R.id.floralName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.floralRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.floralRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(floral: Floral) {
            imageView.setImageResource(floral.imageResId)
            nameView.text = floral.name
            ratingBar.rating = floral.rating
            ratingText.text = "Rating: ${floral.rating} (${floral.reviewCount}+ reviews)"

            favoriteButton.setImageResource(
                if (floral.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(floral) }
            websiteButton.setOnClickListener { onWebsiteClick(floral.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloralViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_floral, parent, false)
        return FloralViewHolder(view)
    }

    override fun onBindViewHolder(holder: FloralViewHolder, position: Int) {
        holder.bind(floralList[position])
    }

    override fun getItemCount() = floralList.size
}