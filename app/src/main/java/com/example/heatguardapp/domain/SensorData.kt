package com.example.heatguardapp.domain

import androidx.compose.ui.graphics.Color

data class SensorData(
    val title: String,
    val icon: Int,
    val value: String,
    val unit: String,
    val isHigh: Boolean,
)
