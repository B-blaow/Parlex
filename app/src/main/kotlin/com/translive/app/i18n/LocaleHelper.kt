package com.translive.app.i18n

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    const val SYSTEM = "system"
    const val ENGLISH = "en"
    const val RUSSIAN = "ru"
    const val CHINESE_SIMPLIFIED = "zh-CN"
    const val CHINESE_TRADITIONAL = "zh-TW"

    val supportedLanguageCodes = listOf(
        SYSTEM,
        ENGLISH,
        RUSSIAN,
        CHINESE_SIMPLIFIED,
        CHINESE_TRADITIONAL
    )

    fun localizedContext(base: Context, languageCode: String): Context {
        if (languageCode == SYSTEM) return base
        val locale = languageCode.toLocale()
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return base.createConfigurationContext(config)
    }

    private fun String.toLocale(): Locale = when (this) {
        CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
        CHINESE_TRADITIONAL -> Locale.TRADITIONAL_CHINESE
        else -> Locale.forLanguageTag(this)
    }
}
