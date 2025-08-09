package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class PlannerChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<PlannerChatAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RECEIVED = 0
        private const val VIEW_TYPE_SENT = 1
        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isReceived) VIEW_TYPE_RECEIVED else VIEW_TYPE_SENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutRes = when (viewType) {
            VIEW_TYPE_RECEIVED -> R.layout.item_message_received
            else -> R.layout.item_message_sent
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        private val messageText: TextView
        private val messageTime: TextView

        init {
            messageText = itemView.findViewById(R.id.textMessage)
            messageTime = itemView.findViewById(R.id.textTime)

        }

        fun bind(message: ChatMessage) {

            messageText.text = message.message
            messageTime.text = timeFormat.format(Date(message.timestamp))
        }
    }
}