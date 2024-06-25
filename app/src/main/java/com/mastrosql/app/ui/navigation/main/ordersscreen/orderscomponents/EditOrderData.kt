package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderUtils
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme
import com.mastrosql.app.utils.DateHelper

/**
 * OrderDataEdit composable to display the order details for editing.
 */
@ExperimentalMaterial3Api
@Composable
fun EditOrderData(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    destination: DestinationData? = null,
    onDismissButton: (Boolean) -> Unit = {},
    onConfirmButton: (Order) -> Unit = {},
) {

    //State to hold the modified order details item
    val orderState by remember { mutableStateOf(OrderState()) }

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(orderState) {
        focusRequester.requestFocus()
    }

//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(Unit) { // Trigger only once on composition
//        focusRequester.requestFocus()
//    }

    orderState.customerId.intValue = customer?.id ?: 0
    orderState.customerName.value = TextFieldValue(customer?.businessName ?: "")
    orderState.destinationId.intValue = destination?.id ?: 0
    orderState.destinationName.value = TextFieldValue(destination?.destinationName ?: "")

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
                value = orderState.customerName.value,
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
                    value = orderState.destinationName.value,
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
                value = orderState.orderDescription.value,
                label = { Text(stringResource(id = R.string.order_description)) },
                onValueChange = { orderState.orderDescription.value = it },
                isError = orderState.orderDescription.value.text.isEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        val isDateValid = remember(orderState.deliveryDate.value.text) {
            DateHelper
                .formatDateToInput(orderState.deliveryDate.value.text)
                .isNotEmpty()
        }
        val isDateBeforeToday = remember(orderState.deliveryDate.value.text) {
            DateHelper.isDateBeforeToday(orderState.deliveryDate.value.text)
        }

        // Delivery Date
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(modifier = textFieldModifier.clickable(onClick = {
                showDatePickerDialog.value = true
            }),
                singleLine = true,
                value = orderState.deliveryDate.value,
                label = { Text(stringResource(R.string.row_deliveryDate)) },
                onValueChange = {
                    orderState.deliveryDate.value = it
                },
                readOnly = false,
                isError = orderState.deliveryDate.value.text.isNotEmpty() && (!isDateValid || isDateBeforeToday),
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
                showDatePickerDialog = showDatePickerDialog, orderState = orderState
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
                val newOrder = OrderUtils.createNewOrderFromState(orderState)
                onConfirmButton(newOrder)
            }) {
                Text(stringResource(id = R.string.confirm_button))
            }

            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}

/**
 * OrderState
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun OrderDataEditPreview() {
    MastroAndroidPreviewTheme {
        EditOrderData(customer = CustomerMasterData(
            id = 1,
            businessName = "Customer 1",
            street = "Street 1",
            postalCode = "12345",
            city = "City 1",
            province = "Province 1",
            nation = "Nation 1",
            businessName2 = "Customer 1",
            taxId = "taxId 1",
            vat = "vat 1",
            links = emptyList(),
            metadata = Metadata(etag = ""),
            page = 0
        ), destination = DestinationData(
            id = 1,
            customerId = 1,
            destinationName = "Destination 1",
            street = "Street 1",
            postalCode = "12345",
            city = "City 1",
            province = "Province 1",
            links = emptyList(),
            metadata = com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.Metadata(
                etag = ""
            ),
            page = 0
        ), onDismissButton = {}, onConfirmButton = {})
    }
}
