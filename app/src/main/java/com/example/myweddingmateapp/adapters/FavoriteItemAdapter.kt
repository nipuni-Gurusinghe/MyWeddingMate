package com.example.myweddingmateapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.databinding.ItemFavoriteBinding
import com.example.myweddingmateapp.models.FavoriteItem


class FavoriteItemAdapter(
    private val items: List<FavoriteItem>,
    private val onItemClick: (FavoriteItem) -> Unit
) : RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteItem) {
            binding.tvCategory.text = item.category
            binding.tvItemId.text = item.itemId
            binding.tvTimestamp.text = item.timestamp
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}