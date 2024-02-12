package com.example.heatguardapp.domain.model

import android.bluetooth.BluetoothDevice

data class BluetoothBLEDeviceModel(
    val device: BluetoothDevice,
    val name: String,
    val address: String,
    val rssi: Int
)
