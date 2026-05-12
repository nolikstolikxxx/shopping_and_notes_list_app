package com.example.shoppingAndNotesListApp.ui.dialogs


//noinspection SuspiciousImport
import android.R
import android.content.Context
import android.view.LayoutInflater
import com.example.shoppingAndNotesListApp.databinding.DeleteDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Helper object for showing delete confirmation dialog.
 *
 * Responsibilities:
 * - Displays a custom Material dialog
 * - Handles user confirmation (Delete / Cancel)
 * - Notifies caller via callback
 *
 * Architecture:
 * - Stateless (object)
 * - Does NOT contain business logic
 * - Used by Activities/Fragments only for UI interaction
 *
 * Usage:
 * DeleteDialog.showDialog(context) {
 *     // handle delete action
 * }
 *
 * UI:
 * - Custom layout (ViewBinding)
 * - Transparent background
 * - Material Design dialog
 */
object DeleteDialog {

    /**
     * Shows delete confirmation dialog
     *
     * @param context Activity context (required for dialog)
     * @param listener callback triggered on delete confirmation
     */
    fun showDialog(context: Context , listener: Listener) {

        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()

        // Apply transparent background for custom layout
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        // ================= ACTIONS =================

        /**
         * Confirm delete action
         *
         * Important:
         * - First dismiss dialog (UI)
         * - Then trigger callback (logic)
         */
        binding.btnDelete.setOnClickListener {
            dialog.dismiss()
            listener.onClick()
        }

        /**
         * Cancel action
         *
         * Only closes dialog, no side effects
         */
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    // ================= LISTENER =================

    /**
     * Callback interface for delete confirmation
     */
    interface Listener {
        fun onClick()
    }
}