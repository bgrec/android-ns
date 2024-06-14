package com.mastrosql.app.data.datasource.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mastrosql.app.R
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

/**
 * Class to handle the error response from the network
 */
object NetworkSuccessHandler {
    /**
     * Function to handle the   response from the API
     */
    inline fun handleUnauthorized(
        context: Context,
        coroutineScope: CoroutineScope,
        crossinline onUnauthorized: () -> Unit
    ) {
        showToastMessage(
            context,
            context.getString(R.string.error_unauthorized),
            coroutineScope
        )

        onUnauthorized()
    }

    /**
     * Function to handle the 404 response from the API
     */
    fun handleNotFound(
        context: Context,
        coroutineScope: CoroutineScope,
        responseCode: Int
    ) {
        showToastMessage(
            context,
            context.getString(R.string.error_api_not_found, responseCode),
            coroutineScope
        )
    }

    /**
     * Function to handle the 500 and 503 response from the API
     */
    fun handleServerError(
        context: Context,
        response: Response<*>,
        coroutineScope: CoroutineScope
    ) {
        val errorMessage = parseErrorMessage(context, response)
        val responseCode = response.code()

        showToastMessage(
            context,
            context.getString(R.string.error_api, responseCode, errorMessage),
            coroutineScope
        )
    }

    /**
     * Function to handle the unknown error response from the API
     */
    fun handleUnknownError(
        context: Context,
        response: Response<*>,
        coroutineScope: CoroutineScope
    ) {
        val errorMessage = parseErrorMessage(context, response)
        val responseCode = response.code()
        showToastMessage(
            context,
            context.getString(R.string.error_api, responseCode, errorMessage),
            coroutineScope
        )
    }

    /**
     * Function to show a toast message
     */
    fun showToastMessage(
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

    /**
     * Function to parse the error message from the error body
     * @param response The response from the API
     */
    private fun parseErrorMessage(context: Context, response: Response<*>): String {
        val errorBody: String? = response.errorBody()?.string()
        val responseBody: String = errorBody ?: ""

        /**
         * Parses the error message from the response body if it is not empty.
         * Returns an empty string if no error message is found or an unexpected error occurs.
         */
        return if (responseBody.isNotEmpty()) {

            /**
             * Parses the error message from a JSON response body.
             */
            try {
                val jsonError = JSONObject(responseBody)
                jsonError.optString("message", "")
            }

            /**
             * Catches and handles any exception that occurs while parsing a JSON response.
             */
            catch (e: Exception) {
                Log.e("NetworkExceptionHandler", "Error parsing response ${e.message} ")
                context.getString(R.string.error_unexpected_error, e.message ?: "")
            }
        }

        /**
         * Parses the error message from a JSON response body.
         */
        else {
            context.getString(R.string.error_unexpected_error, "")
        }
    }
}