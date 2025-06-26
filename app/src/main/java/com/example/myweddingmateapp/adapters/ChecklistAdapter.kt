package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Client

class ChecklistAdapter(
    private val clients: List<Client>,
    private val onItemClick: (Client) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ClientViewHolder>() {

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textClientName)
        val image: ImageView = itemView.findViewById(R.id.imageProfile)

        fun bind(client: Client) {
            name.text = client.name
            image.setImageResource(client.imageResId)
            itemView.setOnClickListener { onItemClick(client) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(clients[position])
    }

    override fun getItemCount(): Int = clients.size
}
