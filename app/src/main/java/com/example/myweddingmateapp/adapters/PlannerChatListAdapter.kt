package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.User

class PlannerChatListAdapter(
    private var allUsers: List<User>,
    private val currentUserRole: String = "Wedding Planner",
    private val currentUserId: String,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<PlannerChatListAdapter.ViewHolder>() {

    private val filteredList = mutableListOf<User>()

    init {
        filterList()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val lastMessage: TextView = view.findViewById(R.id.txtLastMessage)
        val time: TextView = view.findViewById(R.id.txtTime)
        val profileImage: ImageView = view.findViewById(R.id.imgProfile)
        val unreadBadge: View = view.findViewById(R.id.viewUnreadBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = filteredList[position]
        holder.name.text = user.name
        holder.lastMessage.text = user.lastMessage
        holder.time.text = user.lastMessageTime

        if (user.unreadCount > 0) {
            holder.unreadBadge.visibility = View.VISIBLE
        } else {
            holder.unreadBadge.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = filteredList.size

    fun updateList(newList: List<User>) {
        allUsers = newList
        filterList()
        notifyDataSetChanged()
    }

    fun updateLastMessage(userId: String, message: String, timestamp: String, recipientId: String, unreadCount: Int = 0) {
        val index = filteredList.indexOfFirst { it.uid == userId }
        if (index != -1) {
            filteredList[index].lastMessage = message
            filteredList[index].lastMessageTime = timestamp
            filteredList[index].recipientId = recipientId
            filteredList[index].unreadCount = unreadCount
            notifyItemChanged(index)
        }
    }

    private fun filterList() {
        filteredList.clear()
        for (user in allUsers) {
            if (currentUserRole == "User") {
                if (user.role == "Wedding Planner") {
                    filteredList.add(user)
                }
            } else if (currentUserRole == "Wedding Planner") {
                if (user.role == "User") {
                    filteredList.add(user)
                }
            }
        }
    }
}