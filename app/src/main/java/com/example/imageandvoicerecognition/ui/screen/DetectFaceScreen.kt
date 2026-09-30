package com.example.imageandvoicerecognition.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.imageandvoicerecognition.data.model.FaceAttributesInfo
import com.example.imageandvoicerecognition.ui.viewmodel.DetectFaceViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectFaceScreen(viewModel: DetectFaceViewModel, navController : NavController) {
    // 收集状态
    // 选中的图片Uri
    val imageUri by viewModel.imageUri.collectAsState()
    // 人脸信息列表
    val faceInfoList by viewModel.faceInfoList.collectAsState()
    // 加载状态
    val isLoading by viewModel.isLoading.collectAsState()
    // 人脸属性信息
    val faceAttributesInfo by viewModel.faceAttributesInfo.collectAsState()

    Scaffold(
        topBar = {
//            顶部导航栏（TopAppBar）
//            显示标题 "人脸检测功能"
//            左侧返回按钮，点击通过navController.navigateUp()返回上一级页面
            TopAppBar(
                title = { Text("人脸检测功能") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp()}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
//        主内容区域
//        图片选择按钮：点击触发viewModel.pickImage()，用于选择图片
//        图片预览：
//        当imageUri不为空时，通过FaceImagePreview组件显示选中的图片
//        使用coil库的rememberAsyncImagePainter加载图片，支持 ContentScale.Crop 缩放模式
//        加载指示器：
//        当isLoading为 true 时，显示CircularProgressIndicator
//            人脸信息卡片：
//        通过FaceInfoListInfoCard组件展示人脸属性信息
//        仅在faceAttributesInfo不为空时显示
        paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(6.dp)) {
            // 选择图片按钮
            Button(onClick = { viewModel.pickImage() }) {
                Text("选择图片")
            }
            // 图片预览
            imageUri?.let {
                uri -> FaceImagePreview(uri = uri)
            }
            Spacer(modifier = Modifier.height(20.dp))
            // 加载指示器
            if(isLoading) {
                CircularProgressIndicator()
            }
            // 人脸信息列表
            FaceInfoListInfoCard(faceAttributesInfo)
        }
        // 记住 ActivityResultLauncher
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) {
            uri: Uri? -> viewModel.setImageUri(uri)
        }
        // 收集 ViewModel 中的事件并触发图片选择器
//        图片选择逻辑
//        使用rememberLauncherForActivityResult注册图片选择器
//        通过ActivityResultContracts.GetContent()启动系统图片选择
//                监听 ViewModel 中的imagePickerEvent，当事件触发时启动图片选择器：
        LaunchedEffect(viewModel) {
            viewModel.imagePickerEvent.collect {
                imagePickerLauncher.launch("image/*")
            }
        }
    }
}

//FaceImagePreview：图片预览组件
//接收 Uri 参数，使用 Coil 加载图片
//固定高度 200dp，宽度充满父容器
@Composable
fun FaceImagePreview(uri: Uri) {
    val painter = rememberAsyncImagePainter(model = uri)
    Image(
        painter = painter,
        contentDescription = "Loaded image",
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentScale = ContentScale.Crop
    )
}

//FaceInfoListInfoCard：人脸信息卡片
//使用Card组件包裹，包含标题和属性列表
//标题区域显示 "人脸属性信息" 和图标
//通过Divider分隔标题和内容
//循环展示各类人脸属性（年龄、颜值、表情等）
@Composable
fun FaceInfoListInfoCard(faceAttributesInfo: FaceAttributesInfo?) {
    faceAttributesInfo?.let {
        info ->
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Face Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "人脸属性信息",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                        )
                    }
                Divider(color = Color.Gray, thickness = 1.dp)
                AttributeItem("年龄","${info.Age}")
                AttributeItem("颜值", "${info.Beauty}")
                AttributeItem("表情", "${info.Expression}")
                AttributeItem("眼睛是否睁开", if (info.EyeOpen) "是" else "否")
                AttributeItem("性别", if (info.Gender > 50) "男性" else "女性")
                AttributeItem("是否戴眼镜", if (info.Glass) "是" else "否")
                AttributeItem("是否戴帽子", if (info.Hat) "是" else "否")
                AttributeItem("是否戴口罩", if (info.Mask) "是" else "否")
                AttributeItem("头部姿态", "Pitch ${info.Pitch}, Roll ${info.Roll}, Yaw ${info.Yaw}")
            }
        }
    }
}

//AttributeItem：属性项组件
//水平排列的键值对布局
//左侧显示属性名（加粗），右侧显示属性值
//充满宽度，内容两端对齐
@Composable
fun AttributeItem(attributeName: String, attributeValue: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = attributeName,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = attributeValue,
            fontWeight = FontWeight.Light
        )
    }
}