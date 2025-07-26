package com.example.myweddingmateapp.models

data class BeauticianGroom(
    val id: String,
    val name: String,
    val imageResId: Int,
    val rating: Float,
    val reviewCount: Int,
    val websiteUrl: String,
    var isFavorite: Boolean = false
)