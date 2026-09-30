package com.example.imageandvoicerecognition.data.repository

import com.example.imageandvoicerecognition.data.api.SpeechService
import java.io.File

//语音识别数据仓库
class SpeechRecognitionRepository(private val speechToTextService: SpeechService) {
    suspend fun getSpeechToText(audioData: File?): String {
        return speechToTextService.startListening(audioData)
    }
}