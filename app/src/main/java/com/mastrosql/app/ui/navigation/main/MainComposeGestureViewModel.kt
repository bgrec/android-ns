package com.mastrosql.app.ui.navigation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainComposeGestureViewModel : ViewModel() {
    private val _gesturesEnabled = mutableStateOf(false)
    val gesturesEnabled: State<Boolean> = _gesturesEnabled

    fun setGesturesEnabled(enabled: Boolean) {
        _gesturesEnabled.value = enabled
    }
}