package com.example.shoppingAndNotesListApp.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.billing.BillingManager
import com.example.shoppingAndNotesListApp.core.utils.ThemeUtils
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity

/**
 * Fragment for app settings.
 *
 * Features:
 * - Theme mode selection (light/dark/system)
 * - Color palette selection
 * - Billing / Remove Ads
 * - Dynamic settings updates
 *
 * Architecture notes:
 * - Uses PreferenceFragmentCompat
 * - UI recreation handled by BaseActivity
 * - Does NOT recreate Activity manually
 */
class SettingsFragment : PreferenceFragmentCompat() ,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var billingManager: BillingManager
    private lateinit var removeAdsPref: Preference

    // ================= LIFECYCLE =================

    override fun onCreatePreferences(savedInstanceState: Bundle? , rootKey: String?) {

        // Load preferences from XML
        setPreferencesFromResource(R.xml.settings_preference , rootKey)

        // SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Update MainActivity UI
        (activity as? MainActivity)?.updateNavigationUI("Settings")

        setupThemeModePref()
        setupAppThemePref()
        initBilling()
    }

    // ================= THEME SETTINGS =================

    /**
     * Setup theme mode preference:
     * - Light
     * - Dark
     * - System
     */
    private fun setupThemeModePref() {
        // === Theme mode (light / dark / system)
        val themeModePref = findPreference<ListPreference>(ThemeUtils.KEY_THEME_MODE)

        // Automatically show selected value as summary
        themeModePref?.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()

        themeModePref?.setOnPreferenceChangeListener { _ , _ ->
            /**
             * Apply night mode immediately.
             *
             * Activity recreation will be handled automatically
             * by BaseActivity via SharedPreferences listener.
             */
            ThemeUtils.applyNightMode(prefs)

            true
        }
    }

    /**
     * Setup app color palette preference.
     *
     * Examples:
     * - Blue Gray
     * - Indigo
     * - Lime
     * - Dynamic colors
     */
    private fun setupAppThemePref() {
        // === App color theme (blue_gray / deep_orange / indigo / lime / dynamic)
        val appThemePref = findPreference<ListPreference>(ThemeUtils.KEY_APP_THEME)

        // Automatically show selected value as summary
        appThemePref?.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()

        appThemePref?.setOnPreferenceChangeListener { _ , _ ->
            /**
             * Theme recreation handled automatically
             * by BaseActivity through SharedPreferences listener.
             *
             * No manual recreate() needed here.
             */
            true
        }
    }

    // ================= BILLING =================

    /**
     * Initialize Google Billing
     * for "Remove Ads" purchase.
     */
    private fun initBilling() {

        (activity as? AppCompatActivity)?.let {
            billingManager = BillingManager(it)
        }

        removeAdsPref = findPreference("remove_ads_key")!!

        removeAdsPref.setOnPreferenceClickListener {

            Log.d(TAG , "Remove Ads pressed")
            billingManager.startConnection()

            true
        }
    }

    // ================= PREFERENCE LISTENER =================

    /**
     * Debug listener for preference changes.
     *
     * Activity recreation is handled in BaseActivity.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences , key: String?) {

        when (key) {

            ThemeUtils.KEY_APP_THEME ,
            ThemeUtils.KEY_THEME_MODE ,
            "font_size_key" ,
            "title_size_key" ,
            "content_size_key" -> {

                Log.d(TAG , "Preference changed: $key")
            }
        }
    }

    // ================= DEBUG LIFECYCLE =================

    override fun onResume() {
        super.onResume()

        // Register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {

        // Unregister listener to avoid leaks
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onDestroy() {

        // Close Billing connection
        billingManager.closeConnection()
        super.onDestroy()
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "SettingsFragment"
    }
}