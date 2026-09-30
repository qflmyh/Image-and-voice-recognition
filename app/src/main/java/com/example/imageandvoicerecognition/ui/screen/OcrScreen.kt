package com.example.imageandvoicerecognition.ui.screen

import android.net.Uri
import android.util.Base64
import android.Manifest
import android.content.Context

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.imageandvoicerecognition.ui.viewmodel.OcrViewModel
import java.util.concurrent.Executor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(ocrViewModel: OcrViewModel, navController: NavController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBase64 by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OCR识别") },
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
        val context = LocalContext.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            uri: Uri? ->
            imageUri = uri
            imageUri?.let {
                context.contentResolver.openInputStream(it)?.use {
                    inputStream ->
                    val bytes = inputStream.readBytes()
                    imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                }
            }
        }

        var isCameraEnabled by remember { mutableStateOf(false) }
        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
            if (isGranted) {
                isCameraEnabled = true
            }
        }

        Column (
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize()
        ) {
            Button(
                onClick = { launcher.launch("image/*")},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("从相册选择图片")
            }
            Spacer(modifier = Modifier.height((8.dp)))
            Button(
                onClick = { cameraLauncher.launch(Manifest.permission.CAMERA)},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("打开摄像头")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isCameraEnabled) {
                CameraPreviewView(
                    context = context,
                    executor = ContextCompat.getMainExecutor(context),
                    onImageCaptured = {
                        image, _ ->
                        imageBase64 = convertImageProxyToBase64(image)
                        // Hide camera preview after capturing the image
                        isCameraEnabled = false
                        ocrViewModel.recognizeBankCard(imageBase64)
                    }
                )
            }
            else {
                imageUri?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { ocrViewModel.recognizeBankCard(imageBase64) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("点击识别")
                }
                Spacer(modifier = Modifier.height(16.dp))

                val _dataInfo by ocrViewModel.bankCardInfo.collectAsState()
                val isLoading by ocrViewModel.isLoading.collectAsState()

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else {
                    Text("识别文字:", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(_dataInfo) {
                            detectedTextInfo ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        "Text: ${detectedTextInfo.detectedText}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "Confidence: ${detectedTextInfo.confidence}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreviewView(context: Context, executor: Executor, onImageCaptured: (ImageProxy, Int) -> Unit) {
    AndroidView(
        factory = {
            ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        ctx as LifecycleOwner, cameraSelector, preview, imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView.setOnClickListener {
                    imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                             onImageCaptured(image, image.imageInfo.rotationDegrees)
                            image.close()
                        }
                    })
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f).padding(16.dp)
    )
}

fun convertImageProxyToBase64(image: ImageProxy) : String {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}