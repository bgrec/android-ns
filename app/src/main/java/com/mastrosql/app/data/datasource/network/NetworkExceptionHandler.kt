package com.mastrosql.app.data.datasource.network

import android.content.Context
import android.widget.Toast
import com.mastrosql.app.R
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Sealed class to represent the network exceptions
 */

sealed class NetworkException(message: String) : Exception(message) {

    /**
     * Custom IOException class that extends NetworkException.
     */
    class IOException(message: String) : NetworkException(message)

    /**
     * Custom HttpException class that extends NetworkException.
     */
    class HttpException(message: String) : NetworkException(message)

    /**
     * Custom UnknownException class that extends NetworkException.
     */
    class UnknownException(message: String) : NetworkException(message)
}

/**
 * Object to handle the network exceptions
 */

object NetworkExceptionHandler {
    /**
     * Function to handle the exception
     */
    fun handleException(
        context: Context,
        exception: Exception,
        coroutineScope: CoroutineScope
    ) {

        /**
         * Handles different types of NetworkException and displays corresponding error messages.
         */
        when (exception) {

            /**
             * Handles IOException derived from NetworkException and displays a network error message.
             */
            is NetworkException.IOException -> {
                showToastMessage(
                    context,
                    context.getString(
                        R.string.error_network_error,
                        exception.message
                    ),
                    coroutineScope
                )
            }

            /**
             * Handles HttpException derived from NetworkException and displays an HTTP error message.
             */
            is NetworkException.HttpException -> {
                showToastMessage(
                    context,
                    context.getString(
                        R.string.error_http,
                        exception.message
                    ),
                    coroutineScope
                )
            }

            /**
             * Handles UnknownException derived from NetworkException and displays an unexpected error message.
             */
            is NetworkException.UnknownException -> {
                showToastMessage(
                    context,
                    context.getString(
                        R.string.error_unexpected_error,
                        exception.message
                    ),
                    coroutineScope
                )
            }

        /**
         * Handles any other types of NetworkException that are not explicitly handled.
         * Displays an unexpected error message if the exception type is unknown.
         */
            else -> {
                showToastMessage(
                    context,
                    context.getString(
                        R.string.error_unexpected_error,
                        exception.message ?: ""
                    ),
                    coroutineScope
                )
                //Log.d("NetworkExceptionHandler", exception.message ?: "")
            }
        }
    }

    /**
     * Function to handle the socket timeout exception
     *
     */
    fun handleSocketTimeoutException(
        context: Context,
        coroutineScope: CoroutineScope
    ) {

        /**
         * Launches a coroutine to display a Toast message indicating a connection timeout error.
         */
        coroutineScope.launch {
            ToastUtils.showToast(
                context,
                Toast.LENGTH_LONG,
                context.getString(R.string.error_connection_timeout)
            )
        }
    }

    /**
     * Shows a toast message on the UI thread using the provided context and coroutine scope.
     */
    private fun showToastMessage(
        context: Context,
        message: String,
        coroutineScope: CoroutineScope
    )

        /**
         * Shows a toast message on the UI thread using the provided context and coroutine scope.
         */
    {
        coroutineScope.launch {
            ToastUtils.showToast(
                context,
                Toast.LENGTH_LONG,
                message
            )
        }
    }
}