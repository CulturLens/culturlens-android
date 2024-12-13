package com.example.culturlens.ui.forum

import com.example.culturlens.api.ApiService
import com.example.culturlens.response.ForumsResponse
import retrofit2.Call

class ForumRepository(private val apiService: ApiService) {

    fun getForums(): Call<ForumsResponse> {
        return apiService.getForums()
    }
}