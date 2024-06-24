package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.WhOutboundUiState

@ExperimentalMaterial3Api
@Composable
fun EditWhOutboundDataDialog(
    modifier: Modifier = Modifier,
    showEditWhOutboundDataDialog: MutableState<Boolean>,
    whOutboundUiState: WhOutboundUiState.Success,
    onUpdateWhOutboundData: (WhOutboundState) -> Unit = {}
) {
    //State to hold the modified warehouse outbound state data
    val whOutboundState by remember { mutableStateOf(WhOutboundState()) }

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showEditWhOutboundDataDialog) {
        if (showEditWhOutboundDataDialog.value) {
            val modifiedWhOutbound =
                whOutboundUiState.whOutboundsList.find { it.id == whOutboundUiState.modifiedWhOutboundId.intValue }
            if (modifiedWhOutbound != null) {
                whOutboundState.operationId.intValue = modifiedWhOutbound.id
                whOutboundState.customerId.intValue = modifiedWhOutbound.customerId
                whOutboundState.customerName.value =
                    TextFieldValue(modifiedWhOutbound.businessName ?: "")
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
                    R.string.warehouse_outbound_edit,
                    whOutboundState.operationId.intValue,
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

