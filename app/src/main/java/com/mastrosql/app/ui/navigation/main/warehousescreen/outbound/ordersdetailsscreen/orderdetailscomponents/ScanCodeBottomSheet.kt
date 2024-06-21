package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.OrderDetailsUiState

/**
 * Composable function to show a bottom sheet to scan codes.
 */
@ExperimentalMaterial3Api
@Composable
fun ScanCodeBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    orderDetailsUiState: OrderDetailsUiState.Success,
    scannerState: ScannerState,
    onSendScannedCode: (Int, String) -> Unit,
) {

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }

    // State to control the bottom sheet
    val sheetState = rememberModalBottomSheetState()

    // Function to set focus on the text input when bottom sheet is opened
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet.value) {
            scannerState.isTextInputFocused.value = true
            focusRequester.requestFocus()
            keyboardController?.hide()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
            scannerState.isTextInputFocused.value = false
            scannerState.isTextFieldPressed.value = false
        }, sheetState = sheetState, modifier = Modifier.nestedScroll(
            connection = rememberNestedScrollInteropConnection()
        )
    ) {
        KeyboardBarcodeReader(
            scannerState = scannerState,
            orderDetailsUiState = orderDetailsUiState,
            onSendScannedCode = onSendScannedCode,
            focusRequester = focusRequester,
            keyboardController = keyboardController
        )
    }
}

/**
 * Composable function to show a manual code entry field.
 */
@Composable
fun KeyboardBarcodeReader(
    scannerState: ScannerState,
    orderDetailsUiState: OrderDetailsUiState.Success,
    onSendScannedCode: (Int, String) -> Unit,
    focusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController?
) {

    Column(
        modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {

            val rowKeyboardController = LocalSoftwareKeyboardController.current

            if (scannerState.isTextInputFocused.value) {
                keyboardController?.hide()
            }

            // On opening the keyboard si always hidden
            rowKeyboardController?.hide()

            // If the text field was pressed for manual entry, show the keyboard
            if (scannerState.isTextFieldPressed.value) {
                rowKeyboardController?.show()
                scannerState.isKeyboardVisible.value = true
            }

            // Icon button to show or hide the software keyboard
            IconButton(onClick = {
                // Toggle keyboard visibility
                scannerState.isKeyboardVisible.value = if (scannerState.isKeyboardVisible.value) {
                    rowKeyboardController?.hide()
                    false
                } else {
                    rowKeyboardController?.show()
                    scannerState.isTextFieldPressed.value = true
                    true
                }
            }) {
                Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
            }

            // Text input to read scanned codes
            OutlinedTextField(value = scannerState.scannedCode.value,
                onValueChange = {
                    scannerState.scannedCode.value = it
                    // Check if the last character is a newline character
                    if (it.endsWith("\n")) {
                        // Call ViewModel method to send scanned code to server
                        if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0 && scannerState.scannedCode.value.isNotEmpty()) {
                            onSendScannedCode(
                                orderDetailsUiState.orderId, scannerState.scannedCode.value
                            )
                        }
                        // Clear the scanned code after sending
                        scannerState.scannedCode.value = ""
                    }
                },
                label = { Text(stringResource(R.string.order_details_qr_scan_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = {
                    // Call ViewModel method to send scanned code to server
                    if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0 && scannerState.scannedCode.value.isNotEmpty()) {
                        onSendScannedCode(
                            orderDetailsUiState.orderId, scannerState.scannedCode.value
                        )
                    }
                    // Clear the scanned code after sending
                    scannerState.scannedCode.value = ""

                    rowKeyboardController?.hide()
                    scannerState.isKeyboardVisible.value = false
                    scannerState.isTextFieldPressed.value = false
                }),

                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
                    .focusRequester(focusRequester),

                interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                // works like onClick for the text field
                                scannerState.isTextFieldPressed.value = true
                            }
                        }
                    }
                })

            // Button to send the scanned code to the server
            IconButton(onClick = {
                rowKeyboardController?.hide()
                scannerState.isKeyboardVisible.value = false
                scannerState.isTextFieldPressed.value = false

                // Call ViewModel method to send scanned code to server
                if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0 && scannerState.scannedCode.value.isNotEmpty()) {
                    onSendScannedCode(
                        orderDetailsUiState.orderId, scannerState.scannedCode.value
                    )
                }

                // Clear the scanned code after sending
                scannerState.scannedCode.value = ""
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send_scanned_code)
                )
            }
        }
    }
}

/**
 * Preview for [KeyboardBarcodeReader]
 */
@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun KeyboardBarcodeReaderPreview() {
    KeyboardBarcodeReader(
        scannerState = ScannerState(),
        orderDetailsUiState = OrderDetailsUiState.Success(
            orderDetailsList = emptyList(), swipeActionsPreferences = SwipeActionsPreferences()
        ),
        onSendScannedCode = { _, _ -> },
        focusRequester = FocusRequester(),
        keyboardController = null
    )
}
