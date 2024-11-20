package com.example.culturlens

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingPreferences(private val context: Context) {

    private val themeKey = booleanPreferencesKey("theme_setting")

    val themeSetting: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[themeKey] ?: false
        }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }
}