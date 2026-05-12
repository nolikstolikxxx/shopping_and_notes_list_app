package com.example.shoppingAndNotesListApp

import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI test for Navigation Component behavior.
 *
 * Test scenario:
 * 1. Launch MainActivity
 * 2. Click Notes item in BottomNavigationView
 * 3. Verify navigation to NoteFragment
 */
@RunWith(AndroidJUnit4::class)
class MainNavigationTest {

    @Test
    fun openNotesFragment_fromBottomNav() {

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // ----------------------------
        // Open Notes screen from BottomNavigationView
        // ----------------------------
        onView(withId(R.id.notes)).perform(click())

        // ----------------------------
        // Verify current navigation destination
        // ----------------------------
        scenario.onActivity { activity ->

            // Find NavHostFragment
            val navHostFragment = activity.supportFragmentManager.fragments
                .filterIsInstance<NavHostFragment>()
                .firstOrNull()

            // Verify NavHostFragment exists
            assertNotNull(navHostFragment)

            // Get current destination id
            val currentDestinationId = navHostFragment!!
                .navController
                .currentDestination
                ?.id

            // Verify navigation to NoteFragment
            assertEquals(R.id.noteFragment , currentDestinationId)
        }
    }
}