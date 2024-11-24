package com.example.culturlens.di

import android.content.Context
import com.example.culturlens.ui.login.UserPreference
import com.example.culturlens.ui.login.UserRepository
import com.example.culturlens.ui.login.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}