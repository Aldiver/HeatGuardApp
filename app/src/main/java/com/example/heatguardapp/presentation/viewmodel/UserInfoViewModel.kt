package com.example.heatguardapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.heatguardapp.dao.AppDatabase
import com.example.heatguardapp.dao.UserInfoDao
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun insertUserInfo(userInfo: UserInfoEntity) {
        viewModelScope.launch {
            userRepository.insertUserInfo(userInfo)
        }
    }

    fun getUserInfoLive(): LiveData<List<UserInfoEntity>> {
        return userRepository.getUserInfoLive()
    }

    fun deleteUser(userInfo: UserInfoEntity) {
        viewModelScope.launch {
            userRepository.deleteUser(userInfo)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch {
            userRepository.deleteAllUsers()
        }
    }
}