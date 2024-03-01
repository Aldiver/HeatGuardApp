package com.example.heatguardapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val age: String,
    val bmi: String
)
