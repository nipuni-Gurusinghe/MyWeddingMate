package com.example.myweddingmateapp.models

data class User(
    val uid: String = "",
    var name: String = "",
    var email: String = "",
    val role: String = "",
    var lastMessage: String = "",
    var lastMessageTime: String = "",
    var recipientId: String = "",
    var unreadCount: Int = 0,
    var phoneNumber: String = "",
    var bio: String = "",
    var location: String = "",
    var company: String = "",
    var yearsExperience: Int? = null,
    var priceRange: String = "",
    var specialties: List<String> = emptyList(),
    var availability: String = "",
    var instagram: String = "",
    var facebook: String = "",
    var website: String = "",
    var profileImage: String = "",
    var services: List<String> = emptyList(),
    var portfolioImages: List<String> = emptyList()
)