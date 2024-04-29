package com.example.heatguardapp.repository

import androidx.lifecycle.LiveData
import com.example.heatguardapp.dao.UserInfoDao
import com.example.heatguardapp.data.UserInfoEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userInfoDao: UserInfoDao) {

    fun getUserInfoLive(): LiveData<List<UserInfoEntity>> {
        return userInfoDao.getUserInfoLive()
    }

    fun getUserInfo(): Flow<List<UserInfoEntity>> {
        return userInfoDao.getUserInfo()
    }

    suspend fun insertUserInfo(userInfo: UserInfoEntity) {
        userInfoDao.insert(userInfo)
    }

    suspend fun deleteUser(userInfo: UserInfoEntity) {
        userInfoDao.delete(userInfo)
    }

    suspend fun deleteAllUsers() {
        userInfoDao.deleteAllUsers()
    }
}
