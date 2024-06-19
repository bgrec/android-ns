package com.mastrosql.app.ui.navigation.main.loginscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastrosql.app.PRIMARY_URL
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.login.LoginRepository
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesUiState
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * The dialer code to reset the cooldown period during development.
 */
const val CREDENTIAL_DIALER_CODE: String = "*#*#66382723#*#*"

/**
 * The UI state for the Login screen.
 */
@Suppress("KDocMissingDocumentation")
data class LoginUiState(
    val isSecondaryBaseUrlProvided: Boolean = false,
    val isNotSecuredApi: Boolean = false,
    val selectedUrl: Int = PRIMARY_URL,
    val baseUrlName: String = "Primary",
    val baseUrl2Name: String = "Secondary",
)

/**
 * The ViewModel class for the Login screen.
 */
class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * The CredentialManager object.
     */
    private lateinit var credentialManager: CredentialManager

    /**
     * Flag to check if the API is not secured.
     */
    private var isNotSecuredApi = false

    /**
     * Initialize the CredentialManager object.
     */
    fun initCredentialManager(context: Context) {
        this.credentialManager = CredentialManager.create(context)
        getIsNotSecuredApi()
    }

    /**
     * Mutable state flow to hold the UI state.
     */
    private val _uiState = MutableStateFlow(LoginUiState())

    /**
     * UI state for the Login screen.
     */
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Update the UI state.
     */
    private fun updateUiState(newState: LoginUiState) {
        _uiState.value = newState
    }

    private fun getIsNotSecuredApi() {
        /**
         * Collect the value of isNotSecuredApi from the data store.
         */
        viewModelScope.launch {
            userPreferencesRepository
                .getIsNotSecuredApi()
                .collect {
                    isNotSecuredApi = it
                    updateUiState(uiState.value.copy(isNotSecuredApi = isNotSecuredApi))
                }
        }

        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl2()
                .collect {
                    if (it.isNotEmpty()) {
                        updateUiState(uiState.value.copy(isSecondaryBaseUrlProvided = true))
                        userPreferencesRepository.getSelectedUrl().collect { selectedUrl ->
                            updateUiState(uiState.value.copy(selectedUrl = selectedUrl))
                        }
                        userPreferencesRepository.getBaseUrlName().collect { baseUrlName ->
                            updateUiState(uiState.value.copy(baseUrlName = baseUrlName))
                        }
                        userPreferencesRepository.getBaseUrl2Name().collect { baseUrl2Name ->
                            updateUiState(uiState.value.copy(baseUrl2Name = baseUrl2Name))
                        }
                    }
                }
        }
    }

    /**
     * Perform the login request and handle the response.
     */
    fun login(
        context: Context, username: String, password: String, isCredentialManagerLogin: Boolean
    ) {
        if (isNotSecuredApi) {
            loginNotSecuredApi()
        } else {
            loginSecuredApi(context, username, password, isCredentialManagerLogin)
        }
    }

    /**
     * Perform the login without credentials.
     */
    private fun loginNotSecuredApi() {
        viewModelScope.launch(Dispatchers.IO) {
            //Save the logged in state in the data store
            userPreferencesRepository.saveLoggedIn(true)
        }
    }

    /**
     * Perform the login request and handle the response.
     */
    private fun loginSecuredApi(
        context: Context, username: String, password: String, isCredentialManagerLogin: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform the login request and handle the response
                val response = loginRepository.login(username, password)

                /** The server responds with a temporary redirect that is ../authentication/status
                 * but the session cookie is set before redirect
                 * so in RetrofitInstance the redirect is not followed
                 * (there is a flag to not follow redirects)
                 */

                if (response.code() == 307) {
                    val cookies = response
                        .headers()
                        .values("Set-Cookie")
                    val sessionCookie = extractSessionCookie(cookies)

                    // Set the session cookie in the SessionManager singleton if it is not empty
                    if (sessionCookie.isNotEmpty()) {
                        SessionManager.setSessionCookie(sessionCookie)
                    }

                    //Save credentials if login is successful and the user logged in manually
                    //not using the CredentialManager
                    if (!isCredentialManagerLogin) {
                        saveNewCredential(context, username, password)
                    }

                    //Save the logged in state in the data store
                    userPreferencesRepository.saveLoggedIn(true)

                } else {

                    when (response.code()) {
                        401 -> NetworkSuccessHandler.handleUnauthorized(
                            context, viewModelScope
                        ) {
                            // Handle unauthorized error
                        }

                        404 -> NetworkSuccessHandler.handleNotFound(
                            context, viewModelScope, response.code()
                        )

                        500, 503 -> NetworkSuccessHandler.handleServerError(
                            context, response, viewModelScope
                        )

                        else -> NetworkSuccessHandler.handleUnknownError(
                            context, response, viewModelScope
                        )
                    }

                    //Save the logged in state in the data store
                    userPreferencesRepository.saveLoggedIn(false)
                }

            } catch (e: IOException) {
                NetworkExceptionHandler.handleException(
                    context, e, viewModelScope
                )
            } catch (e: HttpException) {
                NetworkExceptionHandler.handleException(
                    context, e, viewModelScope
                )
            } catch (e: SocketTimeoutException) {
                NetworkExceptionHandler.handleSocketTimeoutException(
                    context, viewModelScope
                )
            } catch (e: Exception) {
                NetworkExceptionHandler.handleException(
                    context, e, viewModelScope
                )
            }
        }
    }

    /**
     * Extract the session cookie from the list of cookies returned by the server.
     */
    private fun extractSessionCookie(cookies: List<String>): String {
        return cookies.firstOrNull { it.startsWith("session_") } ?: ""
    }

    private suspend fun saveNewCredential(
        activityContext: Context, username: String, password: String
    ) {

        // Initialize a CreatePasswordRequest object.
        val createPasswordRequest = CreatePasswordRequest(id = username, password = password)

        // Create credential and handle result.
        viewModelScope.launch {
            try {
                val result = credentialManager.createCredential(
                    // Use an activity based context to avoid undefined
                    // system UI launching behavior.
                    activityContext, createPasswordRequest
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

        // Retrieves the user's saved password from the credential provider.
        val getPasswordOption = GetPasswordOption()

        /*
         // Get passkey from the user's public key credential provider.
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestJson)
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
                    context = activityContext, request = getCredRequest
                )
                handleSignIn(result, activityContext)
            } catch (e: GetCredentialException) {
                //handleFailure(e)

                Log.e("LoginViewModel", "Error fetching the credentials", e)
            } catch (e: GetCredentialCancellationException) {
                // The user intentionally canceled the operation and chose not
                // to sign in.
                Log.e("LoginViewModel", "User canceled the operation", e)
            } catch (e: GetCredentialUnknownException) {
                // An unknown error occurred.
                Log.e("LoginViewModel", "An unknown error occurred", e)
            } catch (e: GetCredentialInterruptedException) {
                // Retry-able error. Consider retrying the call.
                Log.e("LoginViewModel", "Retry-able error. Consider retrying the call", e)
            } catch (e: NoCredentialException) {
                ToastUtils.showToast(
                    activityContext, Toast.LENGTH_SHORT, "NoCredentialException"
                )
                launchDialer(activityContext)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching the credentials", e)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching the credentials", e)
            }
        }
    }


    private fun launchDialer(context: Context) {
        val code = CREDENTIAL_DIALER_CODE
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$code"))
        context.startActivity(intent)
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
