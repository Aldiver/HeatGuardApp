package com.example.heatguardapp.utils

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class HeatstrokeClassifier(assetManager: AssetManager) {

    private var interpreter: Interpreter
    init {
        val model = loadModelFile(assetManager, "heatguard.tflite")
        interpreter = Interpreter(model)
    }

    private fun loadModelFile(assetManager: AssetManager, filename: String): ByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assetManager.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyHeatStroke(input: FloatArray): String {
        // Prepare input tensor
        val inputBuffer = ByteBuffer.allocateDirect(input.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        inputBuffer.put(input)
        inputBuffer.rewind()

        // Prepare output tensor
        val outputBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer()

        // Run inference
        interpreter.run(inputBuffer, outputBuffer)

        // Get the predicted class (0 or 1)
        val predictedClass = if (outputBuffer[0] > 0.5f) 1 else 0

        return "Prediction is ${outputBuffer[0]}"
    }
}
