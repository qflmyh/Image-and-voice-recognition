package com.example.imageandvoicerecognition.data.api

import com.google.gson.Gson
import com.tencentcloudapi.tiia.v20190529.TiiaClient
import com.tencentcloudapi.tiia.v20190529.models.*

class TiiaService(private val tiiaClient: TiiaClient) {

//    Gson 是 Google 提供的一个 JSON 解析库，用于将对象与 JSON 字符串相互转换。
    private val gson = Gson()

    //图像标签
//    定义了一个名为 analyzeImage 的函数，接收一个 String 类型的参数 imageBase64（Base64 编码的图像数据），返回值为 String 类型（JSON 格式的分析结果或错误信息）。
    fun analyzeImage(imageBase64: String): String {
        try {

//            DetectLabelRequest 是腾讯云 TIIA 服务提供的标签检测请求类，用于封装接口调用的参数。
//            apply 是 Kotlin 的作用域函数，用于在对象创建后直接设置其属性。这里通过 setImageBase64(imageBase64) 将输入的 Base64 图像数据设置到请求中
            val req = DetectLabelRequest().apply {
                setImageBase64(imageBase64)
            }

//            调用 DetectLabel(req) 方法，传入请求对象 req，发起图像标签检测请求，返回的 resp 是接口的响应对象（包含检测到的标签信息，如物体名称、置信度等）。
            val resp = tiiaClient.DetectLabel(req)
            return gson.toJson(resp)
        } catch (e: Exception) {
            return "Error: ${e.toString()}"
        }
    }

    //商品识别
    fun detectProductInfo(imageBase64: String): String {
        try {
            val req = DetectProductRequest().apply {
                setImageBase64(imageBase64)
            }
            val resp = tiiaClient.DetectProduct(req)
            return gson.toJson(resp)
        } catch (e: Exception) {
            return "Error: ${e.toString()}"
        }
    }
}