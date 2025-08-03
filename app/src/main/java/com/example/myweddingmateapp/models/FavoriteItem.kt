package com.example.myweddingmateapp.models
data class FavoriteItem(
    val id: String = "",
    val category: String = "",
    val favoriteId: String = "",
    val itemId: String = "",
    val name: String = "",
    val timestamp: String = "",
    val budget: Double = 0.0,
    val isFavorite:Boolean = false
)