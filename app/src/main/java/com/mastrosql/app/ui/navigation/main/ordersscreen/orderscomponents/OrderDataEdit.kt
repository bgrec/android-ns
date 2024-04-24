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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderUtils
import com.mastrosql.app.utils.DateHelper

@Composable
fun OrderDataEdit(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    destination: DestinationData? = null,
    onDismissButton: (Boolean) -> Unit = {},
    onConfirmButton: (Order) -> Unit = {},
) {

    //State to hold the modified order details item
    val orderState by remember {
        mutableStateOf(
            OrderState(
                mutableIntStateOf(0),
                mutableStateOf(TextFieldValue("")),
                mutableIntStateOf(0),
                mutableStateOf(TextFieldValue("")),
                mutableStateOf(TextFieldValue("")),
                mutableStateOf(TextFieldValue(""))
            )
        )
    }

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(orderState) {
        focusRequester.requestFocus()
    }

    orderState.customerId.intValue = customer?.id ?: 0
    orderState.customerName.value = TextFieldValue(customer?.businessName ?: "")
    orderState.destinationId.intValue = destination?.id ?: 0
    orderState.destinationName.value = TextFieldValue(destination?.destinationName ?: "")

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = textFieldModifier
                    .focusRequester(focusRequester),
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = textFieldModifier.clickable(onClick = {
                    showDatePickerDialog.value = true
                }),
                singleLine = true,
                value = orderState.deliveryDate.value,
                label = { Text(stringResource(R.string.order_details_dialog_edit_expirationDate)) },
                onValueChange = {
                    orderState.deliveryDate.value = it
                },
                readOnly = false,
                isError = orderState.deliveryDate.value.text.isNotEmpty() && DateHelper.formatDateToInput(
                    orderState.deliveryDate.value.text
                ).isEmpty() && DateHelper.isDateBeforeToday(
                    orderState.deliveryDate.value.text
                ),
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
                })
        }

        if (showDatePickerDialog.value) {
            DateEditDialog(
                showDatePickerDialog = showDatePickerDialog,
                orderState = orderState
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {

            TextButton(
                onClick = { onDismissButton(false) }
            ) {
                Text(stringResource(id = R.string.dismiss_button))
            }

            TextButton(
                onClick = {
                    val newOrder = OrderUtils.createNewOrderFromState(orderState)
                    onConfirmButton(newOrder)
                }
            ) {
                Text(stringResource(id = R.string.confirm_button))
            }

            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}


/*
Column(
    modifier = Modifier.padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {/*
        val rowKeyboardController = LocalSoftwareKeyboardController.current

        if (scannerState.isTextInputFocused.value) {
            keyboardController?.hide()
        }

        // On opening the keyboard si always hidden
        rowKeyboardController?.hide()

        // If the text field was pressed for manual entry, show the keyboard
        if (scannerState.isTextFieldPressed.value) {
            rowKeyboardController?.show()
            scannerState.isKeyboardVisible.value = true*/
    }

    // Icon button to show or hide the software keyboard
    IconButton(
        onClick = {/*
                // Toggle keyboard visibility
                scannerState.isKeyboardVisible.value =
                    if (scannerState.isKeyboardVisible.value) {
                        rowKeyboardController?.hide()
                        false
                    } else {
                        rowKeyboardController?.show()
                        scannerState.isTextFieldPressed.value = true
                        true
                    }
            */
        }
    ) {
        Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
    }

    // Text input to read scanned codes
    /*OutlinedTextField(
        value = scannerState.scannedCode.value,
        onValueChange = {
            scannerState.scannedCode.value = it
            // Check if the last character is a newline character
            if (it.endsWith("\n")) {
                // Call ViewModel method to send scanned code to server
                if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0
                    && scannerState.scannedCode.value.isNotEmpty()
                ) {
                    onSendScannedCode(
                        orderDetailsUiState.orderId,
                        scannerState.scannedCode.value
                    )
                }
                // Clear the scanned code after sending
                scannerState.scannedCode.value = ""
            }
        },
        label = { Text(stringResource(R.string.order_details_qrscan_text)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                // Call ViewModel method to send scanned code to server
                if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0
                    && scannerState.scannedCode.value.isNotEmpty()
                ) {
                    onSendScannedCode(
                        orderDetailsUiState.orderId,
                        scannerState.scannedCode.value
                    )
                }
                // Clear the scanned code after sending
                scannerState.scannedCode.value = ""

                rowKeyboardController?.hide()
                scannerState.isKeyboardVisible.value = false
                scannerState.isTextFieldPressed.value = false
            }
        ),

        modifier = Modifier
            .weight(0.5f)
            .padding(horizontal = 8.dp)
            .focusRequester(focusRequester),

        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            // works like onClick for the text field
                            scannerState.isTextFieldPressed.value = true
                        }
                    }
                }
            }
    )*/

    // Button to send the scanned code to the server
    IconButton(
        onClick = {
            /*rowKeyboardController?.hide()
            scannerState.isKeyboardVisible.value = false
            scannerState.isTextFieldPressed.value = false

            // Call ViewModel method to send scanned code to server
            if (orderDetailsUiState.orderId != null
                && orderDetailsUiState.orderId > 0 && scannerState.scannedCode.value.isNotEmpty()
            ) {
                onSendScannedCode(
                    orderDetailsUiState.orderId,
                    scannerState.scannedCode.value
                )
            }

            // Clear the scanned code after sending
            scannerState.scannedCode.value = ""*/
        }
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send Scanned Code"
        )
    }
}*/