package com.example.shoppingAndNotesListApp.core.utils

import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Utility object for date and time formatting.
 *
 * Responsibilities:
 * - Provides current time
 * - Converts stored time into user-selected format
 * - Handles formatting safely
 *
 * Architecture notes:
 * - Used by Notes UI layer
 * - Stateless utility object
 * - Locale-aware formatting
 */
object TimeManager {

    // ================= CONSTANTS =================

    /**
     * Default internal date format.
     *
     * Used for:
     * - storing timestamps
     * - parsing saved dates
     */
    private const val DEF_TIME_FORMAT = "hh:mm:ss - dd/MM/yyyy"

    /**
     * Log tag for debugging.
     */
    private const val TAG = "TimeManager"

    // ================= CURRENT TIME =================

    /**
     * Returns current date/time
     * in default internal format.
     *
     * Example:
     * 09:45:12 - 07/05/2026
     */
    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat(DEF_TIME_FORMAT , Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    // ================= TIME FORMAT =================

    /**
     * Converts stored date string
     * into user-selected format.
     *
     * Used mainly in Notes Adapter.
     *
     * Flow:
     * 1. Parse internal date format
     * 2. Read user format from SharedPreferences
     * 3. Return formatted result
     *
     * Fallback:
     * Returns original string if parsing fails.
     */
    fun getTimeFormat(
        time: String ,
        defPreferences: SharedPreferences
    ): String {

        return try {

            // ================= PARSE DEFAULT FORMAT =================

            val defFormatter = SimpleDateFormat(DEF_TIME_FORMAT , Locale.getDefault())
            val defDate = defFormatter.parse(time)

            // ================= LOAD USER FORMAT =================

            val newFormat = defPreferences.getString(
                "time_format_key" ,
                DEF_TIME_FORMAT
            ) ?: DEF_TIME_FORMAT

            // ================= FORMAT RESULT =================

            val newFormatter = SimpleDateFormat(newFormat , Locale.getDefault())

            if (defDate != null) newFormatter.format(defDate) else time

        } catch (e: Exception) {

            // ================= ERROR HANDLING =================

            Log.e(TAG , "Error parsing time: $time" , e)

            time
        }
    }
}