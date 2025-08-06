package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.ItemPlannerFavouriteBinding
import com.example.myweddingmateapp.models.PlannerFavouriteItem

class PlannerFavouriteAdapter(
    private var items: List<PlannerFavouriteItem>,
    private val onItemClick: (PlannerFavouriteItem) -> Unit,
    private val onFavoriteToggle: (PlannerFavouriteItem) -> Unit
) : RecyclerView.Adapter<PlannerFavouriteAdapter.PlannerFavouriteViewHolder>() {

    inner class PlannerFavouriteViewHolder(val binding: ItemPlannerFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlannerFavouriteItem) {
            binding.tvCategory.text = item.category
            binding.tvVendorId.text = item.vendorId
            binding.tvAddedDate.text = "Added: ${item.addedDate}"
            binding.tvBudget.text = if (item.budget > 0) "$${"%,.2f".format(item.budget)}" else "Set Budget"
            binding.ivFavorite.setImageResource(if (item.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_favorite_border)
            binding.ivFavorite.setOnClickListener { onFavoriteToggle(item) }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannerFavouriteViewHolder {
        val binding = ItemPlannerFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlannerFavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlannerFavouriteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<PlannerFavouriteItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}