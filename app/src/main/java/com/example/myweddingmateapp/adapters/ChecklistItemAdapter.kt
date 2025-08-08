package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.databinding.ItemChecklistBinding
import com.example.myweddingmateapp.models.ChecklistItem
import java.text.NumberFormat
import java.util.Locale

class ChecklistItemAdapter(
    private val items: List<ChecklistItem>,
    private val onItemClick: (ChecklistItem) -> Unit
) : RecyclerView.Adapter<ChecklistItemAdapter.ViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    inner class ViewHolder(val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChecklistItem) {
            with(binding) {
                tvItemName.text = item.name
                tvCategory.text = item.category
                tvBudget.text = currencyFormat.format(item.budget)
                tvDueDate.text = item.dueDate
                cbCompleted.isChecked = item.isCompleted

                root.setOnClickListener { onItemClick(item) }
                cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                    item.isCompleted = isChecked
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChecklistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}