package com.example.culturlens.api

import com.example.culturlens.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val userId: Int, val name: String, val username: String, val email: String)

interface ApiService {
    @POST("users")
    fun register(@Body request: RegisterRequest): Call<ApiResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<ApiResponse>
}