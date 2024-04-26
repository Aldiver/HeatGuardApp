package com.example.heatguardapp.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.heatguardapp.R
import com.example.heatguardapp.data.ConnectionState
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.domain.SensorData
import com.example.heatguardapp.presentation.viewmodel.SensorsViewModel
import com.example.heatguardapp.presentation.viewmodel.UserInfoViewModel
import com.example.heatguardapp.presentation.permissions.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScanScreen(
    viewModel: SensorsViewModel = hiltViewModel(),
    userInfoViewModel: UserInfoViewModel = hiltViewModel()
) {
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState
    val loadingMessage = viewModel.initializingMessage
    val backgroundColor = if (viewModel.togglePrediction) Color.Transparent else Color.Green
    val toggleColor = if (viewModel.togglePrediction) Color.Red else Color.Green
    val fontColor = if (viewModel.togglePrediction) Color.Black else Color.White
    val errorMessage = viewModel.errorMessage

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                    if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                        viewModel.reconnect()
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    if (bleConnectionState == ConnectionState.Connected) {
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            if (bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(0.7f)
                .fillMaxWidth()
//                .padding(bottom = 16.dp)
        ){
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth(),
                content = {
                    // Other sensor data cards
                    listOf(
                        SensorData(
                            "Heart Rate",
                            R.drawable.electrocardiogram,
                            viewModel.heartRate.toString(),
                            "bpm"
                        ),
                        SensorData(
                            "Skin Res",
                            R.drawable.hydrating,
                            if (viewModel.skinRes > 0) "High" else "Low",
                            ""),
                        SensorData(
                            "Core Temp",
                            R.drawable.core_temp,
                            String.format("%.2f", viewModel.coreTemp),
                            "°C"
                        ),
                        SensorData(
                            "Skin Temp",
                            R.drawable.temperature,
                            String.format("%.2f", viewModel.skinTemp),
                            "°C"
                        ),
                        SensorData(
                            "A. Humid",
                            R.drawable.humidity,
                            viewModel.ambientHumidity.toString(),
                            "%"
                        ),
                        SensorData(
                            "A. Temperature",
                            R.drawable.temperatures,
                            viewModel.ambientTemperature.toString(),
                            "°C"
                        ),
                    ).forEach { sensorData ->
                        item {
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),

                                ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = sensorData.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Image(
                                        painter = painterResource(id = sensorData.icon),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(60.dp)
                                    )
                                    Text(
                                        text = sensorData.value,
                                        fontSize = 28.sp,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = sensorData.unit,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            )
            if(bleConnectionState == ConnectionState.CurrentlyInitializing){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Black.copy(alpha = .5f))
                        .fillMaxSize()
                ){
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxSize()
                .weight(.1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when(bleConnectionState){
                ConnectionState.CurrentlyInitializing -> {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        text ="$loadingMessage",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ConnectionState.Uninitialized -> {
                    Button(
                        onClick = { viewModel.initializeConnection() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ){
                        Text(
                            text = "BLE Uninitialized: Reconnect Again"
                        )
                    }
                }

                ConnectionState.Connected -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        if(viewModel.togglePrediction){
                            if (viewModel.heatStrokeMessage == 1) {
                                Text(
                                    text = "HeatStroke detected",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }else{
                                Text(
                                    text = "No HeatStroke detected",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }else{
                            Text ("")
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(fraction = .9f)
                                .fillMaxHeight(fraction = 1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                        ){
                            Button(
                                onClick = { viewModel.togglePrediction() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = fontColor
                                ),
                                border = BorderStroke(
                                    width = 5.dp,
                                    color = toggleColor
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(fraction = .7f)
                                    .fillMaxHeight(fraction = .7f)
                            ){
                                if(!viewModel.togglePrediction){
                                    Text(
                                        text = "Start Prediction",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                else{
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ){
                                            Box(
                                                modifier = Modifier
                                                    .background(Color.Red)
                                                    .size(20.dp)
                                                    .align(Alignment.Center)
                                            )
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(30.dp),
                                                color = Color.Red
                                            )
                                        }
                                        Text(
                                            text = "Stop Prediction",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(start = 20.dp)
                                        )
                                    }
                                }
                            }

                            if(viewModel.togglePrediction){
                                Image(
                                    painter = painterResource(id = R.drawable.store),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxHeight(fraction = .7f)
                                        .clickable {
                                            val userInfo = UserInfoEntity(
                                                ambientTemp = viewModel.ambientTemperature,
                                                skinTemp = viewModel.skinTemp,
                                                coreTemp = viewModel.coreTemp,
                                                ambientHumidity = (viewModel.ambientHumidity / 100).toFloat(),
                                                bmi = viewModel.bmi,
                                                heartRate = viewModel.heartRate.toFloat(),
                                                age = viewModel.age,
                                                skinRes = viewModel.skinRes.toFloat(),
                                                heatstroke = if (viewModel.heatStrokeMessage == 0) 1f else 0f
                                            )
                                            userInfoViewModel.insertUserInfo(userInfo)
                                        }
                                )
                            }else{
                                Spacer(
                                    modifier = Modifier.fillMaxHeight(fraction = .75f)
                                )
                            }
                        }
                    }
                }

                ConnectionState.Disconnected -> {
                    Button(
                        onClick = { viewModel.reconnect() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ){
                        Text(
                            text = "BLE Uninitialized: Reconnect Again"
                        )
                    }
                }
            }

        }
    }
}
