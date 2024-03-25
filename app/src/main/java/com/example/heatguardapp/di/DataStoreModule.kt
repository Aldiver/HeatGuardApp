package com.example.heatguardapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.heatguardapp.utils.UserDataPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")
@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Singleton
    @Provides
    fun provideUserDataPreferencesManager(@ApplicationContext context: Context): UserDataPreferencesManager = UserDataPreferencesManager(context)
}