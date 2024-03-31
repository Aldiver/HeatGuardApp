package com.example.heatguardapp.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.heatguardapp.data.ConnectionState
import com.example.heatguardapp.data.SensorResult
import com.example.heatguardapp.data.SensorResultManager
import com.example.heatguardapp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class SensorsBLEReceiveManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
): SensorResultManager {

    private val DEVICE_NAME = "HEATGUARD"
    private val RASP_SENSOR_SERVICE_UIID = "00001811-0000-1000-8000-00805f9b34fb"
    private val RASP_SENSOR_CHARACTERISTICS_UUID = "00000540-0000-1000-8000-00805f9b34fb"
    private val ALERT_NOTIF_CHARACTERISTICS_UUID = "00002a06-0000-1000-8000-00805f9b34fb"

    override val data: MutableSharedFlow<Resource<SensorResult>> = MutableSharedFlow()

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
            if(result.device.name == DEVICE_NAME || result.device.name == "raspberrypi"){
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Connecting to device..."))
                }
                if(isScanning){
                    result.device.connectGatt(context,false, gattCallback)
                    isScanning = false
                    bleScanner.stopScan(this)
                }
            }
        }
    }

    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPTS = 5

    private val gattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(newState == BluetoothProfile.STATE_CONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services..."))
                    }
                    gatt.discoverServices()
                    this@SensorsBLEReceiveManager.gatt = gatt
                } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = SensorResult(
                            heartRate = 0,
                            skinTemp = 0f,
                            skinRes = 0,
                            ambientHumidity = 0,
                            ambientTemperature = 0f,
                            connectionState = ConnectionState.Disconnected
                        )))
                    }
                    gatt.close()
                }
            }else{
                gatt.close()
                currentConnectionAttempt+=1
                coroutineScope.launch {
                    data.emit(
                        Resource.Loading(
                            message = "Attempting to connect $currentConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"
                        )
                    )
                }
                if(currentConnectionAttempt<=MAXIMUM_CONNECTION_ATTEMPTS){
                    startReceiving()
                }else{
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt){
                printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space..."))
                }
                gatt.requestMtu(517)
            }

            val characteristic = findCharacteristics(RASP_SENSOR_SERVICE_UIID, RASP_SENSOR_CHARACTERISTICS_UUID)
            if(characteristic == null){
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find temp and humidity publisher"))
                }
                return
            }
            enableNotification(characteristic)
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            val characteristic = findCharacteristics(RASP_SENSOR_SERVICE_UIID, RASP_SENSOR_CHARACTERISTICS_UUID)
            if(characteristic == null){
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find temp and humidity publisher"))
                }
                return
            }
            enableNotification(characteristic)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic){
                when(uuid){
                    UUID.fromString(RASP_SENSOR_CHARACTERISTICS_UUID) -> {
                        val heartRate = value[0].toInt() and 0xFF
                        val skinRes = if (value[1].toInt() > 0) 1 else 0
                        val skinTempMultiplier = if (value[2].toInt() > 0) -1 else 1
                        val skinTempValue = value[3].toInt() + value[4].toInt() / 10f
                        val skinTemp = skinTempMultiplier * skinTempValue
                        //get byte value humidity %
                        val ambientHumidity = value[5].toInt()
                        //converting byte value to floating point (signed)
                        val ambientTempMultiplier = if (value[6].toInt() > 0) -1 else 1
                        val ambientTempValue = value[7].toInt() + value[8].toInt() / 10f
                        val ambientTemperature = ambientTempMultiplier * ambientTempValue

                        // Creating and returning the SensorResult object
                        val sensorData = SensorResult(
                            heartRate = heartRate,
                            skinRes = skinRes,
                            skinTemp = skinTemp,
                            ambientHumidity = ambientHumidity,
                            ambientTemperature = ambientTemperature,
                            connectionState = ConnectionState.Connected // Assuming you have a default ConnectionState value
                        )

                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(data = sensorData)
                            )
                            Log.d("Emmitter->","$sensorData")
                        }
                    }
                    else -> Unit
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BLE Write", "Characteristic written successfully $characteristic")
            } else {
                Log.e("BLE Write", "Characteristic write failed with status: $status")
            }
        }

    }

    private fun enableNotification(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic, true) == false){
                Log.d("BLEReceiveManager","set characteristics notification failed")
                print("this really happend or nah?")
                return
            }
            writeDescription(cccdDescriptor, payload)
        }
    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        gatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }

    override fun notifyAlert(status: Int) {
        val alertCharacteristic = findCharacteristics(RASP_SENSOR_SERVICE_UIID, ALERT_NOTIF_CHARACTERISTICS_UUID)

        // Check if the characteristic was found
        if (alertCharacteristic != null) {
            // Write your data to the characteristic here
            val data = status.toString().toByteArray()
            Log.e("TAG", "Characteristic found")
            writeCharacteristic(alertCharacteristic, data)
        } else {
            Log.e("TAG", "Characteristic not found")
        }
    }

    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        characteristic.setValue(value)
        gatt?.writeCharacteristic(characteristic)
    }

    private fun findCharacteristics(serviceUUID: String, characteristicsUUID:String):BluetoothGattCharacteristic?{
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    override fun startReceiving() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Scanning Ble devices..."))
        }
        isScanning = true
        bleScanner.startScan(null,scanSettings,scanCallback)
    }

    override fun reconnect() {
        gatt?.connect()
    }

    override fun disconnect() {
        gatt?.disconnect()
    }



    override fun closeConnection() {
        bleScanner.stopScan(scanCallback)
        val characteristic = findCharacteristics(RASP_SENSOR_SERVICE_UIID, RASP_SENSOR_CHARACTERISTICS_UUID)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
        }
        gatt?.close()
    }

    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d("TempHumidReceiveManager","set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

}