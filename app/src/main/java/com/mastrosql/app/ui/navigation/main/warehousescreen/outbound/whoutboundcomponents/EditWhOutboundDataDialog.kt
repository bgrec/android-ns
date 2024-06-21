package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

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
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.WhOutboundUiState
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme


@ExperimentalMaterial3Api
@Composable
fun EditWhOutboundDataDialog(
    modifier: Modifier = Modifier,
    showEditWhOutboundDataDialog: MutableState<Boolean>,
    whOutboundUiState: WhOutboundUiState.Success,
    onUpdateWhOutboundData: (WhOutboundState) -> Unit = {}
) {
    //State to hold the modified order details item
    val whOutboundState by remember { mutableStateOf(WhOutboundState()) }

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showEditWhOutboundDataDialog) {
        if (showEditWhOutboundDataDialog.value) {
            val modifiedOrder =
                whOutboundUiState.whOutboundsList.find { it.id == whOutboundUiState.modifiedWhOutboundId.intValue }
            if (modifiedOrder != null) {
                whOutboundState.operationId.intValue = modifiedOrder.id
                whOutboundState.customerId.intValue = 0//modifiedOrder.clientId ?: 0
                whOutboundState.customerName.value =
                    TextFieldValue(modifiedOrder.businessName ?: "")
//                whOutboundState.destinationId.intValue = 0//modifiedOrder.destinationId ?: 0
//                whOutboundState.destinationName.value =
//                    TextFieldValue(/*modifiedOrder.destinationName ?:*/ "")
//                whOutboundState.orderDescription.value =
//                    TextFieldValue(/*modifiedOrder.description ?:*/ "")

//                // Update the delivery state
//                whOutboundState.deliveryDate.value =
//                    (modifiedOrder.businessName?.let {
//                        TextFieldValue(
//                            DateHelper.formatDateToDisplay(it)
//                        )
//                    } ?: return@LaunchedEffect)
            }
        }
    }

//    // Create a FocusRequester
//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }

    AlertDialog(modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showEditWhOutboundDataDialog.value = false },
        title = {
            Text(
                stringResource(
                    R.string.order_details_edit,
                    whOutboundState.operationId.intValue,
                    //whOutboundState.orderDescription.value.text
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
                        value = whOutboundState.customerName.value,
                        label = { Text(stringResource(id = R.string.businessName)) },
                        onValueChange = { },
                        readOnly = true,
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))

                // Destination Name (Read-only if destination is not null)
//                if (whOutboundState.destinationId.intValue > 0) {
                if (true) {
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
                        modifier = textFieldModifier,
                        // not requesting focus on this field
                        //.focusRequester(focusRequester),
                        value = "",//whOutboundState.orderDescription.value,
                        label = { Text(stringResource(id = R.string.order_description)) },
                        onValueChange = { /*whOutboundState.orderDescription.value = it*/ },
                        isError = false,//whOutboundState.orderDescription.value.text.isEmpty(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                        )
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                val isDateValid = false
//                    remember(whOutboundState.deliveryDate.value.text) {
//                    DateHelper
//                        .formatDateToInput(whOutboundState.deliveryDate.value.text)
//                        .isNotEmpty()
//                }
                val isDateBeforeToday = false
//                    remember(whOutboundState.deliveryDate.value.text) {
//                    DateHelper.isDateBeforeToday(whOutboundState.deliveryDate.value.text)
//                }

                // Delivery Date
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(modifier = textFieldModifier.clickable(onClick = {
                        showDatePickerDialog.value = true
                    }),
                        singleLine = true,
                        value = "",//whOutboundState.deliveryDate.value,
                        label = { Text(stringResource(R.string.order_deliveryDate)) },
                        onValueChange = {
//                            whOutboundState.deliveryDate.value = it
                        },
                        readOnly = false,
                        isError = false,//whOutboundState.deliveryDate.value.text.isNotEmpty() && (!isDateValid || isDateBeforeToday),
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
                        showDatePickerDialog = showDatePickerDialog,
                        whOutboundState = whOutboundState
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showEditWhOutboundDataDialog.value = false
            }) {
                Text(stringResource(R.string.dismiss_button))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showEditWhOutboundDataDialog.value = false
                onUpdateWhOutboundData(whOutboundState)
            }) {
                Text(stringResource(R.string.confirm_button))
            }
        })
}

/**
 * Preview for [EditWhOutboundDataDialog]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun PreviewEditOrderDataDialog() {
    MastroAndroidPreviewTheme {
        EditWhOutboundDataDialog(
            showEditWhOutboundDataDialog = mutableStateOf(true),
            whOutboundUiState = WhOutboundUiState.Success(
                whOutboundsList = emptyList(), modifiedWhOutboundId = mutableIntStateOf(1)
            )
        )
    }
}
