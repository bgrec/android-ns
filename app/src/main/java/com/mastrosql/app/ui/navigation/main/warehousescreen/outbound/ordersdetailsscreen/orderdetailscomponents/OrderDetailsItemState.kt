package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Data class to hold the state of the order details item that is being edited
 */
data class OrderDetailsItemState(
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
