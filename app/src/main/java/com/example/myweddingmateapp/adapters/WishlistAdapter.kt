package com.example.myweddingmateapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.WishlistItem

class WishlistAdapter(
    private val items: List<WishlistItem>,
    private val onItemClick: (WishlistItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemImage)
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val description: TextView = itemView.findViewById(R.id.itemDescription)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item = items[position]

        holder.image.setImageResource(item.imageRes)
        holder.title.text = item.title
        holder.description.text = item.description

        holder.favoriteIcon.setImageResource(
            if (item.isFavorite) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.favoriteIcon.setOnClickListener {
            // Handle favorite toggle here
            // You might want to update the item's isFavorite status
            // and notifyItemChanged(position)
        }
    }

    override fun getItemCount() = items.size
}