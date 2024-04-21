package com.example.heatguardapp.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.heatguardapp.api.repository.UpdateModelRepository
import com.example.heatguardapp.api.service.UpdateModelService
import com.example.heatguardapp.data.SensorResultManager
import com.example.heatguardapp.data.ble.SensorsBLEReceiveManager
import com.example.heatguardapp.utils.UserDataPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideSensorReceiveManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ) : SensorResultManager{
        return SensorsBLEReceiveManager(bluetoothAdapter, context)
    }

    @Provides
    fun provideUpdateModelRepository(updateModelService: UpdateModelService) = UpdateModelRepository(updateModelService)
}
