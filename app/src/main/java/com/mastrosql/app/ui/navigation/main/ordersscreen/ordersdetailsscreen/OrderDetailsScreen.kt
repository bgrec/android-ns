package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.formatDate
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination.route
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination.titleRes
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailList
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsSearchView
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsTopAppBar


object OrderDetailsDestination : NavigationDestination {
    override val route = "order_details"
    override val titleRes = R.string.order_details_edit
    const val ORDER_ID_ARG = "orderId"
    const val ORDER_DESCRIPTION_ARG = "orderDescription"
    val routeWithArgs = "$route/{$ORDER_ID_ARG}?orderDescription={$ORDER_DESCRIPTION_ARG}"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Edit : Screen(route, titleRes)
    object Add : Screen(route, titleRes)
}

val items = listOf(
    Screen.Edit,
    Screen.Edit,
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrderDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val orderDetailsUiState = viewModel.orderDetailsUiState
    val modifier = Modifier.fillMaxSize()

    when (orderDetailsUiState) {
        is OrderDetailsUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController,
            loading = true
        )

        is OrderDetailsUiState.Success -> OrderDetailResultScreen(
            navigateToEditItem = navigateToEditItem,
            navigateBack = navigateBack,
            orderDetailList = orderDetailsUiState.orderDetailsList,
            orderId = orderDetailsUiState.orderId,
            orderDescription = orderDetailsUiState.orderDescription,
            modifier = modifier.fillMaxWidth(),
            navController = navController,
            viewModel = viewModel
        )

        is OrderDetailsUiState.Error -> ErrorScreen(
            orderDetailsUiState.exception,
            viewModel::getOrderDetails,
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController
        )

        else -> {
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun OrderDetailResultScreen(
    //backStackEntry: NavBackStackEntry,
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    orderDetailList: List<OrderDetailsItem>,
    orderId: Int?,
    orderDescription: String?,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OrderDetailsViewModel
) {
    //val orderId = backStackEntry.arguments?.getInt(OrderDetailsDestination.orderIdArg)


    var batch by remember { mutableStateOf("") }
    var quantity by remember { mutableDoubleStateOf(0.0) }
    var quantityString by remember { mutableStateOf(quantity.toString()) }
    var expirationDate by remember { mutableStateOf(formatDate("")) }

    var scannedCode by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    // State to control the focus of the text input
    var isTextInputFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current


    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }

    // Function to set focus on the text input
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            isTextInputFocused = true
            focusRequester.requestFocus()
            keyboardController?.hide()
        }
    }

    Scaffold(
        topBar = {
            OrderDetailsTopAppBar(
                title = stringResource(
                    OrderDetailsDestination.titleRes,
                    orderId ?: 0,
                    orderDescription ?: ""
                ),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.open_scanner)) },
                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner") },
                onClick = {

                    showBottomSheet = true

                },
            )
        }
    ) { innerPadding ->

        if (showEditDialog.value) {
            AlertDialog(
                modifier = Modifier.wrapContentSize(),
                onDismissRequest = { showEditDialog.value = false },
                title = { Text(stringResource(R.string.order_details_dialog_edit_title)) },
                text = {
                    val focusManager = LocalFocusManager.current

                    Column(modifier = Modifier.wrapContentSize()) {
                        OutlinedTextField(
                            value = batch ?: "",
                            label = { Text(stringResource(R.string.order_details_dialog_edit_batch)) },
                            onValueChange = { batch = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = quantityString,
                            label = { Text(stringResource(R.string.order_details_dialog_edit_Quantity)) },
                            onValueChange = { quantityString = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = expirationDate,
                            label = { Text(stringResource(R.string.order_details_dialog_edit_expirationDate)) },
                            onValueChange = { expirationDate = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                //fare stessa cosa fatta nel bottone di conferma del dialog
                                focusManager.clearFocus()
                            })
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEditDialog.value = false
                    }) {
                        Text(stringResource(R.string.dismiss_button))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showEditDialog.value = false
                    }) {
                        Text(stringResource(R.string.confirm_button))
                    }
                }
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon button to show keyboard
                        IconButton(
                            onClick = {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        ) {
                            Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
                        }

                        // Text input to read scanned codes
                        OutlinedTextField(
                            value = scannedCode,
                            onValueChange = { scannedCode = it },
                            label = { Text(stringResource(R.string.order_details_qrscan_text)) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    // Call ViewModel method to send scanned code to server
                                    viewModel.sendScannedCode(scannedCode)
                                    // Clear the scanned code after sending
                                    scannedCode = ""

                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .focusRequester(focusRequester)
                        )

                        // Button to send the scanned code to the server
                        IconButton(
                            onClick = {
                                // Call ViewModel method to send scanned code to server
                                viewModel.sendScannedCode(scannedCode)
                                // Clear the scanned code after sending
                                scannedCode = ""
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }


        // Screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            OrderDetailsSearchView(state = textState)
            OrderDetailList(
                orderDetailList = orderDetailList,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController,
                showEditDialog = showEditDialog,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun OrdersScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    OrderDetailsScreen(
        navigateToEditItem = {},
        navigateBack = {},
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current)
    )
}

@Preview
@Composable
fun SearchBarPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
}

/*
*  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val marsViewModel: MarsViewModel = viewModel()
            HomeScreen(marsUiState = marsViewModel.marsUiState)
        }
    }
}

@Composable
fun MarsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}
*
* */


/**
 * Versione con SearchBar diversa
 *
 * @ExperimentalMaterial3Api
 * @Composable
 * fun OrdersResultScreen(
 *     customerMasterDataList: List<CustomerMasterData>,
 *     modifier: Modifier = Modifier,
 *     drawerState: DrawerState,
 *     navController: NavController
 * ) {
 *     Scaffold(
 *         topBar = {
 *             //AppBar(drawerState = drawerState)
 *             SearchBar(
 *                 customerMasterDataList = customerMasterDataList,
 *                 navController = navController
 *             )
 *         },
 *         bottomBar = {
 *             // BottomBar(drawerState = drawerState, navController = navController)
 *         },
 *     ) {
 *         Surface(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 .padding(it)
 *         ) {
 *         }
 *     }
 * }
 */