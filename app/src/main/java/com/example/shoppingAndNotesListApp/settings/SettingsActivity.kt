package com.example.shoppingAndNotesListApp.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.databinding.ActivitySettingsBinding
import com.example.shoppingAndNotesListApp.ui.activities.BaseActivity

/**
 * Activity for application settings.
 *
 * Features:
 * - Hosts SettingsFragment
 * - Supports theme switching
 * - Supports toolbar back navigation
 *
 * Architecture notes:
 * - Uses BaseActivity for theme/font handling
 * - Fragment contains all Preference logic
 *
 * Important:
 * - Theme recreation handled automatically in BaseActivity
 * - No manual recreate() calls needed here
 */
class SettingsActivity : BaseActivity() , SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         * Initialize preferences BEFORE super.onCreate()
         *
         * Important for:
         * - theme
         * - font scaling
         * - night mode
         */
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        super.onCreate(savedInstanceState)

        Log.d(TAG , "onCreate an instance at $this")

        // ================= VIEW BINDING =================

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ================= TOOLBAR =================

        setupToolbar()

        // ================= FRAGMENT =================

        /**
         * Attach SettingsFragment only once
         *
         * Prevents duplicate fragments after recreation.
         */
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.placeHolder , SettingsFragment())
                .commit()
        }

        // ================= PREF LISTENER =================

        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    // ================= TOOLBAR =================

    /**
     * Setup Toolbar as ActionBar
     *
     * Back navigation handled through:
     * - onOptionsItemSelected()
     */
    private fun setupToolbar() {

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            binding.toolbar.navigationContentDescription = "Navigate up"
        }
    }

    // ================= TOOLBAR BACK =================

    /**
     * Handles Toolbar back arrow click
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ================= DEBUG LIFECYCLE =================

    override fun onStart() {
        super.onStart()
        Log.d(TAG , "onStart an instance at $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG , "onResume an instance at $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG , "onPause an instance at $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG , "onStop an instance at $this")
    }

    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
        Log.d(TAG , "onDestroy an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "SettingsActivity"
    }
}