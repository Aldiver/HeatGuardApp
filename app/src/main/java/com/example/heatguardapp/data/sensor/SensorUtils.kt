package com.example.heatguardapp.data.sensor

import com.example.heatguardapp.data.SensorResult
import com.example.heatguardapp.domain.model.SensorsData
import java.nio.ByteBuffer

class SensorUtils {

    companion object {
        fun parseSensorData(value: ByteArray?): SensorsData {
            // Parse the received byte array into SensorData object
            // Example: Extract individual values and construct SensorData object
            val heartRate = parseHeartRate(value.sliceArray(0 until 4))
            val coreTemp = parseTemperature(value.sliceArray(4 until 8))
            val skinRes = parseSkinResistance(value.sliceArray(8 until 12))
            val skinTemp = parseTemperature(value.sliceArray(12 until 16))
            val ambientHumid = parseHumidity(value.sliceArray(16 until 20))
            val ambientTemp = parseTemperature(value.sliceArray(20 until 24))

            return SensorsData(
                heartRate,
                coreTemp,
                skinRes,
                skinTemp,
                ambientHumid,
                ambientTemp
            )
        }
    }
    private fun parseHeartRate(bytes: ByteArray): Int {
        // Parse heart rate from 4 bytes
        return ByteBuffer.wrap(bytes).int // Assuming heart rate occupies 1 byte
    }

    private fun parseTemperature(bytes: ByteArray): Float {
        // Parse temperature from 4 bytes
        // Convert byte array to integer (assuming it represents a fixed-point value) and then divide by a scaling factor
        val value = ByteBuffer.wrap(bytes).int
        return value.toFloat() / TEMPERATURE_SCALING_FACTOR
    }

    private fun parseSkinResistance(bytes: ByteArray): String {
        // Parse skin resistance from 4 bytes
        // Implement your logic to parse skin resistance from the byte array
        val resistance = bytes[0].toInt()
        return when (resistance) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> "Unknown"
        }
    }

    private fun parseHumidity(bytes: ByteArray): Int {
        // Parse humidity from 4 bytes
        // Convert byte array to integer (assuming it represents an integer value)
        return ByteBuffer.wrap(bytes).int
    }

}