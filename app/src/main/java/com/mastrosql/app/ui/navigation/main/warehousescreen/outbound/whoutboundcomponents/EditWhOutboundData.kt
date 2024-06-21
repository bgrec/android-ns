package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound

@ExperimentalMaterial3Api
@Composable
fun EditWhOutboundData(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    destination: DestinationData? = null,
    onDismissButton: (Boolean) -> Unit = {},
    onConfirmButton: (WarehouseOutbound) -> Unit = {},
) {

    //State to hold the modified warehouse outbound state data
    val whOutboundState by remember { mutableStateOf(WhOutboundState()) }

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(whOutboundState) {
        focusRequester.requestFocus()
    }

//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(Unit) { // Trigger only once on composition
//        focusRequester.requestFocus()
//    }

    whOutboundState.customerId.intValue = customer?.id ?: 0
    whOutboundState.customerName.value = TextFieldValue(customer?.businessName ?: "")
//    whOutboundState.destinationId.intValue = destination?.id ?: 0
//    whOutboundState.destinationName.value = TextFieldValue(destination?.destinationName ?: "")

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showDatePickerDialog = remember { mutableStateOf(false) }

        //Title of the dialog
        Row {
            Text(
                text = stringResource(R.string.new_order),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Customer Name (Read-only)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = textFieldModifier,
                value = whOutboundState.customerName.value,
                label = { Text(stringResource(id = R.string.businessName)) },
                onValueChange = { },
                readOnly = true,
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        // Destination Name (Read-only if destination is not null)
        if (destination != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    modifier = textFieldModifier,
                    value = "",//whOutboundState.destinationName.value,
                    label = { Text(stringResource(id = R.string.destination_description)) },
                    onValueChange = { },
                    readOnly = true,
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
        }

        // Order Description
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = textFieldModifier.focusRequester(focusRequester),
                value = "",//whOutboundState.orderDescription.value,
                label = { Text(stringResource(id = R.string.order_description)) },
                onValueChange = { /*whOutboundState.orderDescription.value = it*/ },
                isError = false, //whOutboundState.orderDescription.value.text.isEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        val isDateValid = false
//            remember(whOutboundState.deliveryDate.value.text) {
//            DateHelper
//                .formatDateToInput(whOutboundState.deliveryDate.value.text)
//                .isNotEmpty()
//        }
        val isDateBeforeToday = false
//            remember(whOutboundState.deliveryDate.value.text) {
//            DateHelper.isDateBeforeToday(whOutboundState.deliveryDate.value.text)
//        }

        // Delivery Date
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(modifier = textFieldModifier.clickable(onClick = {
                showDatePickerDialog.value = true
            }),
                singleLine = true,
                value = /*whOutboundState.deliveryDate.value*/ TextFieldValue(""),
                label = { Text(stringResource(R.string.order_deliveryDate)) },
                onValueChange = {
//                    whOutboundState.deliveryDate.value = it
                },
                readOnly = false,
                isError = false,
                //whOutboundState.deliveryDate.value.text.isNotEmpty() && (!isDateValid || isDateBeforeToday),
                //visualTransformation = DateTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePickerDialog.value = true
                    }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_a_date)
                        )
                    }
                },
                placeholder = { Text("gg/mm/aaaa") })
        }

        if (showDatePickerDialog.value) {
            DateEditDialog(
                showDatePickerDialog = showDatePickerDialog, whOutboundState = whOutboundState
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {

            TextButton(onClick = { onDismissButton(false) }) {
                Text(stringResource(id = R.string.dismiss_button))
            }

            TextButton(onClick = {
//                val newOrder = OrderUtils.createNewOrderFromState(orderState)
//                onConfirmButton(newOrder)
            }) {
                Text(stringResource(id = R.string.confirm_button))
            }

            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}


