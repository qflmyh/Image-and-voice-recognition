package com.example.imageandvoicerecognition.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageandvoicerecognition.data.model.Label
import com.example.imageandvoicerecognition.data.model.TagListRes
import com.example.imageandvoicerecognition.data.repository.ImageRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//继承AndroidViewModel，持有Application上下文，适合需要全局上下文的场景（如访问 ContentResolver）。
//依赖ImageRepository：数据仓库层，负责实际的图片分析网络请求或本地处理（解耦 ViewModel 与数据来源）
class TagViewModel(application: Application, private val imageRepository: ImageRepository) : AndroidViewModel(application) {

    // 创建一个 Channel 来发送事件
//    通过Channel发送 "选择图片" 的事件，UI 层（Compose 或 Activity/Fragment）可观察imagePickerEvent来响应事件（如打开系统相册）。
//    优点：事件一次性消费，避免配置变更（如旋转屏幕）后重复处理。
    private val _imagePickerEvent = Channel<Unit>(Channel.BUFFERED)

    // 提供一个 Flow 来观察事件
//    通过MutableStateFlow维护可观察的状态，对外暴露不可变的StateFlow，确保状态只能通过 ViewModel 内部修改：
//    状态变量	类型	作用
//    _tags	List<Label>	存储图片分析得到的标签列表
//    _isLoading	Boolean	标记是否正在加载（用于显示加载动画）
//    _error	String?	存储错误信息（如请求失败原因）
//    _imageUri	Uri?	存储选中图片的 Uri
    val imagePickerEvent = _imagePickerEvent.receiveAsFlow()

    private val _tags = MutableStateFlow<List<Label>>(emptyList())
    val tag: StateFlow<List<Label>> = _tags
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUri

    private val gson = Gson()


//    获取图片标签
//    开始加载时，设置_isLoading为true，清空之前的错误。
//    调用imageRepository.getImageAnalysis获取图片分析结果（可能是网络请求）。
//    使用Gson解析返回的 JSON 字符串为TagListRes对象，提取标签列表并更新_tags。
//    结束加载，设置_isLoading为false
    fun getTagsForImage(imageData: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null

            val analysisResult = imageRepository.getImageAnalysis(imageData)

            val tagList = gson.fromJson(analysisResult, TagListRes::class.java)
            _tags.value = tagList.Labels
            _isLoading.value = false
        }
    }

//    触发选择图片的事件
//    触发图片选择流程：向_imagePickerEvent发送事件，UI 层观察到事件后打开图片选择器。
    fun pickImage() {

        viewModelScope.launch {
            _imagePickerEvent.send(Unit)
        }
    }

//    设置图片 Uri 并处理
//    接收图片选择器返回的Uri，更新状态并触发后续处理：
//    将Uri保存到_imageUri（UI 可观察并显示图片）。
//    调用convertImageToBase64将图片转为 Base64 编码（网络请求常用格式）。
//    调用getTagsForImage分析图片并获取标签。
    fun setImageUri(uri: Uri?) {

        viewModelScope.launch {
            uri?.let {
                _imageUri.value = it

                val imageData = convertImageToBase64(it)
                getTagsForImage(imageData)
            }
        }
    }


//    作用：将图片 Uri 对应的文件转为 Base64 字符串（网络接口常要求此格式上传图片）。
//    细节：
//    使用withContext(Dispatchers.IO)切换到 IO 线程，避免阻塞主线程。
//    通过ContentResolver打开 Uri 对应的输入流，读取字节并编码。
    suspend fun convertImageToBase64(uri: Uri): String {

        return withContext(Dispatchers.IO) {
            val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
            val bytes = inputStream!!.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

}


//ViewModel 工厂
class TagViewModelFactory(

    private val application: Application,

    private val imageRepository: ImageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagViewModel(application, imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}