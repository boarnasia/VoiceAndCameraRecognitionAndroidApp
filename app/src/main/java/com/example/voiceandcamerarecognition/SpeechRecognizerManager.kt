package com.example.voiceandcamerarecognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast

/** 音声認識マネージャー */
class SpeechRecognizerManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var onResultCallback: ((String) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private val recognitionDelay = 500L // 次の認識開始までの遅延時間（ミリ秒）

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            setupRecognitionListener()
        } else {
            Toast.makeText(context, "音声認識が利用できません", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnResultCallback(callback: (String) -> Unit) {
        onResultCallback = callback
    }

    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    onResultCallback?.invoke(recognizedText)
                }

                // 遅延を入れてから次の認識を開始
                handler.postDelayed({
                    startListening()
                }, recognitionDelay)
            }

            override fun onError(error: Int) {
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        // エラー7の場合は少し待ってから再試行
                        handler.postDelayed({
                            startListening()
                        }, recognitionDelay)
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        // 認識エンジンがビジーの場合は少し待ってから再試行
                        handler.postDelayed({
                            startListening()
                        }, recognitionDelay * 2)
                    }
                    else -> {
                        Toast.makeText(context, "エラーが発生しました: $error", Toast.LENGTH_SHORT).show()
                        // その他のエラーの場合は少し長めに待ってから再試行
                        handler.postDelayed({
                            startListening()
                        }, recognitionDelay * 3)
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            // 例外が発生した場合は少し待ってから再試行
            handler.postDelayed({
                startListening()
            }, recognitionDelay)
        }
    }

    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        speechRecognizer?.destroy()
    }
} 