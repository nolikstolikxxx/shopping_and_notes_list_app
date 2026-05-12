package com.example.shoppingAndNotesListApp.core.utils

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.preference.PreferenceManager

/**
 * Handles global font scaling for the app.
 *
 * Features:
 * - Applies user-selected font size
 * - Supports:
 *   - Small
 *   - Medium
 *   - Large
 * - Updates Activity configuration
 *
 * Architecture notes:
 * - Used before Activity UI creation
 * - Called from BaseActivity
 */
object ThemeFontManager {

    // ================= FONT SCALE =================

    /**
     * Apply font scaling based on user preferences.
     *
     * Preference values:
     * - small
     * - medium
     * - large
     */
    fun applyFontScale(activity: Activity) {

        // SharedPreferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        // Current selected font size
        val fontSize = prefs.getString("font_size_key" , "medium")

        // Convert preference → scale factor
        val scale = when (fontSize) {
            "small" -> 0.85f
            "medium" -> 1.0f
            "large" -> 1.15f
            else -> 1.0f
        }

        val configuration =
            Configuration(activity.resources.configuration)

        /**
         * Apply only if scale changed.
         *
         * Prevents unnecessary updates.
         */
        if (configuration.fontScale != scale) {

            configuration.fontScale = scale

            /**
             * Deprecated:
             * resources.updateConfiguration()
             *
             * Modern approach:
             * createConfigurationContext()
             */
            activity.applyOverrideConfiguration(configuration)
        }

        Log.d(TAG , "Font scale applied: $scale")
    }

    // ================= CONSTANTS =================

    private const val TAG = "ThemeFontManager"
}