package com.example.myweddingmateapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.Invitation

class InvitationAdapter(
    private val invitations: List<Invitation>,
    private val onFavoriteClick: (Invitation) -> Unit,
    private val onWebsiteClick: (String) -> Unit
) : RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>() {

    inner class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.invitationImage)
        private val nameView: TextView = itemView.findViewById(R.id.invitationName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.invitationRatingBar)
        private val ratingText: TextView = itemView.findViewById(R.id.invitationRatingText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val websiteButton: Button = itemView.findViewById(R.id.visitWebsiteButton)

        fun bind(invitation: Invitation) {
            imageView.setImageResource(invitation.imageResId)
            nameView.text = invitation.name
            ratingBar.rating = invitation.rating
            ratingText.text = "Rating: ${invitation.rating} (${invitation.reviewCount}+ orders)"

            favoriteButton.setImageResource(
                if (invitation.isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            favoriteButton.setOnClickListener { onFavoriteClick(invitation) }
            websiteButton.setOnClickListener { onWebsiteClick(invitation.websiteUrl) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invitation, parent, false)
        return InvitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.bind(invitations[position])
    }

    override fun getItemCount() = invitations.size
}