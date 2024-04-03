package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

//Data class to hold the state of the order details item that is being edited
data class OrderDetailsItemState(
    var batch: MutableState<TextFieldValue>,
    var quantity: MutableState<TextFieldValue>,
    var expirationDate: MutableState<TextFieldValue>
)
