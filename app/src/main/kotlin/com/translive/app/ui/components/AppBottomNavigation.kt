package com.translive.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.translive.app.R

enum class BottomNavDestination {
    TRANSLATE,
    DIALOGUE,
    CAMERA,
    HISTORY,
    MODELS,
    SETTINGS
}

@Composable
fun AppBottomNavigation(
    selected: BottomNavDestination,
    onNavigateToTranslate: () -> Unit,
    onNavigateToDialogue: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToModels: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == BottomNavDestination.TRANSLATE,
            onClick = onNavigateToTranslate,
            icon = { Icon(Icons.Filled.Translate, stringResource(R.string.nav_translate)) }
        )
        NavigationBarItem(
            selected = selected == BottomNavDestination.DIALOGUE,
            onClick = onNavigateToDialogue,
            icon = { Icon(Icons.Filled.Mic, stringResource(R.string.nav_dialogue)) }
        )
        NavigationBarItem(
            selected = selected == BottomNavDestination.CAMERA,
            onClick = onNavigateToCamera,
            icon = { Icon(Icons.Filled.CameraAlt, stringResource(R.string.nav_camera)) }
        )
        NavigationBarItem(
            selected = selected == BottomNavDestination.HISTORY,
            onClick = onNavigateToHistory,
            icon = { Icon(Icons.Filled.History, stringResource(R.string.nav_history)) }
        )
        NavigationBarItem(
            selected = selected == BottomNavDestination.MODELS,
            onClick = onNavigateToModels,
            icon = { Icon(Icons.Filled.Storage, stringResource(R.string.nav_models)) }
        )
        NavigationBarItem(
            selected = selected == BottomNavDestination.SETTINGS,
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Filled.Settings, stringResource(R.string.nav_settings)) }
        )
    }
}
