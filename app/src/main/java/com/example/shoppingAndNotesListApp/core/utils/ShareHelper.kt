package com.example.shoppingAndNotesListApp.core.utils

import android.content.Intent
import com.example.shoppingAndNotesListApp.data.model.ShopListItem

/**
 * Helper object for sharing shopping lists.
 *
 * Features:
 * - Converts shopping list into formatted text
 * - Creates ACTION_SEND intent
 * - Supports system share menu
 *
 * Common use cases:
 * - Share via Messenger
 * - Share via Telegram
 * - Copy to Notes / Email
 */
object ShareHelper {

    // ================= SHARE INTENT =================

    /**
     * Create share Intent with formatted shopping list.
     *
     * @param shopList List of shopping items
     * @param listName Shopping list title
     */
    fun shareShopList(shopList: List<ShopListItem> , listName: String): Intent {

        val intent = Intent(Intent.ACTION_SEND)

        // Shared content type
        intent.type = "text/plane"

        intent.apply {

            putExtra(Intent.EXTRA_TEXT , makeShareText(shopList , listName))

        }
        return intent
    }

    // ================= TEXT GENERATION =================

    /**
     * Convert shopping list into readable text.
     *
     * Example:
     *
     * <<Groceries>>
     * 1 - Milk
     * 2 - Bread (2 pcs)
     * 3 - Apples
     */
    private fun makeShareText(shopList: List<ShopListItem> , listName: String): String {

        val sBuilder = StringBuilder()

        // List title
        sBuilder.append("<<$listName>>")
        sBuilder.append("\n")

        var counter = 0

        shopList.forEach {

            // Item without additional info
            if (it.itemInfo.isEmpty()) {

                sBuilder.append("${++counter} - ${it.name}")
                sBuilder.append("\n")

            } else {

                // Item with additional info
                sBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")

                sBuilder.append("\n")

            }
        }
        return sBuilder.toString()
    }
}