package com.example.shoppingAndNotesListApp.data.repository

import android.util.Log
import com.example.shoppingAndNotesListApp.data.db.dao.MainDao
import com.example.shoppingAndNotesListApp.data.model.LibraryItem
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import com.example.shoppingAndNotesListApp.data.model.ShopListWithCounters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository layer.
 *
 * Acts as a single source of truth between ViewModel and DAO.
 * Contains business logic and abstracts data operations.
 */
class MainRepository(private val dao: MainDao) {
    // ================= READ =================

    val allNotes: Flow<List<NoteItem>> =
        dao.getAllNotes()

    val allShopListNames: Flow<List<ShopListNameItem>> =
        dao.getAllShopListNames()

    val shopListsWithCounters: Flow<List<ShopListWithCounters>> =
        dao.getShopListsWithCounters()

    fun getShopListItems(listId: Int): Flow<List<ShopListItem>> =
        dao.getAllShopListItems(listId)

    fun getShopListNameById(id: Int): Flow<ShopListNameItem> =
        dao.getShopListNameById(id)

    suspend fun getLibraryItems(name: String): List<LibraryItem> =
        dao.getAllLibraryItems(name)

    suspend fun getLibraryItemByName(name: String): LibraryItem? =
        dao.getLibraryItemByName(name)

    suspend fun getShopListNameByIdOnce(id: Int): ShopListNameItem? =
        dao.getShopListNameByIdOnce(id)

    // ================= INSERT =================

    suspend fun insertNote(note: NoteItem) =
        dao.insertNote(note)

    suspend fun insertShopListName(item: ShopListNameItem) =
        dao.insertShopListName(item)

    /**
     * Inserts a shopping item into the list.
     *
     * Also adds item to library if it does not exist.
     */
    suspend fun insertShopItem(item: ShopListItem) {
        dao.insertItem(item)

        val existing = dao.getLibraryItemByName(item.name)
        if (existing == null) {
            dao.insertLibraryItem(
                LibraryItem(null , item.name)
            )
        }
    }

    suspend fun insertLibraryItem(name: String) {
        val existing = dao.getLibraryItemByName(name)
        if (existing == null) {
            dao.insertLibraryItem(LibraryItem(null , name))
        }
    }

    // ================= UPDATE =================

    suspend fun updateNote(note: NoteItem) =
        dao.updateNote(note)

    suspend fun updateShopItem(item: ShopListItem) =
        dao.updateListItem(item)

    suspend fun updateShopListName(item: ShopListNameItem) =
        dao.updateListName(item)

    suspend fun updateLibraryItem(item: LibraryItem) =
        dao.updateLibraryItem(item)

    // ================= DELETE =================

    suspend fun deleteNote(id: Int) =
        dao.deleteNote(id)

    /**
     * FULL delete (list + items)
     */
    suspend fun deleteShopList(id: Int) {
        dao.deleteShopListName(id)
        dao.deleteShopItemsByListId(id)
    }

    /**
     * Deletes ONLY items (keeps list).
     */
    suspend fun clearShopList(id: Int) {
        dao.deleteShopItemsByListId(id)
    }

    suspend fun deleteShopListItem(id: Int) =
        dao.deleteShopListItem(id)

    suspend fun deleteLibraryItem(id: Int) =
        dao.deleteLibraryItem(id)

    // ================= BUSINESS LOGIC =================

    /**
     * Toggle item and update counters.
     */
    suspend fun toggleItem(item: ShopListItem) {

        val updatedItem = item.copy(itemChecked = !item.itemChecked)
        // Verifying database mutations
        Log.d(
            "CHECK" ,
            "VM update item=${item.name}, newChecked=${updatedItem.itemChecked}"
        )
        dao.updateListItem(updatedItem)

        val items = dao.getAllShopListItems(item.listId).first()

        val total = items.size
        val checked = items.count { it.itemChecked }

        val list = dao.getAllShopListNames()
            .first()
            .find { it.id == item.listId }

        list?.let {
            dao.updateListName(
                it.copy(
                    allItemCounter = total ,
                    checkedItemsCounter = checked
                )
            )
        }
    }
}
