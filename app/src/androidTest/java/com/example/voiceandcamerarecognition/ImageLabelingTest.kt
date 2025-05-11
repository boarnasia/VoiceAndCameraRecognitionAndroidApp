// app/src/androidTest/java/com/example/voiceandcamerarecognition/ImageLabelingTest.kt

package com.example.voiceandcamerarecognition

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.android.gms.tasks.Tasks // Tasks.await() を使うために必要
import com.google.common.truth.Truth.assertThat // Truth for assertions
import com.google.mlkit.vision.label.ImageLabeler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

/**
 * Instrumented test for ML Kit Image Labeling functionality.
 * Executes on an Android device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class ImageLabelingTest {

    private lateinit var appContext: Context
    private lateinit var imageLabeler: ImageLabeler

    @Before
    fun setUp() {
        // テスト実行中のアプリケーションコンテキストを取得
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // デフォルトモデルで ImageLabeler を初期化
        // ここで設定する confidenceThreshold は、テストの期待値と合わせる必要がある
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f) // CameraManager と同じ閾値を使用
            .build()
        imageLabeler = ImageLabeling.getClient(options)
    }

    @After
    fun tearDown() {
        // ImageLabeler を閉じる (リソース解放)
        imageLabeler.close()
    }

    // アセットから画像を読み込むヘルパー関数
    private fun loadBitmapFromAsset(assetFileName: String): android.graphics.Bitmap? {
        var inputStream: InputStream? = null
        return try {
            inputStream = appContext.assets.open(assetFileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }
    }

    @Test
    fun testImageLabelingWithCat1Image() {
        // テスト用の画像をアセットから読み込み
        val bitmap = loadBitmapFromAsset("cat-7182671_1920.jpg")
        assertThat(bitmap).isNotNull() // 画像が正しく読み込まれたか確認

        // Bitmap から InputImage を作成
        val inputImage = InputImage.fromBitmap(bitmap!!, 0) // 回転角度は0と仮定

        // ImageLabeler で画像を処理
        // Tasks.await() は非同期タスクの結果を同期的に待つ (テスト用途で便利)
        val labels = Tasks.await(imageLabeler.process(inputImage))

        // 結果を検証
        assertThat(labels).isNotEmpty() // ラベルが一つ以上検出されたか

        // 期待するラベルが含まれているか、かつ信頼度が閾値を超えているかを確認
        // "Cat" というラベルが検出され、その信頼度が 0.7 以上であることを期待
        val label = labels.find { it.text == "Cat" }
        assertThat(label).isNotNull() // "Cat" ラベルが存在するか
        assertThat(label!!.confidence).isAtLeast(0.7f) // 信頼度が閾値以上か
    }

    /**
     * 男性が塀にもたれかかっている画像
     * この画像だと Sitting のラベルは取れるが Dude とかは出力されないらしい
     */
    @Test
    fun testImageLabelingWithMan1Image() {
        // テスト用の画像をアセットから読み込み
        val bitmap = loadBitmapFromAsset("guy-1211433_1920.jpg")
        assertThat(bitmap).isNotNull() // 画像が正しく読み込まれたか確認

        // Bitmap から InputImage を作成
        val inputImage = InputImage.fromBitmap(bitmap!!, 0) // 回転角度は0と仮定

        // ImageLabeler で画像を処理
        // Tasks.await() は非同期タスクの結果を同期的に待つ (テスト用途で便利)
        val labels = Tasks.await(imageLabeler.process(inputImage))

        // 結果を検証
        assertThat(labels).isNotEmpty() // ラベルが一つ以上検出されたか

        // 期待するラベルが含まれているか、かつ信頼度が閾値を超えているかを確認
        val label = labels.find { it.text == "Standing" }
        assertThat(label).isNotNull()
        assertThat(label!!.confidence).isAtLeast(0.7f)
    }

    /**
     * 男性が洋室で座っている画像
     * この画像だと Sitting のラベルは取れるが Dude とかは出力されないらしい
     */
    @Test
    fun testImageLabelingWithMan2Image() {
        // テスト用の猫画像をアセットから読み込み
        val bitmap = loadBitmapFromAsset("groom-4696727_1920.jpg")
        assertThat(bitmap).isNotNull() // 画像が正しく読み込まれたか確認

        // Bitmap から InputImage を作成
        val inputImage = InputImage.fromBitmap(bitmap!!, 0) // 回転角度は0と仮定

        // ImageLabeler で画像を処理
        // Tasks.await() は非同期タスクの結果を同期的に待つ (テスト用途で便利)
        val labels = Tasks.await(imageLabeler.process(inputImage))

        // 結果を検証
        assertThat(labels).isNotEmpty() // ラベルが一つ以上検出されたか

        // 期待するラベルが含まれているか、かつ信頼度が閾値を超えているかを確認
        val label = labels.find { it.text == "Sitting" }
        assertThat(label).isNotNull()
        assertThat(label!!.confidence).isAtLeast(0.7f)
    }

}
