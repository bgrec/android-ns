package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

/**
 * WhOutboundState class to hold the state of the outbound details.
 */
@Suppress("KDocMissingDocumentation")
data class WhOutboundState(
    var operationId: MutableIntState = mutableIntStateOf(0),
    var customerId: MutableIntState = mutableIntStateOf(0),
    var customerName: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
)
