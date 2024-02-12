package com.example.heatguardapp.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.example.heatguardapp.data.SensorResult
import com.example.heatguardapp.data.sensor.SensorUtils
import com.example.heatguardapp.domain.model.BluetoothBLEDeviceModel
import com.example.heatguardapp.domain.model.IBluetoothBLEDevice
import com.example.heatguardapp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothBLEDeviceController(
    private val context : Context,
    private val bluetoothAdapter: BluetoothAdapter,
) : IBluetoothBLEDevice {

    private val _scannedDevices = MutableStateFlow<List<BluetoothBLEDeviceModel>>(emptyList())

    override val data: MutableSharedFlow<Resource<SensorResult>>
        get() = MutableSharedFlow()
    override val scannedDevices: StateFlow<List<BluetoothBLEDeviceModel>>
        get() = _scannedDevices.asStateFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var gatt: BluetoothGatt? = null

    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallback = object : ScanCallback(){

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scannedDevice = BluetoothBLEDeviceModel(
                device = result.device,
                name = result.device.name,
                address = result.device.address,
                rssi = result.rssi,
            )
            updateScannedDevices(scannedDevice)
        }
    }

    private fun updateScannedDevices(device: BluetoothBLEDeviceModel) {
        val currentDevices = _scannedDevices.value.toMutableList()
        if(device !in currentDevices){
            currentDevices.add(device)
            _scannedDevices.value = currentDevices
        }
    }
    override fun startScanning() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Scanning Ble devices..."))
        }
        isScanning = true
        bleScanner.startScan(null,scanSettings,scanCallback)
    }

    override fun stopScanning() {
        isScanning = false
        bleScanner.stopScan(scanCallback)
    }

    override fun connect(bluetoothDevice: BluetoothBLEDeviceModel) {
        bluetoothDevice.device.connectGatt(context,false, gattCallback)
        isScanning = false
        bleScanner.stopScan(scanCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            // Handle connection state changes
            // Implement connection retry logic if necessary
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            // Handle service discovery
            // Adjust MTU space if needed
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            // Perform actions after MTU change, if required
            // For example, enable notifications/indications on characteristics
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            // Handle characteristic value changes
            // Extract data from the characteristic value and update UI or process it further
            when (characteristic.uuid) {
                // Handle notifications from different characteristics if needed
                // Example:
                UUID.fromString(RASP_SENSOR_CHARACTERISTICS_UUID) -> {
                    // Process the received data from the characteristic value
                    // Example: Parse bytes to SensorData object
                    val sensorData = SensorUtils.parseSensorData(characteristic.value)
                    // Emit the sensor data using the shared flow
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = sensorData))
                    }
                }
                // Add other characteristic UUIDs and processing logic as needed
                else -> Unit
            }
        }
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun startReceiving() {
        TODO("Not yet implemented")
    }
}