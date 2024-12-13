package com.example.culturlens.api

import com.example.culturlens.response.ForumsResponse
import com.example.culturlens.response.GenericResponse
import com.example.culturlens.response.LoginRequest
import com.example.culturlens.response.LoginResponse
import com.example.culturlens.response.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<LoginResponse>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("forums")
    fun getForums(
    ): Call<ForumsResponse>

    @Multipart
    @POST("forum")
    fun createPost(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("username") username: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<GenericResponse>

}