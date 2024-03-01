package com.example.heatguardapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.heatguardapp.R
import com.example.heatguardapp.data.UserInfoEntity
import com.example.heatguardapp.presentation.UserInfoViewModel

@Composable
fun StartScreen(viewModel: UserInfoViewModel, onNextScreen: () -> Unit) {
    var age by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var isAgeEmpty by remember { mutableStateOf(false) }
    var isBmiEmpty by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.heatguard_logo),
            contentDescription = "",
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (age.isNotBlank() && bmi.isNotBlank()) {
                    viewModel.insertUserInfo(UserInfoEntity(age = age, bmi = bmi))
                    onNextScreen()
                } else {
                    isBmiEmpty = true
                    isAgeEmpty = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }
    }
}
