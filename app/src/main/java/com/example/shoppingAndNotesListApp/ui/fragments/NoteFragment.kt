package com.example.shoppingAndNotesListApp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.shoppingAndNotesListApp.core.utils.mainViewModel
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.databinding.FragmentNoteBinding
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import com.example.shoppingAndNotesListApp.ui.activities.NoteActivity
import com.example.shoppingAndNotesListApp.ui.adapters.NoteAdapter
import com.example.shoppingAndNotesListApp.ui.dialogs.DeleteDialog

/**
 * Fragment for displaying notes list.
 *
 * Features:
 * - Displays notes in list or grid mode
 * - Supports create / edit / delete
 *
 * Architecture:
 * - MVVM (ViewModel + LiveData)
 * - UI layer only (no DB access here)
 *
 * Performance:
 * - RecyclerView + DiffUtil
 * - Stable IDs in adapter
 */
class NoteFragment : BaseFragment() , NoteAdapter.Listener {

    // ================= UI =================

    private lateinit var binding: FragmentNoteBinding
    private lateinit var adapter: NoteAdapter

    // ================= ACTIVITY RESULT =================

    private lateinit var editLauncher: ActivityResultLauncher<Intent>

    // ================= PREFS =================

    private lateinit var defPref: SharedPreferences

    // ================= VIEW MODEL =================

    private val mainViewModel by mainViewModel()

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate an instance at $this")

        setupEditResultLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(
            inflater ,
            container ,
            false
        )

        Log.d(TAG , "onCreateView an instance at $this")
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        Log.d(TAG , "onViewCreated an instance at $this")

        initRecyclerView()
        observeNotes()
    }

    // ================= RECYCLER VIEW =================

    /**
     * Initializes RecyclerView with adapter and layout manager
     */
    private fun initRecyclerView() = with(binding) {

        defPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        rcViewNote.layoutManager = getLayoutManager()

        // Disable animations (prevents glitches during fast updates)
        // ⭐ IMPORTANT - Disables animations
        // (added based on the results of the FullUserFlowTestShopList test)
        rcViewNote.itemAnimator = null

        adapter = NoteAdapter(
            this@NoteFragment ,
            defPref ,
            viewLifecycleOwner.lifecycleScope
        )

        rcViewNote.adapter = adapter

        (activity as? MainActivity)?.updateNavigationUI("Notes")

        Log.d(TAG , "RecyclerView initialized")
    }

    /**
     * Returns layout manager based on user preference
     */
    private fun getLayoutManager(): RecyclerView.LayoutManager {
        val style = defPref.getString("note_style_key" , "linear")

        return if (style == "linear") {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(
                2 ,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    // ================= DATA OBSERVING =================

    /**
     * Observes notes from ViewModel
     *
     * IMPORTANT:
     * - submitList triggers DiffUtil
     * - DO NOT use toList() (breaks diff)
     */
    private fun observeNotes() {
        mainViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
        }
        Log.d(TAG , "observeNotes initialized")
    }

    // ================= ACTIVITY RESULT =================

    /**
     * Handles result from NoteActivity (create/edit)
     */
    private fun setupEditResultLauncher() {

        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult
            val editState = data.getStringExtra(EDIT_STATE_KEY)

            val note: NoteItem? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data.getParcelableExtra(NEW_NOTE_KEY , NoteItem::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    data.getParcelableExtra(NEW_NOTE_KEY)
                }

            if (note == null) return@registerForActivityResult

            when (editState) {
                "update" -> mainViewModel.updateNote(note)
                else -> mainViewModel.insertNote(note)
            }
        }
    }

    // ================= USER ACTIONS =================

    /**
     * Click on "New Note"
     */
    override fun onClickNew() {
        editLauncher.launch(
            Intent(
                activity ,
                NoteActivity::class.java
            )
        )
    }

    /**
     * Click on existing note (open edit)
     */
    override fun onClickItem(note: NoteItem) {
        val intent = Intent(
            activity ,
            NoteActivity::class.java
        ).apply {

            putExtra(NEW_NOTE_KEY , note)
        }
        editLauncher.launch(intent)
    }

    /**
     * Delete note with confirmation dialog
     */
    override fun deleteItem(id: Int) {
        (activity as? AppCompatActivity)?.let {
            DeleteDialog.showDialog(
                it ,
                object : DeleteDialog.Listener {

                    override fun onClick() {
                        mainViewModel.deleteNote(id)
                    }
                })
        }
    }

    // ================= DEBUG LIFECYCLE =================

    override fun onStart() {
        super.onStart()
        Log.d(TAG , "onStart an instance at $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG , "onResume an instance at $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG , "onPause an instance at $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG , "onStop an instance at $this")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG , "onDestroyView an instance at $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG , "onDestroy an instance at $this")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG , "onDetach an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG , "onViewStateRestored an instance at $this")
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "NoteFragment"

        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"
    }
}