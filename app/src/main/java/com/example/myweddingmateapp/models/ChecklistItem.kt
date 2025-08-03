package com.example.myweddingmateapp.models

data class ChecklistItem(
    val itemId: String = "",
    val category: String = "",
    val favoriteId: String = "",
    val timestamp: String = "",
    val name: String = "",
    val description: String = "",
    var isCompleted: Boolean = false,
    val budget: Double = 0.0,
    val notes: String = "",
    val dueDate: String = "",
    val priority: Int = 0,
    val vendorId: String = "",
    val vendorName: String = "",
    val userId: String = ""
) {
    fun isHighPriority() = priority > 5
}