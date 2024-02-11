package com.example.heatguardapp.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.heatguardapp.data.ble.SensorsBLEReceiveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val sensorsBLEReceiveManager: SensorsBLEReceiveManager
) : ViewModel() {

    private val _scannedDevices = MutableLiveData<List<BluetoothDevice>>()
    val scannedDevices: LiveData<List<BluetoothDevice>> = _scannedDevices

    init {
        startScanning()
    }

    private fun startScanning() {
        sensorsBLEReceiveManager.startReceiving { devices ->
            _scannedDevices.postValue(devices)
        }
    }

}