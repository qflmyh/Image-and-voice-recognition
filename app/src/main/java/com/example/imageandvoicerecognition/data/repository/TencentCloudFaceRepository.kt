package com.example.imageandvoicerecognition.data.repository

import com.example.imageandvoicerecognition.data.api.FaceService

class FaceRepository(private val faceService: FaceService) {

    suspend fun getDetectFaceInfo(imageBase64: String): String {
        return faceService.faceImage(imageBase64)
    }
}