package com.example.myweddingmateapp.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.BeauticianBride
import com.example.myweddingmateapp.utils.ResourceHelper

class BeauticianBrideAdapter(
    private val beauticians: List<BeauticianBride>,
    private val onFavoriteClick: (BeauticianBride) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<BeauticianBrideAdapter.BeauticianBrideViewHolder>() {

    inner class BeauticianBrideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.beauticianBrideImage)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val name: TextView = itemView.findViewById(R.id.beauticianBrideName)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val ratingText: TextView = itemView.findViewById(R.id.ratingText)
        val websiteButton: Button = itemView.findViewById(R.id.websiteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeauticianBrideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beautician_bride, parent, false)
        return BeauticianBrideViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeauticianBrideViewHolder, position: Int) {
        val beautician = beauticians[position]

        try {
            holder.image.setImageResource(
                ResourceHelper.getDrawable(beautician.imageResId, "beautician")
            )
            holder.name.text = beautician.name
            holder.ratingBar.rating = beautician.rating
            holder.ratingText.text = "Rating: ${beautician.rating} (${beautician.reviewCount}+)"

            holder.favoriteButton.setImageResource(
                if (beautician.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            holder.favoriteButton.setOnClickListener {
                onFavoriteClick(beautician)
            }

            holder.websiteButton.setOnClickListener {
                onWebsiteClick(beautician.websiteUrl)
            }
        } catch (e: Exception) {
            //Log.e("Adapter", "Error binding view", e)
        }
    }

    override fun getItemCount() = beauticians.size
}