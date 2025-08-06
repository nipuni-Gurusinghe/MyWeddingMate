package com.example.myweddingmateapp.models

data class PlannerFavouriteItem(
    val id: String = "",
    val category: String = "",
    val vendorId: String = "",
    val addedDate: String = "",
    val budget: Double = 0.0,
    val isFavorite: Boolean = false,
    val  notes:String =""
)