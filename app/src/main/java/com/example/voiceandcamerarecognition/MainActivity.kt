package com.example.voiceandcamerarecognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voiceandcamerarecognition.components.*
import com.example.voiceandcamerarecognition.schemas.RecognitionItem
import com.example.voiceandcamerarecognition.viewmodels.RecognitionItemViewModel
import com.example.voiceandcamerarecognition.ui.theme.VoiceAndCameraRecognitionTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    private lateinit var speechRecognizerManager: SpeechRecognizerManager
    private lateinit var cameraManager: CameraManager

    // 権限リクエストコードを定数として定義
    private val PERMISSIONS_TO_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 権限の要求処理
        requirePermissions()

        setContent {
            VoiceAndCameraRecognitionTheme {
                MainScreen(
                    speechRecognizerManager,
                    cameraManager
                    )
            }
        }
    }

    /**
     * 権限の許可状況をチェックし必要なら権限の要求を行う
     */
    fun requirePermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest,
                PERMISSIONS_TO_REQUEST
            )
        } else {
            initializeManagers()
        }
    }

    // 権限要求の結果を処理するコールバックメソッド
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        // 定義したリクエストコードじゃなければ抜ける
        if (requestCode != PERMISSIONS_TO_REQUEST) return

        // 権限の許可状況を調べる
        var allPermissionsGranted = true
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
        }

        // 本来は未許可の権限が合ったケースでの処理を入れるべきだが
        // 今回はそこまでは作り込まない
        initializeManagers()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizerManager.destroy()
        cameraManager.shutdown()
    }

    /**
     * マネージャの初期化処理
     */
    fun initializeManagers() {
        speechRecognizerManager = SpeechRecognizerManager(this)
        cameraManager = CameraManager(this)
    }
}

@Composable
fun MainScreen(
    speechRecognizerManager: SpeechRecognizerManager,
    cameraManager: CameraManager
) {
    val voiceRecognitionItemViewModel = RecognitionItemViewModel()
    val videoRecognitionItemViewModel = RecognitionItemViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        speechRecognizerManager.setOnResultCallback { text ->
            voiceRecognitionItemViewModel.addRecognitionItem(RecognitionItem("voice", text, LocalDateTime.now()))
        }
        cameraManager.setOnResultCallback { text ->
            videoRecognitionItemViewModel.addRecognitionItem(RecognitionItem("video", text, LocalDateTime.now()))
        }
        speechRecognizerManager.startListening()
        cameraManager.startCamera(lifecycleOwner)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("音声認識:", modifier = Modifier.padding(8.dp))
            RecognitionItemListScreen(voiceRecognitionItemViewModel)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("画像認識:", modifier = Modifier.padding(8.dp))
            RecognitionItemListScreen(videoRecognitionItemViewModel)
        }

    }
}