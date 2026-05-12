package com.example.shoppingAndNotesListApp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Room entity representing a single note.
 *
 * Purpose:
 * Stores user-created notes with title, content,
 * timestamp and category.
 *
 * Architecture notes:
 * - Stored in Room database table "note_list"
 * - Implements Parcelable for passing between Activities
 * - Used in MVVM (ViewModel → Repository → DAO → Room)
 */
@Parcelize
@Entity(tableName = "note_list")
data class NoteItem(

    // ================= PRIMARY KEY =================

    /**
     * Auto-generated unique identifier for each note.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int? ,

    // ================= NOTE CONTENT =================

    /**
     * Note title shown in list UI.
     */
    @ColumnInfo(name = "title")
    val title: String ,

    /**
     * Full note content (supports HTML formatting).
     */
    @ColumnInfo(name = "content")
    val content: String ,

    /**
     * Creation or update timestamp.
     *
     * Stored as String for simplicity,
     * formatted via TimeManager.
     */
    @ColumnInfo(name = "time")
    val time: String ,

    // ================= METADATA =================

    /**
     * Category or tag for grouping notes.
     *
     * Example:
     * - Work
     * - Personal
     * - Ideas
     */
    @ColumnInfo(name = "category")
    val category: String ,

    ) : Parcelable
