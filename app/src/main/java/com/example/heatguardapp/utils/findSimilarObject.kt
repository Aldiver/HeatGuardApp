package com.example.heatguardapp.utils

import android.util.Log
import com.example.heatguardapp.api.models.UserInfoApi
import kotlin.math.abs
fun findSimilarObject(userInfoList: List<UserInfoApi>, target: UserInfoApi): UserInfoApi? {
    Log.d("Check for Inputs:", " List $userInfoList, Target: $target")
    return userInfoList.find { info ->
        info.age == target.age &&
                info.bmi == target.bmi &&
                info.skinRes == target.skinRes &&
                (abs(info.ambientTemp - target.ambientTemp) / target.ambientTemp <= 0.05) &&
                (abs(info.skinTemp - target.skinTemp) / target.skinTemp <= 0.05) &&
                (abs(info.ambientHumidity - target.ambientHumidity) / target.ambientHumidity <= 0.05) //&&
                (abs(info.heartRate - target.heartRate) / target.heartRate <= 0.05)
    }
}
