package com.example.imageandvoicerecognition.data.api

import com.google.gson.Gson
import com.tencentcloudapi.iai.v20200303.models.*
import com.tencentcloudapi.iai.v20200303.IaiClient


class FaceService(private val iaiClient: IaiClient) {
    private val gson = Gson()

    fun faceImage(imageBase64: String): String {
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            val req = DetectFaceRequest().apply {
                setImage(imageBase64)
                setNeedFaceAttributes(1)
            }
            // 返回的resp是一个DetectFaceAttributesResponse的实例，与请求对象对应
            val resp = iaiClient.DetectFace(req)
            return gson.toJson(resp)
        } catch (e: Exception) {
            return "Error: ${e.toString()}"
        }
    }
}