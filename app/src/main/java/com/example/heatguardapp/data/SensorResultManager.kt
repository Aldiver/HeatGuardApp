package com.example.heatguardapp.data

import com.example.heatguardapp.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface SensorResultManager {

    val data: MutableSharedFlow<Resource<SensorResult>>
    fun reconnect()
    fun disconnect()
    fun closeConnection()
    fun startReceiving()
    fun notifyAlert(status: Int)
}