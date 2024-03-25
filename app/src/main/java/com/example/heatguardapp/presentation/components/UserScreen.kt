package com.example.heatguardapp.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.heatguardapp.R
import com.example.heatguardapp.presentation.UserInfoPreferencesViewModel
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    viewModel: UserInfoPreferencesViewModel = hiltViewModel(),
    navController: NavController
){
    val preferenceAge by viewModel.age.observeAsState()
    val preferenceBmi by viewModel.bmi.observeAsState()

    var age by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var isAgeEmpty by remember { mutableStateOf(false) }
    var isBmiEmpty by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    // Set initial values when the composable is launched
    LaunchedEffect(preferenceAge, preferenceBmi) {
        age = preferenceAge ?: ""
        bmi = preferenceBmi ?: ""
        Log.d("Message", "age is $age and BMI is $bmi")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.heatguard_logo),
            contentDescription = "",
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Age TextField
        if (isAgeEmpty) {
            Text(
                text = "Age must not be empty",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        TextField(
            value = age,
            onValueChange = {
                age = it
                isAgeEmpty = it.isBlank() // Update isAgeEmpty when TextField value changes
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number // Set keyboard type to Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // BMI TextField
        if (isBmiEmpty) {
            Text(
                text = "BMI must not be empty",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        TextField(
            value = bmi,
            onValueChange = {
                bmi = it
                isBmiEmpty = it.isBlank() // Update isBmiEmpty when TextField value changes
            },
            label = { Text("BMI") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number // Set keyboard type to Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteUser()
//                        delay(1000)
                        navController.navigate("start_screen")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .width(150.dp)
            ) {
                Text(text = "Delete")
            }
            Button(
                onClick = {
                    if (age.isNotBlank() && bmi.isNotBlank()) {
                        viewModel.saveUser(age = age, bmi = bmi)
                        navController.navigate("home_screen")
                    } else {
                        isBmiEmpty = true
                        isAgeEmpty = true
                    }
                },
                modifier = Modifier
                    .width(150.dp)
            ) {
                Text(text = "Update")
            }
        }
    }
}