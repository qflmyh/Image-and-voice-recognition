package com.example.imageandvoicerecognition.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageandvoicerecognition.data.repository.TencentCloudTextTranslateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TextTranslateViewModel (application: Application, private val translateRepository: TencentCloudTextTranslateRepository): ViewModel() {
    var translatedText = mutableStateOf("")
        private set

    fun translateText(sourceText: String, sourceLanguage: String, targetLanguage: String) {
        viewModelScope.launch ( Dispatchers.IO ) {
            try {
                val result = translateRepository.translateText(sourceText, sourceLanguage, targetLanguage)
                translatedText.value = result
            } catch (e: Exception) {
                translatedText.value = "Error: ${e.message}"
            }
        }
    }
}

class TextTranslateViewModelFactory(
    private val application: Application,
    private val translateRepository: TencentCloudTextTranslateRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(TextTranslateViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
             return TextTranslateViewModel(application, translateRepository) as T
         }
         throw IllegalArgumentException("Unknown ViewModel class")
    }
}