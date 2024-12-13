package com.example.culturlens.response

data class LikesResponse(
    val user_id: String,
    val like_count: Int,
    val likes: List<LikeItem>
)

data class LikeItem(
    val id: Int,
    val post_id: Int,
    val user_id: Int,
    val created_at: String
)