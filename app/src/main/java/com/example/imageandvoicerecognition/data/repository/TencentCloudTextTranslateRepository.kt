package com.example.imageandvoicerecognition.data.repository

import com.example.imageandvoicerecognition.data.api.TmtService

//多语言翻译数据仓库
class TencentCloudTextTranslateRepository(private val tmtService: TmtService) {
    suspend fun translateText(sourceText: String, sourceLanguage: String, targetLanguage: String): String {
        return tmtService.translateText(sourceText, sourceLanguage,targetLanguage)
    }
}