package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.ShowToast
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.EditDeliveryStateDialog
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.NewOrderBottomSheet
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersList
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersSearchView
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    //onNewOrder: () -> Unit,
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
            loading = true
        )

        is OrdersUiState.Success -> OrdersResultScreen(
            navigateToOrderDetails = navigateToOrderDetails,
            //onNewOrder = onNewOrder,
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
            navController = navController
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun OrdersResultScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    //onNewOrder: () -> Unit,
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
    val showBottomSheet = remember { mutableStateOf(false) }

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
                searchTextState = textState,
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
            // Bottom sheet to add a new order
            NewOrderBottomSheet(
                navController = navController,
                showBottomSheet = showBottomSheet,
                modifier = modifier,
                onDismissButton = { showBottomSheet.value = it },
                onConfirmButton = { order ->
                    viewModel.addNewOrder(context, order)
                    showBottomSheet.value = false
                }
            )
        }
    }
}
