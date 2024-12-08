package com.example.culturlens.model

data class ForumItem(
    val id: String,
    val title: String,
    val description: String,
    val username: String,
    val image: String,
    val createdAt: String,
    var isLiked: Boolean = false
)

