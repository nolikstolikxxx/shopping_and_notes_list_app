package com.example.shoppingAndNotesListApp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingAndNotesListApp.data.db.dao.MainDao
import com.example.shoppingAndNotesListApp.data.db.database.MainDataBase
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

/**
 * Instrumentation tests for Room database.
 *
 * These tests verify:
 * 1. Data insertion
 * 2. Data reading
 * 3. Data deletion
 * 4. DAO correctness
 */
@RunWith(AndroidJUnit4::class)
class DataBaseTest {

    private lateinit var db: MainDataBase
    private lateinit var mainDao: MainDao

    @Before
    fun setup() {

        // Create in-memory database for testing
        // This database exists only during test execution
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext() ,
            MainDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        // Initialize DAO
        mainDao = db.getDao()
    }

    @After
    fun close() {

        // Close database after each test
        db.close()
    }

    @Test
    fun insertShopListName_and_read() = runBlocking {

        // ----------------------------
        // Create test item
        // ----------------------------
        val item = ShopListNameItem(
            id = null ,
            name = "Test list" ,
            time = System.currentTimeMillis().toString() ,
            allItemCounter = 0 ,
            checkedItemsCounter = 0 ,
            itemsIds = ""
        )

        // ----------------------------
        // Insert item into database
        // ----------------------------
        mainDao.insertShopListName(item)

        // ----------------------------
        // Read inserted item
        // ----------------------------
        val result = mainDao.getShopListNameByIdOnce(1)

        // ----------------------------
        // Verify insertion result
        // ----------------------------
        Assert.assertNotNull(result)
        Assert.assertEquals("Test list" , result?.name)
    }

    @Test
    fun insert_and_delete() = runBlocking {

        // ----------------------------
        // Create test item
        // ----------------------------
        val item = ShopListNameItem(
            null ,
            "Delete test" ,
            System.currentTimeMillis().toString() ,
            0 ,
            0 ,
            ""
        )

        // ----------------------------
        // Insert item into database
        // ----------------------------
        mainDao.insertShopListName(item)

        // ----------------------------
        // Verify item was inserted
        // ----------------------------
        val inserted = mainDao.getShopListNameByIdOnce(1)

        Assert.assertNotNull(inserted)

        // ----------------------------
        // Delete inserted item
        // ----------------------------
        mainDao.deleteShopListName(1)

        // ----------------------------
        // Verify item was deleted
        // ----------------------------
        val deleted = mainDao.getShopListNameByIdOnce(1)

        Assert.assertNull(deleted)
    }
}