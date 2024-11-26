package com.example.heatguardapp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heatguardapp.api.models.UserInfoApi
import com.example.heatguardapp.data.ConnectionState
import com.example.heatguardapp.data.SensorResultManager
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.ml.ModelHeatguard
import com.example.heatguardapp.repository.UserRepository
import com.example.heatguardapp.utils.Resource
import com.example.heatguardapp.utils.UserDataPreferencesManager
import com.example.heatguardapp.utils.findSimilarObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject

@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val sensorResultManager: SensorResultManager,
    private val userPreferences: UserDataPreferencesManager,
    private val userRepository: UserRepository,
    application: Application
) : ViewModel(){

    private val model: ModelHeatguard = ModelHeatguard.newInstance(application.applicationContext)
    private val inputFeature0: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 8), DataType.FLOAT32)

    private var userInfoApiList by mutableStateOf<List<UserInfoApi>>(emptyList())

    var age by mutableFloatStateOf(0f)
        private set
    var bmi by mutableFloatStateOf(0f)
        private set

    private var ageCap: Int = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.getUserPreferences().collect { userProfile ->
                withContext(Dispatchers.Main) {
                    age = userProfile.age?.toFloatOrNull() ?: 0f
                    bmi = userProfile.bmi?.toFloatOrNull() ?: 0f

                    ageCap = 208 - (0.7 * age).toInt()
                    Log.d("SensorInit", "agecap value: $ageCap")
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserInfo().collect { userInfoList ->
                Log.d("User Info List:", "$userInfoList")
                withContext(Dispatchers.Main){
                    for (userInfoEntity in userInfoList) {
                        val userInfoApi = UserInfoApi(
                            ambientTemp = userInfoEntity.ambientTemp,
                            skinTemp = userInfoEntity.skinTemp,
                            coreTemp = userInfoEntity.coreTemp,
                            ambientHumidity = userInfoEntity.ambientHumidity,
                            bmi = userInfoEntity.bmi,
                            heartRate = userInfoEntity.heartRate,
                            age = userInfoEntity.age,
                            skinRes = userInfoEntity.skinRes
                        )
                        userInfoApiList += userInfoApi
                    }
                }
            }
        }
    }

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

    var skinTemp  by mutableFloatStateOf(0f)
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
                Log.d("viewmodel", "collect $result")
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        heartRate = minOf(result.data.heartRate, ageCap) //capped HR
                        coreTemp = result.data.skinTemp + (.7665f * (result.data.skinTemp - result.data.ambientTemperature))
                        skinRes = result.data.skinRes
                        skinTemp = result.data.skinTemp
                        ambientHumidity = result.data.ambientHumidity
                        ambientTemperature = result.data.ambientTemperature

                        //Auto calib
                        Log.d("ApiList", "$userInfoApiList")

                        val currValue = UserInfoApi(
                            ambientTemperature,
                            skinTemp,
                            coreTemp,
                            (ambientHumidity).toFloat(),
                            bmi,
                            heartRate.toFloat(),
                            age,
                            skinRes.toFloat(),
                            0.0f
                        )
                        val data = findSimilarObject(userInfoApiList, currValue)
                        Log.d("Similar Object:", "$data")

                        if(togglePrediction){
                            detectHeatStroke(data?.heatstroke?.toInt())
//                            heatStrokeMessage = if (data != null) {
//                                data.heatstroke?.toInt()!!
//                                notifyAlert(heatStrokeMessage)
//                                Log.d("AutoCalib", "calibrated hs value: ${data.heatstroke.toInt()}")
//                            } else{
//                                Log.d("AutoCalib:", "Failed autocalib checker")
//                                detectHeatStroke()
//                            }
                        }
                        Log.d("Heatstroke", "Toggle: $togglePrediction HS Data $heatStrokeMessage")
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
        sensorResultManager.disconnect()
        initializeConnection()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        sensorResultManager.startReceiving()
    }

    private fun notifyAlert(value: Int){
//        if(heatStrokeMessage != value){
            sensorResultManager.notifyAlert(value)
        Log.d("Notify Alert:", "$value")
//        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorResultManager.closeConnection()
    }

    private fun detectHeatStroke(data : Int?){
//         val inputArray = floatArrayOf(37.7f, 37.15631672f, 39.43051572f, 0.1f, 20.88450016f, 85f, 23f, 1f) // testing heatstroke
//         use below to use actual sensor readings
        if (data != null){
            heatStrokeMessage = data
        }else{
            val inputArray = floatArrayOf(
                ambientTemperature,
                skinTemp,
                coreTemp,
                (ambientHumidity / 100).toFloat(),
                bmi,
                heartRate.toFloat(),
                age,
                skinRes.toFloat()
            )
            //ambient temp, coreTemp (body), ambientHumidity (%), bmi, heartRate, skinRes (0/1),
            inputFeature0.loadArray(inputArray)
            val outputs = model.process(inputFeature0)
            val result = outputs.outputFeature0AsTensorBuffer.floatArray[0]

            val finalOutput = if (result > 0.5) 1 else 0
            heatStrokeMessage = finalOutput

        }
        notifyAlert(heatStrokeMessage)
    }

    fun togglePredictionButton() {
        togglePrediction = !togglePrediction
    }
}