package com.mastrosql.app.ui.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mastrosql.app.ui.navigation.main.MainNavOption

class AppNavigationViewModel : ViewModel() {
    private val _gesturesEnabled = mutableStateOf(false)
    val gesturesEnabled: State<Boolean> = _gesturesEnabled

    fun setGesturesEnabled(enabled: Boolean) {
        _gesturesEnabled.value = enabled
    }

    private val _currentScreen = mutableStateOf<MainNavOption?>(null)
    val currentScreen: State<MainNavOption?> = _currentScreen

    // Flow to observe current screen
    private fun updateGestures() {
        val currentScreenValue = _currentScreen.value

        _gesturesEnabled.value = when (currentScreenValue) {
            MainNavOption.LoginScreen -> false
            MainNavOption.HomeScreen -> false
            MainNavOption.OldHomeScreen -> true
            MainNavOption.CustomersScreen -> true
            MainNavOption.CustomersPagedScreen -> true
            MainNavOption.ArticlesScreen -> true
            MainNavOption.ItemsScreen -> true
            MainNavOption.OrdersScreen -> true
            MainNavOption.SettingsScreen -> false
            MainNavOption.CartScreen -> true
            MainNavOption.AboutScreen -> false
            MainNavOption.Logout -> false
            // Removed the "else" case to avoid cases where the value is another screen that is not handled here
            //else -> false
            null -> false
        }
    }

    fun setCurrentScreen(screen: MainNavOption) {
        _currentScreen.value = screen

        //Update gestures when screen changes
        updateGestures()
    }
}