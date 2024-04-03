package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsUiState
import com.mastrosql.app.utils.DateHelper

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditOrderDetailsItem(
    showEditDialog: MutableState<Boolean>,
    orderDetailsUiState: OrderDetailsUiState.Success,
    orderDetailsItemState: OrderDetailsItemState,
    onEditOrderDetailsItem: (Int, Double, String, String) -> Unit,

    ) {
    //create a FocusManager
    val focusManager = LocalFocusManager.current

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showEditDialog) {
        if (showEditDialog.value) {
            // Ensure modifiedIndex is not null and within range
            val index = orderDetailsUiState.modifiedIndex?.intValue ?: -1
            if (index >= 0 && index < orderDetailsUiState.orderDetailsList.size) {

                orderDetailsUiState.modifiedOrderDetailsItem =
                    orderDetailsUiState.orderDetailsList[index]

                val initialBatch = orderDetailsUiState.modifiedOrderDetailsItem!!.batch ?: ""
                orderDetailsItemState.batch.value = TextFieldValue(initialBatch)

                val initialQuantity = orderDetailsUiState.modifiedOrderDetailsItem!!.quantity ?: 0.0
                orderDetailsItemState.quantity.value = TextFieldValue(initialQuantity.toString())

                val initialExpirationDate = DateHelper.formatDateToDisplay(
                    orderDetailsUiState.modifiedOrderDetailsItem!!.expirationDate ?: ""
                )
                orderDetailsItemState.expirationDate.value = TextFieldValue(initialExpirationDate)

            } else {
                Log.e("OrderDetailsScreen", "Invalid index: $index")
            }

            //Use the focusRequester to focus on the text field when the dialog is opened
            //Only if active on Modifier.focusRequester(focusRequester)
            //If not active it will crash, use onFocusChanged {  } to avoid crash
            //focusRequester.requestFocus()

            // Hide the keyboard when focusing on the quantity field if needed
            //keyboardController?.hide()
        }
    }

    //Description of the dialog, if the item is not null, show the row details
    var dialogDescription = stringResource(
        R.string.order_details_dialog_edit_title
    )
    if (orderDetailsUiState.modifiedOrderDetailsItem != null) {
        dialogDescription = stringResource(
            R.string.order_details_dialog_edit_title_row,
            orderDetailsUiState.modifiedOrderDetailsItem?.orderRow ?: 0,
            orderDetailsUiState.modifiedOrderDetailsItem?.articleId ?: 0,
            orderDetailsUiState.modifiedOrderDetailsItem?.sku ?: ""
        )
    }

    // Show the edit dialog when the state is true
    AlertDialog(modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showEditDialog.value = false },
        title = { Text(dialogDescription) },
        text = {
            val showDatePickerDialog = remember { mutableStateOf(false) }

            Column(modifier = Modifier.wrapContentSize()) {
                Row {
                    OutlinedTextField(
                        value = orderDetailsItemState.batch.value,
                        label = { Text(stringResource(R.string.order_details_dialog_edit_batch)) },
                        onValueChange = { orderDetailsItemState.batch.value = it },
                        isError = orderDetailsItemState.batch.value.text.isEmpty(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                        )
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row {
                    OutlinedTextField(singleLine = true,
                        value = orderDetailsItemState.expirationDate.value,
                        label = { Text(stringResource(R.string.order_details_dialog_edit_expirationDate)) },
                        onValueChange = {
                            orderDetailsItemState.expirationDate.value = it
                        },
                        modifier = Modifier.clickable(onClick = {
                            showDatePickerDialog.value = true
                        }),//not working
                        readOnly = false,
                        isError = orderDetailsItemState.expirationDate.value.text.isNotEmpty() && DateHelper.formatDateToInput(
                            orderDetailsItemState.expirationDate.value.text
                        ).isEmpty() && DateHelper.isDateBeforeToday(
                            orderDetailsItemState.expirationDate.value.text
                        ),
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
                    ExpirationDatePickerDialog(
                        showDatePickerDialog = showDatePickerDialog,
                        orderDetailsItemState = orderDetailsItemState
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.weight(0.2f),
                        onClick = {
                            if (getOrderQuantity(orderDetailsItemState) >= 1) {
                                orderDetailsItemState.quantity.value =
                                    orderDetailsItemState.quantity.value.copy(
                                        text = (orderDetailsItemState.quantity.value.text.toDouble() - 1.00).toString()
                                    )
                            } else {
                                orderDetailsItemState.quantity.value =
                                    orderDetailsItemState.quantity.value.copy(
                                        text = "0"
                                    )
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            contentDescription = "Scala la quantità",
                            modifier = Modifier.fillMaxSize(),
                            tint = if (getOrderQuantity(orderDetailsItemState) <= 0)
                                Color.Gray else MaterialTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.padding(4.dp))

                    val isFirstTimeFocused = remember { mutableStateOf(true) }

                    OutlinedTextField(
                        value = orderDetailsItemState.quantity.value,
                        label = { Text(stringResource(R.string.order_details_dialog_edit_Quantity)) },
                        onValueChange = { input ->
                            orderDetailsItemState.quantity.value = input
                        },

                        modifier = Modifier
                            .weight(0.6f)

                            //Use the focusRequester to focus on the text field when the dialog is opened
                            //used with onFocusChanged {  }
                            //.focusRequester(focusRequester)

                            //Used alone to focus on the text field when the dialog is opened
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused && isFirstTimeFocused.value) {

                                    val inputText = orderDetailsItemState.quantity.value.text
                                    orderDetailsItemState.quantity.value =
                                        orderDetailsItemState.quantity.value.copy(
                                            selection = TextRange(
                                                0, inputText.length
                                            )
                                        )
                                    isFirstTimeFocused.value = false

                                } else {
                                    //isFirstTimeFocused.value = true
                                }
                            }
                            // When using focusRequester, the text is selected when the dialog is opened
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    val inputText = orderDetailsItemState.quantity.value.text
                                    orderDetailsItemState.quantity.value =
                                        orderDetailsItemState.quantity.value.copy(
                                            selection = TextRange(
                                                0, inputText.length
                                            )
                                        )
                                    isFirstTimeFocused.value = false
                                } else {
                                    isFirstTimeFocused.value = true
                                }
                            },
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            // works like onClick for the text field
                                            isFirstTimeFocused.value = !isFirstTimeFocused.value
                                        }
                                    }
                                }
                            },

                        isError = orderDetailsItemState.quantity.value.text.isEmpty()
                                || orderDetailsItemState.quantity.value.text.toDoubleOrNull() == null
                                || orderDetailsItemState.quantity.value.text.toDouble() <= 0,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            onEditOrderDetailsItem(
                                orderDetailsUiState.modifiedOrderDetailsItem?.id ?: 0,
                                getOrderQuantity(orderDetailsItemState),
                                orderDetailsItemState.batch.value.text,
                                orderDetailsItemState.expirationDate.value.text
                            )
                            focusManager.clearFocus()
                            showEditDialog.value = false
                        }),
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    IconButton(modifier = Modifier.weight(0.2f), onClick = {
                        if (getOrderQuantity(orderDetailsItemState) >= 0)
                            orderDetailsItemState.quantity.value =
                                orderDetailsItemState.quantity.value.copy(
                                    text = (orderDetailsItemState.quantity.value.text.toDouble() + 1).toString()
                                )
                    }) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Scala la quantità",
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showEditDialog.value = false
            }) {
                Text(stringResource(R.string.dismiss_button))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showEditDialog.value = false
                onEditOrderDetailsItem(
                    orderDetailsUiState.modifiedOrderDetailsItem?.id ?: 0,
                    getOrderQuantity(orderDetailsItemState),
                    orderDetailsItemState.batch.value.text,
                    orderDetailsItemState.expirationDate.value.text
                )
            }) {
                Text(stringResource(R.string.confirm_button))
            }
        })
}

private fun getOrderQuantity(orderDetailsItemState: OrderDetailsItemState): Double {
    return if (orderDetailsItemState.quantity.value.text.toDoubleOrNull() == null || orderDetailsItemState.quantity.value.text.toDouble() <= 0)
        0.0
    else
        orderDetailsItemState.quantity.value.text.toDouble()
}