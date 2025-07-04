package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.ChatMessage

class PlannerChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<PlannerChatAdapter.ChatViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isReceived) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) R.layout.item_message_received
        else R.layout.item_message_sent
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSender: TextView? = itemView.findViewById(R.id.textSender)
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)

        fun bind(message: ChatMessage) {
            textSender?.text = message.senderName
            textMessage.text = message.message
            textTime.text = message.displayTime
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
}