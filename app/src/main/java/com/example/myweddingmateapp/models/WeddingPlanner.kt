package com.example.myweddingmateapp.models


import android.os.Parcel
import android.os.Parcelable

/**
 * Data model for Wedding Planner
 * Implements Parcelable to pass between activities
 */
data class WeddingPlanner(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val experience: Int = 0, // Years of experience
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val location: String = "",
    val specialties: List<String> = emptyList(),
    val priceRange: String = "", // e.g., "$5000-$10000"
    val profileImageUrl: String = "",
    val isAvailable: Boolean = true,
    val portfolioImages: List<String> = emptyList(),
    val services: List<String> = emptyList()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(bio)
        parcel.writeInt(experience)
        parcel.writeFloat(rating)
        parcel.writeInt(reviewCount)
        parcel.writeString(location)
        parcel.writeStringList(specialties)
        parcel.writeString(priceRange)
        parcel.writeString(profileImageUrl)
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeStringList(portfolioImages)
        parcel.writeStringList(services)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeddingPlanner> {
        override fun createFromParcel(parcel: Parcel): WeddingPlanner {
            return WeddingPlanner(parcel)
        }

        override fun newArray(size: Int): Array<WeddingPlanner?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Get formatted rating text
     */
    fun getRatingText(): String {
        return if (rating > 0) "★ $rating" else "No rating"
    }

    /**
     * Get formatted review text
     */
    fun getReviewText(): String {
        return when {
            reviewCount == 0 -> "No reviews"
            reviewCount == 1 -> "($reviewCount review)"
            else -> "($reviewCount reviews)"
        }
    }

    /**
     * Get experience text
     */
    fun getExperienceText(): String {
        return when {
            experience == 0 -> "New planner"
            experience == 1 -> "$experience year experience"
            else -> "$experience years experience"
        }
    }

    /**
     * Get specialties as formatted string
     */
    fun getSpecialtiesText(): String {
        return if (specialties.isNotEmpty()) {
            specialties.joinToString(", ")
        } else {
            "General wedding planning"
        }
    }

    /**
     * Check if planner is highly rated
     */
    fun isHighlyRated(): Boolean {
        return rating >= 4.5f && reviewCount >= 10
    }

    /**
     * Get availability status text
     */
    fun getAvailabilityText(): String {
        return if (isAvailable) "Available" else "Fully booked"
    }
}