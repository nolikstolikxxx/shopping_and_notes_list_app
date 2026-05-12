package com.example.shoppingAndNotesListApp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingAndNotesListApp.R
import com.example.shoppingAndNotesListApp.databinding.ShopListNameItemBinding
import com.example.shoppingAndNotesListApp.data.model.ShopListWithCounters
import com.example.shoppingAndNotesListApp.core.utils.TimeManager
import com.google.android.material.R as MaterialR

/**
 * Adapter for displaying shopping lists (names with counters).
 *
 * Shows:
 * - List name
 * - Creation time
 * - Progress (checked / total items)
 *
 * Features:
 * - Uses Material3 colors for progress state
 * - Supports edit/delete actions
 * - Uses stable IDs for better performance
 *
 * NOTE:
 * - Works with ShopListWithCounters (joined DB model)
 * - Does not contain any business logic (UI only)
 */
class ShopListAdapter(
    private val listener: Listener ,
    private val defPref: SharedPreferences
) : ListAdapter<ShopListWithCounters , ShopListAdapter.ItemHolder>(ItemComparator()) {

    init {
        setHasStableIds(true)
    }

    /**
     * Stable IDs improve RecyclerView animations
     */
    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.shop_list_name_item ,
                parent ,
                false
            )
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder , position: Int) {
        holder.bind(getItem(position) , listener , defPref)
    }

    // ================= VIEW HOLDER =================

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ShopListNameItemBinding.bind(view)

        /**
         * Binds data to UI
         *
         * Handles:
         * - Text (name, time)
         * - Progress state
         * - Click actions
         */
        @SuppressLint("SetTextI18n")
        fun bind(
            item: ShopListWithCounters ,
            listener: Listener ,
            defPref: SharedPreferences
        ) = with(binding) {

            // ================= TEXT =================

            tvListName.text = item.name
            tvTime.text = TimeManager.getTimeFormat(item.time , defPref)

            // ================= PROGRESS =================

            progressBar.max = item.allItemCounter
            progressBar.progress = item.checkedItemsCounter

            // Material3 tokens for the progress bar
            val isCompleted = item.allItemCounter > 0 &&
                    item.allItemCounter == item.checkedItemsCounter
            val color = if (isCompleted) {
                getColorFromAttr(root.context , MaterialR.attr.colorPrimary)
            } else {
                getColorFromAttr(root.context , MaterialR.attr.colorError)
            }

            val colorState = ColorStateList.valueOf(color)

            progressBar.progressTintList = colorState
            counterCard.backgroundTintList = colorState

            tvCounter.text = "${item.checkedItemsCounter} / ${item.allItemCounter}"

            // ================= CLICK =================

            root.setOnClickListener {
                listener.onClickItem(item.id)
            }

            btnDeleteList.setOnClickListener {
                listener.deleteItem(item.id)
            }

            btnEditList.setOnClickListener {
                listener.editItem(item.id)
            }
        }

        /**
         * Resolves color from theme attribute
         */
        private fun getColorFromAttr(context: Context , @AttrRes attr: Int): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                attr ,
                typedValue ,
                true
            )
            return typedValue.data
        }
    }
    // ================= DIFF =================

    class ItemComparator : DiffUtil.ItemCallback<ShopListWithCounters>() {
        override fun areItemsTheSame(
            oldItem: ShopListWithCounters ,
            newItem: ShopListWithCounters
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShopListWithCounters ,
            newItem: ShopListWithCounters
        ): Boolean = oldItem == newItem
    }

    // ================= LISTENER =================

    interface Listener {
        fun deleteItem(id: Int)
        fun editItem(id: Int)
        fun onClickItem(id: Int)
    }
}