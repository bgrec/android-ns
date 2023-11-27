package com.mastrosql.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserPreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // UI states access for various [DessertReleaseUiState]
    val isLinearLayoutUiState: StateFlow<DessertReleaseUiState> =
        userPreferencesRepository.isLinearLayout.map { isLinearLayout ->
            DessertReleaseUiState(isLinearLayout)
        }.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DessertReleaseUiState()
        )

    // UI states access for various [IsOnBoardingCompletedUiState]

    val isOnBoardingCompletedUiState: StateFlow<IsOnBoardingCompletedUiState> =
        userPreferencesRepository.isOnboardingCompleted.map { isOnBoardingCompleted ->
            IsOnBoardingCompletedUiState(isOnBoardingCompleted)
        }.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IsOnBoardingCompletedUiState()
        )

    /*
     * [selectLayout] change the layout and icons accordingly and
     * save the selection in DataStore through [userPreferencesRepository]
     */
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
            // Call the saveUserOnboarding method in IntroViewModel when onboarding is completed
            /* if (isOnBoardingCompleted) {
                 introViewModel.saveUserOnboarding()
             }*/
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

data class IsOnBoardingCompletedUiState(
    val isOnBoardingCompleted: Boolean = false
)