package com.example.shoppingAndNotesListApp.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.billing.BillingManager
import com.example.shoppingAndNotesListApp.databinding.ActivityMainBinding
import com.example.shoppingAndNotesListApp.settings.SettingsActivity
import com.example.shoppingAndNotesListApp.ui.fragments.BaseFragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Main Activity of the app.
 *
 * Features:
 * - Navigation (NavController + BottomNavigation)
 * - Toolbar integration
 * - Interstitial Ads
 * - Global back handling
 *
 * Architecture notes:
 * - Single Activity (Navigation Component)
 * - Fragments handle UI logic
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var pref: SharedPreferences

    private var currentMenuItemId = R.id.shop_list

    // ================= ADS =================

    private var iAd: InterstitialAd? = null
    private var adShowCounter = 0
    private var adShowCounterMax = 5

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate an instance at $this")

        pref = getSharedPreferences(BillingManager.MAIN_PREF , MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigation()
        setupBottomNav()
        observeNavigation()
        setupBackHandling()

        initAdsIfNeeded()
    }

    // ================= TOOLBAR =================

    /**
     * Setup Toolbar as ActionBar
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    // ================= NAVIGATION =================

    /**
     * Setup Navigation Component (NavController + ActionBar)
     */
    private fun setupNavigation() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment

        navController = navHostFragment.navController
    }

    // ================= NAVIGATION UI SYNC =================

    /**
     * Observes Navigation changes and keeps UI in sync.
     *
     * Responsibilities:
     * - Updates Toolbar title based on current destination
     * - Controls Back arrow visibility (root vs child screens)
     * - Synchronizes BottomNavigation selected item
     *
     * Why it's important:
     * - Prevents UI inconsistencies (wrong title, wrong arrow state)
     * - Avoids "theme looks broken" issues caused by stale UI state
     * - Centralizes all navigation-related UI updates in one place
     *
     * Must be called in onCreate() AFTER setupNavigation()
     */
    private fun observeNavigation() {

        navController.addOnDestinationChangedListener { _ , destination , _ ->

            val isRoot = destination.id == R.id.shopListFragment

            supportActionBar?.apply {

                title = when (destination.id) {
                    R.id.noteFragment -> getString(R.string.notes)
                    R.id.shopListFragment -> getString(R.string.shop_list)
                    else -> getString(R.string.app_name)
                }

                setDisplayHomeAsUpEnabled(!isRoot)
            }

            // Sync BottomNavigation WITHOUT triggering listener
            binding.bottomNav.menu.findItem(destination.id)?.isChecked = true
        }
    }

    /**
     * Handle Toolbar back button
     */
    override fun onSupportNavigateUp(): Boolean {
        return handleBack()
    }

    // ================= BACK HANDLING =================

    /**
     * Handle system back press
     */
    private fun setupBackHandling() {
        onBackPressedDispatcher.addCallback(this) {
            val handled = handleBack()
            if (!handled) {
                finish()
            }
        }
    }

    /**
     * Unified back logic
     */
    private fun handleBack(): Boolean {

        val currentDestination = navController.currentDestination?.id

        return when (currentDestination) {

            // ================= NOTES → BACK TO SHOP LIST =================
            R.id.noteFragment -> {
                navController.popBackStack()
                true
            }

            // ================= SHOP LIST (ROOT) =================
            R.id.shopListFragment -> {
                Toast.makeText(
                    this ,
                    getString(R.string.this_is_main_screen) ,
                    Toast.LENGTH_SHORT
                ).show()
                true
            }

            // ================= DEFAULT =================
            else -> {
                navController.popBackStack()
            }
        }
    }

    // ================= BOTTOM NAV =================

    /**
     * Setup BottomNavigation listener
     */
    private fun setupBottomNav() {

        binding.bottomNav.setOnItemSelectedListener {

            currentMenuItemId = it.itemId

            when (it.itemId) {
                R.id.settings -> {
                    Log.d(TAG , "settings")
                    showInterAd {
                        startActivity(
                            Intent(
                                this@MainActivity ,
                                SettingsActivity::class.java
                            )
                        )
                    }
                    true
                }

                R.id.notes -> {
                    Log.d(TAG , "notes")
                    showInterAd {
                        navigateSingle(R.id.noteFragment)
                    }
                    true
                }

                R.id.shop_list -> {
                    Log.d(TAG , "shop_list")
                    showInterAd {
                        navigateSingle(R.id.shopListFragment)
                    }
                    true
                }

                R.id.nav_new -> {
                    Log.d(TAG , "new_item reselection")

                    handleNewItemClick()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Handles "New" button click
     * Delegates action to current Fragment
     */
    private fun handleNewItemClick() {

        val fragment =
            supportFragmentManager.primaryNavigationFragment
                ?.childFragmentManager
                ?.primaryNavigationFragment

        if (fragment is BaseFragment) {
            fragment.onClickNew()
        }
    }

    /** * Navigate with singleTop behavior */
    private fun navigateSingle(@IdRes destination: Int) {
        navController.navigate(
            destination ,
            null ,
            NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(navController.graph.startDestinationId , false)
                .build()
        )
    }

    // ================= UI UPDATE =================

    /**
     * Updates Toolbar title and BottomNav state
     */
    fun updateNavigationUI(tag: String) {

        // 1. Toolbar title
        supportActionBar?.title = when (tag) {
            "Notes" -> getString(R.string.notes)
            "ShopList" -> getString(R.string.shop_list)
            else -> getString(R.string.app_name)
        }

        // 2. BottomNav selection WITHOUT triggering listener
        val itemId = when (tag) {
            "Notes" -> R.id.notes
            "ShopList" -> R.id.shop_list
            else -> R.id.shop_list
        }

        binding.bottomNav.menu.findItem(itemId).isChecked = true
        currentMenuItemId = itemId
    }

    // ================= ADS =================

    /**
     * Initialize ads if needed
     */
    private fun initAdsIfNeeded() {
        if (!isRunningTest() &&
            !pref.getBoolean(BillingManager.REMOVE_ADS_KEY , false)
        ) {
            loadInterAd()
        }
    }

    /**
     * Load Interstitial Ad
     */
    private fun loadInterAd() {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            this ,
            getString(R.string.inter_ads_id) ,
            request ,
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    iAd = ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    iAd = null
                }
            })
    }

    /**
     * Show Interstitial Ad with callback
     */
    private fun showInterAd(onFinish: () -> Unit) {
        if (isRunningTest()) {
            onFinish()
            return
        }

        if (iAd != null &&
            adShowCounter > adShowCounterMax &&
            !pref.getBoolean(BillingManager.REMOVE_ADS_KEY , false)
        ) {
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                    onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    iAd = null
                    loadInterAd()
                    onFinish()
                }

                override fun onAdShowedFullScreenContent() {
                    iAd = null
                }
            }

            adShowCounter = 0
            iAd?.show(this)

        } else {
            adShowCounter++
            onFinish()
        }
    }

    /**
     * Detect test environment
     */
    fun isRunningTest(): Boolean {
        return try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (_: Exception) {
            false
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
        super.onDestroy()
        Log.d(TAG , "onDestroy an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "MainActivity"
    }
}