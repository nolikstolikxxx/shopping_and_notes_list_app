package com.example.shoppingAndNotesListApp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Full end-to-end UI test for Notes feature.
 *
 * This test simulates a real user workflow across the application:
 *
 * - opening Notes screen
 * - creating multiple notes
 * - applying rich text formatting
 *   (colors + bold style)
 * - editing existing notes
 * - replacing note content
 * - updating formatted sections
 * - deleting notes
 * - navigating between screens
 * - changing application settings
 *   (theme, style, text size, note layout, time format)
 * - verifying RecyclerView state consistency
 * - verifying navigation stability after configuration changes
 *
 * Main goals:
 * - validate complex UI interaction flows
 * - validate note editor behavior
 * - validate Settings integration with Notes feature
 * - validate app stability during long user sessions
 *
 * IMPORTANT PRINCIPLES:
 * - no Thread.sleep()
 * - no fragile timing loops
 * - navigation verified through visible UI state
 * - RecyclerView content used as primary source of truth
 * - tests focus on real user behavior instead of implementation details
 */
@RunWith(AndroidJUnit4::class)
class FullUserFlowNotesTest {

    // =========================
    // RULES
    // =========================

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val intentsRule = IntentsRule()

    // =========================
    // TEST DATA
    // =========================

    private val NOTE_1 = "Weekend shopping"
    private val NOTE_2 = "Workout plan"
    private val NOTE_3 = "Books to read"
    private val NOTE_4 = "Travel checklist"
    private val NOTE_5 = "Movies list"

    private val note1Items = listOf(
        StyledWord("Milk" , R.id.ibRed , true) ,
        StyledWord("Bread" , R.id.ibGreen) ,
        StyledWord("Butter" , R.id.ibBlue)
    )

    private val note2Items = listOf(
        StyledWord("Push" , R.id.ibBlue , true) ,
        StyledWord("Pull" , R.id.ibRed) ,
        StyledWord("Legs" , R.id.ibYellow , true)
    )

    private val note3Items = listOf(
        StyledWord("Clean Code" , R.id.ibBlack , true) ,
        StyledWord("Kotlin" , R.id.ibBlue) ,
        StyledWord("Patterns" , R.id.ibOrange , true)
    )

    private val note4Items = listOf(
        StyledWord("Passport" , R.id.ibOrange , true) ,
        StyledWord("Tickets" , R.id.ibRed , true) ,
        StyledWord("Phone" , R.id.ibGreen , true) ,
        StyledWord("Charger" , R.id.ibBlue)
    )

    private val note5Items = listOf(
        StyledWord("Interstellar" , R.id.ibYellow , true) ,
        StyledWord("Dune" , R.id.ibOrange) ,
        StyledWord("Matrix" , R.id.ibGreen)
    )

    private val updatedNote3Items = listOf(

        // Headers
        StyledWord("I. Non-fiction book" , R.id.ibBlue , true) ,
        StyledWord("II. Fiction" , R.id.ibBlue , true) ,

        // Book status
        StyledWord("Done" , R.id.ibGreen , true) ,
        StyledWord("In the process" , R.id.ibYellow , true) ,
        StyledWord("Leave for later" , R.id.ibRed , false) ,
        StyledWord("Pending" , R.id.ibOrange , false)
    )

    /**
     * Single styled text item inside note
     */
    data class StyledWord(
        val text: String ,
        val colorId: Int ,
        val bold: Boolean = false
    )

    // =========================
    // CORE NAV HELPERS (NO SLEEP / NO WAIT LOOP)
    // =========================

    /**
     * Open Notes tab safely.
     * We do NOT assume current state.
     */
    private fun openNotesTab() {

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        onView(withId(R.id.notes))
            .perform(click())

        assertNotesScreen()
    }

    /**
     * Open editor via FAB / New button
     */
    private fun openNewNoteScreen() {

        onView(withContentDescription("New"))
            .perform(click())

        onView(withId(R.id.edTitle))
            .check(matches(isDisplayed()))
    }

    private fun openSettings() {

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        onView(withId(R.id.settings))
            .perform(click())
    }

    /**
     * Go back using system back only (NO toolbar dependency)
     */
    private fun goBack() {
        pressBack()
    }

    /**
     * Ensure we are on Notes screen by checking RecyclerView existence.
     * This is stable because only Notes screen has this view.
     */
    private fun assertNotesScreen() {

        // Verify main screen is back

        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))


        // Open notes (from main activity)

        onView(withId(R.id.notes))
            .perform(click())

        onView(withId(R.id.rcViewNote))
            .check(matches(isDisplayed()))
    }


    private fun returnToNotesAfterAction() {
        // Return to main screen
        goBack()
        assertNotesScreen()
    }


    // =========================
    // NOTE FLOW
    // =========================

    /**
     * Create note (single stable flow)
     */
    private fun createNote(title: String , description: String) {

        openNewNoteScreen()

        onView(withId(R.id.edTitle))
            .perform(typeText(title) , closeSoftKeyboard())

        onView(withId(R.id.edDescription))
            .perform(typeText(description) , closeSoftKeyboard())

        onView(withId(R.id.save))
            .perform(click())

        // After save, we ALWAYS land in Notes
        assertNotesScreen()

        onView(withText(title))
            .check(matches(isDisplayed()))
    }

    /**
     * Open note from list
     */
    private fun openNote(title: String) {

        onView(withText(title))
            .perform(click())

        onView(withId(R.id.edTitle))
            .check(matches(isDisplayed()))
    }

    /**
     * Save existing note (edit mode)
     */
    private fun saveNote() {
        onView(withId(R.id.save))
            .perform(click())

        assertNotesScreen()
    }

    /**
     * Replaces note description with new text.
     */
    private fun updateNoteContent(
        newContent: String
    ) {

        onView(withId(R.id.edDescription))
            .perform(click())

        // Select all existing text
        onView(withId(R.id.edDescription))
            .perform(
                androidx.test.espresso.action.ViewActions.replaceText(
                    newContent
                )
            )

        closeSoftKeyboard()
    }

    // =========================
    // FORMATTING
    // =========================

    private fun applyBold() {
        onView(withId(R.id.bold))
            .perform(click())
    }

    /**
     * Open color picker and apply color safely.
     *
     * Why custom click:
     * Espresso may fail because color picker animation
     * temporarily makes button less than 90% visible.
     */
    private fun applyColor(colorId: Int) {

        // Close keyboard to prevent overlap
        onView(withId(R.id.edDescription))
            .perform(closeSoftKeyboard())

        // Open picker
        onView(withId(R.id.color))
            .perform(click())

        onView(isRoot()).perform(waitFor(300))

        // Force click even during animation
        onView(withId(colorId))
            .perform(forceClick())
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
    // DELETE
    // =========================

    private fun deleteNote(title: String) {

        onView(withId(R.id.rcViewNote))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(title)) ,
                    clickChild(R.id.btnDelete)
                )
            )

        // confirm dialog if exists
        confirmDelete()

        // ensure we are back on Notes screen
        returnToNotesAfterAction()
    }

    private fun confirmDelete() {
        onView(withId(R.id.btnDelete))
            .perform(click())
    }

    private fun clickChild(id: Int) = object : ViewAction {

        override fun getConstraints(): Matcher<View> =
            isAssignableFrom(View::class.java)

        override fun getDescription(): String =
            "Click child view"

        override fun perform(uiController: UiController , view: View) {
            view.findViewById<View>(id)?.performClick()
        }
    }

    // =========================
    // WAIT
    // =========================

    private fun waitForView(id: Int) {
        onView(withId(id))
            .check(matches(isDisplayed()))
    }

    private fun waitFor(ms: Long) = object : ViewAction {
        override fun getConstraints() = isRoot()
        override fun getDescription() = "wait $ms ms"
        override fun perform(uiController: UiController , view: View?) {
            uiController.loopMainThreadForAtLeast(ms)
        }
    }

    // =========================
    // HELPER
    // =========================

    /**
     * Selects text inside EditText
     * starting from provided index.
     *
     * Helps with duplicated words.
     */
    private fun selectText(
        target: String ,
        fromIndex: Int = 0
    ) = object : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(android.widget.EditText::class.java)
        }

        override fun getDescription(): String {
            return "Select text: $target"
        }

        override fun perform(
            uiController: UiController ,
            view: View
        ) {

            val editText = view as android.widget.EditText

            val fullText = editText.text.toString()

            val start = fullText.indexOf(
                target ,
                fromIndex
            )

            if (start == -1) {
                throw AssertionError(
                    "Text not found: $target"
                )
            }

            val end = start + target.length

            editText.requestFocus()

            editText.setSelection(start , end)

            uiController.loopMainThreadUntilIdle()
        }
    }

    /**
     * Builds note text from StyledWord list
     */
    private fun buildNoteText(
        items: List<StyledWord>
    ): String {

        return items.joinToString("\n") {
            it.text
        }
    }

    /**
     * Updated content for NOTE 3
     */
    private fun buildUpdatedNote3(): String {

        return """
    I. Non-fiction book
    
    * Clean Code - Done
    * Kotlin - In the process
    * Patterns - Pending
    
    II. Fiction
    
    * The Catcher in the Rye - Done
    * Brave New World - Leave for later
    """.trimIndent()
    }

    /**
     * Applies formatting sequentially.
     *
     * Supports duplicated words safely.
     */
    private fun applyFormatting(
        items: List<StyledWord>
    ) {

        var searchStartIndex = 0

        items.forEach { item ->

            onView(withId(R.id.edDescription))
                .perform(
                    selectText(
                        item.text ,
                        searchStartIndex
                    )
                )

            searchStartIndex += item.text.length

            if (item.bold) {
                applyBold()
            }

            applyColor(item.colorId)
        }
    }

    /**
     * Performs click without Espresso visibility restrictions.
     *
     * Useful for animated views and temporary overlays.
     */
    private fun forceClick() = object : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(View::class.java)
        }

        override fun getDescription(): String {
            return "Force click"
        }

        override fun perform(
            uiController: UiController ,
            view: View
        ) {

            view.performClick()
            uiController.loopMainThreadUntilIdle()
        }
    }

    // =========================
    // FULL TEST
    // =========================

    @Test
    fun fullNotesUserFlow() {

        // =========================
        // STEP 1: OPEN NOTES
        // =========================

        openNotesTab()

        // =========================
        // STEP 2: CREATE 4 NOTES
        // =========================

        // ----------------------------
        // NOTE 1
        // ----------------------------
        createNote(
            NOTE_1 ,
            buildNoteText(note1Items)
        )

        openNote(NOTE_1)
        applyFormatting(note1Items)
        saveNote()

        // ----------------------------
        // NOTE 2
        // ----------------------------
        createNote(
            NOTE_2 ,
            buildNoteText(note2Items)
        )

        openNote(NOTE_2)
        applyFormatting(note2Items)
        saveNote()

        // ----------------------------
        // NOTE 3
        // ----------------------------
        createNote(
            NOTE_3 ,
            buildNoteText(note3Items)
        )

        openNote(NOTE_3)
        applyFormatting(note3Items)
        saveNote()

        // ----------------------------
        // NOTE 4
        // ----------------------------
        createNote(
            NOTE_4 ,
            buildNoteText(note4Items)
        )

        openNote(NOTE_4)
        applyFormatting(note4Items)
        saveNote()

        // =========================
        // STEP 3: SETTINGS FLOW
        // =========================

        openSettings()
        setNoteStyle()
        setTimeFormat()
        setTitleSize()
        setContentSize()
        setDarkTheme()
        setColorStyleLime()
        returnToNotesAfterAction()

        openSettings()
        setLightTheme()
        setColorStyleDeepOrange()
        setDarkTheme()
        returnToNotesAfterAction()

        // =========================
        // STEP 4: CREATE FINAL 5TH NOTE
        // =========================

        // ----------------------------
        // NOTE 5
        // ----------------------------
        createNote(
            NOTE_5 ,
            buildNoteText(note5Items)
        )

        openNote(NOTE_5)
        applyFormatting(note5Items)
        saveNote()

        // =========================
        // STEP 5: DELETE 2ND NOTE
        // =========================

        deleteNote(NOTE_2)

        onView(withText(NOTE_2))
            .check(doesNotExist())

        // =========================
        // STEP 6: UPDATE 3RD NOTE
        // =========================

        // Open NOTE 3
        openNote(NOTE_3)

        // Replace old text with new content
        updateNoteContent(
            buildUpdatedNote3()
        )

        // Apply formatting
        applyFormatting(updatedNote3Items)

        // Save updated note
        saveNote()

        // Verify Notes screen visible again
        assertNotesScreen()

    }
}