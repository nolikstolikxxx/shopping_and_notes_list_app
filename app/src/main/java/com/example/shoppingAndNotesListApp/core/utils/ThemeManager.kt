package com.example.shoppingAndNotesListApp.core.utils

import android.content.SharedPreferences
import com.example.shoppingAndNotesListApp.R

/**
 * Handles application theme selection.
 *
 * Features:
 * - Resolves selected color palette
 * - Supports Material3 themes
 * - Supports Dynamic Colors
 *
 * Supported themes:
 * - Blue Gray
 * - Deep Orange
 * - Indigo
 * - Lime
 * - Dynamic (Material You)
 *
 * Architecture notes:
 * - Used before super.onCreate()
 * - Called from BaseActivity
 */
object ThemeManager {

    /**
     * SharedPreferences key
     * for selected app theme.
     */
    private const val KEY_APP_THEME = "app_theme"

    // ================= THEME RESOLUTION =================

    /**
     * Resolve currently selected app theme.
     *
     * Returns:
     * - Material3 theme resource ID
     *
     * Must be called BEFORE:
     * super.onCreate()
     */
    fun resolveTheme(prefs: SharedPreferences): Int {
        return when (prefs.getString(KEY_APP_THEME , "blue_gray")) {
            "blue_gray" -> R.style.Theme_shoppingAndNotesListApp_BlueGray
            "deep_orange" -> R.style.Theme_shoppingAndNotesListApp_DeepOrange
            "indigo" -> R.style.Theme_shoppingAndNotesListApp_Indigo
            "lime" -> R.style.Theme_shoppingAndNotesListApp_Lime
            "dynamic" -> R.style.Theme_shoppingAndNotesListApp_Dynamic
            else -> R.style.Theme_shoppingAndNotesListApp_BlueGray
        }
    }

    // ================= DYNAMIC COLORS =================

    /**
     * Checks whether Dynamic Colors
     * (Material You) are enabled.
     *
     * Used for:
     * - Android 12+
     * - Monet engine support
     */
    fun isDynamic(prefs: SharedPreferences): Boolean {
        return prefs.getString(KEY_APP_THEME , "") == "dynamic"
    }
}