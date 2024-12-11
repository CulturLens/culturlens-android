package com.example.culturlens.response

data class UserRequest(
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val profilePhotoUrl: String? = null
)
