package com.example.shoppingAndNotesListApp.data.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppingAndNotesListApp.data.db.dao.MainDao
import com.example.shoppingAndNotesListApp.data.model.LibraryItem
import com.example.shoppingAndNotesListApp.data.model.NoteItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem

/**
 * Main Room database of the application.
 *
 * Contains all entities and provides access to DAO.
 *
 * Version should be incremented on schema changes.
 */
@Database(
    entities = [
        LibraryItem::class ,
        NoteItem::class ,
        ShopListItem::class ,
        ShopListNameItem::class
    ] ,
    version = 2 ,
    exportSchema = true
)

abstract class MainDataBase : RoomDatabase() {
    /**
     * Provides DAO instance.
     */
    abstract fun getDao(): MainDao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null

        /**
         * Returns singleton instance of database.
         */
        fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext ,
                    MainDataBase::class.java ,
                    "shopping_list.data_base"
                )
                    // WARNING:
                    // Set to true only during development
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}