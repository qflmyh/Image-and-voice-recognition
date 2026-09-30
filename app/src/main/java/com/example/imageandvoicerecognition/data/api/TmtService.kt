package com.example.imageandvoicerecognition.data.api

import android.view.textclassifier.TextLanguage
import com.tencentcloudapi.common.exception.TencentCloudSDKException
import com.tencentcloudapi.tmt.v20180321.TmtClient
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateResponse


class TmtService (private val tmtClient: TmtClient) {
    fun translateText(sourceText: String, sourceLanguage: String, targetLanguage: String): String {
        try {
            val request = TextTranslateRequest().apply {
                setSourceText(sourceText)
                setSource(sourceLanguage)
                setTarget(targetLanguage)
                setProjectId(1)
            }
            val response: TextTranslateResponse = tmtClient.TextTranslate(request)
            return response.targetText
        } catch (e: TencentCloudSDKException){
            return e.message.toString()
        }
    }
}