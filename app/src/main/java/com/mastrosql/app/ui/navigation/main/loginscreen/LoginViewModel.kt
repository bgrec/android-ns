package com.mastrosql.app.ui.navigation.main.loginscreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.datasource.network.AppCookieJar
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

                    // Set the session cookie in the AppCookieJar singleton
                    if (sessionCookie.isNotEmpty()) {
                        AppCookieJar.setSessionCookie(sessionCookie)

                    }
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
        // You may need to extract the correct cookie value based on your server's response
        // This is just a simple example, you may need to parse the cookies properly
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

    fun extractSessionToken(sessionCookie: String?): String? {
        // Parse session token from cookie header
        if (sessionCookie != null) {
            val cookieParts = sessionCookie.split(";")
            for (part in cookieParts) {
                if (part.startsWith("session_")) {
                    val tokenParts = part.split("=")
                    if (tokenParts.size == 2) {
                        return tokenParts[1]
                    }
                }
            }
        }
        return null
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = loginRepository.logout()
                userPreferencesRepository.saveLoggedIn(false)
                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            // Refresh the list
                            //getOrderDetails()
                        }
                    }
                }
            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
            } catch (e: Exception) {
                // Handle generic exception
            }

        }
    }
    /*
    package khttp.structures.authorization

import java.util.Base64

data class BasicAuthorization(val user: String, val password: String) : Authorization {
    override val header: Pair<String, String>
        get() {
            val b64 = Base64.getEncoder().encode("${this.user}:${this.password}".toByteArray()).toString(Charsets.UTF_8)
            return "Authorization" to "Basic $b64"
        }
}
     */

}
/*2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Postman-Token=1f173aa1-8d6b-4358-a6c4-6853089c513f
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Host=localhost:8445
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Accept-Encoding=gzip, deflate, br
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Connection=keep-alive
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:35:41-20
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): authenticate
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] AuthorizeManager::is_authorized(service_id:11eee223c1c7a5b5a5299828a641a2a2, session_id:2024-03-15 17:35:41-20, can_use_jwt:yes)
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): dispatch(method:/myService/authentication/logout, path:GET)
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response status: 401
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response status text: Unauthorized
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response method: GET
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Credentials=true
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Headers=Content-Type, Authorization, X-Requested-With, Origin, X-Auth-Token
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Methods=GET, POST, PUT, DELETE, OPTIONS
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Content-Type=application/json
2024-03-15 17:36:34 mysql_rest_service DEBUG [39ac] HTTP Response body: {"message":"OK","status":200}
2024-03-15 17:36:42 http_server DEBUG [39ac] uri.get_path()=/myService/authentication/login
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] handle_request(service_id:11eee223c1c7a5b5a5299828a641a2a2): start(url='/myService/authentication/login')
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request method: GET
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Authorization=Basic bWFzdHJvOm1hc3Rybw==
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cache-Control=no-cache
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Postman-Token=de34ca39-c195-4466-ab83-78fabf6f8430
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Host=localhost:8445
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:35:41-20
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): authenticate
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] AuthorizeManager::authorize(service_id:11eee223c1c7a5b5a5299828a641a2a2, session_id:2024-03-15 17:35:41-20, can_use_jwt:yes)
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] new session id=2024-03-15 17:36:42-21
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] WwwAuthenticationHandler::redirects
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] selected_handler::redirects(yes)
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] WwwAuth: Authorize user
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] WwwAuth: execute
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] user_get {'login_permitted':'true', 'vendor_user_id':'mastro@%'}, update_changed=true
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Authentication handler ok.
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): dispatch(method:/myService/authentication/login, path:GET)
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Current session state:4
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HandlerAuthorize::handle_get - before redirects
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] WwwAuthenticationHandler::redirects
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Redirection to '/notesServed?app=MySQL%20Internal&login=success'
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Catch: http::Error status: 307
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Catch: http::Error message: Temporary Redirect
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] void handle_error(RequestContext *ctxt, const Err &err)
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] Current session state:4
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] session->onRedirect=url_param->onRedirect
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response status: 307
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response status text: Temporary Redirect
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response method: GET
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Credentials=true
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Headers=Content-Type, Authorization, X-Requested-With, Origin, X-Auth-Token
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Methods=GET, POST, PUT, DELETE, OPTIONS
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Set-Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:36:42-21; Path=/; SameSite=None; Secure; HttpOnly
2024-03-15 17:36:42 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Location=/notesServed?app=MySQL%20Internal&login=success
2024-03-15 17:36:42 http_server DEBUG [39ac] uri.get_path()=/notesServed
2024-03-15 17:36:53 http_server DEBUG [39ac] uri.get_path()=/myService/authentication/logout
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] handle_request(service_id:11eee223c1c7a5b5a5299828a641a2a2): start(url='/myService/authentication/logout')
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request method: GET
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: User-Agent=PostmanRuntime/7.36.3
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Accept=*
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cache-Control=no-cache
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Postman-Token=951a8a28-c248-4ed8-9fd4-f265e15e784a
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Host=localhost:8445
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Accept-Encoding=gzip, deflate, br
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Connection=keep-alive
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:36:42-21
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): authenticate
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] AuthorizeManager::is_authorized(service_id:11eee223c1c7a5b5a5299828a641a2a2, session_id:2024-03-15 17:36:42-21, can_use_jwt:yes)
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): dispatch(method:/myService/authentication/logout, path:GET)
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response status: 401
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response status text: Unauthorized
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response method: GET
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Credentials=true
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Headers=Content-Type, Authorization, X-Requested-With, Origin, X-Auth-Token
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Methods=GET, POST, PUT, DELETE, OPTIONS
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Content-Type=application/json
2024-03-15 17:36:53 mysql_rest_service DEBUG [39ac] HTTP Response body: {"message":"OK","status":200}
2024-03-15 17:37:01 http_server DEBUG [39ac] uri.get_path()=/myService/authentication/login
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] handle_request(service_id:11eee223c1c7a5b5a5299828a641a2a2): start(url='/myService/authentication/login')
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request method: GET
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Authorization=Basic bWFzdHJvOm1hc3RybzI=
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cache-Control=no-cache
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Postman-Token=58a04cd2-7230-44ac-827a-bed714d2b948
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Host=localhost:8445
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Request parameters: Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:36:42-21
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] RestRequestHandler(service_id:11eee223c1c7a5b5a5299828a641a2a2): authenticate
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] AuthorizeManager::authorize(service_id:11eee223c1c7a5b5a5299828a641a2a2, session_id:2024-03-15 17:36:42-21, can_use_jwt:yes)
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] new session id=2024-03-15 17:37:01-22
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] WwwAuthenticationHandler::redirects
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] selected_handler::redirects(yes)
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response method: GET
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Credentials=true
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Headers=Content-Type, Authorization, X-Requested-With, Origin, X-Auth-Token
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Access-Control-Allow-Methods=GET, POST, PUT, DELETE, OPTIONS
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response parameters: Set-Cookie=session_11eee223c1c7a5b5a5299828a641a2a2=2024-03-15 17:37:01-22; Path=/; SameSite=None; Secure; HttpOnly
2024-03-15 17:37:01 mysql_rest_service DEBUG [39ac] HTTP Response parameters: WWW-Authenticate=basic







*/