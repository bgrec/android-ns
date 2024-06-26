package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mastrosql.app.PRIMARY_URL
import com.mastrosql.app.PRIMARY_URL_NAME
import com.mastrosql.app.SECONDARY_URL_NAME
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val selectedUrlName: String = PRIMARY_URL_NAME,
    val baseUrlName: String = PRIMARY_URL_NAME,
    val baseUrl2Name: String = SECONDARY_URL_NAME
)

/**
 * The ViewModel class for the Login screen.
 */
class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Mutable state flow to hold the UI state.
     */
    private val _uiState = MutableStateFlow(HomeUiState())

    /**
     * UI state for the Login screen.
     */
    val uiState: StateFlow<HomeUiState> = _uiState

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
        // Observe changes in activeButtons and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getActiveButtons()
                .collect { activeButtons ->
                    updateUiState(uiState.value.copy(activeButtons = activeButtons))
                }
        }

        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrlName()
                .collect {
                    updateUiState(uiState.value.copy(baseUrlName = it))
                }
        }

        viewModelScope.launch {
            userPreferencesRepository
                .getBaseUrl2Name()
                .collect {
                    updateUiState(uiState.value.copy(baseUrl2Name = it))
                }
        }

        viewModelScope.launch {
            userPreferencesRepository
                .getSelectedUrl()
                .collect {
                    updateUiState(uiState.value.copy(selectedUrl = it))
                }
            userPreferencesRepository.changeBaseUrl(uiState.value.selectedUrl)
        }
        // Observe changes in selectedUrl and update UI state accordingly
        viewModelScope.launch {
            userPreferencesRepository
                .getSelectedUrl()
                .collect { selectedUrl ->
                    updateUiState(uiState.value.copy(selectedUrl = selectedUrl))
                    updateUiState(
                        uiState.value.copy(
                            selectedUrlName = if (selectedUrl == PRIMARY_URL) {
                                uiState.value.baseUrlName
                            } else {
                                uiState.value.baseUrl2Name
                            }
                        )
                    )
                }
            userPreferencesRepository.changeBaseUrl(uiState.value.selectedUrl)
        }

    }

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

    /**
     * Switch the base URL.
     */
    fun changeBaseUrl(selectedUrl: Int) {
        viewModelScope.launch {
            userPreferencesRepository.changeBaseUrl(selectedUrl)
        }
    }
}
