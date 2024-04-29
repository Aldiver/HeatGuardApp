package com.example.heatguardapp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.heatguardapp.api.models.UserInfoApi
import com.example.heatguardapp.api.repository.UpdateModelRepository
import com.example.heatguardapp.utils.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

// UpdateViewModel
@HiltViewModel
class UpdateDataViewModel @Inject constructor(
    private val updateModelRepository: UpdateModelRepository,
    private val application: Application
): BaseViewModel() {

    private val _updateStatus = MutableLiveData<UpdateStatus>()
    val updateStatus = _updateStatus

    fun updateModel(userInfo: List<UserInfoApi>, coroutinesErrorHandler: CoroutinesErrorHandler) {
        baseRequest(_updateStatus, coroutinesErrorHandler) {
            updateModelRepository.updateUserModel(userInfo)
                .map { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            val responseBody = response.data.bytes()
                            saveFileToAssets(responseBody)
                            UpdateStatus.Success
                        }
                        is ApiResponse.Failure -> {
                            UpdateStatus.Error("Failed to update model: ${response.errorMessage}")
                        }
                        is ApiResponse.Loading -> {
                            UpdateStatus.Error("Loading...")
                        }
                    }
                }
                .catch { e ->
                    emit(UpdateStatus.Error("Error: ${e.localizedMessage ?: "Unknown error"}"))
                }
        }
    }

    private fun saveFileToAssets(fileContent: ByteArray) {
        try {
            val filename = "ModelHeatguard.tflite"
            val assetManager = application.applicationContext.assets

            // Check if the file already exists
            if (assetManager.list("")?.contains(filename) == true) {
                // Delete the existing file
                val existingFile = File(application.applicationContext.filesDir, filename)
                existingFile.delete()
            }

            // Save the new file
            val outputStream = FileOutputStream(File(application.applicationContext.filesDir, filename))
            outputStream.write(fileContent)
            outputStream.close()
        } catch (e: IOException) {
            Log.e("Error", "Error saving file to assets: ${e.localizedMessage}")
        }
    }
}

// UpdateStatus
sealed class UpdateStatus {
    object Success : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}