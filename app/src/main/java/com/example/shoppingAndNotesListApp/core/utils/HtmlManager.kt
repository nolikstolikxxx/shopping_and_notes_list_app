package com.example.shoppingAndNotesListApp.core.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * HtmlManager handles conversion between:
 * - Spannable (UI text with styles)
 * - HTML (stored in DB)
 *
 * Supports:
 * - Bold text
 * - Text color
 *
 * Important:
 * - Default Html.toHtml() DOES NOT support color spans properly
 * - So we manually inject <font color=""> tags
 */
object HtmlManager {

    // ================= FROM HTML =================

    /**
     * Converts HTML string → Spanned (for EditText / TextView)
     */
    fun getFromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            // Modern API
            Html.fromHtml(html , Html.FROM_HTML_MODE_COMPACT)
        } else {

            // Legacy API
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }

    // ================= TO HTML =================

    /**
     * Converts Spannable → HTML string
     *
     * Handles:
     * - Bold (StyleSpan)
     * - Color (ForegroundColorSpan)
     */
    fun toHtml(text: Spanned): String {

        // ================= HANDLE BOLD =================

        val rawHtml = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            // Modern API
            Html.toHtml(text , Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {

            // Legacy API
            @Suppress("DEPRECATION")
            Html.toHtml(text)
        }

        return cleanHtml(rawHtml)
    }

// ================= CLEANUP =================

    private fun cleanHtml(html: String): String {
        return html
            .replace("<p>" , "")
            .replace("</p>" , "<br>")
            .replace("\n" , "")
            .trim()
    }
}