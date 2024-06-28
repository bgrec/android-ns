package com.mastrosql.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.PRIMARY_URL
import com.mastrosql.app.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedUrl = MutableStateFlow(PRIMARY_URL)
    val selectedUrl: StateFlow<Int> = _selectedUrl

    init {
        observeSelectedUrl()
    }

    private fun observeSelectedUrl() {
        viewModelScope.launch {
            userPreferencesRepository
                .getSelectedUrl()
                .collect { newUrl ->
                    _selectedUrl.value = newUrl
                }
        }
    }
}
