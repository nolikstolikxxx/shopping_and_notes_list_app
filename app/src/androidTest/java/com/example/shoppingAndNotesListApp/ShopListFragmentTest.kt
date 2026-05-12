package com.example.shoppingAndNotesListApp

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.fragments.ShopListFragment
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test for ShopListFragment.
 *
 * This test verifies:
 * 1. Fragment launches successfully
 * 2. Fragment is attached to activity
 * 3. Fragment view is created
 */
@RunWith(AndroidJUnit4::class)
class ShopListFragmentTest {

    @Test
    fun fragment_launch_successfully() {

        // ----------------------------
        // Launch ShopListFragment
        // ----------------------------
        val scenario = launchFragmentInContainer<ShopListFragment>(
            themeResId = R.style.Theme_shoppingAndNotesListApp
        )

        // ----------------------------
        // Verify fragment state
        // ----------------------------
        scenario.onFragment { fragment ->

            // Verify fragment attached to Activity
            assertTrue(fragment.isAdded)

            // Verify fragment view created successfully
            assertNotNull(fragment.view)
        }
    }
}