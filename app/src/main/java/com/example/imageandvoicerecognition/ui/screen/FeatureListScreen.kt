package com.example.imageandvoicerecognition.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


//Feature 数据类：定义了功能项的数据结构，包含两个字段：
//name：功能的显示名称（如 “图像标签识别”）。
//route：功能对应的导航路由（用于跳转至该功能的界面，如 “tag”）。
data class Feature(val name: String, val route: String)

//features 列表：存储了所有支持的功能项，是界面展示的数据源，包含 6 个 AI 相关功能（图像标签识别、商品识别、人脸检测等
val features = listOf(
    Feature("图像标签识别", "tag"),
    Feature("商品识别", "detectProductScreen"),
    Feature("人脸检测", "detectFaceScreen"),
    Feature("语音识别", "speechScreen"),
    Feature("多语言翻译", "textTranslateScreen"),
    Feature("OCR识别", "ocrScreen")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureListScreen(navController: NavController) {
    Scaffold(
        topBar = {
//            TopAppBar：顶部导航栏，显示应用标题 “AI 语音图像识别综合平台”，无额外交互按钮。
            TopAppBar(
                title = { Text("AI语音图像识别综合平台") }
            )
        }
    ) {
//        LazyColumn：高效的滚动列表组件，仅渲染当前可见的项，适合展示大量数据（这里虽然只有 6 项，但遵循最佳实践）。
//        contentPadding：接收 Scaffold 传递的内边距（避免内容被顶部导航栏遮挡）。
//        modifier = Modifier.fillMaxSize()：让列表占满整个屏幕空间。
        paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(features) {
//                列表项（Card）：
//                通过 items(features) 遍历 features 列表，为每个功能项创建一个 Card（卡片）组件。
//                Card 样式：
//                宽度填满屏幕（fillMaxWidth()）。
//                四周留白 8dp（padding(8.dp)）。
//                可点击（clickable），点击时通过 navController.navigate(feature.route) 跳转到对应的功能界面（路由由 Feature 的 route 字段指定）。
//                设置阴影高度为 4dp（elevation = CardDefaults.cardElevation(4.dp)），增强视觉层次感。
//                卡片内容：内部用 Column 包裹一个 Text，显示功能名称（feature.name），文字样式使用 Material3 的 titleLarge 主题样式，内容四周留白 16dp。
                feature ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(feature.route)
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(feature.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}