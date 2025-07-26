package com.example.myweddingmateapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photography(
    val id: String = "",
    val name: String = "",
    val imageResId: String = "", // Changed to String
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val websiteUrl: String = "",
    val style: String? = null,
    val experience_years: Int? = null,
    val starting_price: Int? = null,
    var isFavorite: Boolean = false
) : java.io.Serializable, Parcelable