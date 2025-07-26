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
import com.example.myweddingmateapp.models.WeddingCar

class WeddingCarAdapter(
    private val weddingCars: List<WeddingCar>,
    private val onFavoriteClick: (WeddingCar) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<WeddingCarAdapter.WeddingCarViewHolder>() {

    inner class WeddingCarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.weddingCarImage)
        private val nameView: TextView = itemView.findViewById(R.id.weddingCarName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.weddingCarRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.weddingCarRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(weddingCar: WeddingCar) {
            imageView.setImageResource(weddingCar.imageResId)
            nameView.text = weddingCar.name
            ratingBar.rating = weddingCar.rating
            ratingText.text = "Rating: ${weddingCar.rating} (${weddingCar.reviewCount}+ bookings)"

            favoriteButton.setImageResource(
                if (weddingCar.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(weddingCar) }
            websiteButton.setOnClickListener { onWebsiteClick(weddingCar.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeddingCarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wedding_car, parent, false)
        return WeddingCarViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeddingCarViewHolder, position: Int) {
        holder.bind(weddingCars[position])
    }

    override fun getItemCount() = weddingCars.size
}