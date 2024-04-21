package com.example.heatguardapp.api.service

import com.example.heatguardapp.api.models.UserInfoApi
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UpdateModelService {
    @POST("update_model")
    suspend fun updateUserModel(@Body userInfoApi: List<UserInfoApi>): Response<ResponseBody>
}