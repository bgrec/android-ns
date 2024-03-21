package com.mastrosql.app.ui.navigation.main.loginscreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    fun login(context: Context, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform the login request
                val response = loginRepository.login(username, password)

                //The server responds with a temporary redirect that is ../authentication/status
                //but the session cookie is set before redirect so in RetrofitInstance
                if (response.code() == 307) {
                    val cookies = response.headers().values("Set-Cookie")
                    val sessionCookie = extractSessionCookie(cookies)

                    Log.d("LoginViewModel", "Session cookie: $sessionCookie")

                    // Set the session cookie in the SessionManager singleton if it is not empty
                    if (sessionCookie.isNotEmpty()) {
                        SessionManager.setSessionCookie(sessionCookie)
                    }

                    // Save the logged in state in the data store
                    userPreferencesRepository.saveLoggedIn(true)

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)

                    when (response.code()) {

                        401 -> {
                            showToast(context, Toast.LENGTH_LONG, errorMessage)
                        }

                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Collegamento riuscito, api not trovata ${response.code()}"
                        )

                        500 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        503 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        else -> {
                            showToast(context, Toast.LENGTH_LONG, "Errore api: ${response.code()}")
                        }
                    }

                    userPreferencesRepository.saveLoggedIn(false)
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                showToast(context, Toast.LENGTH_LONG, "Network error occurred: ${e.message}")
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                showToast(context, Toast.LENGTH_LONG, "HTTP error occurred: ${e.message}")
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "Connection timed out. Please try again later."
                )
            } catch (e: Exception) {
                // Handle generic exception
                showToast(context, Toast.LENGTH_LONG, "An unexpected error occurred: ${e.message}")
            }
        }
    }

    // Extract the session cookie from the response headers
    private fun extractSessionCookie(cookies: List<String>): String {
        return cookies.firstOrNull { it.startsWith("session_") } ?: ""
    }

    //Get the message in the body
    private fun parseErrorMessage(errorBody: String?): String {
        val jsonError = JSONObject(errorBody ?: "{}")
        return jsonError.optString("message")
    }


    private fun showToast(context: Context, toastLength: Int, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (message.isNotEmpty()) {
                val toast = Toast.makeText(context, message, toastLength)
                //Error for toast gravity with message
                //toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else {
                // Hide loading message by not showing any toast
            }
        }
    }
}
