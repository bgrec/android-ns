package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreenForBottomSheet
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderBottomSheet(
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    modifier: Modifier,
    onConfirmButton: (Order) -> Unit = {},
    onDismissButton: (Boolean) -> Unit = {},
) {

    val showCustomersList = remember { mutableStateOf(true) }
    lateinit var selectedCustomerMasterData: CustomerMasterData
    var selectedDestination: DestinationData? = null


    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }

    // State to control the bottom sheet
    val sheetState = rememberModalBottomSheetState()

    // Function to set focus on the text input when bottom sheet is opened
    LaunchedEffect(showBottomSheet) {
        /*if (showBottomSheet.value) {
            scannerState.isTextInputFocused.value = true
            focusRequester.requestFocus()
            keyboardController?.hide()
        }*/
    }

    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
        },
        sheetState = sheetState,
        modifier = Modifier.nestedScroll(
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
                },
                navController = navController
            )
        } else {
            OrderDataEdit(
                modifier = modifier,
                customer = selectedCustomerMasterData,
                destination = selectedDestination,
                onConfirmButton = onConfirmButton,
                onDismissButton = onDismissButton
            )
        }
    }
}