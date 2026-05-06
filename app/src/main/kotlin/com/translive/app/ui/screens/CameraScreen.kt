package com.translive.app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.translive.app.ui.components.LanguagePickerSheet
import com.translive.app.ui.viewmodel.CameraMode
import com.translive.app.ui.viewmodel.CameraViewModel
import com.translive.app.ui.viewmodel.TranslatedBlock
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@androidx.camera.core.ExperimentalGetImage
@Composable
fun CameraScreen(
    onNavigateToTranslate: () -> Unit = {},
    onNavigateToDialogue: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToModels: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }

    // Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.setPermissionGranted(granted) }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.setPermissionGranted(true)
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            NavigationBar(tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = false, onClick = onNavigateToTranslate,
                    icon = { Icon(Icons.Filled.Translate, "Text") },
                    label = { Text("Текст") }
                )
                NavigationBarItem(
                    selected = false, onClick = onNavigateToDialogue,
                    icon = { Icon(Icons.Filled.RecordVoiceOver, "Dialogue") },
                    label = { Text("Диалог") }
                )
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Icon(Icons.Filled.CameraAlt, "Camera") },
                    label = { Text("Камера") }
                )
                NavigationBarItem(
                    selected = false, onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Filled.History, "History") },
                    label = { Text("История") }
                )
                NavigationBarItem(
                    selected = false, onClick = onNavigateToModels,
                    icon = { Icon(Icons.Filled.DownloadForOffline, "Models") },
                    label = { Text("Модели") }
                )
                NavigationBarItem(
                    selected = false, onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Filled.Settings, "Settings") },
                    label = { Text("Настройки") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!uiState.hasCameraPermission) {
                // Permission denied
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, null, Modifier.size(64.dp), tint = Color.White.copy(0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("Доступ к камере не разрешён", color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Разрешить")
                    }
                }
            } else {
                when (uiState.mode) {
                    CameraMode.LIVE -> {
                        LiveCameraView(viewModel = viewModel, onPreviewView = { previewViewRef = it })

                        // Lightweight OCR highlight — just thin colored borders
                        if (uiState.blocks.isNotEmpty() && uiState.imageWidth > 0) {
                            OcrHighlightOverlay(
                                blocks = uiState.blocks,
                                imageWidth = uiState.imageWidth,
                                imageHeight = uiState.imageHeight
                            )
                        }
                    }
                    CameraMode.CAPTURE -> {
                        CaptureView(
                            bitmap = uiState.capturedBitmap,
                            blocks = uiState.blocks,
                            imageWidth = uiState.imageWidth,
                            imageHeight = uiState.imageHeight
                        )
                    }
                }

                // Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        if (uiState.mode == CameraMode.LIVE) {
                            IconButton(
                                onClick = {
                                    previewViewRef?.bitmap?.let { viewModel.capture(it) }
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f))
                            ) {
                                Box(Modifier.size(56.dp).clip(CircleShape).background(Color.White))
                            }
                        } else {
                            FilledTonalButton(onClick = { viewModel.backToLive() }) {
                                Icon(Icons.Filled.CameraAlt, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Назад к камере")
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Language bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AssistChip(
                            onClick = { showSourcePicker = true },
                            label = { Text("${uiState.sourceLanguage.flag} ${uiState.sourceLanguage.nativeName}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        IconButton(onClick = { viewModel.swapLanguages() }) {
                            Icon(Icons.Filled.SwapHoriz, "Swap")
                        }
                        AssistChip(
                            onClick = { showTargetPicker = true },
                            label = { Text("${uiState.targetLanguage.flag} ${uiState.targetLanguage.nativeName}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (uiState.isProcessing) {
                    LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }
            }
        }
    }

    if (showSourcePicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.sourceLanguage,
            excludeLanguage = uiState.targetLanguage,
            onLanguageSelected = { viewModel.setSourceLanguage(it); showSourcePicker = false },
            onDismiss = { showSourcePicker = false }
        )
    }
    if (showTargetPicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.targetLanguage,
            excludeLanguage = uiState.sourceLanguage,
            onLanguageSelected = { viewModel.setTargetLanguage(it); showTargetPicker = false },
            onDismiss = { showTargetPicker = false }
        )
    }
}

/**
 * Live camera — just the preview, no overlay processing here.
 */
@androidx.camera.core.ExperimentalGetImage
@Composable
private fun LiveCameraView(
    viewModel: CameraViewModel,
    onPreviewView: (PreviewView) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                onPreviewView(this)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val provider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            viewModel.processLiveFrame(imageProxy)
                        }
                    }
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,
                        preview, imageAnalysis
                    )
                } catch (e: Exception) {
                    android.util.Log.e("CameraScreen", "Camera bind failed: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

/**
 * Live OCR overlay — thin colored borders around detected text blocks.
 * No text rendering, just clean outlines showing where text was found.
 */
@Composable
private fun OcrHighlightOverlay(
    blocks: List<TranslatedBlock>,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // The camera image is typically rotated. ML Kit returns coords
        // in the rotated image space. PreviewView FILL_CENTER scales
        // the image to fill the view, so we scale proportionally.
        val scaleX = size.width / imageWidth.toFloat()
        val scaleY = size.height / imageHeight.toFloat()
        val scale = maxOf(scaleX, scaleY) // FILL_CENTER uses max
        val offsetX = (size.width - imageWidth * scale) / 2f
        val offsetY = (size.height - imageHeight * scale) / 2f

        for (block in blocks) {
            val box = block.boundingBox
            // Filter tiny blocks (noise)
            if (box.width() < 20 || box.height() < 10) continue

            val left = box.left * scale + offsetX
            val top = box.top * scale + offsetY
            val w = box.width() * scale
            val h = box.height() * scale

            // Draw rounded border
            drawRoundRect(
                color = Color(0xFF4FC3F7),
                topLeft = Offset(left, top),
                size = Size(w, h),
                cornerRadius = CornerRadius(4f),
                style = Stroke(width = 2.5f)
            )
        }
    }
}

/**
 * Captured image with pinch-to-zoom + translation overlay.
 * Translation text is rendered over a semi-transparent background
 * on top of each detected text block.
 */
@Composable
private fun CaptureView(
    bitmap: Bitmap?,
    blocks: List<TranslatedBlock>,
    imageWidth: Int,
    imageHeight: Int
) {
    if (bitmap == null) return

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()
    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        val canvasW = size.width
        val canvasH = size.height

        // Fit image to canvas
        val fitScale = minOf(canvasW / bitmap.width, canvasH / bitmap.height)
        val baseOffsetX = (canvasW - bitmap.width * fitScale) / 2f
        val baseOffsetY = (canvasH - bitmap.height * fitScale) / 2f

        val totalScale = fitScale * scale
        val totalOffsetX = baseOffsetX + offsetX
        val totalOffsetY = baseOffsetY + offsetY

        // Draw bitmap
        drawImage(
            image = imageBitmap,
            dstOffset = androidx.compose.ui.unit.IntOffset(
                totalOffsetX.toInt(), totalOffsetY.toInt()
            ),
            dstSize = androidx.compose.ui.unit.IntSize(
                (bitmap.width * totalScale).toInt(),
                (bitmap.height * totalScale).toInt()
            )
        )

        // Draw translations on top
        // OCR coords are in bitmap space, so scale by totalScale
        val ocrScaleX = totalScale
        val ocrScaleY = totalScale

        for (block in blocks) {
            val box = block.boundingBox
            if (box.width() < 20 || box.height() < 10) continue

            val left = box.left * ocrScaleX + totalOffsetX
            val top = box.top * ocrScaleY + totalOffsetY
            val w = box.width() * ocrScaleX
            val h = box.height() * ocrScaleY

            val hasTranslation = block.translatedText.isNotBlank()

            if (hasTranslation) {
                // Solid background to cover original text
                drawRoundRect(
                    color = Color(0xE6222222),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4f)
                )

                // Translated text
                val fontSize = (h * 0.35f).coerceIn(8f, 22f)
                val layoutResult = textMeasurer.measure(
                    text = AnnotatedString(block.translatedText),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.15f).sp
                    ),
                    constraints = androidx.compose.ui.unit.Constraints(
                        maxWidth = w.toInt().coerceAtLeast(1)
                    )
                )
                drawText(layoutResult, topLeft = Offset(left + 4f, top + 2f))
            } else {
                // Not yet translated — just a border
                drawRoundRect(
                    color = Color(0xFF4FC3F7),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4f),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
