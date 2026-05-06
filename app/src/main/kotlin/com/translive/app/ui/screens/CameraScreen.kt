package com.translive.app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    val lifecycleOwner = LocalLifecycleOwner.current
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
        if (granted) {
            viewModel.setPermissionGranted(true)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Preview view reference for bitmap capture
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
                // Permission denied fallback
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.CameraAlt, "Camera",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Доступ к камере не разрешён", color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Разрешить")
                    }
                }
            } else {
                when (uiState.mode) {
                    CameraMode.LIVE -> {
                        // Live camera preview
                        LiveCameraView(
                            viewModel = viewModel,
                            onPreviewView = { previewViewRef = it }
                        )

                        // OCR overlay
                        if (uiState.blocks.isNotEmpty()) {
                            TranslationOverlay(
                                blocks = uiState.blocks,
                                imageWidth = uiState.imageWidth,
                                imageHeight = uiState.imageHeight
                            )
                        }
                    }
                    CameraMode.CAPTURE -> {
                        // Captured bitmap with pinch-to-zoom
                        CaptureView(
                            bitmap = uiState.capturedBitmap,
                            blocks = uiState.blocks,
                            imageWidth = uiState.imageWidth,
                            imageHeight = uiState.imageHeight,
                            isProcessing = uiState.isProcessing
                        )
                    }
                }

                // Controls overlay at bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    // Capture / Back button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (uiState.mode == CameraMode.LIVE) {
                            // Capture button
                            IconButton(
                                onClick = {
                                    val bitmap = previewViewRef?.bitmap
                                    if (bitmap != null) {
                                        viewModel.capture(bitmap)
                                    }
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        } else {
                            // Back to live
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

                // Processing indicator
                if (uiState.isProcessing) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }

    // Language pickers
    if (showSourcePicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.sourceLanguage,
            excludeLanguage = uiState.targetLanguage,
            onLanguageSelected = {
                viewModel.setSourceLanguage(it)
                showSourcePicker = false
            },
            onDismiss = { showSourcePicker = false }
        )
    }
    if (showTargetPicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.targetLanguage,
            excludeLanguage = uiState.sourceLanguage,
            onLanguageSelected = {
                viewModel.setTargetLanguage(it)
                showTargetPicker = false
            },
            onDismiss = { showTargetPicker = false }
        )
    }
}

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
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    android.util.Log.e("CameraScreen", "Camera bind failed: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
private fun TranslationOverlay(
    blocks: List<TranslatedBlock>,
    imageWidth: Int,
    imageHeight: Int
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val scaleX = size.width / imageWidth.toFloat()
        val scaleY = size.height / imageHeight.toFloat()

        for (block in blocks) {
            val box = block.boundingBox
            val left = box.left * scaleX
            val top = box.top * scaleY
            val right = box.right * scaleX
            val bottom = box.bottom * scaleY
            val w = right - left
            val h = bottom - top

            // Semi-transparent background
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(left, top),
                size = Size(w, h)
            )

            // Translated text
            val displayText = block.translatedText.ifBlank { block.originalText }
            val fontSize = (h * 0.35f).coerceIn(10f, 24f)

            val layoutResult = textMeasurer.measure(
                text = AnnotatedString(displayText),
                style = TextStyle(
                    color = Color.White,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.2f).sp
                ),
                constraints = androidx.compose.ui.unit.Constraints(
                    maxWidth = w.toInt().coerceAtLeast(1)
                )
            )
            drawText(layoutResult, topLeft = Offset(left + 2f, top + 2f))
        }
    }
}

@Composable
private fun CaptureView(
    bitmap: Bitmap?,
    blocks: List<TranslatedBlock>,
    imageWidth: Int,
    imageHeight: Int,
    isProcessing: Boolean
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
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        // Apply transform
        val canvasW = size.width
        val canvasH = size.height
        val imgAspect = bitmap.width.toFloat() / bitmap.height
        val canvasAspect = canvasW / canvasH

        val baseScale: Float
        val baseOffsetX: Float
        val baseOffsetY: Float

        if (imgAspect > canvasAspect) {
            baseScale = canvasW / bitmap.width
            baseOffsetX = 0f
            baseOffsetY = (canvasH - bitmap.height * baseScale) / 2f
        } else {
            baseScale = canvasH / bitmap.height
            baseOffsetX = (canvasW - bitmap.width * baseScale) / 2f
            baseOffsetY = 0f
        }

        val totalScale = baseScale * scale
        val totalOffsetX = baseOffsetX * scale + offsetX
        val totalOffsetY = baseOffsetY * scale + offsetY

        // Draw bitmap
        drawImage(
            image = imageBitmap,
            dstOffset = androidx.compose.ui.unit.IntOffset(
                totalOffsetX.toInt(),
                totalOffsetY.toInt()
            ),
            dstSize = androidx.compose.ui.unit.IntSize(
                (bitmap.width * totalScale).toInt(),
                (bitmap.height * totalScale).toInt()
            )
        )

        // Draw translated blocks
        for (block in blocks) {
            val box = block.boundingBox
            val left = box.left * totalScale + totalOffsetX
            val top = box.top * totalScale + totalOffsetY
            val w = box.width() * totalScale
            val h = box.height() * totalScale

            drawRect(
                color = Color.Black.copy(alpha = 0.65f),
                topLeft = Offset(left, top),
                size = Size(w, h)
            )

            val displayText = block.translatedText.ifBlank { block.originalText }
            val fontSize = (h * 0.3f).coerceIn(8f, 28f)

            val layoutResult = textMeasurer.measure(
                text = AnnotatedString(displayText),
                style = TextStyle(
                    color = Color.White,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.2f).sp
                ),
                constraints = androidx.compose.ui.unit.Constraints(
                    maxWidth = w.toInt().coerceAtLeast(1)
                )
            )
            drawText(layoutResult, topLeft = Offset(left + 2f, top + 2f))
        }
    }
}
