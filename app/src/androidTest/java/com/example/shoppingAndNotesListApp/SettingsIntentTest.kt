package com.example.shoppingAndNotesListApp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import com.example.shoppingAndNotesListApp.settings.SettingsActivity
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView

/**
 * UI test for Settings screen navigation.
 *
 * Test scenario:
 * 1. Launch MainActivity
 * 2. Click Settings button
 * 3. Verify SettingsActivity intent was launched
 */
@RunWith(AndroidJUnit4::class)
class SettingsIntentTest {

    @Test
    fun openSettingsActivity() {

        // Initialize Espresso Intents
        init()

        // Launch MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        // ----------------------------
        // Open Settings screen
        // ----------------------------
        onView(withId(R.id.settings)).perform(click())

        // ----------------------------
        // Verify SettingsActivity launched
        // ----------------------------
        intended(
            hasComponent(SettingsActivity::class.java.name)
        )

        // Release Espresso Intents
        release()
    }
}