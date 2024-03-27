package com.mastrosql.app.ui.navigation.main.settingsscreen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.util.EnumMap

class UserPreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    // Flow to observe isOnboardingComplete value
    private val isOnboarded: Flow<Boolean> =
        userPreferencesRepository.getIsOnboarded()

    val isOnboardedUiState = isOnboarded.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
        initialValue = false
    )

    private val isLoggedIn: Flow<Boolean> =
        userPreferencesRepository.getIsLoggedIn()

    val isLoggedInUiState = isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
        initialValue = false
    )

    private val baseUrl: Flow<String> =
        userPreferencesRepository.getBaseUrl()

    val baseUrlUiState = baseUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
            initialValue = ""
        )

    private val activeButtons: Flow<EnumMap<MainNavOption, Boolean>> =
        userPreferencesRepository.getActiveButtons()

    val activeButtonsUiState = activeButtons
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
            initialValue = EnumMap(MainNavOption::class.java)

        )

    fun updateActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        viewModelScope.launch {
            userPreferencesRepository.updateActiveButtons(activeButtons)
        }
    }

    fun selectLayout(isLinearLayout: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isLinearLayout)
        }
    }

    /*
     * [onBoardingCompleted] change intro screen and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun onBoardingCompleted(isOnBoardingCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveOnBoardingCompleted(isOnBoardingCompleted)
        }
    }

    private fun loginCompleted(isLoggedIn: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedIn(isLoggedIn)
        }
    }

    fun setBaseUrl(baseUrl: String) {
        viewModelScope.launch {
            if (try {
                    URL(baseUrl).toURI()
                    true
                } catch (e: Exception) {
                    false
                }
            ) {

                if (baseUrl.endsWith("/")) {
                    userPreferencesRepository.saveBaseUrl(baseUrl)
                } else {
                    userPreferencesRepository.saveBaseUrl("$baseUrl/")
                }
            }
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            // Navigate to the login screen
            navController.navigate(MainNavOption.LoginScreen.name) {
                // Pop up to the main route to clear the back stack
                popUpTo(NavRoutes.MainRoute.name) {
                    inclusive = true
                }
            }
            loginCompleted(false)

            //onBoardingCompleted(false)

            // Logout from the app webserver
            userPreferencesRepository.logoutFromServer()
            SessionManager.clearSession()
        }
    }

    fun testRetrofitConnection(context: Context) {
        // Show loading message
        showToast(context, "Collegamento in corso...attendere")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform a test API call using the repository's service
                val response = userPreferencesRepository.testApiCall()
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> showToast(context, "Collegamento riuscito ${response.code()}")

                        401 -> showToast(
                            context,
                            "Collegamento riuscito, non autorizzato ${response.code()}"
                        )

                        404 -> showToast(
                            context,
                            "Collegamento riuscito, api non trovata ${response.code()}"
                        )

                        500 -> {
                            showToast(
                                context,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        503 -> {
                            showToast(
                                context,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        else -> showToast(context, "Errore api: ${response.code()}")
                    }
                }
            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                showToast(context, "Network error occurred: ${e.message}")
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                showToast(context, "HTTP error occurred: ${e.message}")
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                showToast(context, "Connection timed out. Please try again later.")
            } catch (e: Exception) {
                // Handle generic exception
                showToast(context, "An unexpected error occurred: ${e.message}")
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        val jsonError = JSONObject(errorBody ?: "null")
        return jsonError.optString("message")
    }

    private fun showToast(context: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            } else {
                // Hide loading message by not showing any toast
            }
        }
    }

}

data class DessertReleaseUiState(
    val isLinearLayout: Boolean = true,
    val toggleContentDescription: Int = 1,
    //if (isLinearLayout) R.string.grid_layout_toggle else R.string.linear_layout_toggle,
    val toggleIcon: Int = 2,
    //if (isLinearLayout) R.drawable.ic_grid_layout else R.drawable.ic_linear_layout
)

data class IsLoggedInUiState(
    val isOnBoardingCompleted: Boolean = false
)
