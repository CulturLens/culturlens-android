package com.example.culturlens.model

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    val id: Int,
    val message: String,
    @SerializedName("created_at") val createdAt: String?,
    val username: String,
    @SerializedName("action_type") val actionType: String?,
    @SerializedName("profile_photo") val profilePhoto: String?,
    @SerializedName("user_id") val userId: Int
)