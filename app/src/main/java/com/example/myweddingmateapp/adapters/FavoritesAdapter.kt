package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.databinding.ItemFavoriteBinding
import com.example.myweddingmateapp.models.FavoriteItem

class FavoritesAdapter(
    private val onItemClick: (FavoriteItem) -> Unit,
    private val onRemoveClick: (FavoriteItem) -> Unit
) : ListAdapter<FavoriteItem, FavoritesAdapter.FavoriteViewHolder>(DiffCallback()) {

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FavoriteItem) {
            binding.apply {
                name.text = item.name
                category.text = when(item.category) {
                    "beauticianBride" -> "Bridal Beautician"
                    "venue" -> "Wedding Venue"
                    else -> item.category.replaceFirstChar { it.uppercase() }
                }

                item.imageRes?.let { image.setImageResource(it) }

                root.setOnClickListener { onItemClick(item) }
                removeButton.setOnClickListener { onRemoveClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
        override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem) =
            oldItem.id == newItem.id && oldItem.category == newItem.category

        override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem) =
            oldItem == newItem
    }
}
