package com.example.shoppingAndNotesListApp.ui.activities

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.shoppingAndNotesListApp.core.utils.ThemeUtils
import com.example.shoppingAndNotesListApp.data.db.database.MainDataBase
import com.google.android.material.color.DynamicColors

/**
 * Application class used as a single entry point for app-level dependencies.
 *
 * Holds a lazy-initialized instance of [MainDataBase],
 * ensuring a single database instance across the app.
 *
 * NOTE:
 * - Replace with DI (e.g. Hilt) in production environment
 * - Move dynamic color initialization here if Activity-level approach causes issues
 */

class MainApp : Application() {
    /**
     * Lazy database instance.
     * Created only once during app lifecycle.
     */
    val dataBase by lazy { MainDataBase.getDataBase(this) }

    override fun onCreate() {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Apply saved theme on app start
        ThemeUtils.applyNightMode(prefs)

        // Apply Material You dynamic colors globally (optional fallback)
        /**
         * NOTE:
         * Dynamic colors are applied at Activity level by default.
         * If any issues appear (e.g. flickering, delayed theme, inconsistent colors),
         * consider moving this logic to Application using
         * DynamicColors.applyToActivitiesIfAvailable().
         */
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}