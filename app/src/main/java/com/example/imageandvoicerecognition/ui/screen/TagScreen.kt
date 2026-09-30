package com.example.imageandvoicerecognition.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.imageandvoicerecognition.data.model.Label
import com.example.imageandvoicerecognition.ui.viewmodel.TagViewModel
import coil.compose.rememberAsyncImagePainter


//图片标签检测界面
//TagScreen：主界面 composable 函数，接收TagViewModel（数据和逻辑源）和NavController（导航控制器）。
//状态收集：通过collectAsState()观察TagViewModel中的状态（图片 Uri、标签列表、加载状态等），实现 UI 的响应式更新。
//图片选择：使用rememberLauncherForActivityResult注册系统图片选择器，并通过LaunchedEffect响应 ViewModel 的事件触发。

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun TagScreen(viewModel: TagViewModel, navController: NavController) {

    val imageUri by  viewModel.imageUrl.collectAsState()
    val tags by viewModel.tag.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

//    界面结构（Scaffold）
//    采用Scaffold作为基础布局容器，包含顶部导航栏和主体内容
    Scaffold(
//        TopAppBar（顶部导航栏）
//        功能：显示标题 “图像标签检测”，左侧返回按钮通过navController.navigateUp()实现返回上一级页面。
//    样式：使用 Material3 的主题颜色，容器色为primaryContainer，文字色为onPrimaryContainer，符合主题一致性。
        topBar = {
            TopAppBar(

                title = { Text("图像标签检测", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {

                    IconButton(onClick = { navController.navigateUp()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) {
//        主体内容（LazyColumn）主体使用LazyColumn实现可滚动布局
        paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
//           图片上传区域
            item {
//         Card 组件：作为容器，带阴影效果（elevation），提升视觉层次感。
//        选择图片按钮：点击触发viewModel.pickImage()，通过 ViewModel 发送图片选择事件。
//        图片预览：当imageUri不为空时，调用ImagePreview composable 显示选中的图片。
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "上传图片进行标记",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.pickImage()},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("选择图片")
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        imageUri?.let {
                            uri -> ImagePreview(uri = uri)
                        }
                    }
                }
            }

//            标签结果 / 加载状态区域
            item {
//                加载状态：isLoading为true时，显示居中的圆形进度条，提示用户正在分析图片。
//                标签结果：tags不为空时，用 Card 展示标签列表，每个标签通过TagItem组件显示，并用 Divider 分隔。
                if(isLoading) {
                    // 加载中：显示圆形进度条
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                else if(tags.isNotEmpty()) {
                    // 有标签结果：显示标签卡片
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "检测到的标签",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            tags.forEach {
                                tag -> TagItem(tag)
                                // 标签项之间的分隔线
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }

// 注册图片选择器
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) {
//         选择图片后，将Uri传给ViewModel
            uri: Uri? ->
            viewModel.setImageUri(uri)
        }
        // 监听ViewModel的图片选择事件
        LaunchedEffect(viewModel) {
            viewModel.imagePickerEvent.collect {
//               触发系统图片选择器（只允许选择图片）
                imagePickerLauncher.launch("image/*")
            }
        }
    }
}


@Composable
//ImagePreview（图片预览）
//功能：根据图片 Uri 加载并显示图片。
//实现：使用rememberAsyncImagePainter（Coil 库）异步加载图片，避免阻塞 UI。
//样式：固定高度 200dp，圆角 8dp，ContentScale.Crop确保图片填满控件且不拉伸变形。
fun ImagePreview(uri: Uri) {

    val painter = rememberAsyncImagePainter(model = uri)
    Image(
        painter = painter,
        contentDescription = "Loaded image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}


@Composable

fun TagList(tags: List<Label>) {

    LazyColumn {
        items(tags) {
            tag -> TagItem(tag)
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
        }
    }
}


@Composable
//TagItem（单个标签项）
//功能：展示单个标签的信息，使用 Row 横向排列。
//数据：显示Tags对象的FirstCategory（一级分类，如 “物体”“场景”）和Name（具体标签名，如 “猫”“室内”）。
//布局：两个 Text 各占一半宽度（weight(1f)），垂直居中对齐，有上下内边距。
fun TagItem(tag: Label){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tag.FirstCategory,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = tag.Name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}