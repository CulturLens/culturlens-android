package com.example.culturlens.ui.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForumViewModel : ViewModel() {

    private val _forumLikeStatus = MutableLiveData<MutableMap<String, Boolean>>()
    val forumLikeStatus: LiveData<MutableMap<String, Boolean>> = _forumLikeStatus

    init {
        _forumLikeStatus.value = mutableMapOf()
    }

    fun toggleLikeStatus(forumId: String) {
        _forumLikeStatus.value?.let { likeMap ->
            likeMap[forumId] = !(likeMap[forumId] ?: false)
            _forumLikeStatus.value = likeMap
        }
    }

    fun getLikeStatus(forumId: String): Boolean {
        return _forumLikeStatus.value?.get(forumId) ?: false
    }
}
