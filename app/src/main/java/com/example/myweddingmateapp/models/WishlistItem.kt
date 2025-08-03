// WishlistItem.kt
package com.example.myweddingmateapp.models

import android.content.Intent

data class WishlistItem(
    val id: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val isFavorite: Boolean = false,
    val targetActivity: Class<*>
)