package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents.NewWhOutboundBottomSheet
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents.OrdersList
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents.OrdersSearchView
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents.OrdersTopAppBar
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents.WhOutboundState
import kotlinx.coroutines.launch

/**
 * Orders screen composable
 */
@ExperimentalMaterial3Api
@Composable
fun WarehouseOutOperationsScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: WhOutboundViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // State
    val ordersUiState = viewModel.ordersUiState

    val modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()

    when (ordersUiState) {
        is WhOutboundUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(), loading = true
        )

        is WhOutboundUiState.Success -> OrdersResultScreen(
            navigateToOrderDetails = navigateToOrderDetails,
            //onNewOrder = onNewOrder,
            ordersUiState = ordersUiState,
            modifier = modifier,
            drawerState = drawerState,
            navController = navController,
            viewModel = viewModel
        )

        is WhOutboundUiState.Error -> ErrorScreen(
            ordersUiState.exception,
            viewModel::getOrders,
            modifier = modifier.fillMaxSize(),
            navController = navController
        )
    }
}

/**
 * Orders result screen composable, displays the list of orders.
 */
@ExperimentalMaterial3Api
@Composable
fun OrdersResultScreen(
    modifier: Modifier = Modifier,
    navigateToOrderDetails: (Int, String?) -> Unit,
    //onNewOrder: () -> Unit,
    ordersUiState: WhOutboundUiState.Success,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: WhOutboundViewModel,
) {
    // Context used to show the toast
    val context = LocalContext.current

    OrdersResult(modifier = modifier, navigateToOrderDetails = navigateToOrderDetails,
        ordersUiState = ordersUiState,
        drawerState = drawerState,
        navController = navController,
        onUpdateDeliveryState = { orderId, deliveryState ->
            viewModel.updateDeliveryState(
                context = context, orderId = orderId, deliveryState = deliveryState
            )
        },
        onUpdateOrderData = { orderState ->
            viewModel.updateOrderData(
                context = context, orderState = orderState
            )
        },
        onAddNewOrder = { order ->
            viewModel.addNewOrder(
                context, order
            )
        })
}

/**
 * Orders result screen composable, displays the list of orders.
 */
@ExperimentalMaterial3Api
@Composable
fun OrdersResult(
    modifier: Modifier = Modifier,
    navigateToOrderDetails: (Int, String?) -> Unit,
    //onNewOrder: () -> Unit,
    ordersUiState: WhOutboundUiState.Success,
    drawerState: DrawerState,
    navController: NavController,
    onUpdateDeliveryState: (Int, Int) -> Unit,
    onUpdateOrderData: (WhOutboundState) -> Unit,
    onAddNewOrder: (WarehouseOutbound) -> Unit,
) {

    // CoroutineScope to handle scrolling actions
    val coroutineScope = rememberCoroutineScope()

    // State to control the delivery dialog visibility
    val showEditDeliveryDialog = remember { mutableStateOf(false) }

    // State to control the order data dialog visibility
    val showEditOrderDataDialog = remember { mutableStateOf(false) }

    // State to control the bottom sheet visibility
    val showBottomSheet = remember { mutableStateOf(false) }

    // Lazy list state to handle the scroll actions
    val listState = rememberLazyListState()

    // State to show the floating button
    val showFloatingButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    Scaffold(
        topBar = {
            OrdersTopAppBar(drawerState = drawerState,
                title = stringResource(R.string.clients_orders_bar_title),
                onAddOrderClick = {
                    showBottomSheet.value = true
                })
        },
        floatingActionButton = {
            AnimatedVisibility(visible = showFloatingButton) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = stringResource(R.string.order_entry_title)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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
                modifier = Modifier.padding(4.dp),
                listState = listState,
                ordersList = ordersUiState.whOutboundsList,
                modifiedOrderId = ordersUiState.modifiedWhOutboundId,
                searchTextState = textState,
                navigateToOrderDetails = navigateToOrderDetails,
                showEditDeliveryDialog = showEditDeliveryDialog,
                showEditOrderDataDialog = showEditOrderDataDialog
            )
        }

        if (showEditDeliveryDialog.value) {
//            // Edit delivery Alert dialog, used to update the delivery state of an order
//            EditDeliveryStateDialog(showEditDeliveryDialog = showEditDeliveryDialog,
//                ordersUiState = ordersUiState,
//                onUpdateDeliveryState = { orderId, deliveryState ->
//                    onUpdateDeliveryState(orderId, deliveryState)
//                })
        }

        if (showEditOrderDataDialog.value) {
//            // Order data Alert dialog, used to show and edit the order data
//            EditOrderDataDialog(modifier = modifier,
//                showEditOrderDataDialog = showEditOrderDataDialog,
//                ordersUiState = ordersUiState,
//                onUpdateOrderData = { orderState ->
//                    onUpdateOrderData(orderState)
//                })
        }

        if (showBottomSheet.value) {
            // Bottom sheet to add a new order
            NewWhOutboundBottomSheet(navController = navController,
                showBottomSheet = showBottomSheet,
                modifier = modifier,
                onDismissButton = { showBottomSheet.value = it },
                onConfirmButton = { whOutbound ->
                    onAddNewOrder(whOutbound)
                    showBottomSheet.value = false
                })
        }
    }
}


