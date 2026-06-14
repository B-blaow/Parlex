# Project Stage Snapshot

Snapshot date: 2026-06-15

## Repository

- Remote: `https://github.com/RandoTeam/Parlex.git`
- Branch: `main`
- Baseline head before this documentation handoff: `374075a`
- Latest public signed prerelease: `v1.4.0-beta.1`
- Latest public APK asset: `Parlex-v1.4.0-beta.1-release.apk`
- Public APK SHA-256:
  `739cec41ab390414c7338c20acbc5351a59f0e6db54aa92442036dff7f14dbd8`

## App Version

- `versionCode`: 8
- `versionName`: `1.4.0-beta.1`
- `compileSdk`: 36
- `targetSdk`: 35
- `minSdk`: 26
- ABI: `arm64-v8a`

## Current Functional Stage

- Text translation works offline with source auto-detection and separately
  persisted language direction.
- Dialogue mode works with its own persisted language direction, STT, translation,
  and system TTS.
- Camera mode is beta and supports live translation, full-resolution capture,
  gallery photo translation, source auto-detection, mixed-language capture logic,
  torch, camera candidates exposed by Android CameraX, quality hints, and debug
  capture packs in debug builds.
- Model catalog contains Tencent HY-MT 1.5, Tencent Hy-MT2, Google
  TranslateGemma GGUF, and TranslateGemma LiteRT-LM Beta entries.
- Interface localization is implemented for system, English, Russian, Simplified
  Chinese, and Traditional Chinese.
- GitHub Actions `Android Check` builds debug APKs for pull requests and pushes
  to `main`; it does not publish releases.
- Contributor workflow exists through `CONTRIBUTING.md` and the PR template.

## Repository Work Recently Completed

- GitHub issue #2 was closed as completed by `v1.4.0-beta.1`.
- External PR #3 was reviewed, credited as an i18n contribution idea, and closed
  as implemented separately.
- Android CI was added and updated to current action versions.
- i18n was implemented in the project architecture instead of merging the broad
  external PR directly.

## Current Release Blocker

This workstation currently does not contain `keystore.properties` or the Parlex
release keystore. Because of that, a new signed beta APK must not be published
from this checkout yet.

Do not upload `app-debug.apk` as a beta or release. Restore the original release
keystore first so Android users can update from the existing signed APK.

## Restore After Clean Clone

Tracked source does not include generated outputs, local model files, native
engine checkouts, or signing secrets.

Restore when needed:

```powershell
git clone https://github.com/RandoTeam/Parlex.git
cd Parlex

git clone https://github.com/ggml-org/llama.cpp.git app/src/main/cpp/llama.cpp
git -C app/src/main/cpp/llama.cpp checkout 5dcb71166686799f0d873eab7386234302d05ecf
```

For signed builds, also restore:

- `keystore.properties`
- the matching release keystore file referenced by `storeFile`

## Clean Folder Policy

These are intentionally ignored and safe to delete before reinstalling Windows
or preparing a clean project transfer:

- `.gradle/`
- `build/`
- `app/build/`
- `app/.cxx/`
- APK/AAB files
- `keystore.properties`
- local model files: `*.gguf`, `*.litertlm`, `*.tflite`, `*.task`,
  `*.safetensors`, `*.bin`
- local native engine checkout: `app/src/main/cpp/llama.cpp/`
- legacy ignored native checkout path, if present locally:
  `app/src/main/cpp/whisper.cpp/`
- `diagnostics/`

## Next Maintainer Action

1. Restore the release keystore.
2. Bump to the next beta version.
3. Build and verify a signed release APK.
4. Publish the next GitHub prerelease with user-facing release notes.
