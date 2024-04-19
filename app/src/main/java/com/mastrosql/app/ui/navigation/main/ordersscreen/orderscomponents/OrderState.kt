package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

data class OrderState(
    var batch: MutableState<TextFieldValue>,
    var quantity: MutableState<TextFieldValue>,
    var expirationDate: MutableState<TextFieldValue>
)
