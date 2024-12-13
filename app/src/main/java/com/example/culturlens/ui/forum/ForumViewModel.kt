package com.example.culturlens.ui.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.culturlens.model.ForumItem
import com.example.culturlens.response.ForumsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumViewModel(private val repository: ForumRepository) : ViewModel() {

    private val _forums = MutableLiveData<List<ForumItem>>()
    val forums: LiveData<List<ForumItem>> get() = _forums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchForums() {
        _isLoading.value = true

        repository.getForums().enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    forumsResponse?.forums?.let {
                        _forums.value = it.sortedByDescending { forum -> forum.created_at }
                    }
                } else {
                    _errorMessage.value = "Failed to load forums"
                }
            }

            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }
}





