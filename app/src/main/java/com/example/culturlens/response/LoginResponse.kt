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
    val userId: Int,
    val name: String,
    val username: String,
    val message: String,
    val token: String,
    val refreshToken: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)
