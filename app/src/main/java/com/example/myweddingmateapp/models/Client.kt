package com.example.myweddingmateapp.models

import com.example.myweddingmateapp.R

data class Client(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    val imageResId: Int = R.drawable.ic_profile
)