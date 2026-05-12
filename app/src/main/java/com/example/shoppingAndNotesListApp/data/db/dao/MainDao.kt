package com.example.shoppingAndNotesListApp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shoppingAndNotesListApp.data.model.LibraryItem
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import com.example.shoppingAndNotesListApp.data.model.ShopListWithCounters
import kotlinx.coroutines.flow.Flow

/**
 * Main DAO interface for all database operations.
 *
 * Provides access to notes, shopping lists and library.
 */
@Dao
interface MainDao {

    // ================= NOTES =================

    @Query("SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Insert
    suspend fun insertNote(note: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Query("DELETE FROM note_list WHERE id = :id")
    suspend fun deleteNote(id: Int)

    // ================= SHOP LIST NAMES =================

    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Insert
    suspend fun insertShopListName(nameItem: ShopListNameItem)

    @Update
    suspend fun updateListName(shopListNameItem: ShopListNameItem)

    @Query("DELETE FROM shopping_list_names WHERE id = :id")
    suspend fun deleteShopListName(id: Int)

    @Query("SELECT * FROM shopping_list_names WHERE id = :id")
    fun getShopListNameById(id: Int): Flow<ShopListNameItem>

    @Query("SELECT * FROM shopping_list_names WHERE id = :id LIMIT 1")
    suspend fun getShopListNameByIdOnce(id: Int): ShopListNameItem?

    // ================= SHOP LIST ITEMS =================

    @Query("SELECT * FROM shop_list_item WHERE listId = :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Insert
    suspend fun insertItem(shopListItem: ShopListItem)

    @Update
    suspend fun updateListItem(item: ShopListItem)

    @Query("DELETE FROM shop_list_item WHERE id = :id")
    suspend fun deleteShopListItem(id: Int)

    @Query("DELETE FROM shop_list_item WHERE listId = :listId")
    suspend fun deleteShopItemsByListId(listId: Int)

    // ================= LIBRARY =================

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query("SELECT * FROM library WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getLibraryItemByName(name: String): LibraryItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLibraryItem(libraryItem: LibraryItem): Long

    @Update
    suspend fun updateLibraryItem(item: LibraryItem)

    @Query("DELETE FROM library WHERE id = :id")
    suspend fun deleteLibraryItem(id: Int)

    // ================= COMPLEX QUERY =================

    /**
     * Returns shopping lists with calculated item counters.
     */
    @Query(
        """
    SELECT 
        n.id AS id,
        n.name AS name,
        n.time AS time,
        COUNT(i.id) AS allItemCounter,
        SUM(CASE WHEN i.itemChecked = 1 THEN 1 ELSE 0 END) AS checkedItemsCounter
    FROM shopping_list_names n
    LEFT JOIN shop_list_item i ON n.id = i.listId
    GROUP BY n.id
"""
    )
    fun getShopListsWithCounters(): Flow<List<ShopListWithCounters>>
}