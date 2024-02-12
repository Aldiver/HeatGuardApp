package com.example.heatguardapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.heatguardapp.presentation.components.BluetoothScanScreen
import com.example.heatguardapp.presentation.components.HomeScreen
import com.example.heatguardapp.presentation.components.StartScreen
import com.example.heatguardapp.presentation.components.StatScreen
import com.example.heatguardapp.presentation.components.UserScreen

@Composable
fun AppNavigator(
    onBluetoothStateChanged: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route) {
            val hasAccount = false
            if (hasAccount) {
                navController.navigate("home_screen")
            } else {
                navController.navigate("start_screen")
            }
        }
        composable(Screen.StartScreen.route) {
            StartScreen {
                navController.navigate(Screen.HomeScreen.route)
            }
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.UserScreen.route) {
            UserScreen()
        }
        composable(Screen.StatScreen.route) {
            StatScreen()
        }
        composable(Screen.BluetoothScanScreen.route) {
            BluetoothScanScreen(
                onBluetoothStateChanged
            )
        }
    }
}

sealed class Screen(val route: String){
    data object StartScreen: Screen("start_screen")
    data object HomeScreen: Screen("home_screen")
    data object  UserScreen: Screen("user_screen")
    data object StatScreen: Screen("stat_screen")
    data object BluetoothScanScreen: Screen("bluetooth_screen")

}