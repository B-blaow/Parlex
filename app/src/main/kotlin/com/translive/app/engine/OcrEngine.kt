package com.translive.app.engine

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class OcrBlock(
    val text: String,
    val boundingBox: Rect
)

data class OcrResult(
    val blocks: List<OcrBlock>,
    val imageWidth: Int,
    val imageHeight: Int
)

@Singleton
class OcrEngine @Inject constructor() {

    private val latinRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val chineseRecognizer: TextRecognizer =
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

    /**
     * Select recognizer based on source language.
     * Chinese recognizer also handles Latin, so it's a good default for CJK.
     */
    private fun getRecognizer(sourceLanguageCode: String): TextRecognizer {
        return when {
            sourceLanguageCode.startsWith("zh") ||
            sourceLanguageCode == "ja" ||
            sourceLanguageCode == "ko" -> chineseRecognizer
            else -> latinRecognizer
        }
    }

    suspend fun recognize(bitmap: Bitmap, sourceLanguageCode: String = "en"): OcrResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        return recognizeImage(image, sourceLanguageCode)
    }

    @androidx.camera.core.ExperimentalGetImage
    suspend fun recognize(
        imageProxy: androidx.camera.core.ImageProxy,
        sourceLanguageCode: String = "en"
    ): OcrResult {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return OcrResult(emptyList(), 0, 0)
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        return try {
            recognizeImage(image, sourceLanguageCode)
        } finally {
            imageProxy.close()
        }
    }

    private suspend fun recognizeImage(
        image: InputImage,
        sourceLanguageCode: String
    ): OcrResult = suspendCoroutine { cont ->
        val recognizer = getRecognizer(sourceLanguageCode)
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val blocks = result.textBlocks.mapNotNull { textBlock ->
                    val box = textBlock.boundingBox ?: return@mapNotNull null
                    OcrBlock(
                        text = textBlock.text,
                        boundingBox = box
                    )
                }
                cont.resume(
                    OcrResult(
                        blocks = blocks,
                        imageWidth = image.width,
                        imageHeight = image.height
                    )
                )
            }
            .addOnFailureListener { e ->
                android.util.Log.e("OcrEngine", "OCR failed: ${e.message}", e)
                cont.resume(OcrResult(emptyList(), image.width, image.height))
            }
    }

    fun release() {
        latinRecognizer.close()
        chineseRecognizer.close()
    }
}
