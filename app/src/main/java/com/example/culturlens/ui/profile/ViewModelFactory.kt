package com.example.culturlens.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.culturlens.MainViewModel
import com.example.culturlens.di.Injection
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.ui.login.signin.SigninViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

}