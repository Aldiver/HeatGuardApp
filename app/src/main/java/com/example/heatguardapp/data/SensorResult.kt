package com.example.heatguardapp.data

data class SensorResult(
    val heartRate:Float,
    val skinTemp: Float,
    val skinResistance: Float,
    val ambientHumidity: Float,
    val ambientTemperature: Float,
    val connectionState: ConnectionState
)
