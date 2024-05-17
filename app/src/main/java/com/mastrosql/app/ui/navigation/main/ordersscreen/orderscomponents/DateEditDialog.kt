package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.mastrosql.app.R
import com.mastrosql.app.utils.DateHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEditDialog(
    showDatePickerDialog: MutableState<Boolean>,
    orderState: OrderState
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
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    Date(it)
                }
                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
                )
                val formattedDate = selectedDate?.let { dateFormat.format(it) }

                // Update the expirationDate state
                orderState.deliveryDate.value = formattedDate?.let {
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