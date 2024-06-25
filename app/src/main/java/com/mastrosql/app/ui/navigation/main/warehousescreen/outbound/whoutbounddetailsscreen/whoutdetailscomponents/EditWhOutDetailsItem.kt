package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents

import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.WhOutDetailsUiState
import com.mastrosql.app.utils.DateHelper

@ExperimentalMaterial3Api
@Composable
fun EditWhOutDetailsItem2(
    showEditDialog: MutableState<Boolean>,
    whOutDetailsUiState: WhOutDetailsUiState.Success,
    whOutDetailsItemState: WhOutDetailsItemState,
    onEditWhOutDetailsItem: (Int, Double, String, String) -> Unit,

    ) {

    // Get the focus manager
    val focusManager = LocalFocusManager.current

    // Get the context
    val context = LocalContext.current

    //Description of the dialog, if the item is not null, show the row details
    val dialogDescriptionState = remember { mutableStateOf("") }

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showEditDialog) {
        if (showEditDialog.value) {
            // Ensure modifiedIndex is not null and within range
            val index = whOutDetailsUiState.modifiedIndex?.intValue ?: -1
            if (index >= 0 && index < whOutDetailsUiState.whOutDetailsList.size) {

                whOutDetailsUiState.modifiedWhOutDetailsItem =
                    whOutDetailsUiState.whOutDetailsList[index]


                val initialBatch = whOutDetailsUiState.modifiedWhOutDetailsItem!!.batch ?: ""
                whOutDetailsItemState.batch.value = TextFieldValue(initialBatch)

                val initialQuantity = whOutDetailsUiState.modifiedWhOutDetailsItem!!.quantity ?: 0.0
                whOutDetailsItemState.quantity.value = TextFieldValue(initialQuantity.toString())

                val initialExpirationDate = DateHelper.formatDateToDisplay(
                    whOutDetailsUiState.modifiedWhOutDetailsItem!!.expirationDate ?: ""
                )
                whOutDetailsItemState.expirationDate.value = TextFieldValue(initialExpirationDate)

                if (whOutDetailsUiState.modifiedWhOutDetailsItem != null) {
                    dialogDescriptionState.value = context.getString(
                        R.string.row_details_dialog_edit_title_row,
                        whOutDetailsUiState.modifiedWhOutDetailsItem?.whOutRow ?: 0,
                        whOutDetailsUiState.modifiedWhOutDetailsItem?.articleId ?: 0,
                        whOutDetailsUiState.modifiedWhOutDetailsItem?.sku ?: ""
                    )
                }

                //Log.d("dialogDescriptionState", dialogDescriptionState.value)
            } else {
                dialogDescriptionState.value =
                    context.getString(R.string.whout_details_dialog_edit_title)
                Log.e("WhOutDetailsScreen", "Invalid index: $index")
            }

            //Use the focusRequester to focus on the text field when the dialog is opened
            //Only if active on Modifier.focusRequester(focusRequester)
            //If not active it will crash, use onFocusChanged {  } to avoid crash
            //focusRequester.requestFocus()

            // Hide the keyboard when focusing on the quantity field if needed
            //keyboardController?.hide()

        }
    }

    // Show the edit dialog when the state is true
    AlertDialog(modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showEditDialog.value = false },
        title = { Text(dialogDescriptionState.value) },
        text = {
            val showDatePickerDialog = remember { mutableStateOf(false) }

            Column(modifier = Modifier.wrapContentSize()) {
                Row {
                    OutlinedTextField(
                        value = whOutDetailsItemState.batch.value,
                        label = { Text(stringResource(R.string.row_details_dialog_edit_batch)) },
                        onValueChange = { whOutDetailsItemState.batch.value = it },
                        isError = whOutDetailsItemState.batch.value.text.isEmpty(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                        )
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row {
                    OutlinedTextField(singleLine = true,
                        value = whOutDetailsItemState.expirationDate.value,
                        label = { Text(stringResource(R.string.row_details_dialog_edit_expirationDate)) },
                        onValueChange = {
                            whOutDetailsItemState.expirationDate.value = it
                        },
                        modifier = Modifier.clickable(onClick = {
                            showDatePickerDialog.value = true
                        }),//not working
                        readOnly = false,
                        isError = whOutDetailsItemState.expirationDate.value.text.isNotEmpty() && DateHelper.formatDateToInput(
                            whOutDetailsItemState.expirationDate.value.text
                        ).isEmpty() && DateHelper.isDateBeforeToday(
                            whOutDetailsItemState.expirationDate.value.text
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
                        whOutDetailsItemState = whOutDetailsItemState
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.weight(0.2f),
                        onClick = {
                            if (getWhOutQuantity(whOutDetailsItemState) >= 1) {
                                whOutDetailsItemState.quantity.value =
                                    whOutDetailsItemState.quantity.value.copy(
                                        text = (whOutDetailsItemState.quantity.value.text.toDouble() - 1.00).toString()
                                    )
                            } else {
                                whOutDetailsItemState.quantity.value =
                                    whOutDetailsItemState.quantity.value.copy(
                                        text = "0"
                                    )
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            contentDescription = stringResource(R.string.decrease_quantity_by_one),
                            modifier = Modifier.fillMaxSize(),
                            tint = if (getWhOutQuantity(whOutDetailsItemState) <= 0)
                                Color.Gray else MaterialTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.padding(4.dp))

                    val isFirstTimeFocused = remember { mutableStateOf(true) }

                    OutlinedTextField(
                        value = whOutDetailsItemState.quantity.value,
                        label = { Text(stringResource(R.string.row_details_dialog_edit_Quantity)) },
                        onValueChange = { input ->
                            whOutDetailsItemState.quantity.value = input
                        },

                        modifier = Modifier
                            .weight(0.6f)

                            //Use the focusRequester to focus on the text field when the dialog is opened
                            //used with onFocusChanged {  }
                            //.focusRequester(focusRequester)

                            //Used alone to focus on the text field when the dialog is opened
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused && isFirstTimeFocused.value) {

                                    val inputText = whOutDetailsItemState.quantity.value.text
                                    whOutDetailsItemState.quantity.value =
                                        whOutDetailsItemState.quantity.value.copy(
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
                                    val inputText = whOutDetailsItemState.quantity.value.text
                                    whOutDetailsItemState.quantity.value =
                                        whOutDetailsItemState.quantity.value.copy(
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

                        isError = whOutDetailsItemState.quantity.value.text.isEmpty()
                                || whOutDetailsItemState.quantity.value.text.toDoubleOrNull() == null
                                || whOutDetailsItemState.quantity.value.text.toDouble() <= 0,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            onEditWhOutDetailsItem(
                                whOutDetailsUiState.modifiedWhOutDetailsItem?.id ?: 0,
                                getWhOutQuantity(whOutDetailsItemState),
                                whOutDetailsItemState.batch.value.text,
                                whOutDetailsItemState.expirationDate.value.text
                            )
                            focusManager.clearFocus()
                            showEditDialog.value = false
                        }),
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    IconButton(modifier = Modifier.weight(0.2f), onClick = {
                        if (getWhOutQuantity(whOutDetailsItemState) >= 0)
                            whOutDetailsItemState.quantity.value =
                                whOutDetailsItemState.quantity.value.copy(
                                    text = (whOutDetailsItemState.quantity.value.text.toDouble() + 1).toString()
                                )
                    }) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = stringResource(R.string.increase_quantity_by_one),
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
                onEditWhOutDetailsItem(
                    whOutDetailsUiState.modifiedWhOutDetailsItem?.id ?: 0,
                    getWhOutQuantity(whOutDetailsItemState),
                    whOutDetailsItemState.batch.value.text,
                    whOutDetailsItemState.expirationDate.value.text
                )
            }) {
                Text(stringResource(R.string.confirm_button))
            }
        })
}

private fun getWhOutQuantity(whOutDetailsItemState: WhOutDetailsItemState): Double {
    return if (whOutDetailsItemState.quantity.value.text.toDoubleOrNull() == null || whOutDetailsItemState.quantity.value.text.toDouble() <= 0)
        0.0
    else
        whOutDetailsItemState.quantity.value.text.toDouble()
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun EditWhOutDetailsItemPreview(
) {
    val showEditDialog = remember { mutableStateOf(true) }
    val whOutDetailsUiState = WhOutDetailsUiState.Success(
        whOutDetailsList = emptyList(),
        modifiedIndex = remember { mutableIntStateOf(1) },
        modifiedWhOutDetailsItem = null,
        swipeActionsPreferences = SwipeActionsPreferences()
    )
    val whOutDetailsItemState = WhOutDetailsItemState(
        batch = remember { mutableStateOf(TextFieldValue("batch")) },
        quantity = remember { mutableStateOf(TextFieldValue("123")) },
        expirationDate = remember { mutableStateOf(TextFieldValue("01/01/2024")) }
    )

    EditWhOutDetailsItem2(
        showEditDialog = showEditDialog,
        whOutDetailsUiState = whOutDetailsUiState,
        whOutDetailsItemState = whOutDetailsItemState,
        onEditWhOutDetailsItem = { _, _, _, _ -> }
    )
}
