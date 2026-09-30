package com.example.imageandvoicerecognition.data.repository

import com.example.imageandvoicerecognition.data.api.OcrService

//OCR识别数据仓库
class TencentCloudOcrRepository(private val ocrService: OcrService) {
    suspend fun recognizeAdvertisCard(imageBase64: String): String {
        return ocrService.recognizeAdvertiseCard(imageBase64)
    }

}