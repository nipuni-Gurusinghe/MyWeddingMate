package com.example.myweddingmateapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BeauticianBride(
    var id: String = "",  // Change from val to var
    val name: String = "",
    val imageResId: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val websiteUrl: String = "",
    var isFavorite: Boolean = false
) : Parcelable