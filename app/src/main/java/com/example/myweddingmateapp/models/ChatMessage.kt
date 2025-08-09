package com.example.myweddingmateapp.models

data class ChatMessage(
    val senderId: String = "",
    val recipientId: String = "",

    val message: String = "",
    val displayTime: String = "",
    val isReceived: Boolean = false,
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val messageType: String = "text",
    val mediaUrl: String? = null
)