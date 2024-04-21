package com.example.heatguardapp.api.models

data class UserInfoApi(
    val ambientTemp: Float = 0f,
    val skinTemp: Float = 0f,
    val coreTemp: Float = 0f,
    val ambientHumidity: Float = 0f,
    val bmi: Float = 0f,
    val heartRate: Float = 0f,
    val age: Float = 0f,
    val skinRes: Float = 0f,
    val heatstroke: Float = 0f
)