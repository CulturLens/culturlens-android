package com.example.culturlens.api

import com.example.culturlens.model.ForumItem
import com.example.culturlens.response.CommentRequest
import com.example.culturlens.model.NotificationItem
import com.example.culturlens.response.GenericResponse
import com.example.culturlens.response.LoginRequest
import com.example.culturlens.response.LoginResponse
import com.example.culturlens.response.RegisterRequest
import com.example.culturlens.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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
    ): Call<List<ForumItem>>

    @GET("forum/{id}")
    fun getForumDetail(
        @Path("id") id: String
    ): Call<ForumItem>


    @Multipart
    @POST("forum")
    fun createPost(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("username") username: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<GenericResponse>

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") userId: Int
    ): UserResponse

    @POST("forum/comment/{forumId}")
    fun postComment(
        @Path("forumId") forumId: String,
        @Body commentRequest: CommentRequest
    ): Call<GenericResponse>

    @GET("forum/comment/{forumId}")
    fun getComments(
        @Path("forumId") forumId: String
    ): Call<List<CommentItem>>
    @GET("notifications")
    fun getNotifications(
        @Header("Authorization") token: String
    ): Call<List<NotificationItem>>
}