package com.example.imageandvoicerecognition.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.imageandvoicerecognition.ui.viewmodel.TextTranslateViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTranslateScreen(textTranslateViewModel: TextTranslateViewModel, navController: NavController) {
    var sourceText by remember { mutableStateOf("") }
    var sourceLanguage by remember { mutableStateOf("en") }
    var targetLanguage by remember { mutableStateOf("zh")}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("多语言翻译") },
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
        val translateText by textTranslateViewModel.translatedText
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = sourceText,
                onValueChange = { sourceText = it },
                label = { Text("Source Text")}
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                OutlinedTextField(
                    value = sourceLanguage,
                    onValueChange = { sourceLanguage = it},
                    label = { Text("Source Language")},
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = targetLanguage,
                    onValueChange = { targetLanguage = it},
                    label = { Text("Target Language")},
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                textTranslateViewModel.translateText(sourceText, sourceLanguage, targetLanguage)
            }) {
                Text("Translate")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Translated Text:")
            Text(translateText)
        }
    }
}