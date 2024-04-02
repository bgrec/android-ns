package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.runtime.MutableState

//Data class to hold the state of the scanner bottom sheet
data class ScannerState(
    var scannedCode: MutableState<String>,
    var isTextInputFocused: MutableState<Boolean>,
    var isTextFieldPressed: MutableState<Boolean>,
    var isKeyboardVisible: MutableState<Boolean>
)