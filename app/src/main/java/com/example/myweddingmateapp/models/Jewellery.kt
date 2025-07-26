package com.example.myweddingmateapp.models

data class Jewellery(
    val id: String = "", // Added default empty string for Firebase deserialization
    val name: String = "",
    val imageResId: Int = 0, // This will be the drawable ID from R.drawable, not a String
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val websiteUrl: String = "",
    var isFavorite: Boolean = false
)