package com.example.imageandvoicerecognition.ui.viewmodel

import android.net.Uri
import android.app.Application
import android.util.Base64
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.imageandvoicerecognition.data.model.FaceAttributesInfo
import com.example.imageandvoicerecognition.data.model.FaceDetectionResponse
import com.example.imageandvoicerecognition.data.model.FaceInfo
import com.example.imageandvoicerecognition.data.repository.FaceRepository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetectFaceViewModel (application: Application, private val faceRepository: FaceRepository) : AndroidViewModel(application) {
    // 创建一个 Channel 来发送事件
    private val _imagePickerEvent = Channel<Unit>(Channel.BUFFERED)
    // 提供一个 Flow 来观察事件

//    _imagePickerEvent	Channel<Unit>	发送 "选择图片" 事件的通道，UI 层监听后启动系统图片选择器
//    imagePickerEvent	Flow<Unit>	暴露给 UI 层的事件流，用于观察图片选择请求
//    _faceInfo	MutableStateFlow	存储人脸检测返回的原始列表数据（List<FaceInfo>）
//    faceInfoList	StateFlow	暴露给 UI 层的人脸信息列表，不可修改
//    _faceAttributesInfo	MutableStateFlow	存储单个人脸的属性信息（FaceAttributesInfo?），如年龄、性别等
//    faceAttributesInfo	StateFlow	暴露给 UI 层的人脸属性信息
//    _isLoading	MutableStateFlow	标记是否正在加载（如 API 请求中）
//    isLoading	StateFlow	暴露加载状态给 UI 层，用于显示加载指示器
//    _error	MutableStateFlow	存储错误信息（如 API 请求失败时）
//    error	StateFlow	暴露错误信息给 UI 层，用于展示错误提示
//    _imageUri	MutableStateFlow	存储选中图片的Uri
//    imageUri	StateFlow	暴露图片 Uri 给 UI 层，用于预览图片
    val imagePickerEvent = _imagePickerEvent.receiveAsFlow()
    private val _faceInfo = MutableStateFlow<List<FaceInfo>>(emptyList())
    val faceInfoList: StateFlow<List<FaceInfo>> = _faceInfo
    private val _faceAttributesInfo = MutableStateFlow<FaceAttributesInfo?>(null)
    val faceAttributesInfo: StateFlow<FaceAttributesInfo?> = _faceAttributesInfo
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    // 使用 MutableStateFlow 来保存 Uri 类型的 imageUri 状态
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri
    private val gson = Gson()

//    人脸检测逻辑（getDetectFaceInfoForImage）
//    接收 Base64 编码的图片数据（imageData）作为参数
//    用viewModelScope.launch(Dispatchers.IO)在 IO 线程中执行耗时操作：
//    开启加载状态（_isLoading.value = true）
//    调用faceRepository.getDetectFaceInfo获取 API 返回的原始数据（字符串）
//    使用Gson将原始数据解析为FaceDetectionResponse对象（数据模型）
//    提取解析后的数据，更新_faceInfo（人脸列表）和_faceAttributesInfo（首个脸的属性）
//    关闭加载状态（_isLoading.value = false）
    fun getDetectFaceInfoForImage(imageData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            val detectFaceResult = faceRepository.getDetectFaceInfo(imageData)
            Log.d("FaceDetection", "接口返回原始内容: $detectFaceResult") // 添加此行
            val _FaceDetectionResponse = gson.fromJson(detectFaceResult,FaceDetectionResponse::class.java)
            _faceInfo.value = _FaceDetectionResponse.FaceInfos
            _faceAttributesInfo.value = _FaceDetectionResponse.FaceInfos[0].FaceAttributesInfo
            _isLoading.value = false
        }
    }

    // 触发选择图片的事件
//    图片选择相关方法
//    pickImage()：通过_imagePickerEvent.send(Unit)发送事件，通知 UI 层启动图片选择器
    fun pickImage() {
        viewModelScope.launch {
            _imagePickerEvent.send(Unit)
        }
    }

    //    setImageUri(uri: Uri?)：
//    接收图片 Uri 并更新_imageUri状态（供 UI 预览）
//    调用convertImageToBase64将 Uri 转换为 Base64 编码
//    调用getDetectFaceInfoForImage发起人脸检测请求
    fun setImageUri(uri: Uri?) {
        viewModelScope.launch {
            uri?.let {
                _imageUri.value = it
                val imageData = convertImageToBase64(it)
                getDetectFaceInfoForImage(imageData)
            }
        }
    }

//    图片格式转换（convertImageToBase64）
//    在 IO 线程（withContext(Dispatchers.IO)）中执行，避免阻塞主线程
//    通过ContentResolver从 Uri 获取输入流（InputStream）
//    将输入流读取为字节数组（bytes）
//    用Base64.encodeToString将字节数组转换为 Base64 字符串（API 接口通常要求此格式）
    suspend fun convertImageToBase64(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
            val bytes = inputStream!!.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }
}


//工厂类（DetectFaceModelFactory）
//实现ViewModelProvider.Factory，用于自定义 ViewModel 的创建
//接收Application和FaceRepository作为参数，注入到DetectFaceViewModel中
//确保 ViewModel 实例化时能获取到所需的依赖，符合依赖注入原则
class DetectFaceModelFactory(
    private val application: Application,
    private val faceRepository: FaceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetectFaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetectFaceViewModel(application, faceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
