package com.example.shoppingAndNotesListApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Extension function for testing LiveData.
 *
 * This helper waits until LiveData receives a value
 * or throws TimeoutException if no value is emitted.
 *
 * Commonly used in:
 * - ViewModel tests
 * - Repository tests
 * - LiveData unit tests
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2 ,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {

    // Variable for storing emitted LiveData value
    var data: T? = null

    // Latch used for blocking current thread
    // until LiveData emits a value
    val latch = CountDownLatch(1)

    // ----------------------------
    // LiveData observer
    // ----------------------------
    val observer = object : Observer<T> {

        override fun onChanged(o: T) {

            // Save emitted value
            data = o

            // Release waiting thread
            latch.countDown()

            // Remove observer to avoid memory leaks
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    // Start observing LiveData
    this.observeForever(observer)

    // Wait for LiveData value
    if (!latch.await(time , timeUnit)) {

        // Throw exception if timeout exceeded
        throw TimeoutException("LiveData value was never set.")
    }

    // Return emitted value
    return data!!
}