package com.example.culturlens.model

data class ForumItem(
    val id: String,
    val username: String,
    val content: String,
    val imageUrl: String,
    var isLiked: Boolean = false
)

