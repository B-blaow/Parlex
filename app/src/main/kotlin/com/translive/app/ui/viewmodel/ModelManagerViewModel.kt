package com.translive.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translive.app.data.ModelRepository
import com.translive.app.data.model.ModelVariant
import com.translive.app.data.model.TtsModelInfo
import com.translive.app.engine.DownloadState
import com.translive.app.engine.ModelDownloadManager
import com.translive.app.engine.TranslationEngine
import com.translive.app.engine.TtsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import javax.inject.Inject

enum class ModelStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ACTIVE,
    LOADING
}

data class ModelItemState(
    val variant: ModelVariant,
    val status: ModelStatus,
    val downloadState: DownloadState = DownloadState.Idle
)

data class ModelManagerUiState(
    val models: List<ModelItemState> = emptyList(),
    val totalDownloadedSize: Long = 0L,
    val availableSpace: Long = 0L,
    val isLoadingModel: Boolean = false,
    val ttsDownloaded: Boolean = false,
    val ttsDownloading: Boolean = false,
    val ttsProgress: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class ModelManagerViewModel @Inject constructor(
    private val repo: ModelRepository,
    private val downloadManager: ModelDownloadManager,
    private val engine: TranslationEngine,
    private val ttsEngine: TtsEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelManagerUiState())
    val uiState: StateFlow<ModelManagerUiState> = _uiState.asStateFlow()

    // Track active downloads
    private val downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())

    init {
        refreshModels()

        // React to download state changes
        viewModelScope.launch {
            downloadStates.collect { refreshModels() }
        }
    }

    fun refreshModels() {
        val activeId = repo.getActiveModelId()
        val downloads = downloadStates.value

        val models = ModelVariant.ALL.map { variant ->
            val isDownloaded = repo.isDownloaded(variant)
            val isActive = variant.id == activeId && isDownloaded
            val downloadState = downloads[variant.id]

            val status = when {
                isActive -> ModelStatus.ACTIVE
                downloadState is DownloadState.Downloading -> ModelStatus.DOWNLOADING
                isDownloaded -> ModelStatus.DOWNLOADED
                else -> ModelStatus.NOT_DOWNLOADED
            }

            ModelItemState(
                variant = variant,
                status = status,
                downloadState = downloadState ?: DownloadState.Idle
            )
        }

        _uiState.update {
            it.copy(
                models = models,
                totalDownloadedSize = repo.getTotalDownloadedSize(),
                availableSpace = repo.getAvailableSpace(),
                ttsDownloaded = ttsEngine.isModelDownloaded()
            )
        }
    }

    fun downloadModel(variant: ModelVariant) {
        // Check space
        if (repo.getAvailableSpace() < variant.sizeBytes * 1.1) {
            _uiState.update { it.copy(error = "Недостаточно места: нужно ${variant.sizeLabel}") }
            return
        }

        viewModelScope.launch {
            val destFile = repo.getDownloadFile(variant)

            downloadManager.downloadModel(variant, destFile).collect { state ->
                downloadStates.update { it + (variant.id to state) }

                when (state) {
                    is DownloadState.Completed -> {
                        downloadStates.update { it - variant.id }
                        // Auto-activate if no model is active
                        if (repo.getActiveModelId() == null) {
                            selectModel(variant)
                        } else {
                            refreshModels()
                        }
                    }
                    is DownloadState.Failed -> {
                        _uiState.update { it.copy(error = "Ошибка: ${state.error}") }
                        downloadStates.update { it - variant.id }
                    }
                    is DownloadState.Cancelled -> {
                        downloadStates.update { it - variant.id }
                    }
                    else -> {}
                }
            }
        }
    }

    fun cancelDownload(variant: ModelVariant) {
        downloadManager.cancelDownload(variant.id)
    }

    fun selectModel(variant: ModelVariant) {
        if (!repo.isDownloaded(variant)) return

        _uiState.update { it.copy(isLoadingModel = true, error = null) }

        viewModelScope.launch {
            try {
                // Unload current model
                engine.unloadModel()

                // Set new active
                repo.setActiveModelId(variant.id)
                val path = repo.getModelPath(variant) ?: return@launch

                // Load new model
                val threads = Runtime.getRuntime().availableProcessors().coerceIn(2, 8)
                val loaded = engine.loadModel(path, threads)

                if (!loaded) {
                    _uiState.update { it.copy(error = "Не удалось загрузить модель ${variant.quantName}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoadingModel = false) }
                refreshModels()
            }
        }
    }

    fun deleteModel(variant: ModelVariant) {
        if (repo.getActiveModelId() == variant.id) {
            engine.unloadModel()
        }
        repo.deleteModel(variant)
        refreshModels()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun downloadTtsModel() {
        _uiState.update { it.copy(ttsDownloading = true, ttsProgress = 0f) }

        viewModelScope.launch {
            try {
                val ttsDir = File(ttsEngine.modelDir.parent ?: return@launch)
                ttsDir.mkdirs()
                val archiveFile = File(ttsDir, TtsModelInfo.ARCHIVE_FILENAME)

                // Create a fake ModelVariant for the download manager
                val ttsVariant = ModelVariant(
                    id = "tts-kokoro",
                    quantName = TtsModelInfo.DISPLAY_NAME,
                    displayName = TtsModelInfo.DISPLAY_NAME,
                    description = TtsModelInfo.DESCRIPTION,
                    sizeBytes = TtsModelInfo.SIZE_BYTES,
                    ramEstimateMb = TtsModelInfo.RAM_ESTIMATE_MB,
                    downloadUrl = TtsModelInfo.DOWNLOAD_URL,
                    filename = TtsModelInfo.ARCHIVE_FILENAME
                )

                downloadManager.downloadModel(ttsVariant, archiveFile).collect { state ->
                    when (state) {
                        is DownloadState.Downloading -> {
                            _uiState.update { it.copy(ttsProgress = state.progress) }
                        }
                        is DownloadState.Completed -> {
                            // Extract tar.bz2
                            _uiState.update { it.copy(ttsProgress = 0.99f) }
                            extractTarBz2(archiveFile, ttsDir)
                            archiveFile.delete()
                            ttsEngine.loadModel()
                            _uiState.update {
                                it.copy(ttsDownloading = false, ttsDownloaded = true, ttsProgress = 1f)
                            }
                        }
                        is DownloadState.Failed -> {
                            _uiState.update {
                                it.copy(ttsDownloading = false, error = "TTS: ${state.error}")
                            }
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(ttsDownloading = false, error = "TTS error: ${e.message}")
                }
            }
        }
    }

    private fun extractTarBz2(archive: File, destDir: File) {
        try {
            val pb = ProcessBuilder(
                "tar", "xjf", archive.absolutePath, "-C", destDir.absolutePath
            )
            val proc = pb.start()
            proc.waitFor()
        } catch (e: Exception) {
            // Fallback: try using org.apache if tar is not available
            // For Android, tar is typically available in /system/bin/
            throw RuntimeException("Failed to extract TTS model: ${e.message}")
        }
    }
}
