package com.translive.app.i18n

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object AppLocale {
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

    fun normalize(languageCode: String): String =
        if (languageCode in supportedLanguageCodes) languageCode else SYSTEM

    fun localizedContext(base: Context, languageCode: String): Context {
        val normalized = normalize(languageCode)
        if (normalized == SYSTEM) return base

        val locale = normalized.toLocale()
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return base.createConfigurationContext(config)
    }

    fun applyRuntimeLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val normalized = normalize(languageCode)
        val locales = if (normalized == SYSTEM) {
            LocaleList.getEmptyLocaleList()
        } else {
            LocaleList.forLanguageTags(normalized)
        }
        context.getSystemService(LocaleManager::class.java).applicationLocales = locales
    }

    private fun String.toLocale(): Locale = when (this) {
        CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
        CHINESE_TRADITIONAL -> Locale.TRADITIONAL_CHINESE
        else -> Locale.forLanguageTag(this)
    }
}
