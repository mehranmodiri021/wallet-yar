package com.example.settings

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

enum class AppLanguage(val code: String, val titleFa: String, val titleEn: String) {
    PERSIAN("fa", "فارسی", "Persian"),
    ENGLISH("en", "English", "انگلیسی")
}

enum class ColorThemeMode(val title: String) {
    CYAN_NEON("کریپتو نئون (آبی فیروزه‌ای)"),
    GOLD_AMBER("طلایی سلطنتی (بیت‌کوین)"),
    EMERALD_GREEN("سبز زمره‌ای (ماتریکس)")
}

object AppSettingsState {
    var isDarkMode = mutableStateOf(true)
    var currentLanguage = mutableStateOf(AppLanguage.PERSIAN)
    var currentThemeMode = mutableStateOf(ColorThemeMode.CYAN_NEON)
    var fontScaleFactor = mutableFloatStateOf(1.0f) // 0.85f to 1.3f
}
