package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.mastrosql.app.R
import com.mastrosql.app.utils.DateHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Decoupled snackbar host state from scaffold state for demo purposes.
//val snackState = remember { SnackbarHostState() }
//val snackScope = rememberCoroutineScope()
//SnackbarHost(hostState = snackState, Modifier)

@ExperimentalMaterial3Api
@Composable
fun ExpirationDatePickerDialog(
    showDatePickerDialog: MutableState<Boolean>,
    orderDetailsItemState: OrderDetailsItemState
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(onDismissRequest = {
        showDatePickerDialog.value = false
    }, confirmButton = {
        TextButton(
            onClick = {
                showDatePickerDialog.value = false
                /*snackScope.launch {
                        snackState.showSnackbar(
                            "Selected date timestamp: ${datePickerState.selectedDateMillis}"
                        )
                    }*/
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    Date(it)
                }
                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
                )
                val formattedDate = selectedDate?.let { dateFormat.format(it) }

                // Update the expirationDate state
                orderDetailsItemState.expirationDate.value = formattedDate?.let {
                    TextFieldValue(
                        DateHelper.formatDateToDisplay(it)
                    )
                }!!

                // Dismiss the dialog
                showDatePickerDialog.value = false
            }, enabled = confirmEnabled.value
        ) {
            Text(stringResource(R.string.confirm_button))
        }
    }, dismissButton = {
        TextButton(onClick = {
            showDatePickerDialog.value = false
        }) {
            Text(stringResource(R.string.dismiss_button))
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun ExpirationDatePickerDialogPreview() {
    ExpirationDatePickerDialog(
        showDatePickerDialog = remember {
            mutableStateOf(true)
        },
        orderDetailsItemState = OrderDetailsItemState(
            batch = remember { mutableStateOf(TextFieldValue("batch")) },
            quantity = remember { mutableStateOf(TextFieldValue("1")) },
            expirationDate = remember { mutableStateOf(TextFieldValue("gg/mm/aaaa")) }
        )
    )
}