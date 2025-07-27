// BridalWear.kt
package com.example.myweddingmateapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BridalWear(
    val id: String = "",
    val name: String = "",
    val imageResId: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val websiteUrl: String = "",
    var isFavorite: Boolean = false
) : Parcelable