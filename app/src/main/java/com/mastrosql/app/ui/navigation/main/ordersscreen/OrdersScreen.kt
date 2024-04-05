package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.ShowToast
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreenForBottomSheet
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.EditDeliveryStateDialog
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersList
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersSearchView
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersTopAppBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    onNewOrder: () -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrdersViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val ordersUiState = viewModel.ordersUiState
    val modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()

    when (ordersUiState) {
        is OrdersUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController,
            loading = true
        )

        is OrdersUiState.Success -> OrdersResultScreen(
            navigateToOrderDetails = navigateToOrderDetails,
            onNewOrder = onNewOrder,
            ordersUiState = ordersUiState,
            modifier = modifier,
            drawerState = drawerState,
            navController = navController,
            viewModel = viewModel
        )

        is OrdersUiState.Error -> ErrorScreen(
            ordersUiState.exception,
            viewModel::getOrders,
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun OrdersResultScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    onNewOrder: () -> Unit,
    ordersUiState: OrdersUiState.Success,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrdersViewModel,
) {

    var showToast by remember { mutableStateOf(false) }

    // Context used to show the toast
    val context = LocalContext.current

    // State to control the delivery dialog visibility
    val showDeliveryDialog = remember { mutableStateOf(false) }

    // State to control the bottom sheet visibility
    var showBottomSheet = remember { mutableStateOf(false) }

    if (showToast) {
        ShowToast(context, "Navigating to Order Entry")
        // Reset the showToast value after showing the toast
        showToast = false
    }

    Scaffold(
        topBar = {
            OrdersTopAppBar(
                drawerState = drawerState,
                title = stringResource(R.string.clients_orders_bar_title),
                canNavigateBack = false,
                onAddOrderClick = {
                    //TODO navigate to order entry screen
                    showBottomSheet.value = true
                }
            )
        },
        /*
        //Floating action button
        //Not used for now, but it's a good example of how to use the FAB
        floatingActionButton = {
           FloatingActionButton(
               onClick = {
                   navigateToOrderEntry()
                   showToast = true
               },
               shape = MaterialTheme.shapes.medium,
               modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
           ) {
               Icon(
                   imageVector = Icons.Default.Add,
                   contentDescription = stringResource(R.string.order_entry_title)
               )
           }
       },
       floatingActionButtonPosition = FabPosition.Center,
       */
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }

            // Search view for filtering the orders list
            OrdersSearchView(state = textState)

            // Orders list, lazy column with the orders
            OrdersList(
                ordersList = ordersUiState.ordersList,
                modifiedOrderId = ordersUiState.modifiedOrderId,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController,
                navigateToOrderDetails = navigateToOrderDetails,
                showDeliveryDialog = showDeliveryDialog
            )
        }

        if (showDeliveryDialog.value) {
            // Edit delivery Alert dialog, used to update the delivery state of an order
            EditDeliveryStateDialog(
                showDeliveryDialog = showDeliveryDialog,
                ordersUiState = ordersUiState,
                onUpdateDeliveryState = { orderId, deliveryState ->
                    viewModel.updateDeliveryState(
                        context = context,
                        orderId = orderId,
                        deliveryState = deliveryState
                    )
                }
            )
        }

        if (showBottomSheet.value) {
            // Bottom sheet to scan QR codes
            NewOrderBottomSheet(
                navController = navController,
                showBottomSheet = showBottomSheet,
                //orderDetailsUiState = viewModel.orderDetailsUiState,
                //scannerState = viewModel.scannerState,
                /*onSendScannedCode = { orderId, scannedCode ->
                    viewModel.sendScannedCode(context, orderId, scannedCode)
                }*/
            )
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderBottomSheet(
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    //orderDetailsUiState: OrderDetailsUiState.Success,
    //scannerState: ScannerState,
    //onSendScannedCode: (Int, String) -> Unit,
) {

    val showCustomersList = remember { mutableStateOf(true) }
    val showOrderFields = remember { mutableStateOf(false) }


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
            /* scannerState.isTextInputFocused.value = false
             scannerState.isTextFieldPressed.value = false*/
        },
        sheetState = sheetState,
        modifier = Modifier.nestedScroll(
            connection = rememberNestedScrollInteropConnection()
        )
    ) {

        if (showCustomersList.value) {
            // Select the customer from the list
            CustomersScreenForBottomSheet(
                onCustomerSelected = {
                    showCustomersList.value = false
                    showOrderFields.value = true
                },
                navController = navController
            )
        }

        if (showOrderFields.value) {


        }

        /*
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {/*
                val rowKeyboardController = LocalSoftwareKeyboardController.current

                if (scannerState.isTextInputFocused.value) {
                    keyboardController?.hide()
                }

                // On opening the keyboard si always hidden
                rowKeyboardController?.hide()

                // If the text field was pressed for manual entry, show the keyboard
                if (scannerState.isTextFieldPressed.value) {
                    rowKeyboardController?.show()
                    scannerState.isKeyboardVisible.value = true*/
            }

            // Icon button to show or hide the software keyboard
            IconButton(
                onClick = {/*
                        // Toggle keyboard visibility
                        scannerState.isKeyboardVisible.value =
                            if (scannerState.isKeyboardVisible.value) {
                                rowKeyboardController?.hide()
                                false
                            } else {
                                rowKeyboardController?.show()
                                scannerState.isTextFieldPressed.value = true
                                true
                            }
                    */
                }
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
            }

            // Text input to read scanned codes
            /*OutlinedTextField(
                value = scannerState.scannedCode.value,
                onValueChange = {
                    scannerState.scannedCode.value = it
                    // Check if the last character is a newline character
                    if (it.endsWith("\n")) {
                        // Call ViewModel method to send scanned code to server
                        if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0
                            && scannerState.scannedCode.value.isNotEmpty()
                        ) {
                            onSendScannedCode(
                                orderDetailsUiState.orderId,
                                scannerState.scannedCode.value
                            )
                        }
                        // Clear the scanned code after sending
                        scannerState.scannedCode.value = ""
                    }
                },
                label = { Text(stringResource(R.string.order_details_qrscan_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        // Call ViewModel method to send scanned code to server
                        if (orderDetailsUiState.orderId != null && orderDetailsUiState.orderId > 0
                            && scannerState.scannedCode.value.isNotEmpty()
                        ) {
                            onSendScannedCode(
                                orderDetailsUiState.orderId,
                                scannerState.scannedCode.value
                            )
                        }
                        // Clear the scanned code after sending
                        scannerState.scannedCode.value = ""

                        rowKeyboardController?.hide()
                        scannerState.isKeyboardVisible.value = false
                        scannerState.isTextFieldPressed.value = false
                    }
                ),

                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
                    .focusRequester(focusRequester),

                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    // works like onClick for the text field
                                    scannerState.isTextFieldPressed.value = true
                                }
                            }
                        }
                    }
            )*/

            // Button to send the scanned code to the server
            IconButton(
                onClick = {
                    /*rowKeyboardController?.hide()
                    scannerState.isKeyboardVisible.value = false
                    scannerState.isTextFieldPressed.value = false

                    // Call ViewModel method to send scanned code to server
                    if (orderDetailsUiState.orderId != null
                        && orderDetailsUiState.orderId > 0 && scannerState.scannedCode.value.isNotEmpty()
                    ) {
                        onSendScannedCode(
                            orderDetailsUiState.orderId,
                            scannerState.scannedCode.value
                        )
                    }

                    // Clear the scanned code after sending
                    scannerState.scannedCode.value = ""*/
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Scanned Code"
                )
            }
        }*/
    }
}

@Composable
fun OrderEditableFields(
    customer : CustomerMasterData,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon button to show or hide the software keyboard
            IconButton(
                onClick = {
                    // Toggle keyboard visibility
                }
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
            }
        }
    }
}