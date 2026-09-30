package com.example.imageandvoicerecognition.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageandvoicerecognition.data.model.ProductInfo
import com.example.imageandvoicerecognition.data.model.Label
import com.example.imageandvoicerecognition.data.model.TagListRes
import com.example.imageandvoicerecognition.data.model.ProductListRes
import com.example.imageandvoicerecognition.data.repository.ImageRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//DetectProductViewModel：继承 AndroidViewModel（持有 Application 上下文），负责管理与商品识别相关的数据和业务逻辑。
//依赖：需要 ImageRepository（数据仓库，处理网络请求或本地数据操作）和 Application 实例。
//DetectProductModelFactory：自定义 ViewModel 工厂，用于向 ViewModel 注入 Application 和 ImageRepository 依赖。


class DetectProductViewModel(application: Application, private val imageRepository: ImageRepository): AndroidViewModel(application) {
// 创建一个 Channel 来发送事件
    private val _imagePickerEvent = Channel<Unit>(Channel.BUFFERED)
    // 提供一个 Flow 来观察事件
    val imagePickerEvent = _imagePickerEvent.receiveAsFlow()

//    _imagePickerEvent（Channel）：发送图片选择事件，通知 UI 层打开图片选择器。
//    _productinfo（MutableStateFlow）：存储识别到的商品列表，对外暴露不可变的 productList 供观察。
//    _isLoading：标记是否正在加载数据（如网络请求中）。
//    _error：存储错误信息（如请求失败原因）。
//    _imageUri：保存选中图片的 Uri，供 UI 层显示图片。
    private val _productinfo = MutableStateFlow<List<ProductInfo>>(emptyList())
    val productList: StateFlow<List<ProductInfo>> = _productinfo
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    // 使用 MutableStateFlow 来保存 Uri 类型的 imageUri 状态
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val gson = Gson()

//    getProductInfoForImage(imageData: String)：通过 imageRepository 发起网络请求，获取商品信息：
//    开启加载状态（_isLoading = true）。
//    调用仓库的 getImageProductInfo 方法（传入 Base64 图片数据）。
//    将返回的 JSON 字符串通过 Gson 解析为 ProductListRes 实体，提取商品列表并更新 _productinfo。
//    打印日志便于调试。
    fun getProductInfoForImage(imageData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null

            val detectProductResult = imageRepository.getImageProductInfo(imageData)

            val _productList = gson.fromJson(detectProductResult, ProductListRes::class.java)
            _productinfo.value = _productList.Products

            Log.d("Liang", "getTagsForImage.")
            Log.d("Liang", detectProductResult)
        }
    }

    // 触发选择图片的事件
//    pickImage()：通过 _imagePickerEvent 发送事件，触发 UI 层打开图片选择器（如系统相册）。
    fun pickImage() {
        viewModelScope.launch {
            _imagePickerEvent.send(Unit)
        }
    }

//    setImageUri(uri: Uri?)：设置选中图片的 Uri，并触发图片转 Base64 及商品识别流程。
    fun setImageUri(uri: Uri?) {
        Log.d("Liang", "setImageUri")

        viewModelScope.launch {
            uri?.let {
                _imageUri.value = it

                val imageData = convertImageToBase64(it)
                getProductInfoForImage(imageData)
            }
        }
    }

//    convertImageToBase64(uri: Uri)：将图片 Uri 转换为 Base64 字符串（用于网络传输），通过 withContext(Dispatchers.IO) 在 IO 线程执行，避免阻塞主线程。
    suspend fun convertImageToBase64(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
            val bytes = inputStream!!.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }
}

//工厂类 DetectProductModelFactory
//实现 ViewModelProvider.Factory，用于在创建 DetectProductViewModel 时注入 Application 和 ImageRepository 依赖（依赖注入思想，解耦 ViewModel 与依赖的创建）。
//当 ViewModel 构造函数有自定义参数时，必须通过工厂类创建实例。
class DetectProductModelFactory(
    private val application: Application,
    private val imageRepository: ImageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(DetectProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetectProductViewModel(application, imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}