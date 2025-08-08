package com.example.myweddingmateapp.models

data class Review(
    val title: String,
    val comment: String,
    val rating: Float,
    val userName: String = ""
)