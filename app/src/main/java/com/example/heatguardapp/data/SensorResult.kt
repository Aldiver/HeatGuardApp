package com.example.heatguardapp.data

data class SensorResult(
    val heartRate: Int,
    val coreTemp: Int,
    val skinRes: String,
    val skinTemp: Float,
    val ambientHumidity: Int,
    val ambientTemperature: Float,
    val connectionState: ConnectionState
)