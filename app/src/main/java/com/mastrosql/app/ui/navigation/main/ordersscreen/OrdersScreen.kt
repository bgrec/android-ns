package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.ShowToast
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreenForBottomSheet
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.DateEditDialog
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
                modifier = modifier,
                onDismissButton = { showBottomSheet.value = it },
                //orderDetailsUiState = viewModel.orderDetailsUiState,
                //scannerState = viewModel.scannerState,
                /*onSendScannedCode = { orderId, scannedCode ->
                    viewModel.sendScannedCode(context, orderId, scannedCode)
                }*/
            )
        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderBottomSheet(
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    modifier: Modifier,
    onNewOrder: (Order) -> Unit = {},
    onDismissButton: (Boolean) -> Unit = {},
    //orderDetailsUiState: OrderDetailsUiState.Success,
    //scannerState: ScannerState,
    //onSendScannedCode: (Int, String) -> Unit,
) {

    val showCustomersList = remember { mutableStateOf(true) }
    val showOrderFields = remember { mutableStateOf(false) }
    lateinit var selectedCustomerMasterData: CustomerMasterData
    lateinit var selectedDestination: DestinationData


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
                onCustomerSelected = { customerMasterData, destinationData ->
                    //showCustomersList.value = false
                    //showOrderFields.value = true
                    selectedCustomerMasterData = customerMasterData
                    if (destinationData != null) {
                        selectedDestination = destinationData
                        showOrderFields.value = true
                        showCustomersList.value = false
                    } else {
                        showOrderFields.value = true
                        showCustomersList.value = false
                    }

                },
                navController = navController
            )
        }

        if (showOrderFields.value) {
            OrderInfoFields(
                modifier = modifier,
                customer = selectedCustomerMasterData,
                destination = selectedDestination,
                onNewOrder = onNewOrder,
                onDismissButton = onDismissButton,
                navController = navController
            )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderInfoFields(
    modifier: Modifier = Modifier,
    customer: CustomerMasterData? = null,
    destination: DestinationData? = null,
    onConfirmButton: () -> Unit = {},
    onDismissButton: (Boolean) -> Unit = {},
    onNewOrder: (Order) -> Unit = {},

    navController: NavController
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showDatePickerDialog = remember { mutableStateOf(false) }

        Row {
            Text(
                text = "Nuovo Ordine",
            )
        }
        Row {
            Text("Cliente: ${customer?.businessName}")
        }
        Row {
            Text("Destinazione: ${destination?.destinationName}")
        }

//        Row {
//            IconButton(onClick = { expanded = true }) {
//                Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
//            }
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                DropdownMenuItem(
//                    text = { Text("Edit") },
//                    onClick = { /* Handle edit! */ },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Outlined.Edit,
//                            contentDescription = null
//                        )
//                    })
//                DropdownMenuItem(
//                    text = { Text("Settings") },
//                    onClick = { /* Handle settings! */ },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Outlined.Settings,
//                            contentDescription = null
//                        )
//                    })
//                HorizontalDivider()
//                DropdownMenuItem(
//                    text = { Text("Send Feedback") },
//                    onClick = { /* Handle send feedback! */ },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Outlined.Email,
//                            contentDescription = null
//                        )
//                    },
//                    trailingIcon = { Text("F11", textAlign = TextAlign.Center) })
//            }
//        }
        /*@Composable
        fun DropdownMenuItem(
            text: @Composable () -> Unit,
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            leadingIcon: @Composable (() -> Unit)? = null,
            trailingIcon: @Composable (() -> Unit)? = null,
            enabled: Boolean = true,
            colors: MenuItemColors = MenuDefaults.itemColors(),
            contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
            interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        )*/

        Row {
            OutlinedTextField(
                value = "",//orderDetailsItemState.batch.value,
                label = { Text("Descrizione ordine") },
                onValueChange = { /*orderDetailsItemState.batch.value = it*/ },
                //isError = orderDetailsItemState.batch.value.text.isEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        Row {
            OutlinedTextField(singleLine = true,
                value = ""/*orderDetailsItemState.expirationDate.value*/,
                label = { Text(stringResource(R.string.order_details_dialog_edit_expirationDate)) },
                onValueChange = {
                    /*orderDetailsItemState.expirationDate.value = it*/
                },
                modifier = Modifier.clickable(onClick = {
                    showDatePickerDialog.value = true
                }),//not working
                readOnly = false,
                /*isError = orderDetailsItemState.expirationDate.value.text.isNotEmpty() && DateHelper.formatDateToInput(
                    orderDetailsItemState.expirationDate.value.text
                ).isEmpty() && DateHelper.isDateBeforeToday(
                    orderDetailsItemState.expirationDate.value.text
                ),*/
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
            DateEditDialog(
                showDatePickerDialog = showDatePickerDialog,
                //orderDetailsItemState = orderDetailsItemState
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {

            TextButton(
                onClick = { onDismissButton(false) }
            ) {
                Text("Annulla")
            }

            TextButton(
                onClick = {
                    onConfirmButton()
                }
            ) {
                Text("Conferma")
            }

            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}

/*
val sheetState = rememberModalBottomSheetState()
val scope = rememberCoroutineScope()
var showBottomSheet by remember { mutableStateOf(false) }
Scaffold(
    floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text("Show bottom sheet") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "") },
            onClick = {
                showBottomSheet = true
            }
        )
    }
) { contentPadding ->
    // Screen content

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }
    }
}
 */