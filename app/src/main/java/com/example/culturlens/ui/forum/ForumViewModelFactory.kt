package com.example.culturlens.ui.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForumViewModelFactory(private val repository: ForumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForumViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
