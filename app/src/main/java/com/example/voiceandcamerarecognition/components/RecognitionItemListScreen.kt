package com.example.voiceandcamerarecognition.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voiceandcamerarecognition.ui.theme.VoiceAndCameraRecognitionTheme
import com.example.voiceandcamerarecognition.schemas.RecognitionItem
import com.example.voiceandcamerarecognition.viewmodels.RecognitionItemViewModel


// リストの各アイテムを表示する Composable
@Composable
fun RecognitionItemCard(item: RecognitionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Type: ${item.type}, Time: ${item.createdAt}\n${item.text}",
            modifier = Modifier.padding(8.dp)
        )
    }
}

// RecognitionResultList を修正してリストを受け取るようにする
@Composable
fun RecognitionResultList(
    recognitionItems: List<RecognitionItem>, // リストを引数として受け取る
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(recognitionItems) { item ->
            RecognitionItemCard(item = item)
        }
    }
}

@Composable
fun RecognitionItemListScreen(
    viewModel: RecognitionItemViewModel // ViewModel を使用
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        RecognitionResultList(
            viewModel.recognitionItems,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    val myViewModel = RecognitionItemViewModel() // ViewModel のインスタンスを作成
    VoiceAndCameraRecognitionTheme {
       RecognitionItemListScreen(myViewModel)
    }
}
