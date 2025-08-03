package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.ItemClientBinding
import com.example.myweddingmateapp.models.User

class UserListAdapter(
    private val users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemClientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            textClientName.text = user.name
            textClientEmail.text = user.email

            Glide.with(root.context)
                .load(user.profileImage)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(imageClient)

            root.setOnClickListener { onItemClick(user) }
        }
    }

    override fun getItemCount() = users.size
}