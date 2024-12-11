package com.example.culturlens.pref

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.culturlens.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID_KEY = intPreferencesKey("userId")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val PHONE_KEY = stringPreferencesKey("phone")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    // Menyimpan session user
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[EMAIL_KEY] = user.email
            preferences[NAME_KEY] = user.name
            preferences[USERNAME_KEY] = user.username
            preferences[IS_LOGIN_KEY] = user.isLogin
            preferences[TOKEN_KEY] = user.token
            preferences[PHONE_KEY] = user.phone ?: "" // Pastikan phone disertakan
        }
        Log.d("UserPreference", "Saved session: ${user.username}, phone: ${user.phone}") // Log untuk memastikan
    }

    // Mengambil session user
    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val user = UserModel(
                userId = preferences[USER_ID_KEY] ?: 0,
                email = preferences[EMAIL_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                username = preferences[USERNAME_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false,
                token = preferences[TOKEN_KEY] ?: "",
                // Menangani phone yang nullable dengan default ""
                phone = preferences[PHONE_KEY] ?: "" // Jika phone null, beri nilai default ""
            )
            Log.d("UserPreference", "Retrieved session: userId=${user.userId}, username=${user.username}, phone=${user.phone}") // Log untuk memeriksa
            user
        }
    }

    // Status login
    val isLogin: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGIN_KEY] ?: false
    }

    // Logout dan hapus session
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
