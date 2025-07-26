package com.example.myweddingmateapp.models

data class FavoriteItem(
    val id: String,
    val name: String,
    val category: String,
    val imageRes: Int,
    val websiteUrl: String? = null
)