package com.example.shoppingAndNotesListApp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoppingAndNotesListApp.data.model.LibraryItem
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import com.example.shoppingAndNotesListApp.data.model.ShopListWithCounters
import com.example.shoppingAndNotesListApp.data.repository.MainRepository
import kotlinx.coroutines.launch

/**
 * ViewModel layer.
 *
 * Does NOT know about Room/DAO anymore.
 * Only communicates with Repository.
 * Converts Flow -> LiveData
 * and handles UI-related logic.
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {

    // ================= READ =================

    val allNotes: LiveData<List<NoteItem>> =
        repository.allNotes.asLiveData()

    val allShopListNames: LiveData<List<ShopListNameItem>> =
        repository.allShopListNames.asLiveData()

    val shopListsWithCounters: LiveData<List<ShopListWithCounters>> =
        repository.shopListsWithCounters.asLiveData()

    fun getShopListItems(listId: Int): LiveData<List<ShopListItem>> =
        repository.getShopListItems(listId).asLiveData()

    fun getShopListNameById(id: Int): LiveData<ShopListNameItem> =
        repository.getShopListNameById(id).asLiveData()

    suspend fun getShopListNameByIdOnce(id: Int): ShopListNameItem? {
        return repository.getShopListNameByIdOnce(id)
    }

    // Library (manual load)
    private val _libraryItems = MutableLiveData<List<LibraryItem>>()
    val libraryItems: LiveData<List<LibraryItem>> = _libraryItems

    fun loadLibraryItems(name: String) = viewModelScope.launch {
        _libraryItems.value = repository.getLibraryItems(name)
    }

    // ================= INSERT =================

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        repository.insertNote(note)
    }

    fun insertShopListName(item: ShopListNameItem) = viewModelScope.launch {
        repository.insertShopListName(item)
    }

    fun insertShopItem(item: ShopListItem) = viewModelScope.launch {
        repository.insertShopItem(item)
    }

    fun insertLibraryItem(name: String) = viewModelScope.launch {
        repository.insertLibraryItem(name)
    }

    // ================= UPDATE =================

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        repository.updateNote(note)
    }

    fun updateShopItem(item: ShopListItem) = viewModelScope.launch {
        repository.updateShopItem(item)
    }

    fun updateShopListName(item: ShopListNameItem) = viewModelScope.launch {
        repository.updateShopListName(item)
    }

    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        repository.updateLibraryItem(item)
    }

    // ================= DELETE =================

    fun deleteNote(id: Int) = viewModelScope.launch {
        repository.deleteNote(id)
    }

    fun deleteShopList(id: Int) = viewModelScope.launch {
        repository.deleteShopList(id)
    }

    fun clearShopList(id: Int) = viewModelScope.launch {
        repository.clearShopList(id)
    }

    fun deleteShopListItem(id: Int) = viewModelScope.launch {
        repository.deleteShopListItem(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        repository.deleteLibraryItem(id)
    }

    // ================= BUSINESS =================

    fun toggleShopItem(item: ShopListItem) = viewModelScope.launch {
        repository.toggleItem(item)
    }

    // ================= FACTORY =================

    class MainViewModelFactory(
        private val repository: MainRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED CAST")
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}