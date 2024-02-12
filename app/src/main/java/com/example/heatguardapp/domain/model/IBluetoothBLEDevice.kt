package com.example.heatguardapp.domain.model

import com.example.heatguardapp.data.SensorResult
import com.example.heatguardapp.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IBluetoothBLEDevice {

    val data: MutableSharedFlow<Resource<SensorResult>>

    val scannedDevices: StateFlow<List<BluetoothBLEDeviceModel>>
    fun startScanning()
    fun stopScanning()
    fun connect(bluetoothDevice : BluetoothBLEDeviceModel)
    fun disconnect()
    fun startReceiving()
}