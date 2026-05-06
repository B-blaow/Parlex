package com.translive.app.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translive.app.data.model.Language
import com.translive.app.engine.OcrEngine
import com.translive.app.engine.OcrResult
import com.translive.app.engine.TranslationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TranslatedBlock(
    val originalText: String,
    val translatedText: String,
    val boundingBox: Rect
)

enum class CameraMode { LIVE, CAPTURE }

data class CameraUiState(
    val mode: CameraMode = CameraMode.LIVE,
    val sourceLanguage: Language = Language.ENGLISH,
    val targetLanguage: Language = Language.RUSSIAN,
    val blocks: List<TranslatedBlock> = emptyList(),
    val isProcessing: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val hasCameraPermission: Boolean = false
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val ocrEngine: OcrEngine,
    private val translationEngine: TranslationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var translateJob: Job? = null

    /** Throttle: don't process if already processing */
    @Volatile
    private var isLiveProcessing = false

    fun setPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = granted) }
    }

    fun setSourceLanguage(lang: Language) {
        _uiState.update { it.copy(sourceLanguage = lang) }
    }

    fun setTargetLanguage(lang: Language) {
        _uiState.update { it.copy(targetLanguage = lang) }
    }

    fun swapLanguages() {
        _uiState.update {
            it.copy(sourceLanguage = it.targetLanguage, targetLanguage = it.sourceLanguage)
        }
    }

    /**
     * Process a live camera frame. Throttled: skips if previous frame
     * is still processing. Translates only the largest text block.
     */
    @androidx.camera.core.ExperimentalGetImage
    fun processLiveFrame(imageProxy: androidx.camera.core.ImageProxy) {
        if (isLiveProcessing || _uiState.value.mode != CameraMode.LIVE) {
            imageProxy.close()
            return
        }
        isLiveProcessing = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = _uiState.value
                val ocrResult = ocrEngine.recognize(imageProxy, state.sourceLanguage.code)

                if (ocrResult.blocks.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            blocks = emptyList(),
                            imageWidth = ocrResult.imageWidth,
                            imageHeight = ocrResult.imageHeight
                        )
                    }
                } else {
                    // In live mode, translate only the largest block for speed
                    val largest = ocrResult.blocks.maxByOrNull {
                        it.boundingBox.width() * it.boundingBox.height()
                    }

                    val translated = if (largest != null && translationEngine.isLoaded) {
                        try {
                            val result = translationEngine.translate(
                                largest.text, state.sourceLanguage, state.targetLanguage
                            )
                            listOf(TranslatedBlock(largest.text, result, largest.boundingBox))
                        } catch (_: Exception) {
                            // Translation failed, show OCR only
                            ocrResult.blocks.map {
                                TranslatedBlock(it.text, "", it.boundingBox)
                            }
                        }
                    } else {
                        ocrResult.blocks.map {
                            TranslatedBlock(it.text, "", it.boundingBox)
                        }
                    }

                    _uiState.update {
                        it.copy(
                            blocks = translated,
                            imageWidth = ocrResult.imageWidth,
                            imageHeight = ocrResult.imageHeight
                        )
                    }
                }
            } catch (_: Exception) {
                // Ignore frame processing errors
            } finally {
                isLiveProcessing = false
            }
        }
    }

    /**
     * Capture: freeze the bitmap and run full OCR + translate all blocks.
     */
    fun capture(bitmap: Bitmap) {
        _uiState.update {
            it.copy(mode = CameraMode.CAPTURE, capturedBitmap = bitmap, blocks = emptyList(), isProcessing = true)
        }

        translateJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = _uiState.value
                val ocrResult = ocrEngine.recognize(bitmap, state.sourceLanguage.code)

                val translatedBlocks = ocrResult.blocks.map { block ->
                    val translated = if (translationEngine.isLoaded) {
                        try {
                            translationEngine.translate(
                                block.text, state.sourceLanguage, state.targetLanguage
                            )
                        } catch (_: Exception) { "" }
                    } else ""

                    TranslatedBlock(block.text, translated, block.boundingBox)
                }

                _uiState.update {
                    it.copy(
                        blocks = translatedBlocks,
                        imageWidth = ocrResult.imageWidth,
                        imageHeight = ocrResult.imageHeight,
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    /**
     * Return to live camera mode.
     */
    fun backToLive() {
        translateJob?.cancel()
        _uiState.update {
            it.copy(
                mode = CameraMode.LIVE,
                capturedBitmap = null,
                blocks = emptyList(),
                isProcessing = false
            )
        }
    }
}
