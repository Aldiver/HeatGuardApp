package com.example.heatguardapp.presentation.components


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.heatguardapp.api.models.UserInfoApi
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.presentation.viewmodel.CoroutinesErrorHandler
import com.example.heatguardapp.presentation.viewmodel.UpdateDataViewModel
import com.example.heatguardapp.presentation.viewmodel.UserInfoViewModel
import com.example.heatguardapp.utils.ApiResponse

@Composable
fun StatScreen(
    navController: NavController,
    viewModel: UserInfoViewModel = hiltViewModel(),
    updateViewModel: UpdateDataViewModel = hiltViewModel()
) {
    val userInfoList by viewModel.getUserInfoLive().observeAsState(initial = emptyList())


    val updateStatus by
    updateViewModel.updateStatus.observeAsState(ApiResponse.Loading)

    // Define a mutable state for userInfoApiList
    val userInfoApiList = remember { mutableStateOf<List<UserInfoApi>>(emptyList()) }

// Observe changes in userInfoList and update userInfoApiList accordingly
    LaunchedEffect(userInfoList) {
        userInfoApiList.value = userInfoList.map { userInfo ->
            UserInfoApi(
                ambientTemp = userInfo.ambientTemp,
                skinTemp = userInfo.skinTemp,
                coreTemp = userInfo.coreTemp,
                ambientHumidity = userInfo.ambientHumidity,
                bmi = userInfo.bmi,
                heartRate = userInfo.heartRate,
                age = userInfo.age,
                skinRes = userInfo.skinRes,
                heatstroke = userInfo.heatstroke
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // User info list
            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // 1 column
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                items(userInfoList) { userInfo ->
                    UserInfoListItem(userInfo = userInfo) { viewModel.deleteUser(userInfo) }
                }
            }
        }

        // Button at the bottom
        Button(
            onClick = {
                // Pass userInfoApiList to updateViewModel
                updateViewModel.updateModel(userInfoApiList.value,
                    object: CoroutinesErrorHandler {
                        override fun onError(message: String) {
//                            status.value = "Error! $message"
                        }
                    })
                viewModel.deleteAllUsers()
                Log.d("Status", "Status is $updateStatus")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
//                .padding(4.dp)
        ) {
            Text(text = "Button")
        }
    }
}

@Composable
fun UserInfoListItem(
    userInfo: UserInfoEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "A.Temp: ${userInfo.ambientTemp} °C")
                Text(text = "S.Temp: ${userInfo.skinTemp} °C")
                Text(text = "C.Temp: ${userInfo.coreTemp} °C")
                Text(text = "A.Hum: ${userInfo.ambientHumidity} %")
                Text(text = "BMI: ${userInfo.bmi}")
                Text(text = "HR: ${userInfo.heartRate}")
                Text(text = "Age: ${userInfo.age}")
                Text(text = "S.Res: ${userInfo.skinRes}")
                Text(text = "HeatStroke: ${userInfo.heatstroke}")
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}
