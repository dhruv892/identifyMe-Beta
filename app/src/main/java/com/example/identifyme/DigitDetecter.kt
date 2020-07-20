package com.example.identifyme

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class DigitsDetector(private var context: Context) {


    private val TAG = this.javaClass.simpleName

    // The tensorflow lite file
    private var tflite: Interpreter? = null

    fun classify(bitmap: Bitmap): String {
        initializeInterpreter()
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.")
        }

        val resizedImage = Bitmap.createScaledBitmap(
            bitmap,
            28,
            28,
            false
        )
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        // Define an array to store the model output.
        val output = Array(1) { FloatArray(NUMBER_LENGTH) }

        // Run inference with the input data.
        tflite?.run(byteBuffer, output)

        // find the digit that has the highest probability
        // and return it a human-readable string.
        val result = output[0]

        var ans=0
        val max = result.max()
        for(i in result.indices){
            if(result[i] == max){
                ans=i
            }
        }
        return "$ans - $max"

    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        val byteBuffer = ByteBuffer.allocateDirect(BYTE_SIZE_OF_FLOAT * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)
        bitmap.getPixels(pixels, 0, DIM_IMG_SIZE_X, 0, 0, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            // Convert RGB to grayscale and normalize pixel value to [0..1].
            val normalizedPixelValue = (r + g + b) / 3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }
        return byteBuffer
    }


    /**
     * Load the model file from the assets folder
     */
    @Throws(IOException::class)
    private fun loadModelFile(activity: Context): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = activity.assets.openFd(MODEL_PATH)
        val inputStream =
            FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }


    companion object {
        // Name of the file in the assets folder
        private const val MODEL_PATH = "mnist.tflite"

        // Specify the output size
        private const val NUMBER_LENGTH = 10

        // Specify the input size
        private const val DIM_BATCH_SIZE = 1
        const val DIM_IMG_SIZE_X = 28
        const val DIM_IMG_SIZE_Y = 28
        private const val DIM_PIXEL_SIZE = 1

        // Number of bytes to hold a float (32 bits / float) / (8 bits / byte) = 4 bytes / float
        private const val BYTE_SIZE_OF_FLOAT = 4
    }


    private fun initializeInterpreter(){
        val options = Interpreter.Options()
        options.setUseNNAPI(true)
        tflite = Interpreter((loadModelFile(context)), options)

    }

}