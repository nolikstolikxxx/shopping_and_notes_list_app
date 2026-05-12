package com.example.shoppingAndNotesListApp.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.utils.ThemeFontManager
import com.example.shoppingAndNotesListApp.core.utils.ThemeManager
import com.example.shoppingAndNotesListApp.core.utils.ThemeUtils

/**
 * Base activity that applies app-wide UI configuration:
 * - theme (color palette)
 * - dynamic colors (Material You)
 * - night mode
 * - font scaling
 *
 * Also listens for preference changes and recreates activity when needed.
 * IMPORTANT:
 * - DynamicColors moved to Application level
 * - All config BEFORE super.onCreate()
 */
abstract class BaseActivity : AppCompatActivity() ,
    SharedPreferences.OnSharedPreferenceChangeListener {

    // ================= PREFS =================

    private lateinit var prefs: SharedPreferences

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize preferences FIRST (required for theme setup)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Apply UI config BEFORE Activity creation
        applyPreCreateConfig()

        super.onCreate(savedInstanceState)

        // Listen for runtime changes (theme, font, etc.)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    // ================= CONFIG =================

    /**
     * Applies configuration BEFORE UI is created.
     *
     * Order is critical:
     * 1. Font scale
     * 2. Theme
     * 3. Night mode
     */
    private fun applyPreCreateConfig() {
        // 1. Apply light/dark
        ThemeUtils.applyNightMode(prefs)

        // 2. Apply font scaling
        ThemeFontManager.applyFontScale(this)

        // 3. Apply theme (colors, styles)
        setTheme(ThemeManager.resolveTheme(prefs))
    }

    /**
     * Reacts to preference changes and recreates activity when needed.
     */
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences ,
        key: String?
    ) {
        if (key in RECREATE_KEYS) {
            applyRecreateAnimation()
            recreate()
        }
    }

    /**
     * Applies window animation before recreation.
     */
    private fun applyRecreateAnimation() {
        window.setWindowAnimations(R.style.AppWindowAnimation)
    }

    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    // ================= CONSTANTS =================

    companion object {
        /**
         * Preference keys that require Activity recreation.
         */
        private val RECREATE_KEYS = setOf(
            ThemeUtils.KEY_APP_THEME ,
            ThemeUtils.KEY_THEME_MODE ,
            "font_size_key" ,
            "title_size_key" ,
            "content_size_key" ,
            "note_style_key"
        )
    }
}
