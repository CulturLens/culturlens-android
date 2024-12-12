package com.example.culturlens.response

import com.example.culturlens.model.ForumItem

data class ForumsResponse(
    val message: String,
    val timestamp: String,
    val forums: List<ForumItem>
)

data class GenericResponse(
    val message: String,
    val commentId: Int,
    val notificationId: Int
)
