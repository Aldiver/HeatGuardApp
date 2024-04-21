package com.example.heatguardapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.heatguardapp.dao.AppDatabase
import com.example.heatguardapp.dao.UserInfoDao
import com.example.heatguardapp.data.UserInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val userInfoDao: UserInfoDao

    private val db: AppDatabase = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "user-info-db"
    ).build()

    init {
        userInfoDao = db.userInfoDao()
    }

    fun insertUserInfo(userInfo: UserInfoEntity) {
        viewModelScope.launch {
            userInfoDao.insert(userInfo = userInfo)
        }
    }

    fun getUserInfo(): LiveData<List<UserInfoEntity>> {
        return userInfoDao.getUserInfo()
    }

    fun deleteUser(userInfo: UserInfoEntity) { // Method to delete a user entry
        viewModelScope.launch {
            userInfoDao.delete(userInfo)
        }
    }

    // Method to delete all user entries
    fun deleteAllUsers() {
        viewModelScope.launch {
            userInfoDao.deleteAllUsers()
        }
    }
}