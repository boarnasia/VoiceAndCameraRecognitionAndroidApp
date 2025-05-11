package com.example.voiceandcamerarecognition

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.media.Image
import android.util.Base64
import androidx.annotation.OptIn
import java.nio.ByteBuffer


/**
 * [Image] を base64 エンコードした文字列に変換する
 *
 * This function takes an [Image] object, accesses the pixel data from the first plane,
 * converts it into a ByteArray, and then encodes the ByteArray into a Base64 string
 * using the default encoding flags.
 *
 * @return The Base64 encoded string representing the image data, or `null` if the image has no planes.
 * @receiver The [Image] object to convert.
 */
fun Image.toBase64(): String? {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

class CameraManager(private val context: Context) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var onResultCallback: ((String) -> Unit)? = null
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder() // ImageLabelingOptions.Builder() を使用
            .setConfidenceThreshold(0.7f)
            .build()
    )
    public val groupedLabels: Map<String, List<String>> = mapOf(
        "HumanGroup" to listOf(
            "Sitting",
            "Beard",
            "Smile",
            "Person",
            "Toe",
            "Laugh",
            "Dude",
            "Crew",
            "Mouth",
            "Grandparent",
            "Eyelash",
            "Hair",
            "Muscle",
            "Bride",
            "Baby",
            "Hand"
        ),

        "FireGroup" to listOf(
            "Fire",
            "Smoke",
        )
    )

    fun setOnResultCallback(callback: (String) -> Unit) {
        onResultCallback = callback
    }

    @OptIn(ExperimentalGetImage::class)
    suspend fun startCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
            ProcessCameraProvider.getInstance(context).also { future ->
                future.addListener({
                    continuation.resume(future.get())
                }, ContextCompat.getMainExecutor(context))
            }
        }

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        analyzeImage(image, imageProxy)
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                imageAnalyzer
            )
            this.cameraProvider = cameraProvider
        } catch (e: Exception) {
            Log.e("CameraManager", "カメラの起動に失敗しました", e)
        }
    }

    private fun analyzeImage(image: InputImage, imageProxy: ImageProxy) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val results = mutableListOf<String>()
                var humanGroupConfidenceSum = 0.0f
                var humanGroupMatchCount = 0

                for (label in labels) {
                    results.add("${label.text} (${(label.confidence * 100).toInt()}%)")

                    if (groupedLabels["HumanGroup"]?.contains(label.text) == true) {
                        humanGroupConfidenceSum += label.confidence
                        humanGroupMatchCount++
                    }
                }

                if (humanGroupMatchCount > 0) {
                    val averageConfidence = humanGroupConfidenceSum / humanGroupMatchCount
                    val averageConfidencePercentage = (averageConfidence * 100).toInt()
                    results.add(0, "HumanGroup(${averageConfidencePercentage}%)")
                }
                /*
                val results = labels.map { label ->
                    "${label.text} (${(label.confidence * 100).toInt()}%)"
                }
                */
                onResultCallback?.invoke(results.joinToString(", "))
            }
            .addOnFailureListener { e ->
                Log.e("CameraManager", "画像解析に失敗しました", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun shutdown() {
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
} 