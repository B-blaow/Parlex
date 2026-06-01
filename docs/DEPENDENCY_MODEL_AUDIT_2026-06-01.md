# Dependency And Model Audit - 2026-06-01

## Scope

Checked the app baseline against official upstream sources on 2026-06-01:

- Google Maven metadata for AndroidX, ML Kit, LiteRT-LM, AGP.
- Maven Central metadata for Kotlin, KSP, Hilt, OkHttp, Lottie, Commons Compress, Coroutines.
- JitPack and GitHub tags for Tesseract4Android.
- Official `ggml-org/llama.cpp` tags.
- Official Tencent Hugging Face repositories for HY-MT 1.5 and Hy-MT2.
- TranslateGemma LiteRT beta artifact repository used by the current app catalog.

## Updated In Gradle

| Component | Before | After | Source |
|:--|:--|:--|:--|
| Compose BOM | 2024.12.01 | 2026.05.01 | https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/maven-metadata.xml |
| AndroidX Core KTX | 1.15.0 | 1.18.0 | https://dl.google.com/dl/android/maven2/androidx/core/core-ktx/maven-metadata.xml |
| Android Gradle Plugin | 8.7.3 | 8.9.1 | https://dl.google.com/dl/android/maven2/com/android/application/com.android.application.gradle.plugin/maven-metadata.xml |
| compileSdk | 35 | 36 | local installed Android SDK / AndroidX AAR metadata |
| Lifecycle runtime/viewmodel | 2.8.7 | 2.10.0 | https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-runtime-ktx/maven-metadata.xml |
| Activity Compose | 1.9.3 | 1.13.0 | https://dl.google.com/dl/android/maven2/androidx/activity/activity-compose/maven-metadata.xml |
| Navigation Compose | 2.8.5 | 2.9.8 | https://dl.google.com/dl/android/maven2/androidx/navigation/navigation-compose/maven-metadata.xml |
| AndroidX Hilt Navigation Compose | 1.2.0 | 1.3.0 | https://dl.google.com/dl/android/maven2/androidx/hilt/hilt-navigation-compose/maven-metadata.xml |
| Core SplashScreen | 1.0.1 | 1.2.0 | https://dl.google.com/dl/android/maven2/androidx/core/core-splashscreen/maven-metadata.xml |
| Lottie Compose | 6.6.2 | 6.7.1 | https://repo.maven.apache.org/maven2/com/airbnb/android/lottie-compose/maven-metadata.xml |
| OkHttp | 4.12.0 | 5.3.2 | https://repo.maven.apache.org/maven2/com/squareup/okhttp3/okhttp/maven-metadata.xml |
| Commons Compress | 1.26.1 | 1.28.0 | https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/maven-metadata.xml |
| Coroutines Android | 1.9.0 | 1.11.0 | https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-android/maven-metadata.xml |
| CameraX | 1.4.1 | 1.6.1 | https://dl.google.com/dl/android/maven2/androidx/camera/camera-camera2/maven-metadata.xml |
| ML Kit Language ID | not present | 17.0.6 | https://dl.google.com/dl/android/maven2/com/google/mlkit/language-id/maven-metadata.xml |
| LiteRT-LM Android | 0.11.0 | 0.12.0 | https://dl.google.com/dl/android/maven2/com/google/ai/edge/litertlm/litertlm-android/maven-metadata.xml |

## Already Current

| Component | Version | Source |
|:--|:--|:--|
| Room | 2.8.4 | https://dl.google.com/dl/android/maven2/androidx/room/room-runtime/maven-metadata.xml |
| ML Kit Text Recognition | 16.0.1 | https://dl.google.com/dl/android/maven2/com/google/mlkit/text-recognition/maven-metadata.xml |
| ML Kit Translate | 17.0.3 | https://dl.google.com/dl/android/maven2/com/google/mlkit/translate/maven-metadata.xml |
| Tesseract4Android | 4.9.0 | https://github.com/adaptech-cz/Tesseract4Android/tags |
| Sherpa-ONNX local AAR | 1.13.1 | local `app/libs/sherpa-onnx-1.13.1.aar` |

## Held Back Intentionally

| Component | Current | Checked Candidate | Reason |
|:--|:--|:--|:--|
| Android Gradle Plugin | 8.9.1 | 9.2.1 stable, 9.3.0-alpha09 latest metadata | AGP 9 is a major build toolchain migration. Keep separate from this audit commit. |
| Kotlin Gradle Plugin | 2.2.21 | 2.3.21 stable, 2.4.0-RC2 latest metadata | Must be paired with KSP and AGP migration. |
| KSP | 2.2.21-2.0.5 | 2.3.9 | Must match Kotlin plugin migration. |
| Hilt | 2.57.2 | 2.59.2 | Verified by Gradle: Hilt 2.59.2 requires AGP 9.0+, current app baseline is AGP 8.9.1. |
| targetSdk | 35 | installed SDK 36/36.1 | Target SDK changes app runtime behavior; keep separate from dependency compatibility. |

## Native Engine

Updated local ignored `app/src/main/cpp/llama.cpp/` checkout and tracked marker:

- Before: `b9219` / `45b455e66fc09abed65b7d52d42a4a29ba0d45d6`.
- After: `b9464` / `5dcb71166686799f0d873eab7386234302d05ecf`.
- Source: https://github.com/ggml-org/llama.cpp/releases

The actual source folder is ignored by git, so `app/src/main/cpp/llama.cpp.version` is the tracked contract for the expected checkout.

## Model Catalog

Updated Tencent HY-MT 1.5 catalog entries to official Tencent repositories:

- https://huggingface.co/tencent/HY-MT1.5-1.8B-GGUF
- https://huggingface.co/tencent/Hy-MT1.5-1.8B-1.25bit-GGUF
- https://huggingface.co/tencent/Hy-MT1.5-1.8B-2bit-GGUF

Removed the older third-party HY-MT 1.5 GGUF mirror from the active catalog.

Checked Hy-MT2 official repositories and kept them in the catalog:

- https://huggingface.co/tencent/Hy-MT2-1.8B-GGUF
- https://huggingface.co/tencent/Hy-MT2-1.8B-1.25Bit-GGUF
- https://huggingface.co/tencent/Hy-MT2-1.8B-2Bit-GGUF
- https://huggingface.co/tencent/Hy-MT2-7B-GGUF

Hy-MT2 repository metadata is `apache-2.0`; the app catalog now marks Hy-MT2 as Apache 2.0 instead of Tencent HY Community License.

TranslateGemma LiteRT beta remains a beta catalog path:

- https://huggingface.co/barakplasma/translategemma-4b-it-android-task-quantized

## Functional Change Included In This Commit

The current working tree also includes source-language auto-detection work:

- Shared `LanguageDetectionEngine` based on ML Kit Language ID.
- Text translation source can be set to auto-detect.
- Camera translation uses the shared detector for OCR text before selecting source language.

Dialogue mode is intentionally not changed by auto-detection in this pass.
