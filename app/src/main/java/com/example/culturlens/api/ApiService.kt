package com.example.culturlens.api

import com.example.culturlens.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val token: String?)

interface ApiService {
    @POST("users")
    fun register(@Body request: RegisterRequest): Call<ApiResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<ApiResponse>
}
