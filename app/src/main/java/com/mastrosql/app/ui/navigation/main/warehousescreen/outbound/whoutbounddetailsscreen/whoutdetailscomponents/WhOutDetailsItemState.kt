package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Data class to hold the state of the warehouse outbound details item that is being edited
 */
data class WhOutDetailsItemState(
    /**
     * The state of the batch text field
     */
    var batch: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    /**
     * The state of the quantity text field
     */
    var quantity: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    /**
     * The state of the expiration date text field
     */
    var expirationDate: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
)
