package com.translive.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.translive.app.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _appLanguage = MutableStateFlow(settings.appLanguageCode)
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _threads = MutableStateFlow(settings.threads)
    val threads: StateFlow<Int> = _threads.asStateFlow()

    private val _idleTimeout = MutableStateFlow(settings.idleTimeoutMinutes)
    val idleTimeout: StateFlow<Int> = _idleTimeout.asStateFlow()

    private val _backend = MutableStateFlow(settings.backend)
    val backend: StateFlow<String> = _backend.asStateFlow()

    fun setAppLanguage(value: String) {
        settings.appLanguageCode = value
        _appLanguage.value = value
    }

    fun setThreads(value: Int) {
        settings.threads = value
        _threads.value = value
    }

    fun setIdleTimeout(minutes: Int) {
        settings.idleTimeoutMinutes = minutes
        _idleTimeout.value = minutes
    }

    fun setBackend(value: String) {
        settings.backend = value
        _backend.value = value
    }
}
