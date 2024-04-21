package com.example.heatguardapp.api.repository

import com.example.heatguardapp.api.models.UserInfoApi
import com.example.heatguardapp.api.service.UpdateModelService
import com.example.heatguardapp.utils.apiRequestFlow
import javax.inject.Inject

class UpdateModelRepository @Inject constructor(
    private val updateModelService: UpdateModelService
) {
    fun updateUserModel(userInfoApi: List<UserInfoApi>) = apiRequestFlow {
        updateModelService.updateUserModel(userInfoApi)
    }
}