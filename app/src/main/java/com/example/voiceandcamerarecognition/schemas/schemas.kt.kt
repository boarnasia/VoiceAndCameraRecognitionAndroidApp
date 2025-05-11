package com.example.voiceandcamerarecognition.schemas

import java.time.LocalDateTime

data class RecognitionItem(
    val type: String, // "voice" または "video"
    val text: String,
    val createdAt: LocalDateTime
)
