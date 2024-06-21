package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreenForBottomSheet
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound

/**
 * A bottom sheet to create a new order
 */
@ExperimentalMaterial3Api
@Composable
fun NewWhOutboundBottomSheet(
    modifier: Modifier,
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    onConfirmButton: (WarehouseOutbound) -> Unit = {},
    onDismissButton: (Boolean) -> Unit = {},
) {
    // Selected customer and destination
    lateinit var selectedCustomerMasterData: CustomerMasterData
    var selectedDestination: DestinationData? = null

    // State to control the customers list visibility
    val showCustomersList = remember { mutableStateOf(true) }

    // State to control the bottom sheet
    // Skip partially expanded state with skipPartiallyExpanded = true
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
        }, sheetState = sheetState, modifier = Modifier.nestedScroll(
            connection = rememberNestedScrollInteropConnection()
        )

    ) {
        if (showCustomersList.value) {
            // Select the customer from the list
            CustomersScreenForBottomSheet(
                onCustomerSelected = { customerMasterData, destinationData, selectionCompleted ->

                    selectedCustomerMasterData = customerMasterData
                    selectedDestination = destinationData

                    showCustomersList.value = !selectionCompleted

                }, navController = navController
            )
        } else {
            EditWhOutboundData(
                modifier = modifier,
                customer = selectedCustomerMasterData,
                destination = selectedDestination,
                onConfirmButton = onConfirmButton,
                onDismissButton = onDismissButton,
            )
        }
    }
}

