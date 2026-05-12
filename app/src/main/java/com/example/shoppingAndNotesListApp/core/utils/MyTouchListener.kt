package com.example.shoppingAndNotesListApp.core.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * Simple drag-and-drop touch listener.
 *
 * Features:
 * - Allows moving any View by touch
 * - Tracks finger position
 * - Updates View coordinates dynamically
 *
 * Usage:
 * view.setOnTouchListener(MyTouchListener())
 *
 * Common use cases:
 * - Floating panels
 * - Draggable buttons
 * - Custom movable UI elements
 */
class MyTouchListener : OnTouchListener {

    /**
     * Difference between View position
     * and finger position on ACTION_DOWN.
     */
    private var xDelta = 0.0f
    private var yDelta = 0.0f

    // ================= TOUCH HANDLING =================

    /**
     * Handle touch events for dragging View.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View , event: MotionEvent?): Boolean {

        when (event?.action) {

            // ================= TOUCH START =================

            MotionEvent.ACTION_DOWN -> {

                /**
                 * Save offset between:
                 * - View coordinates
                 * - Finger coordinates
                 */
                xDelta = v.x - event.rawX
                yDelta = v.y - event.rawY
            }

            // ================= DRAGGING =================

            MotionEvent.ACTION_MOVE -> {

                /**
                 * Update View position
                 * while finger moves.
                 */
                v.x = xDelta + event.rawX
                v.y = yDelta + event.rawY
            }
        }

        /**
         * Return true to consume touch events.
         */
        return true
    }
}