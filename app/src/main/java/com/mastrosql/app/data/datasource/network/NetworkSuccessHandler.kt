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

object NetworkSuccessHandler {
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

    //404
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

    //500, 503
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
        return if (responseBody.isNotEmpty()) {
            try {
                val jsonError = JSONObject(responseBody)
                jsonError.optString("message", "")
            } catch (e: Exception) {
                Log.e("NetworkExceptionHandler", "Error parsing response ${e.message} ")
                context.getString(R.string.error_unexpected_error, e.message ?: "")
            }
        } else {
            context.getString(R.string.error_unexpected_error, "")
        }
    }
}