package com.example.heatguardapp.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.heatguardapp.presentation.ScanViewModel

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScanScreen(scanViewModel: ScanViewModel = viewModel()) {
    val scannedDevices by scanViewModel.scannedDevices.observeAsState(initial = emptyList())

    Column {
        Text("Scanned Devices:")
        LazyColumn {
            items(scannedDevices) { device ->
                Text(device.name ?: "Unknown Device")
            }
        }
    }
}