package com.example.myweddingmateapp.models

data class CoupleProfile(
    val id: String,
    val partner1Name: String,
    val partner2Name: String,
    val email: String,
    val phone: String,
    val address: String,
    val weddingDate: String,
    val profilePhotoPath: String,
    val createdAt: Long,
    val updatedAt: Long
)