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
import com.example.myweddingmateapp.models.BeauticianGroom

class BeauticianGroomAdapter(
    private val beauticians: List<BeauticianGroom>,
    private val onFavoriteClick: (BeauticianGroom) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<BeauticianGroomAdapter.BeauticianGroomViewHolder>() {

    inner class BeauticianGroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.beauticianGroomImage)
        private val nameView: TextView = itemView.findViewById(R.id.beauticianGroomName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.beauticianGroomRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.beauticianGroomRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(beautician: BeauticianGroom) {
            imageView.setImageResource(beautician.imageResId)
            nameView.text = beautician.name
            ratingBar.rating = beautician.rating
            ratingText.text = "Rating: ${beautician.rating} (${beautician.reviewCount}+ grooms)"

            favoriteButton.setImageResource(
                if (beautician.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(beautician) }
            websiteButton.setOnClickListener { onWebsiteClick(beautician.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeauticianGroomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beautician_groom, parent, false)
        return BeauticianGroomViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeauticianGroomViewHolder, position: Int) {
        holder.bind(beauticians[position])
    }

    override fun getItemCount() = beauticians.size
}