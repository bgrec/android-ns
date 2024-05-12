package com.mastrosql.app.data.datasource.network

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.R
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ExceptionHandler {
    /**
     * Function to handle the exception
     */
    private fun handleException(
        context: Context,
        exception: Exception,
        couroutineScope: CoroutineScope
    ) {
        when (exception) {
            is IOException -> {
                // Handle IOException (e.g., network error)
                couroutineScope.launch {
                    ToastUtils.showToast(
                        context,
                        Toast.LENGTH_LONG,
                        context.getString(R.string.error_network_error, exception.message)
                    )
                }
            }

            is HttpException -> {
                couroutineScope.launch {
                    ToastUtils.showToast(
                        context,
                        Toast.LENGTH_LONG,
                        context.getString(R.string.error_http, exception.message)
                    )
                }
            }

            else -> {
                // Handle generic exception
                viewModelScope.launch {
                    ToastUtils.showToast(
                        context,
                        Toast.LENGTH_LONG,
                        context.getString(R.string.error_unexpected_error, exception.message)
                    )
                }
            }
        }
    }

    /**
     * Function to handle the socket timeout exception
     *
     */
    private fun handleSocketTimeoutException(context: Context) {
        viewModelScope.launch {
            ToastUtils.showToast(
                context,
                Toast.LENGTH_LONG,
                context.getString(R.string.error_connection_timeout)
            )
        }
    }
}