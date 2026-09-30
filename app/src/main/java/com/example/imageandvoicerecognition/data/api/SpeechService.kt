package com.example.imageandvoicerecognition.data.api

import android.util.Log
import com.example.imageandvoicerecognition.util.Utils
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionRequest
import com.tencentcloudapi.asr.v20190614.AsrClient
import com.tencentcloudapi.common.exception.TencentCloudSDKException
import com.tencentcloudapi.ivld.v20210903.models.AudioData
import java.io.File


class SpeechService (private val asrClient: AsrClient) {
    private val _utils = Utils()

    suspend fun startListening(audioData: File?): String {
        if (audioData == null) {
            return "Error: Audio file is required but not provided."
        }

        return try {
            val _data = _utils.fileToBase64(audioData)
            val req = SentenceRecognitionRequest().apply {
                setEngSerViceType("16k_zh")
                setVoiceFormat("wav")
                setSourceType(1)
                setData(_data)
                setDataLen(audioData.length())
            }

            val resp = asrClient.SentenceRecognition(req)
            // Optionally, handle different response statuses
            "${resp.result}"
            //getRecognitionResult(resp.data.taskId)
        } catch (e: TencentCloudSDKException) {
            Log.e("Liang", "SpeechService error: ${e.message}")
            "Error processing the audio file: ${e.message}"
        }
    }
}