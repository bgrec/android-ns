package com.mastrosql.app.data.datasource.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mastrosql.app.R
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Sealed class to represent the network exceptions
 */

sealed class NetworkException(message: String) : Exception(message) {
    class IOException(message: String) : NetworkException(message)
    class HttpException(message: String) : NetworkException(message)
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
        when (exception) {
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

            else -> {
                showToastMessage(
                    context,
                    context.getString(
                        R.string.error_unexpected_error,
                        exception.message ?: ""
                    ),
                    coroutineScope
                )
                Log.d("NetworkExceptionHandler", exception.message ?: "")
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
        coroutineScope.launch {
            ToastUtils.showToast(
                context,
                Toast.LENGTH_LONG,
                context.getString(R.string.error_connection_timeout)
            )
        }
    }

    private fun showToastMessage(
        context: Context,
        message: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            ToastUtils.showToast(
                context,
                Toast.LENGTH_LONG,
                message
            )
        }
    }
}