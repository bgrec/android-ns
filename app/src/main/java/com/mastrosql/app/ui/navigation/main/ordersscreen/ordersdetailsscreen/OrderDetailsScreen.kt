package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen


import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
import kotlinx.coroutines.launch


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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

@Composable
fun OrderDetailsScreen(
    navigateToEditItem: (Int) -> Unit,
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
            navigateToEditItem = navigateToEditItem,
            navigateToNewItem = navigateToNewItem,
            navigateBack = navigateBack,
            orderDetailList = orderDetailsUiState.orderDetailsList,
            modifiedIndex = orderDetailsUiState.modifiedIndex,
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
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun OrderDetailResultScreen(
    navigateToEditItem: (Int) -> Unit,
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    orderDetailList: List<OrderDetailsItem>,
    modifiedIndex: MutableIntState?,
    orderId: Int?,
    orderDescription: String?,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OrderDetailsViewModel
) {
    //val orderId = backStackEntry.arguments?.getInt(OrderDetailsDestination.orderIdArg)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var batch by remember { mutableStateOf("") }
    var quantity by remember { mutableDoubleStateOf(0.0) }
    var quantityString by remember { mutableStateOf(quantity.toString()) }
    var expirationDate by remember { mutableStateOf(formatDate("")) }


    // State to hold the scanned code
    var scannedCode by remember { mutableStateOf("") }

    // State to control the bottom sheet
    val sheetState = rememberModalBottomSheetState()

    // State to control the snackbar visibility
    val snackbarHostState = remember { SnackbarHostState() }

    // State to control the bottom sheet visibility
    var showBottomSheet by remember { mutableStateOf(false) }

    val showEditDialog = remember { mutableStateOf(false) }
    // State to control the focus of the text input
    var isTextInputFocused by remember { mutableStateOf(false) }

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }

    //create a FocusManager
    val focusManager = LocalFocusManager.current

    // State to track keyboard visibility
    var isKeyboardVisible by remember { mutableStateOf(false) }

    // State to track if the text field is pressed
    var isTextFieldPressed by remember { mutableStateOf(false) }

    // State to track if we returned from the NewItemScreen - ArticleScreen
    var returnedFromNewItem by remember { mutableStateOf(false) }

    // State to control the pull to refresh
    var isRefreshing by remember { mutableStateOf(false) }

    // Create a PullRefreshState to control the pull to refresh and fetch
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                viewModel.getOrderDetails()
                isRefreshing = false
            }
        }
    )

    // Function to set focus on the text input when bottom sheet is opened
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            isTextInputFocused = true
            focusRequester.requestFocus()
            keyboardController?.hide()
        }
    }

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
                viewModel.getOrderDetails()

                //Reset the value to false after the refresh
                returnedFromNewItem = false
            }
        }
    }

    // Handle back button press
    BackHandler {
        if (showBottomSheet) {
            showBottomSheet = false
            isTextInputFocused = false
        } else {
            navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        topBar = {
            OrderDetailsTopAppBar(
                title = stringResource(
                    titleRes,
                    orderId ?: 0,
                    orderDescription ?: ""
                ),
                canNavigateBack = true,
                navigateUp = navigateBack,
                onAddItemClick = {
                    navigateToNewItem(orderId ?: 0)
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    isKeyboardVisible = false

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

                OrderDetailList(
                    orderDetailList = orderDetailList,
                    modifiedIndex = modifiedIndex,
                    state = textState,
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .weight(if (showBottomSheet) 0.5f else 1f),
                    showEditDialog = showEditDialog,
                    snackbarHostState = snackbarHostState,
                    onRemove = { orderDetailsItemId ->
                        viewModel.deleteDetailItem(context, orderDetailsItemId)
                    },
                    onDuplicate = { orderDetailsItemId ->
                        viewModel.duplicateDetailItem(context, orderDetailsItemId)
                    }
                )

                if (showEditDialog.value) {
                    AlertDialog(
                        modifier = Modifier.wrapContentSize(),
                        onDismissRequest = { showEditDialog.value = false },
                        title = { Text(stringResource(R.string.order_details_dialog_edit_title)) },
                        text = {

                            //val focusManager = LocalFocusManager.current

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
                                //viewModel


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
                            isTextInputFocused = false
                            isTextFieldPressed = false
                        },
                        sheetState = sheetState,
                        modifier = Modifier.nestedScroll(
                            connection = rememberNestedScrollInteropConnection()
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val rowKeyboardController = LocalSoftwareKeyboardController.current

                                if (isTextInputFocused) {
                                    keyboardController?.hide()
                                }

                                // On opening the keyboard si always hidden
                                rowKeyboardController?.hide()

                                // If the text field was pressed for manual entry, show the keyboard
                                if (isTextFieldPressed) {
                                    rowKeyboardController?.show()
                                    isKeyboardVisible = true
                                }

                                // Icon button to show or hide the software keyboard
                                IconButton(
                                    onClick = {
                                        // Toggle keyboard visibility
                                        isKeyboardVisible = if (isKeyboardVisible) {
                                            rowKeyboardController?.hide()
                                            false
                                        } else {
                                            rowKeyboardController?.show()
                                            isTextFieldPressed = true
                                            true
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Keyboard, contentDescription = "Keyboard")
                                }

                                // Text input to read scanned codes
                                OutlinedTextField(
                                    value = scannedCode,
                                    onValueChange = {
                                        scannedCode = it
                                        // Check if the last character is a newline character
                                        if (it.endsWith("\n")) {
                                            // Call ViewModel method to send scanned code to server
                                            if (orderId != null && orderId > 0 && scannedCode.isNotEmpty()) {
                                                viewModel.sendScannedCode(
                                                    context,
                                                    orderId,
                                                    scannedCode
                                                )
                                            }
                                            // Clear the scanned code after sending
                                            scannedCode = ""
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
                                            if (orderId != null && orderId > 0 && scannedCode.isNotEmpty()) {
                                                viewModel.sendScannedCode(
                                                    context,
                                                    orderId,
                                                    scannedCode
                                                )
                                            }
                                            // Clear the scanned code after sending
                                            scannedCode = ""

                                            rowKeyboardController?.hide()
                                            isKeyboardVisible = false
                                            isTextFieldPressed = false
                                        }
                                    ),

                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                        .focusRequester(focusRequester),

                                    interactionSource = remember { MutableInteractionSource() }
                                        .also { interactionSource ->
                                            LaunchedEffect(interactionSource) {
                                                interactionSource.interactions.collect {
                                                    if (it is PressInteraction.Release) {
                                                        // works like onClick for the text field
                                                        isTextFieldPressed = true
                                                    }
                                                }
                                            }
                                        }
                                )

                                // Button to send the scanned code to the server
                                IconButton(
                                    onClick = {

                                        rowKeyboardController?.hide()
                                        isKeyboardVisible = false
                                        isTextFieldPressed = false


                                        // Call ViewModel method to send scanned code to server
                                        if (orderId != null && orderId > 0 && scannedCode.isNotEmpty()) {
                                            viewModel.sendScannedCode(context, orderId, scannedCode)
                                        }

                                        // Clear the scanned code after sending
                                        scannedCode = ""
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send Scanned Code"
                                    )
                                }
                            }
                        }
                    }
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun OrdersScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    OrderDetailsScreen(
        navigateToEditItem = {},
        navigateToNewItem = {},
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
