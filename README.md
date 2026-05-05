<p align="center">
  <img src="docs/icon.png" width="128" height="128" alt="Parlex Icon" style="border-radius: 22px;" />
</p>

<h1 align="center">Parlex</h1>

<p align="center">
  <strong>Офлайн AI-переводчик для Android</strong><br>
  <sub>33 языка • 1 056 направлений • 100% без интернета • Голосовой диалог</sub>
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" /></a>
  <a href="#"><img src="https://img.shields.io/badge/API-26+-brightgreen" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Arch-arm64--v8a-blue" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Version-1.0.0-orange" /></a>
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Model-Tencent_Hy--MT_1.5_1.8B-red?logo=huggingface&logoColor=white" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Engine-llama.cpp-black?logo=c%2B%2B&logoColor=white" /></a>
  <a href="#"><img src="https://img.shields.io/badge/STT-Whisper_Tiny-purple" /></a>
  <a href="#"><img src="https://img.shields.io/badge/TTS-Kokoro_(Sherpa--ONNX)-pink" /></a>
</p>

---

## ✨ Возможности

| | Функция | Описание |
|:--|:--|:--|
| 🌍 | **33 языка** | Все комбинации — 1 056 направлений перевода |
| ⚡ | **Быстро** | ~1 сек на предложение (Snapdragon 865+) |
| 🔒 | **Офлайн** | Нет сети — нет утечки данных |
| 🎙️ | **Голосовой диалог** | Два человека говорят — приложение переводит в реальном времени |
| 🔊 | **Озвучка** | Перевод читается вслух (Kokoro TTS) |
| 📝 | **История** | Полная история переводов с поиском, фильтрами и избранным |
| ⭐ | **Избранное** | Отмечай важные переводы для быстрого доступа |
| 📦 | **Менеджер моделей** | 12 квантизаций Hy-MT — от 2-bit (574 МБ) до F16 (3.6 ГБ) |
| ⚙️ | **Настройки** | Потоки CPU, бэкенд, авто-выгрузка модели |

---

## 🏗️ Архитектура

```
┌─────────────────────────────────────────────────┐
│                   Parlex App                    │
├──────────┬──────────┬───────────┬───────────────┤
│  Text UI │ Voice UI │ History   │ Model Manager │
│ Compose  │ Compose  │ Room DB   │ Download +    │
│          │          │ Search    │ GGUF select   │
├──────────┴──────────┴───────────┴───────────────┤
│              ViewModels (Hilt DI)               │
├─────────────────────────────────────────────────┤
│            Translation Engine (JNI)             │
│     ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│     │ llama.cpp│  │Whisper   │  │Sherpa-ONNX│  │
│     │ (GGUF)   │  │(STT/VAD) │  │(Kokoro TTS│  │
│     └──────────┘  └──────────┘  └───────────┘  │
├─────────────────────────────────────────────────┤
│               ARM NEON (arm64-v8a)              │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Технологии

| Компонент | Технология |
|:--|:--|
| **UI** | Kotlin + Jetpack Compose (Material 3, Dynamic Color) |
| **Навигация** | 5 вкладок: Текст · Диалог · История · Модели · Настройки |
| **Перевод** | [llama.cpp](https://github.com/ggerganov/llama.cpp) — GGUF модели через JNI |
| **Модель** | [Tencent Hy-MT 1.5 1.8B](https://huggingface.co/tencent/Hy-MT1.5-1.8B) — 12 вариантов квантизации |
| **Распознавание речи** | Whisper Tiny (Sherpa-ONNX) + Silero VAD v5 |
| **Синтез речи** | Kokoro TTS (Sherpa-ONNX) |
| **Хранение** | Room DB — история, сессии, избранное |
| **DI** | Dagger Hilt |
| **Сборка** | Gradle 8.11 + CMake 3.22 + NDK 27.2 |

---

## 📦 Квантизации моделей

Приложение поддерживает скачивание и переключение между 12 вариантами модели:

| Квантизация | Размер | RAM | Качество |
|:--|:--|:--|:--|
| 2-bit (Tencent) | 574 МБ | ~1 ГБ | ★★☆☆☆ |
| Q2_K | ~680 МБ | ~1.1 ГБ | ★★☆☆☆ |
| IQ3_XS | ~750 МБ | ~1.2 ГБ | ★★★☆☆ |
| IQ3_M | ~800 МБ | ~1.3 ГБ | ★★★☆☆ |
| Q3_K_M | ~850 МБ | ~1.3 ГБ | ★★★☆☆ |
| IQ4_XS | ~930 МБ | ~1.4 ГБ | ★★★★☆ |
| **Q4_K_M** | **~1.0 ГБ** | **~1.5 ГБ** | **★★★★☆ рекомендуется** |
| Q5_K_M | ~1.2 ГБ | ~1.7 ГБ | ★★★★☆ |
| Q6_K | ~1.4 ГБ | ~1.9 ГБ | ★★★★★ |
| Q8_0 | ~1.9 ГБ | ~2.4 ГБ | ★★★★★ |
| F16 | ~3.6 ГБ | ~4.1 ГБ | ★★★★★ |
| BF16 | ~3.6 ГБ | ~4.1 ГБ | ★★★★★ |

---

## 🌐 Поддерживаемые языки

<details>
<summary><strong>33 языка (нажми чтобы раскрыть)</strong></summary>

| | Язык | Код |
|:--|:--|:--|
| 🇬🇧 | English | `en` |
| 🇷🇺 | Русский | `ru` |
| 🇨🇳 | 中文 (简体) | `zh` |
| 🇹🇼 | 中文 (繁體) | `zh-TW` |
| 🇯🇵 | 日本語 | `ja` |
| 🇰🇷 | 한국어 | `ko` |
| 🇫🇷 | Français | `fr` |
| 🇩🇪 | Deutsch | `de` |
| 🇪🇸 | Español | `es` |
| 🇵🇹 | Português | `pt` |
| 🇮🇹 | Italiano | `it` |
| 🇳🇱 | Nederlands | `nl` |
| 🇵🇱 | Polski | `pl` |
| 🇨🇿 | Čeština | `cs` |
| 🇹🇷 | Türkçe | `tr` |
| 🇺🇦 | Українська | `uk` |
| 🇲🇲 | မြန်မာ | `my` |
| 🇮🇳 | हिन्दी | `hi` |
| 🇧🇩 | বাংলা | `bn` |
| 🇮🇳 | ગુજરાતી | `gu` |
| 🇮🇳 | मराठी | `mr` |
| 🇮🇳 | தமிழ் | `ta` |
| 🇮🇳 | తెలుగు | `te` |
| 🇵🇰 | اردو | `ur` |
| 🇮🇷 | فارسی | `fa` |
| 🇮🇱 | עברית | `he` |
| 🇸🇦 | العربية | `ar` |
| 🇹🇭 | ไทย | `th` |
| 🇻🇳 | Tiếng Việt | `vi` |
| 🇮🇩 | Bahasa Indonesia | `id` |
| 🇲🇾 | Bahasa Melayu | `ms` |
| 🇵🇭 | Filipino | `fil` |
| 🇰🇭 | ភាសាខ្មែរ | `km` |

**+ 5 диалектов:** кантонский, хоккиен, тибетский, монгольский, уйгурский

</details>

---

## 🚀 Быстрый старт

### Требования

- Android Studio Ladybug 2024.2+
- Android SDK 35 + NDK 27.2.12479018
- CMake 3.22.1
- Устройство: `arm64-v8a`, Android 8.0+ (API 26)

### 1. Клонирование

```bash
git clone https://github.com/RandoTeam/Parlex.git
cd Parlex
```

### 2. Подключение нативных движков

```bash
# llama.cpp (перевод)
cd app/src/main/cpp
git clone --depth 1 https://github.com/ggerganov/llama.cpp.git

# whisper.cpp (распознавание речи) — если ещё нет
git clone --depth 1 https://github.com/ggerganov/whisper.cpp.git
cd ../../../..
```

### 3. Сборка

```bash
# Через Gradle
./gradlew assembleDebug

# Или откройте в Android Studio → Sync → Build
```

### 4. Установка

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

> **Примечание:** Модели скачиваются внутри приложения через встроенный менеджер моделей. Не нужно ничего пушить вручную.

---

## 📱 Экраны приложения

| Экран | Описание |
|:--|:--|
| **Текст** | Ввод текста → мгновенный офлайн-перевод с кнопкой озвучки |
| **Диалог** | Голосовой режим: два человека говорят на разных языках |
| **История** | Все переводы / ⭐ Избранное / 🎤 Голосовые сессии |
| **Модели** | Скачивание, выбор и удаление моделей + TTS/STT |
| **Настройки** | CPU потоки (1-8), бэкенд, таймаут авто-выгрузки |

---

## 📂 Структура проекта

```
app/src/main/
├── kotlin/com/translive/app/
│   ├── data/
│   │   ├── db/              # Room Database + DAO
│   │   ├── model/           # Entities, ModelVariant, Language
│   │   ├── ModelRepository   # Active model management
│   │   └── SettingsRepository # SharedPreferences
│   ├── di/                  # Hilt DI module
│   ├── engine/
│   │   ├── TranslationEngine # JNI → llama.cpp
│   │   ├── SpeechEngine      # Whisper + Silero VAD
│   │   ├── TtsEngine          # Kokoro TTS
│   │   └── ModelDownloadManager
│   └── ui/
│       ├── screens/         # 5 Compose screens
│       ├── viewmodel/       # ViewModels (Translation, Dialogue, History, ModelManager, Settings)
│       ├── components/      # LanguagePicker, etc.
│       └── TransLiveNavHost # Navigation graph
├── cpp/
│   ├── translive_jni.cpp    # JNI bridge
│   ├── llama.cpp/           # git submodule (не в репозитории)
│   └── whisper.cpp/         # git submodule (не в репозитории)
└── res/
    ├── mipmap-*/            # App icons
    ├── drawable/            # Splash screen
    └── values/              # Themes, strings
```

---

## 🔧 Конфигурация

Настройки доступны прямо в приложении:

| Параметр | Значения | По умолчанию |
|:--|:--|:--|
| CPU потоки | 1, 2, 4, 6, 8 | 4 |
| Бэкенд | CPU / GPU (скоро) / NPU (скоро) | CPU |
| Авто-выгрузка | Выкл, 1, 2, 5, 10, 30 мин | 5 мин |

---

## 📄 Лицензия

**Код приложения** — [MIT License](LICENSE)

**Модель перевода** — [Tencent HY Community License](https://huggingface.co/tencent/Hy-MT1.5-1.8B-2bit-GGUF).  
Модель **не включена** в репозиторий. Пользователь должен скачать её самостоятельно и принять лицензию модели.

> ⚠️ Лицензия модели Tencent **не покрывает** ЕС, Великобританию и Южную Корею.

---

## 👨‍💻 Автор

**Ilia Vlasov** — [@RandoTeam](https://github.com/RandoTeam)

---

<p align="center">
  <sub>Made with ❤️ and llama.cpp</sub>
</p>
