# Сброс залипших performance/profiling-состояний Android

Эта инструкция нужна, когда после профилирования, тестов скорости или частых
переустановок приложение начинает вести себя странно: зависает старый режим
профилирования, производительность не возвращается к норме, Android Studio
Profiler не отпускает процесс, либо ART/runtime-профили приложения выглядят
залипшими.

Пакет приложения:

```powershell
$Package = "com.translive.app"
```

## 1. Проверить устройство

```powershell
adb devices
```

Если подключено несколько устройств, дальше добавляй `-s <serial>` после
`adb`:

```powershell
adb -s <serial> devices
```

## 2. Мягкий сброс без удаления данных

Это основной безопасный порядок. Он не удаляет настройки и модели приложения.

```powershell
adb shell am force-stop $Package
adb shell am profile stop $Package
adb shell cmd package compile --reset $Package
adb shell cmd package compile -m verify -f $Package
adb shell cmd package bg-dexopt-job
adb shell am force-stop $Package
```

После этого запусти приложение вручную с телефона или через adb:

```powershell
adb shell monkey -p $Package 1
```

## 3. Если залип Android Studio Profiler или визуальные GPU/HWUI-профили

Сначала закрой Profiler в Android Studio, потом выполни:

```powershell
adb shell am force-stop $Package
adb shell am profile stop $Package
adb shell setprop debug.hwui.profile false
adb shell setprop debug.hwui.show_dirty_regions false
adb shell setprop debug.hwui.overdraw false
adb shell settings put global show_touches 0
adb shell settings put system pointer_location 0
adb shell am force-stop $Package
```

Если на экране все еще остались визуальные полосы/оверлеи, перезагрузи
устройство:

```powershell
adb reboot
```

## 4. Если залип системный режим производительности/термальный тест

Используй только если до этого включались power/thermal-команды через adb.

```powershell
adb shell cmd power set-fixed-performance-mode-enabled false
adb shell cmd thermalservice override-status 0
adb shell dumpsys deviceidle unforce
adb shell am force-stop $Package
```

Если команда `thermalservice override-status 0` не поддерживается на прошивке,
это не критично. Пропусти ее и перезагрузи телефон:

```powershell
adb reboot
```

## 5. Жесткий чистый цикл с переустановкой

Используй, если мягкий сброс не помог. Этот вариант удаляет данные приложения:
настройки, историю, локальные привязки и импортированные данные приложения.

```powershell
adb shell am force-stop $Package
adb shell am profile stop $Package
adb shell cmd package compile --reset $Package
adb uninstall $Package
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p $Package 1
```

Для подписанной release/beta APK путь будет другим:

```powershell
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 6. Быстрая диагностика после сброса

Проверить, что процесс запущен заново:

```powershell
adb shell pidof $Package
```

Проверить последние ошибки приложения:

```powershell
adb logcat -d -t 300 | Select-String "com.translive.app|AndroidRuntime|CameraVM|TranslationEngine"
```

Если после мягкого сброса проблема ушла, жесткий цикл делать не нужно.
