package com.mastrosql.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

// Define CompositionLocal for AppNavigationViewModel
val LocalAppNavigationViewModelProvider = staticCompositionLocalOf<AppNavigationViewModel> {
    error("No AppNavigationViewModel provided")
    //TODO: Add a proper error message here and handle it
}

// Function to provide AppNavigationViewModel to the Composable tree
@Composable
fun ProvideAppNavigationViewModel(content: @Composable () -> Unit) {
    val appNavigationViewModel = remember { AppNavigationViewModel() }
    CompositionLocalProvider(LocalAppNavigationViewModelProvider provides appNavigationViewModel) {
        content()
    }
}

