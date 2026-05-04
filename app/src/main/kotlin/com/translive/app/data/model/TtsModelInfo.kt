package com.translive.app.data.model

/**
 * Kokoro TTS model info for Sherpa-ONNX.
 * Archive contains: model.onnx, voices.bin, tokens.txt, espeak-ng-data/, lexicons
 */
object TtsModelInfo {
    const val DISPLAY_NAME = "Kokoro Multi-lang v1.1"
    const val DESCRIPTION = "82M params • Chinese + English • 102 голоса"
    const val DOWNLOAD_URL = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/kokoro-multi-lang-v1_1.tar.bz2"
    const val ARCHIVE_FILENAME = "kokoro-multi-lang-v1_1.tar.bz2"
    const val EXTRACTED_DIR = "kokoro-multi-lang-v1_1"
    const val SIZE_BYTES = 91_000_000L // ~87 MB compressed
    const val SIZE_LABEL = "87 МБ"
    const val RAM_ESTIMATE_MB = 350
}
