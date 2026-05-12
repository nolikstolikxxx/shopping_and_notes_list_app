package com.example.shoppingAndNotesListApp.data.model

/**
 * Lightweight projection model for UI layer.
 *
 * This class is NOT a Room Entity.
 *
 * Used for:
 * - displaying shopping lists in RecyclerView
 * - showing counters without loading full relations
 *
 * Purpose:
 * - optimization of DB queries (less data loaded)
 * - faster UI rendering
 * - avoids unnecessary joins / parsing
 */
data class ShopListWithCounters(

    // ================= IDENTIFIER =================

    /**
     * Unique identifier of the shopping list.
     */
    val id: Int ,

    // ================= BASIC INFO =================

    /**
     * Name of the shopping list.
     * Example: "Groceries", "Home supplies".
     */
    val name: String ,

    /**
     * Timestamp of list creation or last update.
     * Stored as formatted String for UI display.
     */
    val time: String ,

    // ================= COUNTERS =================

    /**
     * Total number of items in the list.
     */
    val allItemCounter: Int ,

    /**
     * Number of checked (completed) items.
     */
    val checkedItemsCounter: Int
)