package com.example.myweddingmateapp.models

data class Entertainment(
    val id: String = "",
    val name: String = "",
    val imageResId: Int = 0,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val websiteUrl: String = "",
    var isFavorite: Boolean = false
)