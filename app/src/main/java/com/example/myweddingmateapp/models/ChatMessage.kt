package com.example.myweddingmateapp.models

data class ChatMessage(
    val senderName: String,
    val message: String,
    val displayTime: String,
    val isReceived: Boolean,
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val messageType: String = "text",
    val mediaUrl: String? = null
)