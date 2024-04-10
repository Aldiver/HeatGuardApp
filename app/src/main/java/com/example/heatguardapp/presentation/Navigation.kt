package com.example.heatguardapp.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.heatguardapp.presentation.components.BluetoothScanScreen
import com.example.heatguardapp.presentation.components.HomeScreen
import com.example.heatguardapp.presentation.components.StartScreen
import com.example.heatguardapp.presentation.components.UserScreen

@Composable
fun AppNavigator(
    onBluetoothStateChanged: () -> Unit,
) {
    val navController = rememberNavController()
//    val userViewModel: UserInfoViewModel = viewModel()
    val userInfoViewModel: UserInfoPreferencesViewModel = hiltViewModel()
    val age by userInfoViewModel.age.observeAsState()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route) {

            if (age != null) {
                navController.navigate("home_screen")
            } else {
                StartScreen (
//                    viewModel = userInfoViewModel,
                    onNextScreen = {navController.navigate(Screen.HomeScreen.route)}
                )
            }
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.UserScreen.route) {
            UserScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String){
    data object StartScreen: Screen("start_screen")
    data object HomeScreen: Screen("home_screen")
    data object  UserScreen: Screen("user_screen")
    data object StatScreen: Screen("stat_screen")
}