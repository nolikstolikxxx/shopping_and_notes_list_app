package com.example.shoppingAndNotesListApp.core.preferences

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.preference.ListPreference
import com.example.shoppingAndNotesListApp.R

/**
 * Custom ListPreference with dynamic icon support.
 *
 * Features:
 * - Changes Preference icon depending on selected value
 * - Supports icon arrays from XML
 *
 * Usage:
 * - Define app:iconEntries in XML
 * - Icon index must match entries/value index
 *
 * Example:
 * entries[0] -> icon[0]
 * entries[1] -> icon[1]
 */
class IconListPreference(
    context: Context ,
    attrs: AttributeSet?
) : ListPreference(context , attrs) {

    /**
     * Resource ID of drawable array
     * from app:iconEntries
     */
    private var iconArrayResId: Int = 0

    // ================= INIT =================

    init {
        /**
         * Read custom XML attributes.
         *
         * Example:
         * app:iconEntries="@array/theme_icons"
         */
        context.withStyledAttributes(
            attrs ,
            R.styleable.IconListPreference
        ) {

            iconArrayResId = getResourceId(
                R.styleable.IconListPreference_iconEntries ,
                0
            )
        }
    }

    // ================= ICON HANDLING =================

    /**
     * Returns icon based on currently selected value.
     *
     * Logic:
     * 1. Find selected value index
     * 2. Get drawable from icon array
     * 3. Return matching icon
     */
    override fun getIcon(): Drawable? {

        // Fallback to default icon
        if (iconArrayResId == 0) return super.getIcon()

        // Current selected item index
        val index = findIndexOfValue(value)

        // Invalid index fallback
        if (index < 0) return super.getIcon()

        // Load icon array
        val icons = context.resources.obtainTypedArray(iconArrayResId)

        val drawable = icons.getDrawable(index)

        // Prevent TypedArray memory leak
        icons.recycle()

        return drawable ?: super.getIcon()
    }
}