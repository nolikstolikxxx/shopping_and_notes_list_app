package com.example.shoppingAndNotesListApp

import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import org.junit.Assert.assertEquals

/**
 * UI test for checking Navigation Component BackStack behavior.
 *
 * Test scenario:
 * 1. Open Notes screen
 * 2. Press system Back button
 * 3. Verify that user returns to ShopList screen
 */
@RunWith(AndroidJUnit4::class)
class BackStackTest {

    @Test
    fun back_returnsToShopList() {

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // ----------------------------
        // Open Notes screen
        // ----------------------------
        onView(withId(R.id.notes)).perform(click())

        // ----------------------------
        // Simulate system Back button press
        // ----------------------------
        pressBack()

        // ----------------------------
        // Verify current destination
        // ----------------------------
        scenario.onActivity { activity ->

            // Find NavHostFragment
            val navHostFragment = activity.supportFragmentManager
                .findFragmentById(R.id.nav_host) as NavHostFragment

            // Get current destination id from NavController
            val currentDestination = navHostFragment
                .navController
                .currentDestination
                ?.id

            // Verify user returned to ShopListFragment
            assertEquals(R.id.shopListFragment , currentDestination)
        }
    }
}