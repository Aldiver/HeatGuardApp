package com.example.heatguardapp.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.heatguardapp.dao.AppDatabase
import com.example.heatguardapp.dao.UserInfoDao
import com.example.heatguardapp.data.ConnectionState
import com.example.heatguardapp.data.SensorResultManager
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.ml.ModelHeatguard
import com.example.heatguardapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import javax.inject.Inject

@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val sensorResultManager: SensorResultManager,
    application: Application
) : ViewModel(){

    private val userInfoDao: UserInfoDao

//    private val db: AppDatabase = Room.databaseBuilder(
//        application.applicationContext,
//        AppDatabase::class.java, "user-info-db"
//    ).build()

    private val model: ModelHeatguard = ModelHeatguard.newInstance(application.applicationContext)
    private val inputFeature0: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)

    private var age: Float = 0f
    private var bmi: Float = 0f
    private var ageCap: Int = 0


    init {
        userInfoDao = Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java, "user-info-db"
        ).build().userInfoDao()

        // Fetch user info and calculate age and BMI
        val userInfo = getUserInfo().value
        age = userInfo?.age?.toFloatOrNull() ?: 0f
        bmi = userInfo?.bmi?.toFloatOrNull() ?: 0f
        ageCap = 208 - (0.7 * age).toInt()
    }
//    private fun getUserInfo(): UserInfoEntity? {
//        return userInfoDao.getUserInfo().asLiveData()
//    }

    private fun getUserInfo(): LiveData<UserInfoEntity?> {
        return userInfoDao.getUserInfo().asLiveData()
    }
//
//    val userInfo = getUserInfo().value
//
//    // Extract age and BMI from user info
//    val ageString = userInfo?.age ?: "0" // Default value if age is null
//    val bmiString = userInfo?.bmi ?: "0" // Default value if BMI is null
//    val age = ageString.toFloatOrNull() ?: 0f // Convert age to int, or use default value if conversion fails
//    val bmi = bmiString.toFloatOrNull() ?: 0f

    var initializingMessage by mutableStateOf<String?>(null)
        private set
    var heatStrokeMessage by mutableIntStateOf(0)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var heartRate by mutableIntStateOf(0)
        private set
    var coreTemp  by mutableFloatStateOf(0f)
        private set
    var skinRes by mutableIntStateOf(0)
        private set
    var ambientHumidity by mutableIntStateOf(0)
        private set
    var ambientTemperature  by mutableFloatStateOf(0f)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    var togglePrediction by mutableStateOf(false)
        private set

    private fun subscribeToChanges(){
        viewModelScope.launch {
            sensorResultManager.data.collect(){
                result ->
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        heartRate = minOf(result.data.heartRate, ageCap) //capped HR
                        coreTemp = result.data.skinTemp + (.7665f * (result.data.skinTemp - result.data.ambientTemperature))
                        skinRes = result.data.skinRes
                        ambientHumidity = result.data.ambientHumidity
                        ambientTemperature = result.data.ambientTemperature

                        if(togglePrediction){
                            detectHeatStroke()
                        }
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun disconnect(){
        sensorResultManager.disconnect()
    }

    fun reconnect(){
        sensorResultManager.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        sensorResultManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        sensorResultManager.closeConnection()
    }

    fun detectHeatStroke() {
//            val model = ModelHeatguard.newInstance(context)
//            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)
////          var byteBuffer = ByteBuffer.allocate(4 * 7)
//            val inputArray = floatArrayOf(39f, 40.8f, 0.4f, bmi, 166f, age, 0f)
            val inputArray = floatArrayOf(
                ambientTemperature,
                coreTemp,
                (ambientHumidity / 100).toFloat(),
                bmi,
                heartRate.toFloat(),
                age,
                1f
            )
            //ambient temp, coreTemp (body), ambientHumidity (%), bmi, heartRate, skinRes (0/1),
            inputFeature0.loadArray(inputArray)
            val outputs = model.process(inputFeature0)
            val result = outputs.outputFeature0AsTensorBuffer.floatArray[0]

            val final_output = if (result > 0.5) 1 else 0
//            model.close()

            heatStrokeMessage = final_output
    }

    fun togglePrediction() {
        togglePrediction = !togglePrediction
    }
}