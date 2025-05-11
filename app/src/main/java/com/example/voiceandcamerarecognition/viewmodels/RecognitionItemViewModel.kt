package com.example.voiceandcamerarecognition.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.voiceandcamerarecognition.schemas.RecognitionItem

class RecognitionItemViewModel : ViewModel() {
    public val MAX_ITEMS = 500
    private val _recognitionItems = mutableStateListOf<RecognitionItem>()
    val recognitionItems: List<RecognitionItem> = _recognitionItems

    fun addRecognitionItem(item: RecognitionItem) {
        _recognitionItems.add(0, item)

        //-- item数はMAX_ITEMSに収める
        // MAX_ITEMSを取得してそれをクリア済の_recognitionItemsに代入する
        val latestMAXItems = _recognitionItems.take(MAX_ITEMS)
        _recognitionItems.clear()
        _recognitionItems.addAll(latestMAXItems)
    }
}
