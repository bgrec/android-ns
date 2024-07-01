package com.mastrosql.app.ui.navigation.main.homescreen

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.EnumMap

/**
 * The UI state for the Home screen.
 */
@Suppress("KDocMissingDocumentation")
data class HomeUiState(
    val activeButtons: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java),
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
        viewModelScope.launch {
            combine(
                userPreferencesRepository.getSelectedUrl(),
                userPreferencesRepository.getBaseUrlName(),
                userPreferencesRepository.getBaseUrl2Name(),
                userPreferencesRepository.getActiveButtons()
            ) { selectedUrl, primaryUrlName, secondaryUrlName, activeButtons ->
                Quadruple(selectedUrl, primaryUrlName, secondaryUrlName, activeButtons)
            }.collect { (selectedUrl, primaryUrlName, secondaryUrlName, activeButtons) ->
                val selectedUrlName =
                    updateSelectedUrlName(selectedUrl, primaryUrlName, secondaryUrlName)
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedUrl = selectedUrl,
                        primaryUrlName = primaryUrlName,
                        secondaryUrlName = secondaryUrlName,
                        selectedUrlName = selectedUrlName,
                        activeButtons = activeButtons
                    )
                }
                userPreferencesRepository.changeBaseUrl(selectedUrl)
            }//.launchIn(viewModelScope)
        }
    }

    private fun updateSelectedUrlName(
        selectedUrl: Int, primaryUrlName: String, secondaryUrlName: String
    ): String {
        return when (selectedUrl) {
            PRIMARY_URL -> if (primaryUrlName == PRIMARY_URL_NAME) "" else primaryUrlName
            SECONDARY_URL -> if (secondaryUrlName == SECONDARY_URL_NAME) "" else secondaryUrlName
            else -> "" // Handle unexpected values if needed
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
}

/**
 * A simple data class to hold four values.
 */
data class Quadruple<A, B, C, D>(
    val first: A, val second: B, val third: C, val fourth: D
)
