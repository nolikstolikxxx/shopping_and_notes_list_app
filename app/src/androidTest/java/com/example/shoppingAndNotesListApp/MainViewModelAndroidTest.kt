package com.example.shoppingAndNotesListApp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.data.db.database.MainDataBase
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import com.example.shoppingAndNotesListApp.data.repository.MainRepository
import com.example.shoppingAndNotesListApp.ui.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for MainViewModel.
 *
 * These tests verify:
 * 1. Note insertion
 * 2. Shop list insertion
 * 3. Shop item state updates
 * 4. LiveData correctness
 * 5. Repository + Room interaction
 */
@RunWith(AndroidJUnit4::class)
class MainViewModelAndroidTest {

    /**
     * Executes LiveData instantly.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: MainDataBase
    private lateinit var repository: MainRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {

        // Create in-memory database
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context ,
            MainDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        // Initialize repository and ViewModel
        repository = MainRepository(db.getDao())
        viewModel = MainViewModel(repository)
    }

    @After
    fun teardown() {

        // Close database
        db.close()
    }

    // ============================================================
    // NOTE TESTS
    // ============================================================

    @Test
    fun insertNote_and_readSuccessfully() = runBlocking {

        // ----------------------------
        // Create test note
        // ----------------------------
        val note = NoteItem(
            id = 0 ,
            title = "Test Note" ,
            content = "Some content" ,
            time = "2026-02-24" ,
            category = "General"
        )

        // ----------------------------
        // Insert note
        // ----------------------------
        viewModel.insertNote(note)

        // ----------------------------
        // Read notes from LiveData
        // ----------------------------
        val notes = viewModel.allNotes.getOrAwaitValue()

        // ----------------------------
        // Verify insertion result
        // ----------------------------
        Assert.assertEquals(1 , notes.size)
        Assert.assertEquals("Test Note" , notes[0].title)
    }

    // ============================================================
    // SHOP LIST TESTS
    // ============================================================

    @Test
    fun insertShopListName_and_readSuccessfully() = runBlocking {

        // ----------------------------
        // Create test shop list
        // ----------------------------
        val listName = ShopListNameItem(
            id = 0 ,
            name = "Shopping List 1" ,
            time = "2026-02-24" ,
            allItemCounter = 0 ,
            checkedItemsCounter = 0 ,
            itemsIds = "0"
        )

        // ----------------------------
        // Insert shop list
        // ----------------------------
        viewModel.insertShopListName(listName)

        // ----------------------------
        // Read shop lists from LiveData
        // ----------------------------
        val lists = viewModel.allShopListNames.getOrAwaitValue()

        // ----------------------------
        // Verify insertion result
        // ----------------------------
        Assert.assertEquals(1 , lists.size)
        Assert.assertEquals("Shopping List 1" , lists[0].name)
    }

    // ============================================================
    // SHOP ITEM TOGGLE TESTS
    // ============================================================

    @Test
    fun toggleShopItem_updatesCheckedState() = runBlocking {

        // ----------------------------
        // Create shop list
        // ----------------------------
        val list = ShopListNameItem(
            id = 0 ,
            name = "Shopping List 1" ,
            time = "2026-02-24" ,
            allItemCounter = 0 ,
            checkedItemsCounter = 0 ,
            itemsIds = "0"
        )

        viewModel.insertShopListName(list)

        // ----------------------------
        // Create shop item
        // ----------------------------
        val item = ShopListItem(
            id = 0 ,
            listId = 1 ,
            name = "Item 1" ,
            itemChecked = false
        )

        viewModel.insertShopItem(item)

        // ----------------------------
        // Toggle item state
        // ----------------------------
        viewModel.toggleShopItem(item)

        // ----------------------------
        // Verify updated state
        // ----------------------------
        val items = viewModel
            .getShopListItems(1)
            .getOrAwaitValue()

        Assert.assertTrue(items[0].itemChecked)
    }
}