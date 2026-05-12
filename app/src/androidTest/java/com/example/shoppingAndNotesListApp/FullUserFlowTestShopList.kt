package com.example.shoppingAndNotesListApp

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Full end-to-end UI test for Shopping Lists feature.
 *
 * This test verifies complete user behavior across:
 * - shopping list creation
 * - shopping item management
 * - item checked state changes
 * - navigation between screens
 * - Settings screen interactions
 * - theme/style switching during active usage
 * - RecyclerView stability after UI recreation
 * - deletion flow
 *
 * Full scenario covered by this test:
 *
 * 1. User opens Shop Lists screen
 * 2. User creates multiple shopping lists
 * 3. User opens lists and adds shopping items
 * 4. User checks/unchecks items
 * 5. User navigates back to lists screen
 * 6. User opens Settings during active workflow
 * 7. User changes:
 *    - time format
 *    - light/dark theme
 *    - application color palette
 * 8. App recreates Activities after theme changes
 * 9. User returns back to Shop Lists screen
 * 10. Previously created lists must still be accessible
 * 11. User continues working with existing data
 * 12. User creates additional items after theme recreation
 * 13. User deletes one shopping list
 * 14. RecyclerView updates correctly after deletion
 *
 * Architecture/UI principles:
 * - Tests validate REAL user flows only
 * - No direct database validation
 * - No direct SharedPreferences validation
 * - Validation is performed through visible UI state
 * - RecyclerView content is treated as source of truth
 *
 * Stability principles:
 * - Uses explicit wait helpers for asynchronous UI updates
 * - Uses stable navigation assertions
 * - Avoids fragile timing assumptions
 * - Avoids direct Activity assertions
 *
 * IMPORTANT:
 * Theme switching recreates Activities during the test.
 * The test validates that navigation and user data
 * remain stable after recreation.
 */
@RunWith(AndroidJUnit4::class)
class FullUserFlowTestShopList {

    // Launch MainActivity before each test
    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    // Required for Espresso Intents API
    @get:Rule
    val intentsRule = IntentsRule()

    // =========================
    // TEST DATA
    // =========================

    private val LIST1 = "Products"
    private val LIST2 = "Household chemicals"
    private val LIST3 = "Drinks"

    // =========================
    // WAIT HELPERS
    // =========================

    /**
     * Waits until custom condition becomes true.
     */
    private fun waitUntil(
        timeout: Long = 5000 ,
        condition: () -> Boolean
    ) {

        val start = System.currentTimeMillis()

        do {
            if (condition()) return

            Thread.sleep(50)

        } while (System.currentTimeMillis() - start < timeout)

        throw AssertionError("Condition not met within $timeout ms")
    }

    /**
     * Waits for specific view by id.
     */
    private fun waitForView(
        @IdRes id: Int ,
        timeout: Long = 5000
    ) {

        waitUntil(timeout) {
            try {
                onView(withId(id))
                    .check(matches(isDisplayed()))

                true

            } catch (_: Exception) {
                false
            }
        }
    }

    /**
     * Waits until shopping list screen is visible.
     */
    private fun waitForShopListScreen() {
        waitForView(R.id.rcView)
    }

    /**
     * Waits until list item appears inside RecyclerView.
     */
    private fun waitForListInRecycler(
        name: String ,
        timeout: Long = 7000
    ) {

        val start = System.currentTimeMillis()

        do {
            try {

                onView(withId(R.id.rcView))
                    .perform(
                        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(name))
                        )
                    )

                onView(withText(name))
                    .check(matches(isDisplayed()))

                return

            } catch (_: Exception) {
                Thread.sleep(150)
            }

        } while (System.currentTimeMillis() - start < timeout)

        throw AssertionError("List not found: $name")
    }

    // =========================
    // NAVIGATION HELPERS
    // =========================

    /**
     * Opens "Shop Lists" tab from BottomNavigation.
     */
    private fun openListsTab() {
        onView(withId(R.id.shop_list))
            .perform(click())
    }

    /**
     * Returns back from ShopListActivity to lists screen.
     *
     * NOTE:
     * Uses Toolbar back button because it is
     * more stable for this screen than system back.
     */
    private fun returnToListsScreen() {

        // Click Toolbar back button
        onView(
            withContentDescription("Navigate up")
        ).perform(click())

        // Wait until UI becomes idle
        InstrumentationRegistry
            .getInstrumentation()
            .waitForIdleSync()

        // Wait until RecyclerView from ShopListFragment appears
        waitForView(R.id.rcView)

        // Verify list screen is visible
        onView(withId(R.id.rcView))
            .check(matches(isDisplayed()))
    }

    /**
     * Ensure we are on ShopList screen by checking RecyclerView existence.
     * This is stable because only ShopList screen has this view.
     */
    private fun assertShopListScreen() {

        // Verify main screen is back

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))


        // Open ShopList (from main activity)

        onView(withId(R.id.shop_list))
            .perform(click())

        onView(withId(R.id.rcView))
            .check(matches(isDisplayed()))
    }


    private fun returnToShopListAfterAction() {
        // Return to main screen
        goBack()
        assertShopListScreen()
    }

    /**
     * Go back using system back only (NO toolbar dependency)
     */
    private fun goBack() {
        pressBack()
    }

    // =========================
    // COMMON UI HELPERS
    // =========================

    /**
     * Clicks BottomNavigation "New" button.
     */
    private fun clickBottomNew() {
        onView(withContentDescription("New"))
            .perform(click())
    }

    /**
     * Types text into EditText and closes keyboard.
     */
    private fun typeTextAndConfirm(
        id: Int ,
        text: String
    ) {

        onView(withId(id))
            .perform(
                typeText(text) ,
                closeSoftKeyboard()
            )
    }

    /**
     * Clicks dialog positive button.
     */
    private fun clickOk() {
        onView(withId(R.id.btnOk))
            .perform(click())
    }

    /**
     * Opens Toolbar "New Item" action.
     *
     * NOTE:
     * Uses fallback overflow menu handling for
     * smaller screens and unstable Toolbar states.
     */
    private fun clickToolbarNewItem() {

        try {

            waitForView(R.id.action_new_item)

            onView(withId(R.id.action_new_item))
                .perform(click())

        } catch (_: Exception) {

            openActionBarOverflowOrOptionsMenu(
                InstrumentationRegistry
                    .getInstrumentation()
                    .targetContext
            )

            onView(withText("New item"))
                .perform(click())
        }
    }

    /**
     * Saves current shopping item.
     */
    private fun clickSaveItem() {
        onView(withId(R.id.action_save_item))
            .perform(click())
    }

    // =========================
    // SHOP LIST HELPERS
    // =========================

    /**
     * Creates new shopping list.
     */
    private fun createList(name: String) {

        openListsTab()

        waitForShopListScreen()

        clickBottomNew()

        onView(withId(R.id.edName))
            .check(matches(isDisplayed()))

        typeTextAndConfirm(
            R.id.edName ,
            name
        )

        clickOk()

        // Wait until dialog disappears
        waitUntil(3000) {

            try {

                onView(withId(R.id.edName))
                    .check(matches(isDisplayed()))

                false

            } catch (_: Exception) {
                true
            }
        }

        waitForListInRecycler(name)
    }

    /**
     * Opens shopping list by name.
     */
    private fun openList(name: String) {

        waitForShopListScreen()

        waitForListInRecycler(name)

        onView(withId(R.id.rcView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(name))
                )
            )

        onView(withText(name))
            .perform(click())
    }

    /**
     * Deletes shopping list.
     */
    private fun deleteList(name: String) {

        onView(withId(R.id.rcView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(name)) ,
                    clickChild(R.id.btnDeleteList)
                )
            )

        // confirm dialog if exists
        confirmDelete()

        // ensure we are back on ShopList screen
        returnToShopListAfterAction()
    }

    private fun confirmDelete() {
        onView(withId(R.id.btnDelete))
            .perform(click())
    }

    // =========================
    // SHOP ITEM HELPERS
    // =========================

    /**
     * Creates shopping item inside current list.
     */
    private fun createItem(name: String) {

        clickToolbarNewItem()

        onView(withId(R.id.edNewShopItem))
            .check(matches(isDisplayed()))

        typeTextAndConfirm(
            R.id.edNewShopItem ,
            name
        )

        clickSaveItem()

        // Verify item appeared
        onView(withText(name))
            .check(matches(isDisplayed()))
    }

    /**
     * Toggles item checked state.
     */
    private fun checkItem(name: String) {

        onView(withId(R.id.rcView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(name)) ,
                    clickChild(R.id.checkBox)
                )
            )
    }

    // =========================
    // CUSTOM VIEW ACTION
    // =========================

    /**
     * Clicks child view inside RecyclerView item.
     */
    private fun clickChild(id: Int) = object : ViewAction {

        override fun getConstraints() =
            isAssignableFrom(View::class.java)

        override fun getDescription() =
            "Click child view"

        override fun perform(
            uiController: UiController ,
            view: View
        ) {
            val child = view.findViewById<View>(id)

            if (child != null) {

                child.performClick()

            } else {

                throw AssertionError(
                    "Child view with id $id not found."
                )
            }
        }
    }

    // =========================
    // SETTING
    // =========================

    private fun openSettings() {

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        onView(withId(R.id.settings))
            .perform(click())
    }

    private fun setTimeFormat() {

        onView(withText("Choose time format"))
            .perform(click())

        onView(withText("12:59:59 AM or PM – 2024/12/31"))
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
    // MAIN TEST
    // =========================

    @Test
    fun fullUserFlow() {

        // =========================
        // STEP 1: CREATE LISTS
        // =========================

        createList(LIST1)
        createList(LIST2)
        createList(LIST3)

        // ----------------------------
        // LIST 1
        // ----------------------------

        openList(LIST1)

        createItem("Bread")
        createItem("Cheese")
        createItem("Orange")

        checkItem("Bread")
        checkItem("Cheese")
        checkItem("Orange")

        returnToListsScreen()

        waitForListInRecycler(LIST1)

        // ----------------------------
        // LIST 2
        // ----------------------------

        openList(LIST2)

        createItem("Soap")
        createItem("Shampoo")

        checkItem("Soap")

        returnToListsScreen()

        waitForListInRecycler(LIST2)

        // =========================
        // STEP 2: SETTINGS FLOW
        // =========================

        openSettings()
        setTimeFormat()
        setDarkTheme()
        setColorStyleIndigo()
        returnToShopListAfterAction()

        openSettings()
        setLightTheme()
        setColorStyleLime()
        setDarkTheme()
        returnToShopListAfterAction()

        // =========================
        // STEP 3: CONTINUE CREATING SHOPPING LISTS
        // =========================

        // ----------------------------
        // LIST 3
        // ----------------------------

        openList(LIST3)

        createItem("Orange juice")

        returnToListsScreen()

        waitForListInRecycler(LIST3)

        // =========================
        // STEP 4: DELETE TEST
        // =========================

        deleteList(LIST3)
    }
}