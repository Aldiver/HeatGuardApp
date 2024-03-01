package com.example.heatguardapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.heatguardapp.data.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Insert
    suspend fun insert(userInfo: UserInfoEntity)

    @Query("SELECT * FROM user_info")
    fun getUserInfo(): Flow<UserInfoEntity?>
}
