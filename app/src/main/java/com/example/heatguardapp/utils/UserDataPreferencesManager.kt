package com.example.heatguardapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.heatguardapp.data.UserPreferences
import com.example.heatguardapp.di.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

class UserDataPreferencesManager(
    private val context: Context
) {
    companion object {
        private val AGE_KEY = stringPreferencesKey("agePrefKey")
        private val BMI_KEY = stringPreferencesKey("bmiPrefKey")
    }
    fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { preferences ->
            UserPreferences(
                preferences[AGE_KEY],
                preferences[BMI_KEY]
            )
        }
    }

    suspend fun saveUserPreferences(age: String, bmi: String) {
        context.dataStore.edit { preferences ->
            preferences[AGE_KEY] = age
            preferences[BMI_KEY] = bmi
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}