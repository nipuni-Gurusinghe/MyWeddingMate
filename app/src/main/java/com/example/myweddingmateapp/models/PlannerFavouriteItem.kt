package com.example.myweddingmateapp.models

import java.util.Date

data class PlannerFavouriteItem(
    val id: String = "",
    val category: String = "",
    val vendorId: String = "",
    val addedDate: String = "",
    val budget: Double = 0.0,
    val currency: String = "USD",
    val reminderDate: Date? = null,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val userId: String = ""
)