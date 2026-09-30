package com.example.imageandvoicerecognition.data.api

import com.google.gson.Gson
import com.tencentcloudapi.common.exception.TencentCloudSDKException
import com.tencentcloudapi.ocr.v20181119.OcrClient
import com.tencentcloudapi.ocr.v20181119.models.AdvertiseOCRRequest
import okio.GzipSource
import kotlin.io.encoding.Base64

class OcrService(private val ocrClient: OcrClient) {
    @Throws(TencentCloudSDKException::class)
    fun recognizeAdvertiseCard(imageBase64: String): String {
        val request = AdvertiseOCRRequest().apply {
            this.imageBase64 = imageBase64
        }
        val res = ocrClient.AdvertiseOCR(request)
        return Gson().toJson(res.textDetections)
    }
}