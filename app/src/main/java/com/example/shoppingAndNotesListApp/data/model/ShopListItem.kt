package com.example.shoppingAndNotesListApp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single item in a shopping list.
 *
 * Purpose:
 * Stores items that belong to a specific shopping list,
 * including name, additional info, and state (checked/unchecked).
 *
 * Architecture notes:
 * - Stored in Room table "shop_list_item"
 * - Belongs to a parent list via listId
 * - Used in RecyclerView with DiffUtil
 */
@Entity(tableName = "shop_list_item")
data class ShopListItem(

    // ================= PRIMARY KEY =================

    /**
     * Auto-generated unique ID for each item.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int? ,

    // ================= ITEM DATA =================

    /**
     * Main item name shown in UI.
     *
     * Example:
     * - Milk
     * - Bread
     * - Apples
     */
    @ColumnInfo(name = "name")
    val name: String ,

    /**
     * Optional additional information about item.
     *
     * Example:
     * - "2 liters"
     * - "whole grain"
     */
    @ColumnInfo(name = "itemInfo")
    val itemInfo: String = "" ,

    // ================= STATE =================

    /**
     * Indicates whether item is checked (purchased).
     *
     * false → not bought
     * true  → already bought
     */
    @ColumnInfo(name = "itemChecked")
    val itemChecked: Boolean = false ,

    // ================= RELATION =================

    /**
     * ID of parent shopping list.
     *
     * Used to group items by list.
     */
    @ColumnInfo(name = "listId")
    val listId: Int ,

    // ================= TYPE =================

    /**
     * Item type flag.
     *
     * Example usage:
     * - 0 → normal item
     * - 1 → header / separator (if used)
     */
    @ColumnInfo(name = "itemType")
    val itemType: Int = 0 ,
)
