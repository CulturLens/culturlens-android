package com.example.culturlens.model

data class UserModel(
    val userId: Int = 0,
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val isLogin: Boolean = false,
    val token: String = ""
)
