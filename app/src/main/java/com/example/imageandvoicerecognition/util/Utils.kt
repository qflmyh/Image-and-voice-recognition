package com.example.imageandvoicerecognition.util

import android.util.Base64
import java.io.IOException
import java.io.FileInputStream
import java.io.File
//音频转换工具类
class Utils {
    fun fileToBase64(file: File?): String {
        if (file == null) {
            return ""
        }
        return try {
            FileInputStream(file).use {
                inputStream ->
                // 读取所有字节
                val bytes = inputStream.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}