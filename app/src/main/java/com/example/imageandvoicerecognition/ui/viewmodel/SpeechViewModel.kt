package com.example.imageandvoicerecognition.ui.viewmodel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageandvoicerecognition.data.repository.SpeechRecognitionRepository
import com.example.imageandvoicerecognition.util.AudioFileRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SpeechViewModel (application: Application, private val speechRecognitionRepository: SpeechRecognitionRepository): AndroidViewModel(application) {
    private val audioRecordFile = AudioFileRecorder(application)
    private val _recognitionResult = MutableStateFlow("")
    val recognitionResult: StateFlow<String> = _recognitionResult

    fun startRecording() {
        audioRecordFile.startRecording()
    }

    fun stopRecordingAndRecognize() {
        audioRecordFile.stopRecording()
        viewModelScope.launch(Dispatchers.IO) {
            audioRecordFile.getRecordedAudioFile()?.let {
                file ->
                val result = speechRecognitionRepository.getSpeechToText(file)
                withContext(Dispatchers.Main) {
                    _recognitionResult.value = result
                }
            }
        }
    }

    fun stopRecording() {
        audioRecordFile.stopRecording()
    }

    fun getRecordAudioFile(): File? {
        return audioRecordFile.getRecordedAudioFile()
    }

    override fun onCleared() {
        super.onCleared()
        audioRecordFile.stopRecording()
    }
}

class SpeechViewModelFactory(
    private val application: Application,
    private val speechRecognitionRepository: SpeechRecognitionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeechViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpeechViewModel(application, speechRecognitionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}