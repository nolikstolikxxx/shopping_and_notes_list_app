package com.example.shoppingAndNotesListApp.core.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.example.shoppingAndNotesListApp.R

/**
 * Handles Google Play Billing integration.
 *
 * Features:
 * - One-time purchases (INAPP)
 * - Remove Ads purchase
 * - Purchase acknowledgment
 * - Purchase state persistence
 *
 * Architecture notes:
 * - Works independently of UI screens
 * - Uses SharedPreferences for purchase state
 * - Google Billing v6+
 */
class BillingManager(private val activity: AppCompatActivity) {
    private var bClient: BillingClient? = null

    // ================= INIT =================

    init {
        setUpBillingClient()
    }

    /**
     * Initialize BillingClient.
     *
     * Required for:
     * - Google Play connection
     * - Purchase flow
     * - Purchase callbacks
     */
    private fun setUpBillingClient() {

        val pendingPurchasesParams =
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()

        bClient = BillingClient.newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases(pendingPurchasesParams)
            .build()
    }


    // ================= CONNECTION =================

    /**
     * Connect to Google Play Billing service.
     */
    fun startConnection() {

        bClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {

                /**
                 * Called when connection is lost.
                 *
                 * You may add retry logic here later.
                 */
            }

            override fun onBillingSetupFinished(
                billingResult: BillingResult
            ) {

                if (billingResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                ) {

                    // Billing ready → request product info
                    getItem()
                }
            }
        })
    }

    /**
     * Close Billing connection.
     *
     * Important to prevent leaks.
     */
    fun closeConnection() {
        bClient?.endConnection()
    }

    // ================= PRODUCT QUERY =================

    /**
     * Request product details from Google Play.
     *
     * Product type:
     * - INAPP → one-time purchase
     * - SUBS → subscription
     */
    private fun getItem() {

        val productList = listOf(

            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_AD_ITEM)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        bClient?.queryProductDetailsAsync(params) { billingResult , productDetailsList ->

            if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {

                @Suppress("CAST_NEVER_SUCCEEDS")
                val detailsList =
                    (productDetailsList as? List<ProductDetails>)
                        ?: emptyList()

                val productDetails =
                    detailsList.firstOrNull {
                        it.productId == REMOVE_AD_ITEM
                    }

                // Product not found
                if (productDetails == null) {

                    return@queryProductDetailsAsync
                }

                /**
                 * Offer token required only for subscriptions.
                 *
                 * INAPP products do not need it.
                 */
                val offerToken: String? =
                    if (productDetails.productId ==
                        BillingClient.ProductType.SUBS
                    ) {

                        productDetails.subscriptionOfferDetails
                            ?.firstOrNull()
                            ?.offerToken

                    } else {
                        null
                    }

                val productDetailsParamsList = listOf(

                    BillingFlowParams.ProductDetailsParams
                        .newBuilder()
                        .setProductDetails(productDetails)
                        .apply {

                            if (!offerToken.isNullOrEmpty()) {
                                setOfferToken(offerToken)
                            }
                        }
                        .build()
                )

                val billingFlowParams =
                    BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                // Launch Google Play purchase screen
                bClient?.launchBillingFlow(
                    activity ,
                    billingFlowParams
                )
            }
        }
    }

    // ================= PURCHASE LISTENER =================

    /**
     * Handles purchase updates from Google Play.
     */
    private fun getPurchaseListener(): PurchasesUpdatedListener {

        return PurchasesUpdatedListener { bResult , list ->
            run {
                if (bResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                ) {

                    list?.get(0)?.let {
                        nonConsumableItem(it)
                    }
                }
            }
        }
    }

    // ================= PURCHASE PROCESSING =================

    /**
     * Handle non-consumable purchase.
     *
     * Example:
     * - Remove Ads
     */
    private fun nonConsumableItem(purchase: Purchase) {

        if (purchase.purchaseState ==
            Purchase.PurchaseState.PURCHASED
        ) {
            /**
             * Google Play requires acknowledgment.
             *
             * If purchase is not acknowledged,
             * Google may automatically refund it.
             */

            if (!purchase.isAcknowledged) {

                val acParams =
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                bClient?.acknowledgePurchase(acParams) {

                    if (it.responseCode ==
                        BillingClient.BillingResponseCode.OK
                    ) {

                        savePref(true)

                        Toast.makeText(
                            activity ,
                            R.string.thank_for_purchase ,
                            Toast.LENGTH_LONG
                        )
                            .show()

                    } else {

                        savePref(false)

                        Toast.makeText(
                            activity ,
                            R.string.failed_purchase ,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }
    }

    // ================= PREFERENCES =================

    /**
     * Save purchase state locally.
     *
     * Used for:
     * - Remove Ads state
     * - App startup checks
     */
    private fun savePref(isPurchase: Boolean) {

        val pref = activity.getSharedPreferences(
            MAIN_PREF ,
            Context.MODE_PRIVATE
        )

        pref.edit {
            putBoolean(REMOVE_ADS_KEY , isPurchase)
        }
    }


    // ================= CONSTANTS =================

    companion object {

        /**
         * Google Play product ID
         * for Remove Ads purchase.
         */
        const val REMOVE_AD_ITEM = "remove_ad_item_id"

        /**
         * SharedPreferences file name.
         */
        const val MAIN_PREF = "main_pref"

        /**
         * Key for Remove Ads state.
         */
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}