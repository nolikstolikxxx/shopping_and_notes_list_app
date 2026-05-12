package com.example.shoppingAndNotesListApp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.utils.HtmlManager
import com.example.shoppingAndNotesListApp.core.utils.MyTouchListener
import com.example.shoppingAndNotesListApp.core.utils.TimeManager
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.databinding.ActivityNoteBinding
import com.example.shoppingAndNotesListApp.ui.fragments.NoteFragment
import kotlinx.coroutines.launch

/**
 * Activity for creating and editing notes.
 *
 * Features:
 * - Rich text editing (bold, color)
 * - HTML conversion
 * - Color picker with animations
 *
 * Architecture notes:
 * - Works with NoteItem
 * - Returns result via Intent (no direct DB access)
 */
@Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")
class NoteActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate an instance at $this")

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        init()
        setTextSize()
        loadNoteAsync()
        setupColorPickerClicks()
        setupSelectionActionMode()
    }

    // ================= TOOLBAR =================

    /**
     * Setup Toolbar as ActionBar with back navigation
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // ================= INIT =================

    /**
     * Initial setup (preferences, listeners)
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }

    /**
     * Apply text sizes from preferences
     */
    private fun setTextSize() = with(binding) {
        edTitle.setTextSize(pref?.getString("title_size_key" , "16"))
        edDescription.setTextSize(pref?.getString("content_size_key" , "12"))
    }

    /**
     * Extension for safe text size parsing
     */
    private fun EditText.setTextSize(size: String?) {
        if (size != null) this.textSize = size.toFloat()
    }

    // ================= NOTE LOADING =================

    /**
     * Loads note from Intent and formats HTML asynchronously
     */
    private fun loadNoteAsync() {
        note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                NoteFragment.NEW_NOTE_KEY ,
                NoteItem::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NoteFragment.NEW_NOTE_KEY)
        }

        note?.let { note ->

            lifecycleScope.launch {

                binding.edTitle.setText(note.title)
                binding.edDescription.text =
                    SpannableStringBuilder(
                        HtmlManager.getFromHtml(note.content)
                    )
            }
        }
    }

    // ================= OPTIONS MENU =================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG , "onCreateOptionsMenu an instance at $this")
        menuInflater.inflate(R.menu.new_note_menu , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG , "onOptionsItemSelected an instance at $this")

        when (item.itemId) {
            R.id.save -> setMainResult()

            android.R.id.home -> finish()

            R.id.bold -> setBoldForSelectedText()

            R.id.color -> {
                if (binding.colorPicker.isShown) closeColorPicker()
                else openColorPicker()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // ================= TEXT STYLING =================

    /**
     * Toggles bold style for selected text
     */
    private fun setBoldForSelectedText() =
        with(binding) {

            val start = edDescription.selectionStart
            val end = edDescription.selectionEnd

            if (start == end) {
                Toast.makeText(
                    this@NoteActivity ,
                    "The text is not highlighted" ,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

            val styles = edDescription.text?.getSpans(
                start ,
                end ,
                StyleSpan::class.java
            )

            if (!styles.isNullOrEmpty()) {
                edDescription.text?.removeSpan(styles[0])
            } else {
                edDescription.text?.setSpan(
                    StyleSpan(Typeface.BOLD) ,
                    start ,
                    end ,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            edDescription.setSelection(start , end)
        }

    /**
     * Applies color to selected text
     */
    private fun setColorForSelectedText(colorId: Int) =

        with(binding) {

            val start = edDescription.selectionStart
            val end = edDescription.selectionEnd

            if (start == end) {
                Toast.makeText(
                    this@NoteActivity ,
                    "The text is not highlighted" ,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

            val styles = edDescription.text?.getSpans(
                start ,
                end ,
                ForegroundColorSpan::class.java
            )

            if (!styles.isNullOrEmpty()) {
                edDescription.text?.removeSpan(styles[0])
            }

            edDescription.text?.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this@NoteActivity ,
                        colorId
                    )
                ) ,
                start ,
                end ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            edDescription.setSelection(start , end)
        }

    // ================= COLOR PICKER =================

    /**
     * Setup color picker button clicks
     */
    private fun setupColorPickerClicks() = with(binding) {
        ibRed.setOnClickListener { setColorForSelectedText(R.color.picker_red) }
        ibGreen.setOnClickListener { setColorForSelectedText(R.color.picker_green) }
        ibBlue.setOnClickListener { setColorForSelectedText(R.color.picker_blue) }
        ibYellow.setOnClickListener { setColorForSelectedText(R.color.picker_yellow) }
        ibBlack.setOnClickListener { setColorForSelectedText(R.color.picker_black) }
        ibOrange.setOnClickListener { setColorForSelectedText(R.color.picker_orange) }
    }

    private fun openColorPicker() {
        binding.colorPicker.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(this , R.anim.open_color_picker)
        binding.colorPicker.startAnimation(anim)
    }

    private fun closeColorPicker() {
        val anim = AnimationUtils.loadAnimation(this , R.anim.close_color_picker)

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.colorPicker.startAnimation(anim)
    }

    // ================= ACTION MODE =================

    /**
     * Disables default text selection menu
     */
    private fun setupSelectionActionMode() {
        val callback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode? , menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode? , menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode? , item: MenuItem?) = true

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }

        binding.edDescription.customSelectionActionModeCallback = callback
    }

    // ================= SAVE =================

    /**
     * Updates existing note
     */
    private fun updateNote(): NoteItem? = with(binding) {
        val currentNote = note ?: return null

        val updatedContent = edDescription.text?.let {
            HtmlManager.toHtml(it)
        } ?: return null

        return NoteItem(
            id = currentNote.id ,
            title = edTitle.text.toString() ,
            content = updatedContent ,
            time = TimeManager.getCurrentTime() ,
            category = currentNote.category
        )
    }

    /**
     * Creates new note
     */
    private fun createNewNote(): NoteItem? {
        val content = binding.edDescription.text?.let {
            HtmlManager.toHtml(it)
        } ?: return null

        return NoteItem(
            id = null ,
            title = binding.edTitle.text.toString() ,
            content = content ,
            time = TimeManager.getCurrentTime() ,
            category = ""
        )
    }

    /**
     * Sends result back to Fragment
     */
    private fun setMainResult() {
        val isNewNote = note == null

        val resultNote = if (isNewNote) {
            createNewNote()
        } else {
            updateNote()
        }

        if (resultNote == null) {
            Toast.makeText(this , "Empty note" , Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY , resultNote)
            putExtra(
                NoteFragment.EDIT_STATE_KEY ,
                if (isNewNote) "new" else "update"
            )
        }

        setResult(RESULT_OK , intent)
        finish()
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG , "onDestroy an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "NoteActivity"
    }
}