package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.ItemClientBinding
import com.example.myweddingmateapp.models.Client

class PlannerClientAdapter(
    private val clients: List<Client>,
    private val onItemClick: (Client) -> Unit
) : RecyclerView.Adapter<PlannerClientAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemClientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = clients[position]
        holder.binding.apply {
            textClientName.text = client.name
            textClientEmail.text = client.email
            Glide.with(root.context)
                .load(client.profileImage)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(imageClient)
            root.setOnClickListener { onItemClick(client) }
        }
    }

    override fun getItemCount() = clients.size
}