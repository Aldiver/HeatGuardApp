package com.example.heatguardapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.heatguardapp.data.UserInfoEntity

@Dao
interface UserInfoDao {
    @Insert
    suspend fun insert(userInfo: UserInfoEntity)

    @Query("SELECT * FROM user_info")
    fun getUserInfoLive(): LiveData<List<UserInfoEntity>>

    @Delete
    suspend fun delete(userInfo: UserInfoEntity) // Added delete method

    @Query("DELETE FROM user_info") // Add delete all method
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM user_info")
    fun getUserInfo(): List<UserInfoEntity>
}
