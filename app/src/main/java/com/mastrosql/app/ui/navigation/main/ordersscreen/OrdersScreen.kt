package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.ShowToast
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersList
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersTopAppBar
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.SearchViewOrders

object OrdersResultDestination : NavigationDestination {
    override val route = "orders_list"
    override val titleRes = R.string.clients_orders_bar_title
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navigateToOrderDetails: (Int, String?) -> Unit,
    navigateToOrderEntry: () -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrdersViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val ordersUiState = viewModel.ordersUiState
    //val ordersUiState by viewModel.ordersUiState.collectAsState()
    val modifier = Modifier.fillMaxSize()

    when (ordersUiState) {
        is OrdersUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController,
            loading = true
        )

        is OrdersUiState.Success -> OrdersResultScreen(
            navigateToOrderDetails = navigateToOrderDetails,
            navigateToOrderEntry = navigateToOrderEntry,
            ordersUiState.ordersList,
            ordersUiState.modifiedOrderId,
            modifier = modifier.fillMaxWidth(),
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
    navigateToOrderEntry: () -> Unit,
    ordersList: List<Order>,
    modifiedOrderId: MutableState<Int>,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrdersViewModel,
) {
    //val itemsUiState by viewModel.itemsUiState.collectAsState()
    //val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showDeliveryDialog = remember { mutableStateOf(false) }
    val selectedDeliveryState = remember { mutableIntStateOf(0) }


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
                    navigateToOrderEntry()
                    showToast = true
                }
            )
        },
        //Floating action button
        //Not used for now, but it's a good example of how to use the FAB
        /*floatingActionButton = {
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
        floatingActionButtonPosition = FabPosition.Center,*/
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }

            SearchViewOrders(state = textState)

            OrdersList(
                ordersList = ordersList,
                modifiedOrderId = modifiedOrderId,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController,
                navigateToOrderDetails = navigateToOrderDetails,
                showDeliveryDialog = showDeliveryDialog
            )
        }

        if (showDeliveryDialog.value) {

            // LaunchedEffect to set the initial value when the dialog is opened
            LaunchedEffect(showDeliveryDialog) {
                if (showDeliveryDialog.value) {
                    val modifiedOrder = ordersList.find { it.id == modifiedOrderId.value }
                    val modifiedOrderDeliveryState = modifiedOrder?.deliveryState
                    if (modifiedOrderDeliveryState != null) {
                        selectedDeliveryState.intValue = modifiedOrderDeliveryState
                    }
                }
            }

            AlertDialog(
                modifier = Modifier.wrapContentSize(),
                onDismissRequest = { showDeliveryDialog.value = false },
                title = { Text(stringResource(R.string.order_dialog_delivery_title)) },
                text = {


                    Column(modifier = Modifier.wrapContentSize()) {

                        // List of delivery types
                        val deliveryStates = listOf(
                            DeliveryState(0, R.string.order_deliveryState_value0, Color.Red),
                            DeliveryState(1, R.string.order_deliveryState_value1, Color.Green),
                            DeliveryState(2, R.string.order_deliveryState_value2, Color.Black),
                            DeliveryState(
                                3,
                                R.string.order_deliveryState_value3,
                                Color(0xFFFFA500)
                            )//orange
                        )

                        deliveryStates.forEach { deliveryState ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = {
                                            selectedDeliveryState.intValue =
                                                deliveryState.state //deve fare stessa cosa del onClick RadioButton
                                        }
                                    ),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDeliveryState.intValue == deliveryState.state,
                                    onClick = {
                                        selectedDeliveryState.intValue = deliveryState.state
                                    },
                                    colors = RadioButtonDefaults.colors(deliveryState.color),
                                )
                                Text(text = stringResource(deliveryState.nameState))
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeliveryDialog.value = false
                    }) {
                        Text(stringResource(R.string.dismiss_button))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDeliveryDialog.value = false

                        viewModel.updateDeliveryState(
                            context,
                            modifiedOrderId.value,
                            selectedDeliveryState.intValue
                        )
                    }) {
                        Text(stringResource(R.string.confirm_button))
                    }
                }
            )
        }
    }
}


//dialog delivery types
data class DeliveryState(
    val state: Int,
    val nameState: Int,
    val color: Color
)


/*
@Preview
@Composable
fun OrdersScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    OrdersScreen(
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current)
    )
}
*/
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