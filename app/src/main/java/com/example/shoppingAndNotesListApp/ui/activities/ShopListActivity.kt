package com.example.shoppingAndNotesListApp.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.utils.ShareHelper
import com.example.shoppingAndNotesListApp.core.utils.mainViewModel
import com.example.shoppingAndNotesListApp.data.model.LibraryItem
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.example.shoppingAndNotesListApp.databinding.ActivityShopListBinding
import com.example.shoppingAndNotesListApp.ui.adapters.ShopListItemAdapter
import com.example.shoppingAndNotesListApp.ui.dialogs.DeleteDialog
import com.example.shoppingAndNotesListApp.ui.dialogs.EditListItemDialog

/**
 * Activity for managing a single shopping list.
 *
 * Responsibilities:
 * - Displays list items
 * - Handles adding/editing/deleting items
 * - Provides search suggestions from Library
 *
 * Architecture:
 * - Works with MainViewModel (MVVM)
 * - Uses RecyclerView + ListAdapter
 *
 * Important behavior:
 * - Two modes: LIST MODE and SEARCH MODE
 * - Library items are temporary and NOT bound to a list
 *
 * NOTE:
 * - listId is passed via Intent
 * - Library items must always have listId = -1
 */
class ShopListActivity : BaseActivity() , ShopListItemAdapter.Listener {

    private lateinit var binding: ActivityShopListBinding
    private var adapter: ShopListItemAdapter? = null

    private lateinit var newItemMenu: MenuItem
    private lateinit var saveItem: MenuItem
    private var edItem: EditText? = null

    private lateinit var textWatcher: TextWatcher
    private var pref: SharedPreferences? = null

    private var listId: Int = -1

    private val mainViewModel by mainViewModel()

    /*// ================= MODE =================

    */
    /**
     * True -> showing library suggestions
     * False -> showing real list items
     *//*
    private var isSearchMode = false*/

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate an instance at $this")

        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get listId from Intent
        listId = intent.getIntExtra(SHOP_LIST_ID , -1)

        pref = PreferenceManager.getDefaultSharedPreferences(this)

        setupToolbar()
        setupRecyclerView()
        observeListItems()
    }

    // ================= UI SETUP =================

    /**
     * Toolbar setup with back navigation
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Initializes RecyclerView and adapter
     */
    private fun setupRecyclerView() {
        adapter = ShopListItemAdapter(this)

        binding.rcView.apply {
            layoutManager = LinearLayoutManager(this@ShopListActivity)
            // Disable animations (important for fixes test/UI glitches)
            itemAnimator = null
            adapter = this@ShopListActivity.adapter
        }
    }

    // ================= MENU =================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu , menu)

        saveItem = menu?.findItem(R.id.action_save_item)!!

        newItemMenu = menu.findItem(R.id.action_new_item)

        edItem = newItemMenu.actionView!!.findViewById(R.id.edNewShopItem)!!

        newItemMenu.setOnActionExpandListener(expandActionView())

        saveItem.isVisible = false

        textWatcher = createTextWatcher()

        return true
    }

    /**
     * Handles toolbar menu actions
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }

            R.id.action_save_item -> {
                addNewShopItem(edItem?.text.toString())

                // Collapse ActionView after save
                newItemMenu.collapseActionView()
            }

            R.id.delete_list -> {
                mainViewModel.deleteShopList(listId)
                finish()
            }

            R.id.clear_list -> {
                mainViewModel.clearShopList(listId)
            }

            R.id.share_list -> {
                startActivity(
                    Intent.createChooser(
                        ShareHelper.shareShopList(
                            adapter?.currentList!! , "Shopping List"
                        ) , getString(R.string.share_by)
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // ================= TEXT WATCHER =================

    /**
     * TextWatcher for search input
     *
     * Triggers library search with LIKE query
     */
    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence? , start: Int , count: Int , after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence? ,
                start: Int ,
                before: Int ,
                count: Int
            ) {

                Log.d(TAG , "on Text Changed: $s")

                mainViewModel.loadLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    // ================= DATA OBSERVERS =================

    /**
     * Observes real list items (LIST MODE)
     */

    private fun observeListItems() {
        mainViewModel.getShopListItems(listId).observe(this) { items ->
            adapter?.submitList(items)

            binding.tvEmpty.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    /**
     * Observes library suggestions (SEARCH MODE)
     *
     * Converts LibraryItem → ShopListItem (temporary)
     *
     * IMPORTANT:
     * - listId = -1 → prevents leaking into real lists
     * - itemType = 1 → marks as library item
     */
    private fun observeLibraryItems() {
        mainViewModel.libraryItems.observe(this) { libraryList ->

            val tempList = libraryList.map { libItem ->
                ShopListItem(
                    id = libItem.id ,
                    name = libItem.name ,
                    itemInfo = "" ,
                    itemChecked = false ,
                    listId = listId , // ❗ critical
                    itemType = 1 // mark as library item
                )
            }

            adapter?.submitList(tempList)

            binding.tvEmpty.visibility =
                if (tempList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // ================= ACTION VIEW =================

    /**
     * Handles search view expand/collapse
     *
     * Switches between:
     * - LIST MODE
     * - SEARCH MODE
     */
    private fun expandActionView(): MenuItem.OnActionExpandListener {
        return object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true

                edItem?.addTextChangedListener(textWatcher)

                observeLibraryItems()

                mainViewModel.loadLibraryItems("%%")

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false

                edItem?.removeTextChangedListener(textWatcher)

                invalidateOptionsMenu() // important for stable UI

                edItem?.setText("")

                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)

                return true
            }
        }
    }

    // ================= ADD ITEM =================

    /**
     * Adds new item to current shopping list
     *
     * Also inserts into Library (if not exists)
     */
    private fun addNewShopItem(name: String) {
        if (name.isBlank()) return

        val item = ShopListItem(
            id = null ,
            name = name ,
            itemInfo = "" ,
            itemChecked = false ,
            listId = listId ,
            itemType = 0
        )

        edItem?.setText("")

        mainViewModel.insertShopItem(item)

        // keep library in sync
        mainViewModel.insertLibraryItem(name)
    }

    // ================= ADAPTER CALLBACK =================

    /**
     * Handles adapter interactions
     */
    override fun onClickItem(item: ShopListItem , state: Int) {

        // Verify that the correct item is passed to the ViewModel for processing
        Log.d(
            "CHECK" ,
            "CLICK item=${item.name}, checked=${item.itemChecked}, state=$state"
        )

        when (state) {

            ShopListItemAdapter.CHECK_BOX ->
                mainViewModel.toggleShopItem(item)

            ShopListItemAdapter.ADD_ITEM ->
                addNewShopItem(item.name)

            ShopListItemAdapter.EDIT ->
                editListItem(item)

            ShopListItemAdapter.EDIT_LIBRARY_ITEM ->
                editLibraryItem(item)

            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(item.id!!)
                mainViewModel.loadLibraryItems("%${edItem?.text.toString()}%")
            }
        }
    }

    /**
     * Deletes item from list (with confirmation dialog)
     */
    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(this , object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteShopListItem(id)
            }
        })
    }

    // ================= EDIT =================

    /**
     * Edits item in list AND syncs with library
     */
    private fun editListItem(item: ShopListItem) {
        EditListItemDialog.showDialog(
            this ,
            item ,
            object : EditListItemDialog.Listener {

                override fun onClick(item: ShopListItem) {

                    // update list
                    mainViewModel.updateShopItem(item)

                    // sync with library
                    mainViewModel.updateLibraryItem(LibraryItem(item.id , item.name))
                }
            })
    }

    /**
     * Edits item only in library (not in list)
     */
    private fun editLibraryItem(item: ShopListItem) {
        EditListItemDialog.showDialog(
            this ,
            item ,
            object : EditListItemDialog.Listener {

                override fun onClick(item: ShopListItem) {
                    mainViewModel.updateLibraryItem(
                        LibraryItem(item.id , item.name)
                    )

                    mainViewModel.loadLibraryItems("%${edItem?.text.toString()}%")
                }
            })
    }

    // ================= DEBUG LIFECYCLE =================

    override fun onStart() {
        super.onStart()
        Log.d(TAG , "onStart an instance at $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG , "onResume an instance at $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG , "onPause an instance at $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG , "onStop an instance at $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG , "onDestroy an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }
    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "ShopListActivity"
        const val SHOP_LIST_ID = "shop_list_id"
    }
}