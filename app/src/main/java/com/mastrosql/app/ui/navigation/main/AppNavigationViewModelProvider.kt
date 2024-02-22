package com.mastrosql.app.ui.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun AppNavigationViewModelProvider(content: @Composable () -> Unit) {
    //val gestureViewModel = viewModel<MainComposeGestureViewModel>()
    val appNavigationViewModel = remember { AppNavigationViewModel() }
    CompositionLocalProvider(LocalAppNavigationViewModelProvider provides appNavigationViewModel) {
        content()
    }
}

val LocalAppNavigationViewModelProvider = staticCompositionLocalOf<AppNavigationViewModel> {
    error("No AppNavigationViewModel provided")
}
