package com.example.shoppingAndNotesListApp.core.utils

//noinspection SuspiciousImport
import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap

/**
 * Smooth theme transition animation.
 *
 * Creates a temporary screenshot overlay of the current screen
 * and fades it out after theme recreation.
 *
 * Result:
 * - Prevents sharp "flash" during theme change
 * - Makes UI transition visually smoother
 * - Improves UX when switching themes dynamically
 *
 * Usage:
 * Call before recreate():
 *
 * animateThemeChange()
 * recreate()
 */
fun Activity.animateThemeChange() {

    // ================= ROOT VIEW =================

    /**
     * Get Activity root container.
     *
     * android.R.id.content =
     * main content container of the Activity.
     */
    val rootView = findViewById<ViewGroup>(R.id.content)

    // ================= SCREENSHOT =================

    /**
     * Create bitmap snapshot of current UI.
     *
     * drawToBitmap() captures the current screen state.
     */
    val bitmap = rootView.drawToBitmap()

    // ================= OVERLAY =================

    /**
     * Create temporary overlay view
     * using captured bitmap.
     */
    val overlay = View(this)

    overlay.background = bitmap.toDrawable(resources)

    // ================= ATTACH OVERLAY =================

    /**
     * Add overlay above current UI.
     *
     * Overlay fully covers the screen.
     */
    rootView.addView(
        overlay ,
        ViewGroup.LayoutParams.MATCH_PARENT ,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    // ================= FADE ANIMATION =================

    /**
     * Fade overlay out smoothly.
     *
     * After animation ends:
     * remove overlay from hierarchy.
     */
    overlay.animate()
        .alpha(0f)
        .setDuration(250L)
        .withEndAction { rootView.removeView(overlay) }
        .start()
}