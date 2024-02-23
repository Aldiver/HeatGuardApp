package com.example.heatguardapp.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heatguardapp.data.ConnectionState
import com.example.heatguardapp.data.SensorResultManager
import com.example.heatguardapp.ml.ModelHeatguard
import com.example.heatguardapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import javax.inject.Inject
@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val sensorResultManager: SensorResultManager
) : ViewModel(){

    var initializingMessage by mutableStateOf<String?>(null)
        private set
    var heatStrokeMessage by mutableStateOf<String?>("no heatstroke data")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var heartRate by mutableIntStateOf(12)
        private set
    var coreTemp  by mutableIntStateOf(0)
        private set
    var skinRes by mutableStateOf<String?>(null)
        private set
    var skinTemp  by mutableFloatStateOf(0f)
        private set
    var ambientHumidity by mutableIntStateOf(0)
        private set
    var ambientTemperature  by mutableFloatStateOf(0f)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges(){
        Log.d("viewmodel", "subscribe")
        viewModelScope.launch {
            Log.d("viewmodel", "launch")
            Log.d("viewmodel", "$sensorResultManager")
            sensorResultManager.data.collect(){
                result ->
                Log.d("viewmodel", "collect")
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        heartRate = result.data.heartRate
                        coreTemp = result.data.coreTemp
                        skinRes = result.data.skinRes
                        skinTemp = result.data.skinTemp
                        ambientHumidity = result.data.ambientHumidity
                        ambientTemperature = result.data.ambientTemperature
                        Log.d("View Model observer checkek", "$heartRate")
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

    fun detectHeatStroke(context: Context) {
        val model = ModelHeatguard.newInstance(context)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)
        var byteBuffer = ByteBuffer.allocate(4 * 7)

//        val input = floatArrayOf(39f, 40.8f, 0.4f, 24f, 166f, 38f, 0f)

        // Perform classification
//        val prediction = classifier.classifyHeatStroke(input)

//        inputFeature0.loadBuffer(byteBuffer)
        inputFeature0.loadArray(floatArrayOf(39f, 40.8f, 0.4f, 24f, 166f, 38f, 0f))
        val outputs = model.process(inputFeature0)
        val result = outputs.outputFeature0AsTensorBuffer.floatArray[0]

        val final_output = if (result > 0.5) 1 else 0
        model.close()

        heatStrokeMessage = "Prediction: $final_output"
        // Print the prediction
//        println(prediction)
    }

}