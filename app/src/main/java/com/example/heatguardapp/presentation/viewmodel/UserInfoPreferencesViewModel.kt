package com.example.heatguardapp.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heatguardapp.utils.UserDataPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class UserInfoPreferencesViewModel @Inject constructor(
    private val userPreferences: UserDataPreferencesManager,
): ViewModel() {

    val age = MutableLiveData<String?>()
    val bmi = MutableLiveData<String?>()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.getUserPreferences().collect { userProfile ->
                withContext(Dispatchers.Main) {
                    age.value = userProfile.age
                    bmi.value = userProfile.bmi
                }
            }
        }
    }
    fun saveUser(age: String, bmi: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.saveUserPreferences(age, bmi)
        }
    }

    suspend fun deleteUser(): Deferred<Unit> {
        return viewModelScope.async {
            userPreferences.clearAll()
        }
    }
}