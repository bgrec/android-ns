package com.mastrosql.app.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Object to show toast messages in the main thread
 */
object ToastUtils {
    /**
     * Function to show a toast message in the main thread
     * @param context The context to show the toast message
     * @param toastLength The length of the toast message
     * @param message The message to show in the toast
     */
    suspend fun showToast(context: Context, toastLength: Int, message: String) {
        withContext(Dispatchers.Main) {
            if (message.isNotEmpty()) {
                val toast = Toast.makeText(context, message, toastLength)
                //Error for toast gravity with message
                //toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
        }
    }
}