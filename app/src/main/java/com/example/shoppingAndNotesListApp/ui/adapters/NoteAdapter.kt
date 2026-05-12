package com.example.shoppingAndNotesListApp.ui.adapters

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingAndNotesListApp.databinding.NoteListItemBinding
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.core.utils.HtmlManager
import com.example.shoppingAndNotesListApp.core.utils.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for displaying notes list.
 *
 * Features:
 * - Fast preview rendering (without HTML)
 * - Full HTML rendering in background (coroutines)
 * - Caching formatted text for performance
 *
 * Architecture notes:
 * - Works only with UI layer
 * - Does NOT access database
 *
 * Performance:
 * - Uses DiffUtil
 * - Stable IDs enabled
 * - HTML parsing optimized with cache
 */
class NoteAdapter(
    private val listener: Listener ,
    private val defPref: SharedPreferences ,
    private val scope: CoroutineScope
) : ListAdapter<NoteItem , NoteAdapter.NoteViewHolder>(DiffCallback()) {

    init {
        setHasStableIds(true)
    }

    // ================= LISTENER =================

    interface Listener {
        fun onClickItem(note: NoteItem)
        fun deleteItem(id: Int)
    }

    // ================= CACHE CONTROL =================

    // Cache for parsed HTML content
    private val htmlCache = mutableMapOf<Int , CharSequence>()

    override fun submitList(list: List<NoteItem>?) {
        htmlCache.clear()
        super.submitList(list)
    }

    // Precompiled regex for fast HTML stripping
    private val htmlTagRegex = Regex("<.*?>")

    /**
     * Stable IDs improve RecyclerView performance
     */
    override fun getItemId(position: Int): Long {
        return getItem(position).id?.toLong() ?: RecyclerView.NO_ID
    }

    // ================= VIEW HOLDER =================

    inner class NoteViewHolder(
        private val binding: NoteListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentNoteId: Int? = null
        private var job: Job? = null

        /**
         * Binds note item
         *
         * Strategy:
         * 1) Show instant preview (no HTML)
         * 2) Use cache if available
         * 3) Parse full HTML in background
         */
        fun bind(note: NoteItem) {

            val noteId = note.id ?: return
            currentNoteId = noteId

            binding.tvTitleNote.text = note.title
            binding.tvTime.text =
                TimeManager.getTimeFormat(note.time , defPref)

            // ================= FAST PREVIEW =================

            val preview = note.content
                .replace(htmlTagRegex , "")
                .take(200)

            binding.tvDescription.text = preview

            // ================= CACHE =================

            htmlCache[noteId]?.let {
                binding.tvDescription.text = it
                return
            }

            // ================= COROUTINE =================

            job?.cancel()

            job = scope.launch(Dispatchers.Default) {

                val fullFormatted =
                    HtmlManager.getFromHtml(note.content).trim()

                withContext(Dispatchers.Main) {

                    // Check if ViewHolder still valid
                    if (currentNoteId == noteId &&
                        adapterPosition != RecyclerView.NO_POSITION
                    ) {

                        binding.tvDescription.text = fullFormatted
                        htmlCache[noteId] = fullFormatted

                        // Limit cache size
                        if (htmlCache.size > 100) {
                            htmlCache.clear()
                        }
                    }
                }
            }

            // ================= CLICK =================

            binding.root.setOnClickListener {
                listener.onClickItem(note)
            }

            binding.btnDelete.setOnClickListener {
                listener.deleteItem(noteId)
                htmlCache.remove(noteId)
            }
        }
    }

    // ================= ADAPTER =================

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): NoteViewHolder {
        val binding = NoteListItemBinding.inflate(
            LayoutInflater.from(parent.context) ,
            parent ,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder , position: Int) {
        holder.bind(getItem(position))
    }

    // ================= DIFF =================

    class DiffCallback : DiffUtil.ItemCallback<NoteItem>() {
        override fun areItemsTheSame(old: NoteItem , new: NoteItem): Boolean {
            return old.id == new.id
        }

        override fun areContentsTheSame(old: NoteItem , new: NoteItem): Boolean {
            Log.d("NOTE_DEBUG" , "old=${old.content}")
            Log.d("NOTE_DEBUG" , "new=${new.content}")

            return old == new
        }
    }
}