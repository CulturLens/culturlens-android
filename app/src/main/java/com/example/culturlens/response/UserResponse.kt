package com.example.culturlens.response

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val profilePhoto: String,
    val phone: String
)
