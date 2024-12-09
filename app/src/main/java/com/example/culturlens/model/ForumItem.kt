package com.example.culturlens.model

data class ForumItem(
    val id: String,
    val title: String,
    val description: String,
    val username: String,
    val image: String?,
    val user_id: Int,
    val created_at: String,
    var isLiked: Boolean = false
)


