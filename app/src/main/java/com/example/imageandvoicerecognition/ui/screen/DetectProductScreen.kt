package com.example.imageandvoicerecognition.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.imageandvoicerecognition.data.model.ProductInfo
import com.example.imageandvoicerecognition.ui.viewmodel.DetectProductViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectProductScreen(viewModel: DetectProductViewModel, navController: NavController) {
    // 收集状态
//    状态收集：通过 collectAsState() 将 ViewModel 中的可观察状态（StateFlow）转换为 Compose 可感知的状态，实现 UI 自动更新：
//    imageUri：当前选中图片的 Uri（用于预览）。
//    productinfo：识别到的商品列表（用于展示结果）。
//    isLoading：是否正在加载数据（用于显示加载指示器）。
    val imageUri by viewModel.imageUri.collectAsState()
    val productinfo by viewModel.productList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
//            TopAppBar：顶部导航栏，显示标题 “商品识别功能” 和返回按钮（点击后通过 navController.navigateUp() 返回上一级页面）。
            TopAppBar(
                title = { Text("商品识别功能") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
//        内容区域：通过 Column 垂直排列各个元素，包含标题、选择图片按钮、图片预览、结果标题、加载指示器和商品列表
        paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            // 标题
            Text(
                text = "上传商品图片进行识别",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            // 选择图片按钮
            Button(onClick = { viewModel.pickImage()}) {
                Text("选择图片")
            }
            // 图片预览
            imageUri?.let {
                uri -> ProductImagePreview(uri = uri)
            }
            Spacer(modifier = Modifier.height(20.dp))
            // 检测到的标签标题
            Text(
                text = "检测到的商品图片结果",
                style = MaterialTheme.typography.titleLarge
            )
            // 加载指示器
            if (isLoading) {
                CircularProgressIndicator()
            }
            // 商品信息列表
            ProductInfoList(productinfo)
        }

        // 记住 ActivityResultLauncher
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) {
            uri: Uri? ->
            // 处理图片选择结果，例如更新 ViewModel 中的 imageUri 状态
            viewModel.setImageUri(uri)
        }
        // 收集 ViewModel 中的事件并触发图片选择器
        LaunchedEffect(viewModel) {
            viewModel.imagePickerEvent.collect {
                imagePickerLauncher.launch("image/*")
            }
        }
    }
}

//图片预览（ProductImagePreview）
//当 imageUri 不为空时，通过 rememberAsyncImagePainter 加载 Uri 对应的图片（使用 Coil 库高效加载图片）。
//图片显示区域限制为宽度填满屏幕、高度 200dp，使用 ContentScale.Crop 保持比例并裁剪填充。
@Composable
fun ProductImagePreview(uri: Uri) {
    // Display image from uri
    val painter = rememberAsyncImagePainter(model = uri)
    Image(
        painter = painter,
        contentDescription = "Loaded image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun ProductInfoList(productinfo: List<ProductInfo>) {
    Column {
        for (product in productinfo) {
            ProductItem(product)
        }
    }
}

@Composable
fun ProductItem(product: ProductInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
        Text(product.Name, modifier = Modifier.weight(1f))
        Text(product.Parents, modifier = Modifier.weight(1f))
    }
}
