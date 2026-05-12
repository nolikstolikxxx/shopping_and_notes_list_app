package com.example.shoppingAndNotesListApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * ============================================================
 * LIVE DATA TEST UTILITY
 * ============================================================
 *
 * Helper function for testing LiveData in unit and instrumentation tests.
 *
 * Problem it solves:
 * LiveData updates are asynchronous, which makes testing unreliable
 * without a blocking mechanism.
 *
 * This function:
 * 1. Observes LiveData forever
 * 2. Waits until a value is emitted
 * 3. Returns the emitted value
 * 4. Fails test if timeout is reached
 *
 * Used in:
 * - ViewModel tests
 * - Repository tests (if LiveData is exposed)
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    // ----------------------------
    // Holder for emitted data
    // ----------------------------
    var data: T? = null

    // ----------------------------
    // Synchronization latch
    // ----------------------------
    val latch = CountDownLatch(1)

    // ----------------------------
    // LiveData observer
    // ----------------------------
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {

            // Save received value
            data = value

            // Release waiting thread
            latch.countDown()

            // Remove observer after first emission
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    // ----------------------------
    // Start observing LiveData
    // ----------------------------
    this.observeForever(observer)

    try {

        // ----------------------------
        // Wait for LiveData value
        // ----------------------------
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException(
                "LiveData value was never set within timeout: $time $timeUnit"
            )
        }

    } finally {

        // ----------------------------
        // Safety cleanup (prevents memory leaks in tests)
        // ----------------------------
        this.removeObserver(observer)
    }

    // ----------------------------
    // Return emitted value
    // ----------------------------
    return data!!
}