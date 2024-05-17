package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersUiState
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.DeliveryStates.deliveryStates

@Composable
fun EditDeliveryStateDialog(
    showDeliveryDialog: MutableState<Boolean>,
    ordersUiState: OrdersUiState.Success,
    onUpdateDeliveryState: (Int, Int) -> Unit
) {

    // MutableIntState to keep track of the selected delivery state
    val selectedDeliveryState = remember { mutableIntStateOf(0) }

    // LaunchedEffect to set the initial value when the dialog is opened
    LaunchedEffect(showDeliveryDialog) {
        if (showDeliveryDialog.value) {
            val modifiedOrder =
                ordersUiState.ordersList.find { it.id == ordersUiState.modifiedOrderId.intValue }
            val modifiedOrderDeliveryState = modifiedOrder?.deliveryState
            if (modifiedOrderDeliveryState != null) {
                selectedDeliveryState.intValue = modifiedOrderDeliveryState
            }
        }
    }

    AlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showDeliveryDialog.value = false },
        title = { Text(stringResource(R.string.order_dialog_delivery_title)) },
        text = {
            Column(modifier = Modifier.wrapContentSize()) {
                deliveryStates.forEach { deliveryState ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    selectedDeliveryState.intValue =
                                        deliveryState.state //deve fare stessa cosa del onClick RadioButton
                                }
                            ),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDeliveryState.intValue == deliveryState.state,
                            onClick = {
                                selectedDeliveryState.intValue = deliveryState.state
                            },
                            colors = RadioButtonDefaults.colors(deliveryState.color),
                        )
                        Text(text = stringResource(deliveryState.nameState))
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showDeliveryDialog.value = false
            }) {
                Text(stringResource(R.string.dismiss_button))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showDeliveryDialog.value = false
                onUpdateDeliveryState(
                    ordersUiState.modifiedOrderId.intValue,
                    selectedDeliveryState.intValue
                )
            }) {
                Text(stringResource(R.string.confirm_button))
            }
        }
    )
}