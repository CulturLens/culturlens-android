package com.example.culturlens.response

data class LoginRequest(
    val email: String,
    val password: String
)
data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val refreshToken: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val registrationTime: String
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)