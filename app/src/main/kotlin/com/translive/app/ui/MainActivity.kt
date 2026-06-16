package com.translive.app.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.translive.app.i18n.AppLocale
import com.translive.app.ui.theme.TransLiveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("parlex_settings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("app_language", AppLocale.SYSTEM) ?: AppLocale.SYSTEM
        super.attachBaseContext(AppLocale.localizedContext(newBase, languageCode))
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("parlex_settings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("app_language", AppLocale.SYSTEM) ?: AppLocale.SYSTEM
        AppLocale.applyRuntimeLanguage(this, languageCode)
        enableEdgeToEdge()

        setContent {
            TransLiveTheme {
                TransLiveNavHost()
            }
        }
    }
}
