package com.example.culturlens.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.culturlens.MainActivity
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.ui.login.WelcomeActivity
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class SplashActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        userRepository = UserRepository.getInstance(userPreference)

        lifecycleScope.launch {
            userRepository.isLoggedIn().collect { isLoggedIn ->
                if (isLoggedIn) {
                    navigateToMain()
                } else {
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
