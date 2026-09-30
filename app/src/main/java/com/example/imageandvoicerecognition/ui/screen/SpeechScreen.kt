package com.example.imageandvoicerecognition.ui.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.imageandvoicerecognition.ui.viewmodel.SpeechViewModel


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpeechScreen(speechViewModel: SpeechViewModel, navController: NavController) {
//    通过remember和collectAsState管理界面状态：
// 存储临时文本（未实际使用，可能是预留变量）
    var text = remember { mutableStateOf("") }
    // 标记是否正在录音（控制按钮状态和UI反馈）
    var isRecording by remember { mutableStateOf(false) }
    // 从ViewModel收集语音识别结果（UI自动刷新）
    val recognitionResult by speechViewModel.recognitionResult.collectAsState()
    // 音频播放器实例（使用ExoPlayer播放录音）
    val audioPlayer = remember { mutableStateOf<ExoPlayer?>(null) }
//    资源释放（DisposableEffect）
    DisposableEffect(Unit) {
        onDispose {
            // 组件销毁时释放ExoPlayer资源，防止内存泄漏
            audioPlayer.value?.release()
        }
    }

    Scaffold(
        topBar = {
//            顶部导航栏（TopAppBar）
//            标题显示 "语音识别"
//            左侧返回按钮，点击通过navController.navigateUp()返回上一级页面
            TopAppBar(
                title = { Text("语音识别") },
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

        paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//            录音按钮（长按录音逻辑）
            Button(
                onClick = { /* This is required but not used */ },
                modifier = Modifier.fillMaxWidth().background(if (isRecording) Color.Red else Color.Gray)
                    .pointerInteropFilter {
                        // 处理触摸事件：按下开始录音，松开结束录音
                        when {
                            // 按下时：开始录音，更新按钮状态为红色
                            it.action == android.view.MotionEvent.ACTION_DOWN -> {
                                // Start recording
                                speechViewModel.startRecording()
                                isRecording = true
                                true    // Consumes the touch event
                            }

                            // 松开时：停止录音并触发识别，恢复按钮状态
                            it.action == android.view.MotionEvent.ACTION_UP -> {
                                // Stop recording
                                speechViewModel.stopRecordingAndRecognize()
                                isRecording = false
                                true    // Consumes the touch event
                            }
                            // Do not consume the event
                            else -> false
                        }
                    }
            ) {
                Text(if (isRecording) "松开 结束录音" else "按住 说话", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
//            实时显示从 ViewModel 获取的语音识别结果（recognitionResult）
            Text("识别结果: $recognitionResult", style = MaterialTheme.typography.bodyLarge)

//            录音播放按钮
//            仅当存在录音文件（audioFile != null）时显示播放图标
//            点击图标时，使用ExoPlayer播放录音文件：
//            先释放之前的播放器资源
//            创建新播放器实例，设置音频文件路径
//            调用prepare()和play()开始播放
            val audioFile = speechViewModel.getRecordAudioFile()
            if(audioFile != null) {
                Icon(
                    // 播放图标
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    // 点击播放录音
                    modifier = Modifier.size(48.dp).clickable {
                        try {
                            // Ensure any previously playing audio is stopped
                            // 释放之前的播放器实例
                            audioPlayer.value?.release()
                            // 创建新的ExoPlayer实例并配置播放
                            audioPlayer.value = ExoPlayer.Builder(speechViewModel.getApplication()).build().also {
                                player ->
                                val mediaItem = MediaItem.fromUri(Uri.fromFile(audioFile))
                                player.setMediaItem(mediaItem)
                                // 创建新的ExoPlayer实例并配置播放
                                player.prepare()
                                // 开始播放
                                player.play()
                            }
                        } catch (e: Exception) {
                            // 打印播放错误日志
                            Log.d("Liang_player", e.toString())
                        }
                    }
                )
            }
        }
    }
}