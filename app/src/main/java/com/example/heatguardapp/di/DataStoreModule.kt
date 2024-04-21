package com.example.heatguardapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.heatguardapp.api.service.UpdateModelService
import com.example.heatguardapp.utils.UserDataPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")
@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Singleton
    @Provides
    fun provideUserDataPreferencesManager(@ApplicationContext context: Context): UserDataPreferencesManager = UserDataPreferencesManager(context)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl("http://172.18.109.100:5000/")
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideUpdateModelService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): UpdateModelService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(UpdateModelService::class.java)
}