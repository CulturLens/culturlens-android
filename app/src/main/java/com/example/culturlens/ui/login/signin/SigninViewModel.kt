package com.example.culturlens.ui.login.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturlens.model.UserModel
import com.example.culturlens.pref.UserRepository
import kotlinx.coroutines.launch

class SigninViewModel (private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}