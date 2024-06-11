package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mastrosql.app.R
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.ACTION_DATA_CODE_RECEIVED
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.ScanReceiver
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.EditOrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailList
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsDestination
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsItemState
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsSearchView
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsTopAppBar
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.ScanCodeBottomSheet
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.ScannerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * OrderDetailsScreen composable
 */
@ExperimentalMaterial3Api
@Composable
fun OrderDetailsScreen(
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    navController: NavController,
    viewModel: OrderDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Collect the UI state from the ViewModel
    val orderDetailsUiState by viewModel.orderDetailsUiState.collectAsStateWithLifecycle()
    //val updatedOrderDetailsUiState by rememberUpdatedState(orderDetailsUiState)

    val modifier = Modifier.fillMaxSize()

    when (orderDetailsUiState) {
        is OrderDetailsUiState.Loading -> LoadingScreen(
            modifier = modifier, loading = true
        )

        is OrderDetailsUiState.Success -> OrderDetailResultScreen(
            modifier = modifier,
            navigateToNewItem = navigateToNewItem,
            navigateBack = navigateBack,
            orderDetailsUiState = orderDetailsUiState as OrderDetailsUiState.Success,
            navController = navController,
            viewModel = viewModel
        )

        is OrderDetailsUiState.Error -> ErrorScreen(
            modifier = modifier,
            exception = (orderDetailsUiState as OrderDetailsUiState.Error).exception,
            retryAction = viewModel::getOrderDetails,
            navController = navController
        )
    }
}

/**
 * OrderDetailResultScreen composable
 */
@ExperimentalMaterial3Api
@Composable
fun OrderDetailResultScreen(
    modifier: Modifier = Modifier,
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    orderDetailsUiState: OrderDetailsUiState.Success,
    navController: NavController,
    viewModel: OrderDetailsViewModel
) {
    // Get the context
    val context = LocalContext.current

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // State to track if we returned from the NewItemScreen - ArticleScreen
    var returnedFromNewItem by rememberSaveable { mutableStateOf(false) }

    //Read the value of the LiveData when we return from the NewItemScreen = ArticlesScreen
    val backStackEntry by navController.currentBackStackEntryAsState()
    returnedFromNewItem =
        backStackEntry?.savedStateHandle?.getLiveData<Boolean>("shouldRefresh")?.value ?: false

    //Removes the value of the LiveData when we return from the NewItemScreen = ArticlesScreen
    backStackEntry?.savedStateHandle?.remove<Boolean>("shouldRefresh")

    // Trigger getOrderDetails when the user returns from the ArticleScreen, to refresh the list
    LaunchedEffect(returnedFromNewItem) {
        if (returnedFromNewItem) {
            coroutineScope.launch {
                // Refresh the order details
                viewModel.getOrderDetails()

                // Reset the value to false after the refresh
                returnedFromNewItem = false
            }
        }
    }

    // Call the OrderDetails composable
    OrderDetails(
        modifier = modifier,
        context = context,
        navigateToNewItem = navigateToNewItem,
        navigateBack = navigateBack,
        orderDetailsUiState = orderDetailsUiState,
        coroutineScope = coroutineScope,
        onRefresh = { viewModel.getOrderDetails() },
        onDelete = { orderDetailsItemId ->
            viewModel.deleteDetailItem(context, orderDetailsItemId)
        },
        onDuplicate = { orderDetailsItemId ->
            viewModel.duplicateDetailItem(context, orderDetailsItemId)
        },
        onUpdate = { orderDetailsItemId, quantity, batch, expirationDate ->
            viewModel.updateDetailsItemData(
                context = context,
                orderDetailsItemId = orderDetailsItemId,
                quantity = quantity,
                batch = batch,
                expirationDate = expirationDate
            )
        },
        onSendScannedCode = { orderId, scannedCode ->
            viewModel.sendScannedCode(context, orderId, scannedCode)
        },
    )
}

/**
 * OrderDetails composable
 */
@ExperimentalMaterial3Api
@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
    context: Context,
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    orderDetailsUiState: OrderDetailsUiState.Success,
    coroutineScope: CoroutineScope,
    onRefresh: () -> Unit,
    onDelete: (Int) -> Unit,
    onDuplicate: (Int) -> Unit,
    onUpdate: (Int, Double, String, String) -> Unit,
    onSendScannedCode: (Int, String) -> Unit
) {

    //create a FocusManager
    val focusManager = LocalFocusManager.current

    // State to control the snackbar visibility
    val snackbarHostState = remember { SnackbarHostState() }

    // State to control the edit dialog visibility
    val showEditDialog = rememberSaveable { mutableStateOf(false) }

    // State to control the bottom sheet visibility
    val showBottomSheet = remember { mutableStateOf(false) }

    // State to hold the scanned code
    var scannedCode by remember { mutableStateOf("") }

    // Create and register the ScanReceiver to listen for scanned codes from the scanner
    // The ScanReceiver is a BroadcastReceiver that listens
    // for the "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED" broadcast
    val scanReceiver = remember {
        ScanReceiver { barcode ->
            scannedCode = barcode

            // Handle the scanned code
            Log.d("OrderDetailsScreen", "Scanned code: $barcode")
            onSendScannedCode(orderDetailsUiState.orderId ?: 0, barcode)
        }
    }

    // Register the receiver when the composable is first composed
    // and unregister it when the composable is disposed
    DisposableEffect(Unit) {
        val filter = IntentFilter(ACTION_DATA_CODE_RECEIVED)
        context.registerReceiver(scanReceiver, filter)

        // Unregister the receiver when the composable is disposed
        onDispose {
            context.unregisterReceiver(scanReceiver)
        }
    }

    //State to hold the pull to refresh state
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                onRefresh()
                state.endRefresh()
            }
        }
    }

    //State to hold the modified order details item
    val orderDetailsItemState by remember { mutableStateOf(OrderDetailsItemState()) }

    // State to hold the scanner state
    val scannerState by remember { mutableStateOf(ScannerState()) }

    Scaffold(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .nestedScroll(state.nestedScrollConnection),
        topBar = {
            OrderDetailsTopAppBar(
                title = stringResource(
                    OrderDetailsDestination.titleRes,
                    orderDetailsUiState.orderId ?: 0,
                    orderDetailsUiState.orderDescription ?: ""
                ),
                canNavigateBack = true,
                navigateUp = navigateBack,
                onAddItemClick = {
                    navigateToNewItem(orderDetailsUiState.orderId ?: 0)
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet.value = true
                    scannerState.isKeyboardVisible.value = false
                },
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = stringResource(id = R.string.open_scanner)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,

        ) { innerPadding ->

        //Box used for pull to refresh
        Box(
            modifier = Modifier.fillMaxSize()
            //.pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Screen content
                val textState = remember { mutableStateOf(TextFieldValue("")) }

                OrderDetailsSearchView(state = textState)

                val swipeActionsPreferences = orderDetailsUiState.swipeActionsPreferences

                OrderDetailList(
                    orderDetailList = orderDetailsUiState.orderDetailsList,
                    modifiedIndex = orderDetailsUiState.modifiedIndex,
                    searchTextState = textState,
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .weight(if (showBottomSheet.value) 0.5f else 1f),
                    showEditDialog = showEditDialog,
                    snackbarHostState = snackbarHostState,
                    onRemove = { orderDetailsItemId ->
                        onDelete(orderDetailsItemId)
                    },
                    onDuplicate = { orderDetailsItemId ->
                        onDuplicate(orderDetailsItemId)
                    },
                    swipeActionsPreferences = swipeActionsPreferences
                )

                if (showEditDialog.value) {
                    EditOrderDetailsItem(showEditDialog = showEditDialog,
                        orderDetailsUiState = orderDetailsUiState,
                        orderDetailsItemState = orderDetailsItemState,
                        onEditOrderDetailsItem = { orderDetailsItemId, quantity, batch, expirationDate ->
                            onUpdate(orderDetailsItemId, quantity, batch, expirationDate)
                        })
                }
                if (showBottomSheet.value) {
                    ScanCodeBottomSheet(showBottomSheet = showBottomSheet,
                        orderDetailsUiState = orderDetailsUiState,
                        scannerState = scannerState,
                        onSendScannedCode = { orderId, scannedCode ->
                            onSendScannedCode(orderId, scannedCode)
                        })
                }
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = state,
            )
        }
    }

    // Handle back button press to close the bottom sheet or navigate back
    BackHandler {
        if (showBottomSheet.value) {
            showBottomSheet.value = false
            scannerState.isTextInputFocused.value = false
        } else {
            navigateBack()
        }
    }
}

/**
 * Preview the OrderDetailsScreen
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrderDetails(modifier = Modifier.fillMaxSize(),
        context = LocalContext.current,
        navigateToNewItem = {},
        navigateBack = {},
        orderDetailsUiState = OrderDetailsUiState.Success(
            orderId = 1,
            orderDescription = "Order Description",
            orderDetailsList = emptyList(),
            swipeActionsPreferences = SwipeActionsPreferences()
        ),
        coroutineScope = rememberCoroutineScope(),
        onRefresh = {},
        onDelete = {},
        onDuplicate = { },
        onUpdate = { _, _, _, _ -> },
        onSendScannedCode = { _, _ -> })
}
