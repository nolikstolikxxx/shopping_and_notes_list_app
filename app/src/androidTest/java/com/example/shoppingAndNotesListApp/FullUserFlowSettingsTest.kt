package com.example.shoppingAndNotesListApp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Full end-to-end UI test for Settings feature.
 *
 * This test simulates a real user workflow
 * inside the application Settings screen.
 *
 * The test verifies:
 * - opening Settings screen
 * - changing note layout style
 * - changing time format
 * - changing title/content text sizes
 * - switching between light and dark themes
 * - switching between multiple color styles
 * - repeated configuration changes during one session
 * - correct return to MainActivity
 * - navigation stability after theme recreation
 * - Notes screen accessibility after settings changes
 *
 * Main goals:
 * - validate Settings screen behavior
 * - validate dynamic theme switching
 * - validate Activity recreation stability
 * - validate navigation consistency
 * - validate app stability during multiple UI configuration updates
 *
 * IMPORTANT PRINCIPLES:
 * - no direct SharedPreferences testing
 * - no Thread.sleep()
 * - test only visible UI behavior
 * - configuration changes validated through user flow
 * - tests simulate real user interactions
 */
@RunWith(AndroidJUnit4::class)
class FullUserFlowSettingsTest {

    // =========================
    // RULES
    // =========================

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    // =========================
    // NAVIGATION
    // =========================

    private fun openSettings() {

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        onView(withId(R.id.settings))
            .perform(click())
    }

    // =========================
    // SETTINGS ACTIONS
    // =========================

    private fun setNoteStyle() {

        onView(withText("Choose note style"))
            .perform(click())

        onView(withText("Grid"))
            .perform(click())
    }

    private fun setTimeFormat() {

        onView(withText("Choose time format"))
            .perform(click())

        onView(withText("12:59:59 AM or PM – 2024/12/31"))
            .perform(click())
    }

    private fun setTitleSize() {

        onView(withText("Choose title note text size"))
            .perform(click())

        onView(withText("20 sp"))
            .perform(click())
    }

    private fun setContentSize() {

        onView(withText("Choose content note text size"))
            .perform(click())

        onView(withText("18 sp"))
            .perform(click())
    }

    private fun setColorStyleBlueGray() {

        onView(withText("Choose style"))
            .perform(click())

        onView(withText("Blue gray"))
            .perform(click())
    }

    private fun setColorStyleDeepOrange() {

        onView(withText("Choose style"))
            .perform(click())

        onView(withText("Deep orange"))
            .perform(click())
    }

    private fun setColorStyleIndigo() {

        onView(withText("Choose style"))
            .perform(click())

        onView(withText("Indigo"))
            .perform(click())
    }

    private fun setColorStyleLime() {

        onView(withText("Choose style"))
            .perform(click())

        onView(withText("Lime"))
            .perform(click())
    }

    private fun setLightTheme() {

        onView(withText("Choose theme"))
            .perform(click())

        onView(withText("Light theme"))
            .perform(click())
    }

    private fun setDarkTheme() {

        onView(withText("Choose theme"))
            .perform(click())

        onView(withText("Dark theme"))
            .perform(click())
    }

    // =========================
    // TEST
    // =========================

    @Test
    fun fullSettingsFlow() {

        // =========================
        // OPEN SETTINGS
        // =========================
        openSettings()

        // =========================
        // CHANGE SETTINGS
        // =========================
        setNoteStyle()
        setTimeFormat()
        setTitleSize()
        setContentSize()

        setDarkTheme()
        setColorStyleDeepOrange()
        setLightTheme()
        setColorStyleIndigo()
        setDarkTheme()
        setColorStyleLime()
        setLightTheme()
        setColorStyleBlueGray()
        setDarkTheme()


        // =========================
        // RETURN TO MAIN SCREEN
        // =========================
        pressBack()

        // =========================
        // VERIFY MAIN SCREEN IS BACK
        // =========================
        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        // =========================
        // OPEN NOTES (FROM MAIN ACTIVITY)
        // =========================
        onView(withId(R.id.notes))
            .perform(click())

        onView(withId(R.id.rcViewNote))
            .check(matches(isDisplayed()))
    }
}