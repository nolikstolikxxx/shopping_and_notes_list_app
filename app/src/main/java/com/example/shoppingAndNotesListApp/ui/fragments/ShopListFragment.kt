package com.example.shoppingAndNotesListApp.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.core.utils.TimeManager
import com.example.shoppingAndNotesListApp.core.utils.mainViewModel
import com.example.shoppingAndNotesListApp.data.model.ShopListNameItem
import com.example.shoppingAndNotesListApp.databinding.FragmentShopListBinding
import com.example.shoppingAndNotesListApp.ui.activities.MainActivity
import com.example.shoppingAndNotesListApp.ui.activities.ShopListActivity
import com.example.shoppingAndNotesListApp.ui.adapters.ShopListAdapter
import com.example.shoppingAndNotesListApp.ui.dialogs.DeleteDialog
import com.example.shoppingAndNotesListApp.ui.dialogs.TextInputDialogFragment
import kotlinx.coroutines.launch

/**
 * Fragment displaying all shopping lists.
 *
 * Responsibilities:
 * - Show list of ShopListNameItem with counters
 * - Create new list
 * - Edit existing list
 * - Delete or clear list
 * - Navigate to ShopListActivity
 *
 * Architecture:
 * - Uses MVVM (MainViewModel)
 * - Observes LiveData from ViewModel
 * - Does NOT access database directly
 *
 * UI:
 * - RecyclerView + ShopListAdapter
 * - Dialogs for create/edit actions
 */
class ShopListFragment : BaseFragment() , ShopListAdapter.Listener {
    private lateinit var binding: FragmentShopListBinding
    private lateinit var adapter: ShopListAdapter
    private lateinit var defPref: SharedPreferences

    private val mainViewModel by mainViewModel()

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate an instance at $this")
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListBinding.inflate(
            inflater ,
            container ,
            false
        )

        Log.d(TAG , "onCreateView an instance at $this")
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        Log.d(TAG , "onViewCreated an instance at $this")

        initRecyclerView()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG , "onDestroyView an instance at $this")
    }

    // ================= INIT =================

    /**
     * Initializes RecyclerView and adapter
     */
    private fun initRecyclerView() = with(binding) {
        defPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        adapter = ShopListAdapter(this@ShopListFragment , defPref)

        rcView.layoutManager = LinearLayoutManager(requireContext())

        // Disable animations (important for tests stability)
        rcView.itemAnimator = null

        rcView.adapter = adapter

        (activity as? MainActivity)?.updateNavigationUI("ShopList")

        Log.d(TAG , "initRecyclerView an instance at $this")
    }

    // ================= OBSERVERS =================

    /**
     * Observes shopping lists with counters
     */
    private fun observeData() {
        mainViewModel.shopListsWithCounters.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            Log.d(TAG , "observer an instance at $this")
        }
    }

    // ================= CREATE =================

    /**
     * Called from BaseFragment
     *
     * Opens dialog to create new list
     */
    override fun onClickNew() {
        val fm = parentFragmentManager

        // Remove previous dialog if exists (avoid duplicates)
        val prev = fm.findFragmentByTag("NewListDialog")
        if (prev != null) {
            fm.beginTransaction().remove(prev).commitNow()
        }

        Log.d(
            "DIALOG_TEST" ,
            "prev dialog = ${fm.findFragmentByTag("NewListDialog")}"
        )

        val dialog = TextInputDialogFragment.newInstance(
            title = getString(R.string.new_list) ,
            hint = getString(R.string.list_name)
        )

        dialog.setListener { name ->

            val newList = ShopListNameItem(
                null ,
                name ,
                TimeManager.getCurrentTime() ,
                0 ,
                0 ,
                ""
            )

            mainViewModel.insertShopListName(newList)
        }

        dialog.show(fm , "EditListDialog")
    }

    // ================= DELETE =================

    /**
     * Deletes ALL items inside list (not the list itself)
     */
    override fun deleteItem(id: Int) {
        (activity as? AppCompatActivity)?.let {
            DeleteDialog.showDialog(
                it ,
                object : DeleteDialog.Listener {
                    override fun onClick() {
                        mainViewModel.deleteShopList(id)
                    }
                })
        }
    }

    // ================= EDIT =================

    /**
     * Edits list name
     *
     * Uses one-time data fetch to avoid multiple LiveData observers
     */

    override fun editItem(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {

            val listItem = mainViewModel.getShopListNameByIdOnce(id)

            listItem?.let {

                val fm = parentFragmentManager

                // remove previous dialog if exists
                fm.findFragmentByTag("EditListDialog")?.let { prev ->
                    fm.beginTransaction().remove(prev).commitNow()
                }

                TextInputDialogFragment
                    .newInstance(
                        title = getString(R.string.update_list) ,
                        hint = getString(R.string.list_name) ,
                        value = listItem.name
                    )
                    .apply {
                        setListener { newName ->
                            mainViewModel.updateShopListName(
                                listItem.copy(name = newName)
                            )
                        }
                    }
                    .show(fm , "EditListDialog")
            }
        }
    }

    // ================= NAVIGATION =================

    /**
     * Opens selected shopping list
     */
    override fun onClickItem(id: Int) {
        val intent = Intent(
            requireContext() ,
            ShopListActivity::class.java
        ).apply {

            putExtra(ShopListActivity.SHOP_LIST_ID , id)
        }
        startActivity(intent)
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

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG , "onDetach an instance at $this")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG , "onViewStateRestored an instance at $this")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG , "onSaveInstanceState an instance at $this")
    }

    // ================= CONSTANTS =================

    companion object {
        private const val TAG = "ShopListNamesFragment"
    }
}