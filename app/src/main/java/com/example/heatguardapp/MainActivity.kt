package com.example.heatguardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           WelcomeScreen()
        }
    }
}

@Composable
fun WelcomeScreen(){
    Surface(color = MaterialTheme.colorScheme.primary){
        MessageCard(name = "Al Nabigh")
    }
}

@Composable
fun MessageCard(name: String) {
    val expanded = remember { (mutableStateOf<Boolean>(false)) }
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column {
            Text(text = "Hello, ", modifier = Modifier.padding(24.dp))
            OutlinedButton(onClick = { expanded.value = !expanded.value }) {
                Text(if(expanded.value) "True Value" else "False Value", color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageCard() {
    WelcomeScreen()
}
