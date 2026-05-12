package com.example.shoppingAndNotesListApp.ui.dialogs


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.databinding.EditListItemDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Helper object for editing shopping list items and library items.
 *
 * Responsibilities:
 * - Displays dialog for editing item name and optional info
 * - Handles user input validation
 * - Returns updated item via callback
 *
 * Architecture:
 * - Stateless (object)
 * - UI-only component (no database logic)
 * - Works with existing model (ShopListItem)
 *
 * Behavior:
 * - For SHOP item → shows name + info fields
 * - For LIBRARY item → shows only name (info hidden)
 *
 * Usage:
 * EditListItemDialog.showDialog(context, item) { updatedItem ->
 *     // handle update
 * }
 */
object EditListItemDialog {

    /**
     * Shows edit dialog for a given item
     *
     * @param context Activity context
     * @param item original item to edit
     * @param listener callback with updated item
     */
    fun showDialog(context: Context , item: ShopListItem , listener: Listener) {
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()

        // ================= INITIAL STATE =================

        binding.apply {
            edName.setText(item.name)
            edInfo.setText(item.itemInfo)

            // Hide info field for library items
            if (item.itemType == TYPE_LIBRARY) {
                edInfo.visibility = View.GONE
            }
        }
// ================= ACTIONS =================

        /**
         * Update item action
         *
         * Important:
         * - Validate input
         * - Return updated copy of item
         * - Close dialog AFTER action
         */
        binding.btnUpdate.setOnClickListener {

            val name = binding.edName.text.toString()
            val info = binding.edInfo.text.toString()

            if (name.isNotBlank()) {

                val updatedItem = item.copy(
                    name = name ,
                    itemInfo = info
                )

                listener.onClick(updatedItem)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // ================= LISTENER =================

    /**
     * Callback for returning updated item
     */
    interface Listener {
        fun onClick(item: ShopListItem)
    }

    // ================= CONSTANTS =================

    private const val TYPE_LIBRARY = 1
}