package com.example.voiceandcamerarecognition.util

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * デバッグ用のユーティリティクラス
 */
object DebugUtils {
    private const val TAG = "DebugUtils"

    /**
     * デバイスの基本情報を文字列として取得
     */
    fun getDeviceInfo(): String {
        return buildString {
            appendLine("=== デバイス情報 ===")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Board: ${Build.BOARD}")
            appendLine("Bootloader: ${Build.BOOTLOADER}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Display: ${Build.DISPLAY}")
            appendLine("Fingerprint: ${Build.FINGERPRINT}")
            appendLine("Host: ${Build.HOST}")
            appendLine("ID: ${Build.ID}")
            appendLine("Tags: ${Build.TAGS}")
            appendLine("Type: ${Build.TYPE}")
            appendLine("User: ${Build.USER}")
            appendLine("Time: ${Build.TIME}")
            appendLine("Incremental: ${Build.VERSION.INCREMENTAL}")
            appendLine("Security Patch: ${Build.VERSION.SECURITY_PATCH}")
            appendLine("Base OS: ${Build.VERSION.BASE_OS}")
            appendLine("Preview SDK: ${Build.VERSION.PREVIEW_SDK_INT}")
            appendLine("Codename: ${Build.VERSION.CODENAME}")
        }
    }

    /**
     * アプリケーションの情報を文字列として取得
     */
    fun getAppInfo(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return buildString {
            appendLine("=== アプリケーション情報 ===")
            appendLine("Package Name: ${context.packageName}")
            appendLine("Version Name: ${packageInfo.versionName}")
            appendLine("Version Code: ${packageInfo.versionCode}")
            appendLine("First Install Time: ${formatDate(packageInfo.firstInstallTime)}")
            appendLine("Last Update Time: ${formatDate(packageInfo.lastUpdateTime)}")
        }
    }

    /**
     * メモリ使用状況を文字列として取得
     */
    fun getMemoryInfo(): String {
        val runtime = Runtime.getRuntime()
        return buildString {
            appendLine("=== メモリ情報 ===")
            appendLine("Total Memory: ${formatBytes(runtime.totalMemory())}")
            appendLine("Free Memory: ${formatBytes(runtime.freeMemory())}")
            appendLine("Max Memory: ${formatBytes(runtime.maxMemory())}")
            appendLine("Used Memory: ${formatBytes(runtime.totalMemory() - runtime.freeMemory())}")
        }
    }

    /**
     * デバイスの状態をログに出力
     */
    fun logDeviceState(context: Context) {
        Log.d(TAG, getDeviceInfo())
        Log.d(TAG, getAppInfo(context))
        Log.d(TAG, getMemoryInfo())
        Log.d(TAG, getScreenAndCameraInfo(context))
    }

    /**
     * スクリーンサイズとカメラ解像度情報を文字列として取得
     */
    fun getScreenAndCameraInfo(context: Context): String {
        return buildString {
            appendLine("=== スクリーン情報 ===")
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val size = Point()
            windowManager.defaultDisplay.getRealSize(size)
            
            appendLine("物理サイズ: ${size.x}x${size.y}px")
            appendLine("論理サイズ: ${displayMetrics.widthPixels}x${displayMetrics.heightPixels}px")
            appendLine("密度: ${displayMetrics.density}")
            appendLine("DPI: ${displayMetrics.densityDpi}")
            appendLine("スケール: ${displayMetrics.scaledDensity}")

            appendLine("\n=== カメラ情報 ===")
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                for (cameraId in cameraManager.cameraIdList) {
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    val facingStr = when (facing) {
                        CameraCharacteristics.LENS_FACING_FRONT -> "フロントカメラ"
                        CameraCharacteristics.LENS_FACING_BACK -> "バックカメラ"
                        else -> "外部カメラ"
                    }
                    
                    appendLine("\n$facingStr (ID: $cameraId)")
                    
                    val capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                    if (capabilities != null) {
                        appendLine("対応機能:")
                        for (capability in capabilities) {
                            appendLine("- ${getCapabilityName(capability)}")
                        }
                    }
                    
                    val streamConfigurations = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    if (streamConfigurations != null) {
                        val outputSizes = streamConfigurations.getOutputSizes(ImageFormat.YUV_420_888)
                        appendLine("対応解像度:")
                        outputSizes?.forEach { size ->
                            appendLine("- ${size.width}x${size.height}")
                        }
                    }
                }
            } catch (e: Exception) {
                appendLine("カメラ情報の取得に失敗しました: ${e.message}")
            }
        }
    }

    private fun getCapabilityName(capability: Int): String {
        return when (capability) {
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> "後方互換性"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> "手動センサー制御"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> "手動後処理"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> "RAW撮影"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING -> "プライベート再処理"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS -> "センサー設定読み取り"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE -> "バースト撮影"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING -> "YUV再処理"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT -> "深度出力"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO -> "制約付き高速度ビデオ"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING -> "モーション追跡"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> "論理マルチカメラ"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME -> "モノクロ"
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA -> "セキュア画像データ"
            else -> "不明な機能 ($capability)"
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN).format(Date(timestamp))
    }

    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024
        val mb = kb / 1024
        return when {
            mb > 0 -> "$mb MB"
            kb > 0 -> "$kb KB"
            else -> "$bytes B"
        }
    }
} 
