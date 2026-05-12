package com.example.shoppingAndNotesListApp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.shoppingAndNotesListApp.databinding.DialogTextInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * DialogFragment for text input (create / rename).
 *
 * Responsibilities:
 * - Displays input dialog with title, hint, and optional initial value
 * - Validates user input
 * - Returns result via callback
 *
 * Architecture:
 * - UI-only component (no business logic)
 * - Lifecycle-aware (DialogFragment)
 * - Safe for configuration changes
 *
 * Features:
 * - Auto focus + keyboard open
 * - Input validation (empty disabled)
 * - "Enter" action = confirm
 *
 * Usage:
 * TextInputDialogFragment.newInstance(...)
 *     .apply { setListener { text -> ... } }
 *     .show(fragmentManager, TAG)
 */
class TextInputDialogFragment : DialogFragment() {

    // ================= BINDING =================

    private var _binding: DialogTextInputBinding? = null
    private val binding get() = _binding!!

    // ================= CALLBACK =================

    /**
     * Sets callback for result
     */
    private var listener: ((String) -> Unit)? = null

    fun setListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    // ================= DIALOG =================

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogTextInputBinding.inflate(layoutInflater)

        val title = requireArguments().getString(TITLE) ?: ""
        val hint = requireArguments().getString(HINT) ?: ""
        val value = requireArguments().getString(VALUE) ?: ""

        setupUi(title , hint , value)
        setupActions()

        val dialog = MaterialAlertDialogBuilder(
            requireContext()
        )
            .setView(binding.root)
            .create()

        setupKeyboard(dialog)

        return dialog
    }

    // ================= UI SETUP =================

    private fun setupUi(title: String , hint: String , value: String) =
        with(binding) {
            tvTitle.text = title
            edName.hint = hint
            edName.setText(value)

            // Disable OK by default
            btnOk.isEnabled = false

            // Enable OK only when text is not blank
            edName.doAfterTextChanged {
                btnOk.isEnabled = !it.isNullOrBlank()
            }
        }

    // ================= ACTIONS =================

    private fun setupActions() = with(binding) {

        // Cancel action
        btnCancel.setOnClickListener {
            dismiss()
        }

        // Confirm action
        btnOk.setOnClickListener {
            submit()
        }

        // Enter key = confirm
        edName.setOnEditorActionListener { _ , _ , _ ->
            submit()
            true
        }
    }

    /**
     * Handles submit logic
     */
    private fun submit() {
        val text = binding.edName.text.toString()

        if (text.isNotBlank()) {
            dismiss()              // close UI first
            listener?.invoke(text) // then send result
        }
    }

    // ================= KEYBOARD =================

    /**
     * Autofocus + show keyboard
     */
    private fun setupKeyboard(dialog: Dialog) {
        binding.edName.requestFocus()

        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
    }

    // ================= LIFECYCLE =================

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listener = null
    }

    // ================= FACTORY =================

    companion object {

        private const val TITLE = "title"
        private const val HINT = "hint"
        private const val VALUE = "value"

        /**
         * Creates new instance of dialog
         */
        fun newInstance(
            title: String ,
            hint: String ,
            value: String = ""
        ): TextInputDialogFragment {

            val fragment = TextInputDialogFragment()

            fragment.arguments = Bundle().apply {
                putString(TITLE , title)
                putString(HINT , hint)
                putString(VALUE , value)
            }
            return fragment
        }
    }
}