package com.translive.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.translive.app.data.ModelRepository
import com.translive.app.data.model.Language
import com.translive.app.engine.TranslationEngine
import com.translive.app.engine.TtsEngine
import com.translive.app.engine.TtsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogueMessage(
    val sourceText: String,
    val translatedText: String,
    val sourceLang: Language,
    val targetLang: Language
)

data class DialogueUiState(
    val messages: List<DialogueMessage> = emptyList(),
    val inputText: String = "",
    val sourceLanguage: Language = Language.RUSSIAN,
    val targetLanguage: Language = Language.ENGLISH,
    val isTranslating: Boolean = false,
    val isModelReady: Boolean = false,
    val isTtsReady: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DialogueViewModel @Inject constructor(
    private val app: Application,
    private val engine: TranslationEngine,
    private val modelRepository: ModelRepository,
    private val ttsEngine: TtsEngine
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(DialogueUiState())
    val uiState: StateFlow<DialogueUiState> = _uiState.asStateFlow()

    val ttsState: StateFlow<TtsState> = ttsEngine.state

    init {
        // Check if model is loaded
        viewModelScope.launch {
            val activeId = modelRepository.getActiveModelId()
            val loaded = activeId != null
            _uiState.update {
                it.copy(
                    isModelReady = loaded,
                    isTtsReady = ttsEngine.isModelDownloaded()
                )
            }
            if (loaded && ttsEngine.isModelDownloaded()) {
                ttsEngine.loadModel()
            }
        }
    }

    fun setInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        _uiState.update { it.copy(inputText = "", isTranslating = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = _uiState.value

                val translated = engine.translate(
                    sourceText = text,
                    source = state.sourceLanguage,
                    target = state.targetLanguage
                )
                val cleanTranslation = translated.trim()

                val message = DialogueMessage(
                    sourceText = text,
                    translatedText = cleanTranslation,
                    sourceLang = state.sourceLanguage,
                    targetLang = state.targetLanguage
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + message,
                        isTranslating = false
                    )
                }

                // Auto-speak translation if TTS is ready
                if (ttsEngine.isModelReady.value) {
                    ttsEngine.speak(cleanTranslation)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isTranslating = false, error = e.message)
                }
            }
        }
    }

    fun speak(text: String) {
        if (ttsEngine.state.value == TtsState.SPEAKING) {
            ttsEngine.stop()
        } else {
            ttsEngine.speak(text)
        }
    }
}
