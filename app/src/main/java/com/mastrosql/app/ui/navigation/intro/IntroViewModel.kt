package com.mastrosql.app.ui.navigation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//@HiltViewModel
class IntroViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Flow to observe isOnboardingComplete value
    private val isOnboardingComplete: Flow<Boolean> =
        userPreferencesRepository.isOnboardingCompleted

    // Expose the value as a StateFlow
    val isOnboarded = isOnboardingComplete.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0), // adjust the duration as needed
        initialValue = false
    )

    // Function to save user onboarding
    fun saveUserOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.saveOnBoardingCompleted(true)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferencesRepository.saveOnBoardingCompleted(false)
        }
    }

    /*private val _isOnboarded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isOnboarded = _isOnboarded.asStateFlow()


    fun saveUserOnboarding() {
        _isOnboarded.value = true
    }*/

}