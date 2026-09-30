package com.example.imageandvoicerecognition.data.api

import com.tencentcloudapi.asr.v20190614.AsrClient
import com.tencentcloudapi.common.Credential
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.common.profile.Region
import com.tencentcloudapi.tiia.v20190529.TiiaClient
import com.tencentcloudapi.ocr.v20181119.OcrClient
import com.tencentcloudapi.iai.v20200303.IaiClient
import com.tencentcloudapi.tmt.v20180321.TmtClient
import javax.crypto.SecretKey

//初始化腾讯云客户端
object TencentCloudClient {

    // 图像标签客户端
    fun initTiiaClient(secretId: String, secretKey: String, region: String): TiiaClient {

        val cred = Credential(secretId, secretKey)

        val httpProf = HttpProfile().apply {
            endpoint = "tiia.tencentcloudapi.com"
        }

        val clientProfile = ClientProfile().apply {
            httpProfile = httpProf
        }

        return TiiaClient(cred, region, clientProfile)
    }

    //    人脸识别客户端
    fun initFaceClient(secretId: String, secretKey: String, region: String): IaiClient {

        val cred = Credential(secretId, secretKey)

        val httpProf = HttpProfile().apply {
            endpoint = "iai.tencentcloudapi.com"
        }

        val clientProfile = ClientProfile().apply {
            httpProfile = httpProf
        }
        return IaiClient(cred, region, clientProfile)
    }


    //语音识别客户端
    fun initAsrClient(secretId: String, secretKey: String, region: String): AsrClient {
        val cred = Credential(secretId, secretKey)
        val httpProf = HttpProfile().apply {
            endpoint = "asr.tencentcloudapi.com"
        }
        val clientProfile = ClientProfile().apply {
            httpProfile = httpProf
        }
        return AsrClient(cred, region, clientProfile)
    }

    //    多语言翻译客户端
    fun initTmtClient(secretId: String, secretKey: String, region: String): TmtClient {
        val cred = Credential(secretId, secretKey)
        val httpProf = HttpProfile().apply {
            endpoint = "tmt.tencentcloudapi.com"
        }
        val clientProfile = ClientProfile().apply {
            httpProfile = httpProf
        }
        return TmtClient(cred, region, clientProfile)
    }

    //OCR识别客户端
    fun initOcrClient(secretId: String,secretKey: String, region: String): OcrClient {
        val cred = Credential(secretId, secretKey)
        val httpProf = HttpProfile().apply {
            endpoint = "ocr.tencentcloudapi.com"
        }
        val clientProfile = ClientProfile().apply {
            httpProfile = httpProf
        }
        return OcrClient(cred, region, clientProfile)
    }
}
