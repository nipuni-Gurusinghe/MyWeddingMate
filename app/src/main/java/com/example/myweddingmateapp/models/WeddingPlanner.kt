package com.example.myweddingmateapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeddingPlanner(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val bio: String = "",
    val experience: Int = 0,
    val specialties: List<String> = emptyList(),
    val priceRange: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isAvailable: Boolean = true,
    val profileImageUrl: String = "",
    val portfolioImages: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val website: String = "",
    val socialMedia: Map<String, String> = emptyMap()
) : Parcelable {

    // Helper methods for UI display
    fun getRatingText(): String {
        return if (rating > 0.0) {
            "★ ${"%.1f".format(rating)}"
        } else {
            "★ No rating"
        }
    }

    fun getReviewText(): String {
        return when {
            reviewCount == 0 -> "(No reviews)"
            reviewCount == 1 -> "(1 review)"
            else -> "($reviewCount reviews)"
        }
    }

    fun getExperienceText(): String {
        return when {
            experience == 0 -> "New planner"
            experience == 1 -> "1 year experience"
            else -> "$experience years experience"
        }
    }

    fun getSpecialtiesText(): String {
        return if (specialties.isNotEmpty()) {
            specialties.joinToString(", ")
        } else {
            "General Wedding Planning"
        }
    }

    fun getAvailabilityText(): String {
        return if (isAvailable) "Available" else "Unavailable"
    }

    fun getServicesText(): String {
        return if (services.isNotEmpty()) {
            services.joinToString(", ")
        } else {
            "Wedding Planning Services"
        }
    }

    fun getLocationText(): String {
        return location.ifEmpty { "Location not specified" }
    }

    fun getPriceRangeText(): String {
        return if (priceRange.isNotEmpty()) {
            "Budget: $priceRange"
        } else {
            "Contact for pricing"
        }
    }

    fun getBioText(): String {
        return bio.ifEmpty {
            "Professional wedding planner dedicated to making your special day perfect."
        }
    }

    // Helper method to get truncated bio for list display
    fun getTruncatedBio(maxLength: Int = 120): String {
        val bioText = getBioText()
        return if (bioText.length > maxLength) {
            "${bioText.substring(0, maxLength)}..."
        } else {
            bioText
        }
    }

    // Helper method to check if planner has complete profile
    fun hasCompleteProfile(): Boolean {
        return name.isNotEmpty() &&
                email.isNotEmpty() &&
                location.isNotEmpty() &&
                bio.isNotEmpty() &&
                specialties.isNotEmpty()
    }

    // Helper method to get social media display text
    fun getSocialMediaText(): String {
        return if (socialMedia.isNotEmpty()) {
            socialMedia.entries.joinToString(", ") { "${it.key}: ${it.value}" }
        } else {
            "No social media provided"
        }
    }

    // Helper method to get main specialty
    fun getMainSpecialty(): String {
        return specialties.firstOrNull() ?: "Wedding Planning"
    }

    // Helper method to format rating for display
    fun getFormattedRating(): String {
        return if (rating > 0.0) {
            "${"%.1f".format(rating)}/5.0"
        } else {
            "Not rated yet"
        }
    }

    // Helper method to check if planner is highly rated (4.0+)
    fun isHighlyRated(): Boolean {
        return rating >= 4.0 && reviewCount > 0
    }

    // Helper method to check if planner is experienced (3+ years)
    fun isExperienced(): Boolean {
        return experience >= 3
    }

    // Helper method to get contact info summary
    fun getContactSummary(): String {
        return buildString {
            if (phone.isNotEmpty()) append("📞 $phone")
            if (email.isNotEmpty()) {
                if (isNotEmpty()) append(" • ")
                append("✉️ $email")
            }
            if (website.isNotEmpty()) {
                if (isNotEmpty()) append(" • ")
                append("🌐 Website")
            }
        }
    }

    // Helper method to create a new instance with updated id (avoiding copy method conflict)
    fun withId(newId: String): WeddingPlanner {
        return this.copy(id = newId)
    }

    // Helper method to update availability
    fun withAvailability(available: Boolean): WeddingPlanner {
        return this.copy(isAvailable = available)
    }

    // Helper method to update rating
    fun withRating(newRating: Double, newReviewCount: Int): WeddingPlanner {
        return this.copy(rating = newRating, reviewCount = newReviewCount)
    }

    override fun toString(): String {
        return "WeddingPlanner(id='$id', name='$name', email='$email', location='$location', rating=$rating, available=$isAvailable)"
    }
}