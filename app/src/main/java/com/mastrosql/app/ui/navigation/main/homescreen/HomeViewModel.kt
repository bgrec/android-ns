package com.mastrosql.app.ui.navigation.main.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mastrosql.app.PRIMARY_URL
import com.mastrosql.app.PRIMARY_URL_NAME
import com.mastrosql.app.SECONDARY_URL
import com.mastrosql.app.SECONDARY_URL_NAME
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.EnumMap

/**
 * The UI state for the Home screen.
 */
@Suppress("KDocMissingDocumentation")
data class HomeUiState(
    val activeButtons: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java),
    val isSecondaryBaseUrlProvided: Boolean = false,
    val selectedUrl: Int = PRIMARY_URL,
    val selectedUrlName: String = "",
    val primaryUrlName: String = PRIMARY_URL_NAME,
    val secondaryUrlName: String = SECONDARY_URL_NAME
)

/**
 * The ViewModel class for the Login screen.
 */
class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Private mutable state flow to hold the UI state.
     */
    private val _uiState = MutableStateFlow(HomeUiState())

    /**
     * Public UI state for the Home screen exposed as a state flow.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow() //Exposed as a state flow

    /**
     * Update the UI state.
     */
    private fun updateUiState(newState: HomeUiState) {
        _uiState.value = newState
    }

    /**
     * Initialize the ViewModel and collect the flow values from the data store.
     */
    init {
        // Collect and update active buttons
        viewModelScope.launch {
            userPreferencesRepository
                .getActiveButtons()
                .collect { activeButtons ->
                    updateUiState(uiState.value.copy(activeButtons = activeButtons))
                }
        }

        // Collect and update the primary URL name
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrlName()
                .collect {
                    updateUiState(uiState.value.copy(primaryUrlName = it))
                }
        }

        // Collect and update the secondary URL name
        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl2Name()
                .collect {
                    updateUiState(uiState.value.copy(secondaryUrlName = it))
                }
        }

        // Collect selected URL, update UI state, and change base URL
        viewModelScope.launch {
            userPreferencesRepository
                .getSelectedUrl()
                .collect { selectedUrl ->
                    Log.d("HomeViewModel", "Selected URL: $selectedUrl")
                    _uiState.update { currentState ->
                        val selectedUrlName = when (selectedUrl) {
                            PRIMARY_URL -> if (currentState.primaryUrlName == PRIMARY_URL_NAME) ""
                            else currentState.primaryUrlName

                            SECONDARY_URL -> if (currentState.secondaryUrlName == SECONDARY_URL_NAME) ""
                            else currentState.secondaryUrlName

                            else -> "" // Handle unexpected values if needed
                        }
                        currentState.copy(
                            selectedUrl = selectedUrl, selectedUrlName = selectedUrlName
                        )
                    }
                    userPreferencesRepository.changeBaseUrl(selectedUrl)
                }
        }
    }

//    fun updateSelectedUrlName() {
//        viewModelScope.launch {
//            val selectedUrl = userPreferencesRepository.getSelectedUrl()
//            val updatedSelectedUrlName = when (selectedUrl) {
//                PRIMARY_URL -> userPreferencesRepository.getBaseUrlName().
//                SECONDARY_URL -> userPreferencesRepository.getBaseUrl2Name()
//                else -> ""
//            }
//            _uiState.update { currentState ->
//                currentState.copy(selectedUrlName = updatedSelectedUrlName)
//            }
//        }
//    }

    /**
     * Logout the user.
     */
    fun logout(navController: NavController) {
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
}
