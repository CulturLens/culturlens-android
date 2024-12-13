package com.example.culturlens.ui.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturlens.model.UserModel
import com.example.culturlens.pref.UserRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForumViewModel : ViewModel() {

    private val _forumLikeStatus = MutableLiveData<MutableMap<String, Boolean>>()
    val forumLikeStatus: LiveData<MutableMap<String, Boolean>> = _forumLikeStatus

    init {
        _forumLikeStatus.value = mutableMapOf()
    }

    fun toggleLikeStatus(forumId: String, isLiked: Boolean) {
        _forumLikeStatus.value = _forumLikeStatus.value?.apply {
            this[forumId] = isLiked
        }
    }

    fun getLikeStatus(forumId: String): Boolean {
        return _forumLikeStatus.value?.get(forumId) ?: false
    }
}




