package com.example.shoppingAndNotesListApp.ui.fragments

import androidx.fragment.app.Fragment

/**
 * Base class for all fragments in the app.
 *
 * Responsibilities:
 * - Provides a common contract for "Create/New" action
 * - Used by Activity to trigger fragment-specific behavior
 *
 * Architecture:
 * - Acts as an abstraction layer between Activity and specific Fragment
 * - Prevents Activity from knowing implementation details of each screen
 *
 * Usage:
 * - Each fragment must implement [onClickNew()]
 * - Usually triggered from Activity
 */
abstract class BaseFragment : Fragment() {

    /**
     * Called when user clicks "Create/New" action
     *
     * Each fragment defines its own behavior:
     * - ShopListFragment → create new shopping list
     * - NoteFragment → create new note
     *
     * Important:
     * - Should NOT contain heavy logic
     * - Only UI actions (dialogs, navigation)
     */
    abstract fun onClickNew()
}