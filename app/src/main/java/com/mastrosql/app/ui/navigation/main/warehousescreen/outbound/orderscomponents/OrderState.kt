package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

/**
 * OrderState class to hold the state of the order details.
 */
@Suppress("KDocMissingDocumentation")
data class OrderState(
    var orderId: MutableIntState = mutableIntStateOf(0),
    var customerId: MutableIntState = mutableIntStateOf(0),
    var customerName: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    var destinationId: MutableIntState = mutableIntStateOf(0),
    var destinationName: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    var orderDescription: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    var deliveryDate: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
)
