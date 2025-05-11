package com.example.voiceandcamerarecognition.viewmodels

import com.example.voiceandcamerarecognition.schemas.RecognitionItem
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class RecognitionItemViewModelTest {

    /**
     * 最大アイテム数制限のテスト
     *
     * アイテム数が最大アイテム数に収まっていれば良し
     */
    @Test
    fun maxItemsLimitation() {
        val viewModel = RecognitionItemViewModel()
        val numberOfItemsToAdd = viewModel.MAX_ITEMS + 10;

        // 最大アイテム数以上のアイテムを追加する
        for (i in 0 until numberOfItemsToAdd) {
            val item = RecognitionItem("voice$i", "text$i", LocalDateTime.now())
            viewModel.addRecognitionItem(item)
        }

        assertEquals(viewModel.MAX_ITEMS, viewModel.recognitionItems.size)
    }
}