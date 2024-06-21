package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Data class to hold the state of the scanner bottom sheet
 */
data class ScannerState(
    /**
     * The scanned code
     */
    var scannedCode: MutableState<String> = mutableStateOf(""),
    /**
     * The state of the text input field
     */
    var isTextInputFocused: MutableState<Boolean> = mutableStateOf(false),
    /**
     * The state of the text field
     */
    var isTextFieldPressed: MutableState<Boolean> = mutableStateOf(false),
    /**
     * The state of the keyboard visibility
     */
    var isKeyboardVisible: MutableState<Boolean> = mutableStateOf(false)
)
