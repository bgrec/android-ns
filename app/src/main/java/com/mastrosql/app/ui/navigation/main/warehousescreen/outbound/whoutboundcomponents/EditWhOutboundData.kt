package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundUtils

@ExperimentalMaterial3Api
@Composable
fun EditWhOutboundData(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    onDismissButton: (Boolean) -> Unit = {},
    onConfirmButton: (WarehouseOutbound) -> Unit = {},
) {
    //State to hold the modified warehouse outbound state data
    val whOutboundState by remember { mutableStateOf(WhOutboundState()) }

//    // Create a FocusRequester
//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(whOutboundState) {
//        focusRequester.requestFocus()
//    }

    //    val focusRequester = remember { FocusRequester() }
    //    LaunchedEffect(Unit) { // Trigger only once on composition
    //        focusRequester.requestFocus()
    //    }

    whOutboundState.customerId.intValue = customer?.id ?: 0
    whOutboundState.customerName.value = TextFieldValue(customer?.businessName ?: "")

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showDatePickerDialog = remember { mutableStateOf(false) }

        //Title of the dialog
        Row {
            Text(
                text = stringResource(R.string.new_warehouse_outbound),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

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
                showDatePickerDialog = showDatePickerDialog, whOutboundState = whOutboundState
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { onDismissButton(false) }) {
                Text(stringResource(id = R.string.dismiss_button))
            }

            TextButton(onClick = {
                val newWarehouseOutbound = WhOutboundUtils
                    .createNewWarehouseOutboundFromState(whOutboundState)
                onConfirmButton(newWarehouseOutbound)
            }) {
                Text(stringResource(id = R.string.confirm_button))
            }
            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}


