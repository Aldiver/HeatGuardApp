package com.example.heatguardapp.domain.model

data class SensorsData(
    val heartRate: Int,
    val coreTemp: Float,
    val skinRes: String,
    val skinTemp: Float,
    val ambientHumid: Int,
    val ambientTemp: Float
)
