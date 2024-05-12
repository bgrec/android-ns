package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.EditOrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailList
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsDestination
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsItemState
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsSearchView
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsTopAppBar
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.ScanCodeBottomSheet
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.ScannerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navigateToNewItem: (Int) -> Unit,
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
            navigateToNewItem = navigateToNewItem,
            navigateBack = navigateBack,
            orderDetailsUiState = orderDetailsUiState,
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
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun OrderDetailResultScreen(
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    orderDetailsUiState: OrderDetailsUiState.Success,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OrderDetailsViewModel
) {

    // Get the context
    val context = LocalContext.current

    //create a FocusManager
    val focusManager = LocalFocusManager.current

    //val orderId = backStackEntry.arguments?.getInt(OrderDetailsDestination.orderIdArg)
    val coroutineScope = rememberCoroutineScope()

    //State to hold the modified order details item
    val orderDetailsItemState by remember {
        mutableStateOf(
            OrderDetailsItemState(
                mutableStateOf(TextFieldValue("")),
                mutableStateOf(TextFieldValue("")),
                mutableStateOf(TextFieldValue(""))
            )
        )
    }

    // State to hold the scanner state
    val scannerState by remember {
        mutableStateOf(
            ScannerState(
                mutableStateOf(""),
                mutableStateOf(false),
                mutableStateOf(false),
                mutableStateOf(false)
            )
        )
    }

    // State to control the snackbar visibility
    val snackbarHostState = remember { SnackbarHostState() }

    // State to control the bottom sheet visibility
    val showBottomSheet = remember { mutableStateOf(false) }

    // State to control the edit dialog visibility
    val showEditDialog = remember { mutableStateOf(false) }

    // State to track if we returned from the NewItemScreen - ArticleScreen
    var returnedFromNewItem by remember { mutableStateOf(false) }

    // State to control the pull to refresh
    var isRefreshing by remember { mutableStateOf(false) }

    // Create a PullRefreshState to control the pull to refresh and fetch
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
        coroutineScope.launch {
            viewModel.getOrderDetails()
            isRefreshing = false
        }
    })

    //Read the value of the LiveData when we return from the NewItemScreen = ArticlesScreen
    val backStackEntry by navController.currentBackStackEntryAsState()
    returnedFromNewItem =
        backStackEntry?.savedStateHandle?.getLiveData<Boolean>("shouldRefresh")?.value ?: false

    //Removes the value of the LiveData when we return from the NewItemScreen = ArticlesScreen
    backStackEntry?.savedStateHandle?.remove<Boolean>("shouldRefresh")

    // Trigger getOrderDetails when we return from the NewItemScreen
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

    // Handle back button press to close the bottom sheet or navigate back
    BackHandler {
        if (showBottomSheet.value) {
            showBottomSheet.value = false
            scannerState.isTextInputFocused.value = false
        } else {
            navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
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
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Screen content
                val textState = remember { mutableStateOf(TextFieldValue("")) }

                OrderDetailsSearchView(state = textState)

                OrderDetailList(orderDetailList = orderDetailsUiState.orderDetailsList,
                    modifiedIndex = orderDetailsUiState.modifiedIndex,
                    searchTextState = textState,
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .weight(if (showBottomSheet.value) 0.5f else 1f),
                    showEditDialog = showEditDialog,
                    snackbarHostState = snackbarHostState,
                    onRemove = { orderDetailsItemId ->
                        viewModel.deleteDetailItem(context, orderDetailsItemId)
                    },
                    onDuplicate = { orderDetailsItemId ->
                        viewModel.duplicateDetailItem(context, orderDetailsItemId)
                    })

                if (showEditDialog.value) {
                    EditOrderDetailsItem(showEditDialog = showEditDialog,
                        orderDetailsUiState = orderDetailsUiState,
                        orderDetailsItemState = orderDetailsItemState,
                        onEditOrderDetailsItem = { orderDetailsItemId, quantity, batch, expirationDate ->
                            viewModel.updateDetailsItemData(
                                context = context,
                                orderDetailsItemId = orderDetailsItemId,
                                quantity = quantity,
                                batch = batch,
                                expirationDate = expirationDate
                            )
                        })
                }
                if (showBottomSheet.value) {
                    ScanCodeBottomSheet(showBottomSheet = showBottomSheet,
                        orderDetailsUiState = orderDetailsUiState,
                        scannerState = scannerState,
                        onSendScannedCode = { orderId, scannedCode ->
                            viewModel.sendScannedCode(context, orderId, scannedCode)
                        })
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Preview
@Composable
fun OrdersScreenPreview() {
    OrderDetailsScreen(
        navigateToNewItem = {},
        navigateBack = {},
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current)
    )
}

