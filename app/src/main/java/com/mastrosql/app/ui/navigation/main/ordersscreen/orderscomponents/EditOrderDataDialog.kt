package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersUiState
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import com.mastrosql.app.utils.DateHelper

/**
 * OrderDataEdit composable to display the order details for editing.
 */

@ExperimentalMaterial3Api
@Composable
fun EditOrderDataDialog(
    modifier: Modifier = Modifier,
    showEditOrderDataDialog: MutableState<Boolean>,
    ordersUiState: OrdersUiState.Success,
    onUpdateOrderData: (OrderState) -> Unit = {}
) {
    //State to hold the modified order details item
    val orderState by remember { mutableStateOf(OrderState()) }

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showEditOrderDataDialog) {
        if (showEditOrderDataDialog.value) {
            val modifiedOrder =
                ordersUiState.ordersList.find { it.id == ordersUiState.modifiedOrderId.intValue }
            if (modifiedOrder != null) {
                orderState.orderId.intValue = modifiedOrder.id
                orderState.customerId.intValue = modifiedOrder.clientId ?: 0
                orderState.customerName.value = TextFieldValue(modifiedOrder.businessName ?: "")
                orderState.destinationId.intValue = modifiedOrder.destinationId ?: 0
                orderState.destinationName.value =
                    TextFieldValue(modifiedOrder.destinationName ?: "")
                orderState.orderDescription.value = TextFieldValue(modifiedOrder.description ?: "")

                // Update the delivery state
                orderState.deliveryDate.value = (modifiedOrder.deliveryDate?.let {
                    TextFieldValue(
                        DateHelper.formatDateToDisplay(it)
                    )
                } ?: return@LaunchedEffect)
            }
        }
    }

//    // Create a FocusRequester
//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }

    AlertDialog(modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showEditOrderDataDialog.value = false },
        title = {
            Text(
                stringResource(
                    R.string.order_details_edit,
                    orderState.orderId.intValue,
                    orderState.orderDescription.value.text
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                // State to show the date picker dialog
                val showDatePickerDialog = remember { mutableStateOf(false) }

                // TextField modifier
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)

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
                if (orderState.destinationId.intValue > 0) {
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
                        modifier = textFieldModifier,
                        // not requesting focus on this field
                        //.focusRequester(focusRequester),
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
                        label = { Text(stringResource(R.string.order_deliveryDate)) },
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
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showEditOrderDataDialog.value = false
            }) {
                Text(stringResource(R.string.dismiss_button))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showEditOrderDataDialog.value = false
                onUpdateOrderData(orderState)
            }) {
                Text(stringResource(R.string.confirm_button))
            }
        })
}

/**
 * Preview for [EditOrderDataDialog]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun PreviewEditOrderDataDialog() {
    MastroAndroidTheme {
        EditOrderDataDialog(
            showEditOrderDataDialog = mutableStateOf(true), ordersUiState = OrdersUiState.Success(
                ordersList = emptyList(), modifiedOrderId = mutableIntStateOf(1)
            )
        )
    }
}
