package com.example.shoppingAndNotesListApp.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.databinding.ShopLibraryListItemBinding
import com.example.shoppingAndNotesListApp.databinding.ShopListItemBinding
import com.example.shoppingAndNotesListApp.data.model.ShopListItem
import com.google.android.material.R as MaterialR

/**
 * Adapter for displaying shopping list items and library suggestions.
 *
 * Supports two view types:
 * 1) SHOP ITEM (itemType = 0)
 *    - Real item inside a shopping list
 *    - Has checkbox, edit, delete
 *
 * 2) LIBRARY ITEM (itemType = 1)
 *    - Suggestion from Library (search mode)
 *    - Can be added to list, edited, or deleted from library
 *
 * Architecture notes:
 * - Works with a single model (ShopListItem)
 * - Library items are temporary (listId = -1)
 * - Adapter contains ONLY UI logic (no DB access)
 *
 * Performance:
 * - Uses DiffUtil
 * - Stable IDs enabled
 */
class ShopListItemAdapter(private val listener: Listener) :
    ListAdapter<ShopListItem , ShopListItemAdapter.ItemHolder>(ItemHolder.ItemComparator()) {

    init {
        setHasStableIds(true)
    }

    /**
     * Stable IDs improve RecyclerView animations and performance
     */
    override fun getItemId(position: Int): Long {
        return getItem(position).id?.toLong() ?: RecyclerView.NO_ID
    }

    /**
     * Defines which layout to use (shop vs library)
     */
    override fun getItemViewType(position: Int): Int = getItem(position).itemType

    /**
     * Creates ViewHolder based on item type
     */
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ItemHolder {
        return if (viewType == TYPE_SHOP) ItemHolder.createShopItem(parent)
        else ItemHolder.createLibraryItem(parent)
    }

    /**
     * Binds data depending on item type
     */
    override fun onBindViewHolder(holder: ItemHolder , position: Int) {
        val item = getItem(position)
        if (item.itemType == TYPE_SHOP) holder.bindShopItem(item , listener)
        else holder.bindLibraryItem(item , listener)
    }

    // ================= VIEW HOLDER =================

    class ItemHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        /**
         * Binds REAL shopping list item
         *
         * Features:
         * - Checkbox toggle
         * - Strike-through when checked
         * - Edit/Delete actions
         */
        fun bindShopItem(item: ShopListItem , listener: Listener) {
            val binding = ShopListItemBinding.bind(view)

            // Verify if itemChecked is updated in the adapter from the database
            Log.d("CHECK" , "BIND item=${item.name}, checked=${item.itemChecked}")


            binding.apply {
                tvName.text = item.name
                tvInfo.text = item.itemInfo

                tvInfo.visibility = if (item.itemInfo.isEmpty()) View.GONE else View.VISIBLE

                checkBox.isChecked = item.itemChecked

                applyCheckedStateStyle(binding , item , binding.root.context)

                // ================= CLICK =================

                // Checkbox animation + toggle
                checkBox.setOnClickListener {
                    it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                        .withEndAction {
                            it.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        }
                    listener.onClickItem(item , CHECK_BOX)
                }

                ibEdit.setOnClickListener {
                    listener.onClickItem(item , EDIT)
                }

                ibDelete.setOnClickListener {
                    listener.deleteItem(item.id!!)
                }
            }
        }

        /**
         * Binds LIBRARY item (search suggestion)
         *
         * Behavior:
         * - Click → add to list
         * - Edit → updates library
         * - Delete → removes from library
         */
        fun bindLibraryItem(item: ShopListItem , listener: Listener) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = item.name

                // ================= CLICK =================

                ibEdit.setOnClickListener {
                    listener.onClickItem(item , EDIT_LIBRARY_ITEM)
                }
                ibDelete.setOnClickListener {
                    listener.onClickItem(item , DELETE_LIBRARY_ITEM)
                }

                // Important: disable default click
                itemView.setOnClickListener(null)

                root.setOnClickListener {
                    listener.onClickItem(item , ADD_ITEM)
                }
            }
        }

        /**
         * Applies visual style for checked/unchecked state
         *
         * - Strike-through text
         * - Changes text color via Material attributes
         */
        private fun applyCheckedStateStyle(
            binding: ShopListItemBinding ,
            item: ShopListItem ,
            context: Context
        ) {
            val typedValue = TypedValue()

            if (item.itemChecked) {
                binding.tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                context.theme.resolveAttribute(
                    MaterialR.attr.colorSecondary ,
                    typedValue ,
                    true
                )
            } else {
                binding.tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                binding.tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG

                context.theme.resolveAttribute(
                    MaterialR.attr.colorOnSurface ,
                    typedValue ,
                    true
                )
            }

            val color = typedValue.data
            binding.tvName.setTextColor(color)
            binding.tvInfo.setTextColor(color)
        }

        companion object {
            /**
             * Creates ViewHolder for shop item layout
             */
            fun createShopItem(parent: ViewGroup) = ItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.shop_list_item ,
                        parent ,
                        false
                    )
            )

            /**
             * Creates ViewHolder for library item layout
             */
            fun createLibraryItem(parent: ViewGroup) = ItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.shop_library_list_item ,
                        parent ,
                        false
                    )
            )
        }

        // ================= DIFF =================

        class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
            override fun areItemsTheSame(
                oldItem: ShopListItem ,
                newItem: ShopListItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ShopListItem ,
                newItem: ShopListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    // ================= LISTENER =================

    interface Listener {
        fun onClickItem(item: ShopListItem , state: Int)
        fun deleteItem(id: Int)
    }

    companion object {
        const val TYPE_SHOP = 0

        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD_ITEM = 4
    }
}