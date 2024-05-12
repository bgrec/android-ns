package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

data class OrderState(
    var customerId: MutableIntState,
    var customerName: MutableState<TextFieldValue>,
    var destinationId: MutableIntState,
    var destinationName: MutableState<TextFieldValue>,
    var orderDescription: MutableState<TextFieldValue>,
    var deliveryDate: MutableState<TextFieldValue>
)
