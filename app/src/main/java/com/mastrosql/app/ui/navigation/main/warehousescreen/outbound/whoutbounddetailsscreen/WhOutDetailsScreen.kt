package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen

import android.content.Context
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
import com.mastrosql.app.scanner.ScanReceiver
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.EditWhOutDetailsItem2
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.ScanCodeBottomSheet
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.ScannerState
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailList
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailsDestination
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailsItemState
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailsSearchView
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailsTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * WhOutDetailsScreen composable
 */
@ExperimentalMaterial3Api
@Composable
fun WhOutDetailsScreen(
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    navController: NavController,
    viewModel: WhOutDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Collect the UI state from the ViewModel
    val whOutDetailsUiState by viewModel.whOutDetailsUiState.collectAsStateWithLifecycle()
    //val updatedWhOutDetailsUiState by rememberUpdatedState(whOutDetailsUiState)

    val modifier = Modifier.fillMaxSize()

    when (whOutDetailsUiState) {
        is WhOutDetailsUiState.Loading -> LoadingScreen(
            modifier = modifier, loading = true
        )

        is WhOutDetailsUiState.Success -> WhOutDetailResultScreen(
            modifier = modifier,
            navigateToNewItem = navigateToNewItem,
            navigateBack = navigateBack,
            whOutDetailsUiState = whOutDetailsUiState as WhOutDetailsUiState.Success,
            navController = navController,
            viewModel = viewModel
        )

        is WhOutDetailsUiState.Error -> ErrorScreen(
            modifier = modifier,
            exception = (whOutDetailsUiState as WhOutDetailsUiState.Error).exception,
            retryAction = viewModel::getWhOutDetails,
            navController = navController
        )
    }
}

/**
 * WhOutDetailResultScreen composable
 */
@ExperimentalMaterial3Api
@Composable
fun WhOutDetailResultScreen(
    modifier: Modifier = Modifier,
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    whOutDetailsUiState: WhOutDetailsUiState.Success,
    navController: NavController,
    viewModel: WhOutDetailsViewModel
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

    // Trigger getWhOutDetails when the user returns from the ArticleScreen, to refresh the list
    LaunchedEffect(returnedFromNewItem) {
        if (returnedFromNewItem) {
            coroutineScope.launch {
                // Refresh the whOut details
                viewModel.getWhOutDetails()

                // Reset the value to false after the refresh
                returnedFromNewItem = false
            }
        }
    }

    // Call the WhOutDetails composable
    WhOutDetails(
        modifier = modifier,
        context = context,
        navigateToNewItem = navigateToNewItem,
        navigateBack = navigateBack,
        whOutDetailsUiState = whOutDetailsUiState,
        coroutineScope = coroutineScope,
        onRefresh = { viewModel.getWhOutDetails() },
        onDelete = { whOutDetailsItemId ->
            viewModel.deleteDetailItem(context, whOutDetailsItemId)
        },
        onDuplicate = { whOutDetailsItemId ->
            viewModel.duplicateDetailItem(context, whOutDetailsItemId)
        },
        onUpdate = { whOutDetailsItemId, quantity, batch, expirationDate ->
            viewModel.updateDetailsItemData(
                context = context,
                whOutDetailsItemId = whOutDetailsItemId,
                quantity = quantity,
                batch = batch,
                expirationDate = expirationDate
            )
        },
        onSendScannedCode = { whOutId, scannedCode ->
            viewModel.sendScannedCode(context, whOutId, scannedCode)
        },
    )
}

/**
 * WhOutDetails composable
 */
@ExperimentalMaterial3Api
@Composable
fun WhOutDetails(
    modifier: Modifier = Modifier,
    context: Context,
    navigateToNewItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    whOutDetailsUiState: WhOutDetailsUiState.Success,
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
            onSendScannedCode(whOutDetailsUiState.whOutId ?: 0, barcode)
        }
    }

    // Register the receiver when the composable is first composed
    // and unregister it when the composable is disposed
    DisposableEffect(Unit) {
        // Register the ScanReceiver to listen for the scan broadcast
        scanReceiver.register(context)

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

    //State to hold the modified whOut details item
    val whOutDetailsItemState by remember { mutableStateOf(WhOutDetailsItemState()) }

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
            WhOutDetailsTopAppBar(
                title = stringResource(
                    WhOutDetailsDestination.titleRes,
                    whOutDetailsUiState.whOutId ?: 0,
                    whOutDetailsUiState.whOutDescription ?: ""
                ),
                canNavigateBack = true,
                navigateUp = navigateBack,
                onAddItemClick = {
                    navigateToNewItem(whOutDetailsUiState.whOutId ?: 0)
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

                WhOutDetailsSearchView(state = textState)

                val swipeActionsPreferences = whOutDetailsUiState.swipeActionsPreferences

                WhOutDetailList(
                    whOutDetailList = whOutDetailsUiState.whOutDetailsList,
                    modifiedIndex = whOutDetailsUiState.modifiedIndex,
                    searchTextState = textState,
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .weight(if (showBottomSheet.value) 0.5f else 1f),
                    showEditDialog = showEditDialog,
                    snackbarHostState = snackbarHostState,
                    onRemove = { whOutDetailsItemId ->
                        onDelete(whOutDetailsItemId)
                    },
                    onDuplicate = { whOutDetailsItemId ->
                        onDuplicate(whOutDetailsItemId)
                    },
                    swipeActionsPreferences = swipeActionsPreferences
                )

                if (showEditDialog.value) {
                    EditWhOutDetailsItem2(showEditDialog = showEditDialog,
                        whOutDetailsUiState = whOutDetailsUiState,
                        whOutDetailsItemState = whOutDetailsItemState,
                        onEditWhOutDetailsItem = { whOutDetailsItemId, quantity, batch, expirationDate ->
                            onUpdate(whOutDetailsItemId, quantity, batch, expirationDate)
                        })
                }
                if (showBottomSheet.value) {
                    ScanCodeBottomSheet(showBottomSheet = showBottomSheet,
                        whOutDetailsUiState = whOutDetailsUiState,
                        scannerState = scannerState,
                        onSendScannedCode = { whOutId, scannedCode ->
                            onSendScannedCode(whOutId, scannedCode)
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
 * Preview the WhOutDetailsScreen
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun WhOutsScreenPreview() {
    WhOutDetails(modifier = Modifier.fillMaxSize(),
        context = LocalContext.current,
        navigateToNewItem = {},
        navigateBack = {},
        whOutDetailsUiState = WhOutDetailsUiState.Success(
            whOutId = 1,
            whOutDescription = "WhOut Description",
            whOutDetailsList = emptyList(),
            swipeActionsPreferences = SwipeActionsPreferences()
        ),
        coroutineScope = rememberCoroutineScope(),
        onRefresh = {},
        onDelete = {},
        onDuplicate = { },
        onUpdate = { _, _, _, _ -> },
        onSendScannedCode = { _, _ -> })
}
