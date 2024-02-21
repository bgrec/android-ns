package com.mastrosql.app.ui.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProvideGestureViewModel(content: @Composable () -> Unit) {
    //val gestureViewModel = viewModel<MainComposeGestureViewModel>()
    val gestureViewModel = remember { MainComposeGestureViewModel() }
    CompositionLocalProvider(LocalProvideGestureViewModel provides gestureViewModel) {
        content()
    }
}
val LocalProvideGestureViewModel = staticCompositionLocalOf<MainComposeGestureViewModel> {
    error("No MainComposeGestureViewModel provided")
}
