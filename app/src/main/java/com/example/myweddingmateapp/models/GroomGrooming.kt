package com.example.myweddingmateapp.models

data class GroomGrooming(
    val id: String,
    val name: String,
    val imageResId: Int,
    val rating: Float,
    val reviewCount: Int,
    val websiteUrl: String,
    var isFavorite: Boolean = false
)