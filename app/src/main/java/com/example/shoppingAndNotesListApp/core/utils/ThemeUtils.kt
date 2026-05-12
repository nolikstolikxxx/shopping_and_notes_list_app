package com.example.shoppingAndNotesListApp.core.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility object for app theme configuration.
 *
 * Responsibilities:
 * - Stores theme-related preference keys
 * - Applies Light / Dark / System mode
 * - Centralizes night mode logic
 *
 * Architecture notes:
 * - Used by BaseActivity and Settings
 * - Works only with UI configuration
 * - Does NOT contain Activity references
 */
object ThemeUtils {

    // ================= PREFERENCE KEYS =================

    /**
     * Selected color palette key.
     *
     * Examples:
     * - blue_gray
     * - deep_orange
     * - indigo
     * - lime
     * - dynamic
     */
    const val KEY_APP_THEME = "app_theme"

    /**
     * Selected theme mode key.
     *
     * Examples:
     * - light
     * - dark
     * - system
     */
    const val KEY_THEME_MODE = "theme_mode"

    // ================= THEME MODES =================

    /**
     * Force light mode.
     */
    private const val VALUE_THEME_LIGHT = "light"

    /**
     * Force dark mode.
     */
    private const val VALUE_THEME_DARK = "dark"

    /**
     * Follow system theme settings.
     */
    private const val VALUE_THEME_SYSTEM = "system"

    // ================= NIGHT MODE =================

    /**
     * Applies selected night mode globally.
     *
     * Must be called BEFORE Activity UI inflation
     * to avoid incorrect theme flashing.
     *
     * Internally uses AppCompatDelegate.
     */
    fun applyNightMode(prefs: SharedPreferences) {
        when (prefs.getString(KEY_THEME_MODE , VALUE_THEME_SYSTEM)) {

            // ================= LIGHT MODE =================

            VALUE_THEME_LIGHT ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            // ================= DARK MODE =================

            VALUE_THEME_DARK ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            // ================= SYSTEM MODE =================

            else ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}