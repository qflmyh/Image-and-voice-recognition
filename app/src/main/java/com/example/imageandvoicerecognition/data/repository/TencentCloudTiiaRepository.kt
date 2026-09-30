package com.example.imageandvoicerecognition.data.repository

import com.example.imageandvoicerecognition.data.api.TiiaService

class ImageRepository(private val tiiaService: TiiaService) {

//    调用图像标签服务层获取数据
    suspend fun getImageAnalysis(imageBase64: String): String {
        return tiiaService.analyzeImage(imageBase64)
    }

    suspend fun getImageProductInfo(imageBase64: String): String {
        return tiiaService.detectProductInfo(imageBase64)
    }
}