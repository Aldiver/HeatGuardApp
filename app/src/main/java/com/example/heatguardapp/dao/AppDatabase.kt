package com.example.heatguardapp.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.heatguardapp.data.UserInfoEntity

@Database(entities = [UserInfoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
}