# Project Stage Snapshot

Snapshot date: 2026-06-20

## Repository

- Remote: `https://github.com/RandoTeam/Parlex.git`
- Branch: `main`
- Baseline head before this release pass: `c579621`
- Latest public signed prerelease: `v1.4.1-beta.1`
- Latest public APK asset: `Parlex-v1.4.1-beta.1-release.apk`
- Public APK SHA-256:
  `c7935b493db3b5b6a40f62249829b06cb8eb1de9134687a65c99c178ed7da45e`

## App Version

- `versionCode`: 9
- `versionName`: `1.4.1-beta.1`
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

## Current Release Signing State

The original release signing key was not available after the workstation
rebuild, so `v1.4.1-beta.1` uses a newly generated 2026 Parlex release key.

Android treats this as a signing-key rotation without lineage. Users with older
Parlex APKs signed by the previous key must uninstall the old app before
installing `v1.4.1-beta.1`.

Do not upload `app-debug.apk` as a beta or release.

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

These are intentionally ignored. Build outputs and restored dependencies are
safe to delete before reinstalling Windows or preparing a clean project
transfer. Signing secrets must be backed up first if they should remain usable.

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

1. Back up the new local release keystore outside the repository.
2. Run full manual smoke on a real phone.
3. Continue camera document polish.
4. Benchmark LiteRT on Snapdragon 8 Elite against GGUF.
