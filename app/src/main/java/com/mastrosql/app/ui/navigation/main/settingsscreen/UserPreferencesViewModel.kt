package com.mastrosql.app.ui.navigation.main.settingsscreen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.util.EnumMap


/*
* sealed class UserPreferencesUiState {
    object Loading : UserPreferencesUiState()
    data class Success(
        val isOnboarded: Boolean,
        val isLoggedIn: Boolean,
        // Other preferences
    ) : UserPreferencesUiState()
    data class Error(val message: String) : UserPreferencesUiState()
}
*
*/


/**
 * UI state for the user preferences screen.
 */
@Suppress("KDocMissingDocumentation")
data class UserPreferencesUiState(
    val isOnboarded: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isNotSecuredApi: Boolean = false,
    val isSwipeToDeleteDisabled: Boolean = false,
    val isSwipeToDuplicateDisabled: Boolean = false,
    val baseUrl: String = "",
    val baseUrl2: String = "",
    val selectedUrl: Int = 0,
    val baseUrlName: String = "Primary",
    val baseUrl2Name: String = "Secondary",
    val activeButtons: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)
    // collect it as collectAsStateWithLifecycle()
)

/**
 * ViewModel for the user preferences screen.
 */
open class UserPreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    // Mutable state flow for UI state
    private val _uiState = MutableStateFlow(UserPreferencesUiState())

    /**
     * UI state for the user preferences screen.
     */
    val uiState: StateFlow<UserPreferencesUiState> = _uiState

    // Function to update UI state
    private fun updateUiState(newState: UserPreferencesUiState) {
        _uiState.value = newState
    }

    init {

        // Observe changes in isOnboarded and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getIsOnboarded()
                .collect { isOnboarded ->
                    updateUiState(uiState.value.copy(isOnboarded = isOnboarded))
                }
        }

        // Observe changes in isLoggedIn and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getIsLoggedIn()
                .collect { isLoggedIn ->
                    updateUiState(uiState.value.copy(isLoggedIn = isLoggedIn))
                }
        }

        // Observe changes in isNotSecuredApi and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getIsNotSecuredApi()
                .collect { isNotSecuredApi ->
                    updateUiState(uiState.value.copy(isNotSecuredApi = isNotSecuredApi))
                }
        }

        // Observe changes in isSwipeToDeleteDisabled and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getIsSwipeToDeleteDisabled()
                .collect { isSwipeToDeleteDisabled ->
                    updateUiState(
                        uiState.value.copy(isSwipeToDeleteDisabled = isSwipeToDeleteDisabled)
                    )
                }
        }

        // Observe changes in isSwipeToDuplicateDisabled and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getIsSwipeToDuplicateDisabled()
                .collect { isSwipeToDuplicateDisabled ->
                    updateUiState(
                        uiState.value.copy(isSwipeToDuplicateDisabled = isSwipeToDuplicateDisabled)
                    )
                }
        }

        // Observe changes in baseUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl()
                .collect { baseUrl ->
                    updateUiState(uiState.value.copy(baseUrl = baseUrl))
                }
        }

        // Observe changes in baseUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl2()
                .collect { baseUrl2 ->
                    updateUiState(uiState.value.copy(baseUrl2 = baseUrl2))
                }
        }

        // Observe changes in activeButtons and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getActiveButtons()
                .collect { activeButtons ->
                    updateUiState(uiState.value.copy(activeButtons = activeButtons))
                }
        }

        // Observe changes in selectedUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getSelectedUrl()
                .collect { selectedUrl ->
                    updateUiState(uiState.value.copy(selectedUrl = selectedUrl))
                }
        }

        // Observe changes in selectedUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrlName()
                .collect { baseUrlName ->
                    updateUiState(uiState.value.copy(baseUrlName = baseUrlName))
                }
        }

        // Observe changes in selectedUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl2Name()
                .collect { baseUrl2Name ->
                    updateUiState(uiState.value.copy(baseUrl2Name = baseUrl2Name))
                }
        }
    }

    /**
     *
     */
    fun updateActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        viewModelScope.launch {
            userPreferencesRepository.updateActiveButtons(activeButtons)
            //updateUiState(uiState.value.copy(activeButtons = activeButtons))
        }
    }

    /**
     * [onBoardingCompleted] change intro screen and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun onBoardingCompleted(isOnBoardingCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveOnBoardingCompleted(isOnBoardingCompleted)
        }
    }

    /**
     *
     */
    fun setPrimaryBaseUrl(baseUrl: String) {
        viewModelScope.launch {
            if (try {
                    URL(baseUrl).toURI()
                    true
                } catch (e: Exception) {
                    false
                }
            ) {
                if (baseUrl.endsWith("/")) {
                    userPreferencesRepository.savePrimaryBaseUrl(baseUrl)
                } else {
                    userPreferencesRepository.savePrimaryBaseUrl("$baseUrl/")
                }
            }
        }
    }

    /**
     * Set the secondary base URL.
     */
    fun setSecondaryBaseUrl(baseUrl2: String) {
        viewModelScope.launch {
            if (try {
                    URL(baseUrl2).toURI()
                    true
                } catch (e: Exception) {
                    false
                }
            ) {
                if (baseUrl2.endsWith("/")) {
                    userPreferencesRepository.saveSecondaryBaseUrl(baseUrl2)
                } else {
                    userPreferencesRepository.saveSecondaryBaseUrl("$baseUrl2/")
                }
            }
        }
    }

    /**
     * Save the base URL name.
     */
    fun setPrimaryBaseUrlName(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveBaseUrlName(name)
        }
    }

    /**
     * Save the base URL name.
     */
    fun setSecondaryBaseUrlName(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveBaseUrl2Name(name)
        }
    }

    /**
     * Switch the base URL.
     */
    fun changeBaseUrl(selectedUrl: Int) {
        viewModelScope.launch {
            userPreferencesRepository.changeBaseUrl(selectedUrl)
        }
    }

    /**
     * Logout the user.
     */
    open fun logout(navController: NavController) {
        viewModelScope.launch {
            // Navigate to the login screen
            navController.navigate(MainNavOption.LoginScreen.name) {
                // Pop up to the main route to clear the back stack
                popUpTo(NavRoutes.MainRoute.name) {
                    inclusive = true
                }
            }
            // Update the logged-in status
            userPreferencesRepository.saveLoggedIn(false)

            // Logout from the app webserver
            userPreferencesRepository.logoutFromServer()

            // Clear the session
            SessionManager.clearSession()
        }
    }

    /**
     *
     */
    suspend fun testRetrofitConnection(context: Context) {
        // Show loading message
        ToastUtils.showToast(
            context, Toast.LENGTH_LONG, context.getString(R.string.connecting_to_server)
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform a test API call using the repository's service
                val response = userPreferencesRepository.testApiCall()

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> ToastUtils.showToast(
                            context,
                            Toast.LENGTH_LONG,
                            context.getString(R.string.error_api_not_found, response.code())
                        )

                        401 -> NetworkSuccessHandler.handleUnauthorized(context, viewModelScope) {
                            // Handle the unauthorized response
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
                }
            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            } catch (e: Exception) {
                // Handle generic exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            }
        }
    }

    /**
     *
     */
    fun setNotSecuredApi(isNotSecuredApi: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIsNotSecuredApi(isNotSecuredApi)
        }
    }

    /**
     *
     */
    fun setSwipeToDelete(isSwipeToDeleteDisabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIsSwipeToDeleteDisabled(isSwipeToDeleteDisabled)
        }
    }

    /**
     *
     */
    fun setSwipeToDuplicate(isSwipeToDuplicateDisabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIsSwipeToDuplicateDisabled(isSwipeToDuplicateDisabled)
        }
    }

}

// Example
//    // Flow to observe baseUrl value
//    private val baseUrl: Flow<String> = userPreferencesRepository.getBaseUrl()
//
//    private val baseUrlUiState = baseUrl.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
//        initialValue = ""
//    )
//
//    // Flow to observe activeButtons value
//    private val activeButtons: Flow<EnumMap<MainNavOption, Boolean>> =
//        userPreferencesRepository.getActiveButtons()
//
//    open val activeButtonsUiState = activeButtons.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
//        initialValue = EnumMap(MainNavOption::class.java)
//
//    )
