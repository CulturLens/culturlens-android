package com.example.culturlens.pref

import com.example.culturlens.model.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(private val userPreference: UserPreference) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return userPreference.isLogin
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(userPreference: UserPreference): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}

