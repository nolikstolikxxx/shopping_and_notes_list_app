package com.example.shoppingAndNotesListApp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing reusable library items.
 *
 * Purpose:
 * Stores frequently used product/item names
 * for quick access and autocomplete.
 *
 * Database notes:
 * - Uses Room Entity
 * - Table name: "library"
 * - Item names must be unique
 *
 * Performance:
 * - Indexed by "name"
 * - Faster search and duplicate prevention
 */
@Entity(
    tableName = "library" ,

    /**
     * Unique index prevents duplicate item names.
     *
     * Example:
     * "Milk" cannot be inserted twice.
     */
    indices = [Index(value = ["name"] , unique = true)]
)
data class LibraryItem(

    // ================= PRIMARY KEY =================

    /**
     * Auto-generated database ID.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null ,

    // ================= ITEM DATA =================

    /**
     * Library item name.
     *
     * Examples:
     * - Milk
     * - Bread
     * - Apples
     */
    @ColumnInfo(name = "name")
    val name: String ,
)