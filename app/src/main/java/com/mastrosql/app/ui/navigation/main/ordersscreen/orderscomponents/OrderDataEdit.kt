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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order

@Composable
fun OrderDataEdit(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    destination: DestinationData? = null,
    onDismissButton: (Boolean) -> Unit = {},
    onConfirmButton: (Order) -> Unit = {}
) {

    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showDatePickerDialog = remember { mutableStateOf(false) }

        //Title
        Row {
            Text(
                text = stringResource(R.string.new_order),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            OutlinedTextField(
                value = customer?.businessName ?: "",//orderDetailsItemState.batch.value,
                label = { Text("Cliente") },
                onValueChange = { },
                readOnly = true,
            )
        }
        if (destination != null) {
            Spacer(modifier = Modifier.padding(4.dp))
            Row {
                OutlinedTextField(
                    value = destination.destinationName ?: "",//orderDetailsItemState.batch.value,
                    label = { Text("Destinazione") },
                    onValueChange = { },
                    readOnly = true,
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
        }

        Row {
            OutlinedTextField(
                value = "",//orderDetailsItemState.batch.value,
                label = { Text("Descrizione ordine") },
                onValueChange = { /*orderDetailsItemState.batch.value = it*/ },
                //isError = orderDetailsItemState.batch.value.text.isEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        Row {
            OutlinedTextField(singleLine = true,
                value = ""/*orderDetailsItemState.expirationDate.value*/,
                label = { Text(stringResource(R.string.order_details_dialog_edit_expirationDate)) },
                onValueChange = {
                    /*orderDetailsItemState.expirationDate.value = it*/
                },
                modifier = Modifier.clickable(onClick = {
                    showDatePickerDialog.value = true
                }),//not working
                readOnly = false,
                /*isError = orderDetailsItemState.expirationDate.value.text.isNotEmpty() && DateHelper.formatDateToInput(
                    orderDetailsItemState.expirationDate.value.text
                ).isEmpty() && DateHelper.isDateBeforeToday(
                    orderDetailsItemState.expirationDate.value.text
                ),*/
                //visualTransformation = DateTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePickerDialog.value = true
                    }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                })
        }

        if (showDatePickerDialog.value) {
            DateEditDialog(
                showDatePickerDialog = showDatePickerDialog,
                //orderDetailsItemState = orderDetailsItemState
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {

            TextButton(
                onClick = { onDismissButton(false) }
            ) {
                Text("Annulla")
            }

            TextButton(
                onClick = {
                    //onConfirmButton()
                }
            ) {
                Text("Conferma")
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