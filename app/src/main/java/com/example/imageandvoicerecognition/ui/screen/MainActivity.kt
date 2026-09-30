package com.example.imageandvoicerecognition.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imageandvoicerecognition.BuildConfig
import com.example.imageandvoicerecognition.data.api.FaceService
import com.example.imageandvoicerecognition.data.api.OcrService
import com.example.imageandvoicerecognition.data.api.SpeechService
import com.example.imageandvoicerecognition.data.api.TencentCloudClient
import com.example.imageandvoicerecognition.data.api.TiiaService
import com.example.imageandvoicerecognition.data.api.TmtService
import com.example.imageandvoicerecognition.data.repository.FaceRepository
import com.example.imageandvoicerecognition.data.repository.ImageRepository
import com.example.imageandvoicerecognition.data.repository.SpeechRecognitionRepository
import com.example.imageandvoicerecognition.data.repository.TencentCloudOcrRepository
import com.example.imageandvoicerecognition.data.repository.TencentCloudTextTranslateRepository
import com.example.imageandvoicerecognition.ui.theme.ImageAndVoiceRecognitionTheme
import com.example.imageandvoicerecognition.ui.viewmodel.DetectFaceModelFactory
import com.example.imageandvoicerecognition.ui.viewmodel.DetectFaceViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.DetectProductModelFactory
import com.example.imageandvoicerecognition.ui.viewmodel.DetectProductViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.OcrViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.OcrViewModelFactory
import com.example.imageandvoicerecognition.ui.viewmodel.SpeechViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.SpeechViewModelFactory
import com.example.imageandvoicerecognition.ui.viewmodel.TagViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.TagViewModelFactory
import com.example.imageandvoicerecognition.ui.viewmodel.TextTranslateViewModel
import com.example.imageandvoicerecognition.ui.viewmodel.TextTranslateViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(this)
        }
    }
}


@Composable
fun MainScreen(self: MainActivity) {
    val navController = rememberNavController()
    val SECRET_ID = BuildConfig.SECRET_ID
    val SECRET_KEY = BuildConfig.SECRET_KEY
    val REGION = BuildConfig.TENCENT_REGION

    NavHost(navController = navController, startDestination = "feature_list") {
        composable( "feature_list" ) {
            FeatureListScreen(navController)
        }
        composable("tag") {
            val tiiaService = TiiaService(TencentCloudClient.initTiiaClient(SECRET_ID, SECRET_KEY, REGION))
            val imageRepository = ImageRepository(tiiaService)
            val viewModelFactory = TagViewModelFactory(self.application, imageRepository)
            val viewModel = ViewModelProvider(self, viewModelFactory)[TagViewModel::class.java]
            TagScreen(viewModel = viewModel, navController)
        }
        composable("detectProductScreen") {
            val tiiaService = TiiaService(TencentCloudClient.initTiiaClient(SECRET_ID, SECRET_KEY, REGION))
            val imageRepository = ImageRepository(tiiaService)
            val detectProductviewModelFactory = DetectProductModelFactory(self.application, imageRepository)
            val detectProductViewModel = ViewModelProvider(self, detectProductviewModelFactory)[DetectProductViewModel::class.java]
            DetectProductScreen(viewModel = detectProductViewModel, navController)
        }
        composable("detectFaceScreen") {
            val faceService = FaceService(TencentCloudClient.initFaceClient(SECRET_ID, SECRET_KEY, REGION))
            val faceRepository = FaceRepository(faceService)
            val viewModelFactory = DetectFaceModelFactory(self.application, faceRepository)
            val viewModel = ViewModelProvider(self, viewModelFactory)[DetectFaceViewModel::class.java]
            DetectFaceScreen(viewModel = viewModel, navController)
        }
        composable("speechScreen") {
            val speechService = SpeechService(TencentCloudClient.initAsrClient(SECRET_ID, SECRET_KEY, REGION))
            val speechRepository = SpeechRecognitionRepository(speechService)
            val viewModelFactory = SpeechViewModelFactory(self.application,speechRepository)
            val viewModel = ViewModelProvider(self, viewModelFactory)[SpeechViewModel::class.java]
            SpeechScreen(viewModel, navController)
        }
        composable("textTranslateScreen") {
            val tmtService = TmtService(TencentCloudClient.initTmtClient(SECRET_ID, SECRET_KEY, REGION))
            val tmtRepository = TencentCloudTextTranslateRepository(tmtService)
            val viewModelFactory = TextTranslateViewModelFactory(self.application, tmtRepository)
            val viewModel = ViewModelProvider(self, viewModelFactory)[TextTranslateViewModel::class.java]
            TextTranslateScreen(viewModel, navController)
        }
        composable("ocrScreen") {
            val ocrService = OcrService(TencentCloudClient.initOcrClient(SECRET_ID, SECRET_KEY, REGION))
            val ocrRepository = TencentCloudOcrRepository(ocrService)
            val viewModelFactory = OcrViewModelFactory(self.application, ocrRepository)
            val viewModel = ViewModelProvider(self, viewModelFactory)[OcrViewModel::class.java]
            OcrScreen(viewModel, navController)
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
//
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageAndVoiceRecognitionTheme {
        Greeting("Android")
    }
}