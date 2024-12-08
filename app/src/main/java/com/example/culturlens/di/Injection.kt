package com.example.culturlens.di

import android.content.Context
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.ui.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}