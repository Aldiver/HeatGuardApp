package com.example.heatguardapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
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
