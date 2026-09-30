package com.example.imageandvoicerecognition.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageandvoicerecognition.data.model.DetectedTextInfo
import com.example.imageandvoicerecognition.data.repository.TencentCloudOcrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class OcrViewModel (application: Application,private val ocrRepository: TencentCloudOcrRepository) : AndroidViewModel(application) {
    private val _bankCardInfo = MutableStateFlow<List<DetectedTextInfo>>(emptyList())
    val bankCardInfo: StateFlow<List<DetectedTextInfo>> get() = _bankCardInfo
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun recognizeBankCard(imageBase64: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val response = ocrRepository.recognizeAdvertisCard(imageBase64)
                val detectedTextInfos = mutableListOf<DetectedTextInfo>()
                val jsonArray = JSONArray(response)

                for (i in 0  until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val detectedText = jsonObject.getString("DetectedText")
                    val confidence = jsonObject.getLong("Confidence")
                    detectedTextInfos.add(DetectedTextInfo(detectedText, confidence))
                }
                _bankCardInfo.value = detectedTextInfos
            } catch (e: Exception) {
                _bankCardInfo.value = listOf(DetectedTextInfo("Error: ${e.message}", 0))
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class OcrViewModelFactory(private val application: Application, private val ocrRepository: TencentCloudOcrRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcrViewModel(application, ocrRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}