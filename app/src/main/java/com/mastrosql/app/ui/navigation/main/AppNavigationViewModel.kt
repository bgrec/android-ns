package com.mastrosql.app.ui.navigation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppNavigationViewModel : ViewModel() {
    private val _gesturesEnabled = mutableStateOf(false)
    val gesturesEnabled: State<Boolean> = _gesturesEnabled

    fun setGesturesEnabled(enabled: Boolean) {
        _gesturesEnabled.value = enabled
    }

    private val _currentScreen = mutableStateOf<MainNavOption?>(null)
    val currentScreen: State<MainNavOption?> = _currentScreen

    private fun updateGestures() {
        val currentScreenValue = _currentScreen.value

        _gesturesEnabled.value = when (currentScreenValue) {
            MainNavOption.LoginScreen -> false
            MainNavOption.NewHomeScreen -> false
            MainNavOption.HomeScreen -> true
            MainNavOption.CustomersScreen -> true
            MainNavOption.CustomersPagedScreen -> true
            MainNavOption.ArticlesScreen -> true
            MainNavOption.ItemsScreen -> true
            MainNavOption.OrdersScreen -> true
            MainNavOption.SettingsScreen -> false
            MainNavOption.CartScreen -> true
            else -> false
        }
    }
    fun setCurrentScreen(screen: MainNavOption) {
        _currentScreen.value = screen

        //Update gestures when screen changes
        updateGestures()
    }
}