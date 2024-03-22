package com.mastrosql.app.ui.navigation.main.loginscreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
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

    private lateinit var credentialManager: CredentialManager

    fun initCredentialManager(context: Context) {
        this.credentialManager = CredentialManager.create(context)
    }

    fun login(context: Context, username: String, password: String, isCredentialManagerLogin: Boolean) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform the login request and handle the response
                val response = loginRepository.login(username, password)

                //The server responds with a temporary redirect that is ../authentication/status
                //but the session cookie is set before redirect so in RetrofitInstance the redirect is not followed
                if (response.code() == 307) {
                    val cookies = response.headers().values("Set-Cookie")
                    val sessionCookie = extractSessionCookie(cookies)

                    Log.d("LoginViewModel", "Session cookie: $sessionCookie")

                    // Set the session cookie in the SessionManager singleton if it is not empty
                    if (sessionCookie.isNotEmpty()) {
                        SessionManager.setSessionCookie(sessionCookie)
                    }

                    //Save credentials if login is successful and the user logged in manually
                    //not using the CredentialManager
                    if(!isCredentialManagerLogin) {
                        saveNewCredential(context, username, password)
                    }

                    // Save the logged in state in the data store
                    userPreferencesRepository.saveLoggedIn(true)

                } else {
                  
                    when (response.code()) {

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_LONG,
                                " Credenziali non valide o errate ${response.code()}"
                            )
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
                                "${response.code()}"
                            )
                        }

                        503 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                " ${response.code()}"
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

    private suspend fun saveNewCredential(
        activityContext: Context,
        username: String,
        password: String
    ) {
        // Initialize a CreatePasswordRequest object.
        val createPasswordRequest =
            CreatePasswordRequest(id = username, password = password)

        // Create credential and handle result.
        viewModelScope.launch {
            try {
                val result =
                    credentialManager.createCredential(
                        // Use an activity based context to avoid undefined
                        // system UI launching behavior.
                        activityContext,
                        createPasswordRequest
                    )
                // Handle the result of registering or updating password

            } catch (e: CreateCredentialException) {
                handleFailure(e)
            }
        }
    }

    // Function to handle result of registering or updating password
    private fun handleRegisterOrUpdatePasswordResult(result: Boolean) {
        // Handle the result of registering or updating password
        if (result) {
            Log.d("LoginViewModel", "Password registration or update successful")
        } else {
            Log.d("LoginViewModel", "Password registration or update failed")
        }
    }

    fun getCredentials(activityContext: Context)/*: PasswordCredential?*/ {

        // Retrieves the user's saved password for your app from their
        // password provider.
        val getPasswordOption = GetPasswordOption()

        /*
        // Get passkey from the user's public key credential provider.
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestJson
        )
        */

        val getCredRequest = GetCredentialRequest(
            listOf(getPasswordOption/*, getPublicKeyCredentialOption*/)
        )

        //Tell the credential library that we're only interested in password credentials
        /*val getCredRequest = GetCredentialRequest(
            listOf(GetPasswordOption(isAutoSelectAllowed = true))
        )*/

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = activityContext,
                    request = getCredRequest
                )
                handleSignIn(result, activityContext)
            } catch (e: GetCredentialException) {
                //handleFailure(e)

                Log.e("LoginViewModel", "Error fetching the credentials", e)
            }
            catch (e: GetCredentialCancellationException){
                // The user intentionally canceled the operation and chose not
                // to sign in.
                Log.e("LoginViewModel", "User canceled the operation", e)
            }
            catch (e: GetCredentialUnknownException){
                // An unknown error occurred.
                Log.e("LoginViewModel", "An unknown error occurred", e)
            }
            catch (e: GetCredentialInterruptedException){
                // Retry-able error. Consider retrying the call.
                Log.e("LoginViewModel", "Retry-able error. Consider retrying the call", e)
            }
            catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching the credentials", e)
            }
            catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching the credentials", e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse, activityContext: Context) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                // Share responseJson i.e. a GetCredentialResponse on your server to
                // validate and  authenticate
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                // Use id and password to send to your server to validate
                // and authenticate

                login(activityContext, username, password, isCredentialManagerLogin = true)
            }

            is CustomCredential -> {
                //Handle custom credential
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("LoginViewModel", "Unexpected type of credential")
            }
        }
    }

    private fun handleFailure(e: CreateCredentialException) {
        when (e) {
            is CreatePublicKeyCredentialDomException -> {
                // Handle the passkey DOM errors thrown according to the
                // WebAuthn spec.

                // handlePasskeyError(e.domError)
            }

            is CreateCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to register the credential.
            }

            is CreateCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
            }

            is CreateCredentialProviderConfigurationException -> {
                // Your app is missing the provider configuration dependency.
                // Most likely, you're missing the
                // "credentials-play-services-auth" module.
            }
            //is CreateCredentialUnknownException -> ...
            is CreateCredentialCustomException -> {
                // You have encountered an error from a 3rd-party SDK. If you
                // make the API call with a request object that's a subclass of
                // CreateCustomCredentialRequest using a 3rd-party SDK, then you
                // should check for any custom exception type constants within
                // that SDK to match with e.type. Otherwise, drop or log the
                // exception.
            }

            else -> Log.e("Login", "Unexpected exception type ${e::class.java.name}")
        }
    }

}
/*
On Begin Sign In Failure: 16: Caller has been temporarily blocked due to too many canceled sign-in prompts.
If you encounter this 24-hour cooldown period during development, you can reset it by clearing Google Play services' app storage.

Alternatively, to toggle this cooldown on a test device or emulator,
 go to the Dialer app and input the following code: *#*#66382723#*#*.
  The Dialer app clears all input and may close, but there isn't a confirmation message.
 */