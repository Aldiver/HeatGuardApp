package com.example.heatguardapp.data

data class SensorResult(
    val heartRate: Int,
    val skinRes: Int,
    val skinTemp: Float,
    val ambientHumidity: Int,
    val ambientTemperature: Float,
    val connectionState: ConnectionState
)