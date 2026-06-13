package com.translive.app.i18n

import android.content.Context
import androidx.annotation.StringRes
import com.translive.app.data.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalizedTextProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsRepository
) {
    fun text(@StringRes id: Int, vararg args: Any): String =
        AppLocale
            .localizedContext(context, settings.appLanguageCode)
            .getString(id, *args)
}
