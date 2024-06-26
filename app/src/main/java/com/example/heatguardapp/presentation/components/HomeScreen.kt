package com.example.heatguardapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.heatguardapp.R

@Composable
fun HomeScreen(
    navController: NavController) {

    Column(
        modifier = Modifier
             .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .fillMaxHeight(fraction = .1f)
                 .padding(12.dp),
             horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically,
         ) {
             Text(
                 text = "HeatGuard",
                 style = MaterialTheme.typography.headlineMedium,
                 modifier = Modifier.weight(1f),
                 color = MaterialTheme.colorScheme.primary
             )

             Image(
                 painter = painterResource(id = R.drawable.stat),
                 contentDescription = "",
                 modifier = Modifier
                     .size(32.dp)
                     .clickable {
                         navController.navigate("stat_screen")
                     }
             )

             Image(
                 painter = painterResource(id = R.drawable.heatguard_logo),
                 contentDescription = "",
                 modifier = Modifier
                     .clickable {
                     navController.navigate("user_screen")
                 }
            )
         }

        Divider(modifier = Modifier.fillMaxWidth())

        BluetoothScanScreen()
    }
}