package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination.route
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination.titleRes
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailList
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsTopAppBar
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.SearchView


object OrderDetailsDestination : NavigationDestination {
    override val route = "order_details"
    override val titleRes = R.string.order_details_edit
    const val orderIdArg = "orderId"
    const val orderDescriptionArg = "orderDescription"
    val routeWithArgs = "$route/{$orderIdArg}?orderDescription={$orderDescriptionArg}"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Edit : Screen(route, titleRes)
    object Add : Screen(route, titleRes)
}

val items = listOf(
    Screen.Edit,
    Screen.Edit,
)

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
            drawerState = drawerState,
            navController = navController
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
    drawerState: DrawerState,
    navController: NavController,
) {
    //val orderId = backStackEntry.arguments?.getInt(OrderDetailsDestination.orderIdArg)

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
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(16.dp),
                //verticalArrangement = Arrangement.Bottom
            ) {
                /*FloatingActionButton(
                    onClick = {},//{ navigateToEditItem(orderDetailId!!) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.order_details_entry_title),
                    )
                }*/

                Spacer(modifier = Modifier.padding(8.dp))

                FloatingActionButton(
                    onClick = {},//{ navigateToEditItem(orderDetailId!!) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = stringResource(R.string.order_details_qrscan_title),
                    )
                }
            }
        },
        /*bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },*/
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            SearchView(state = textState)
            OrderDetailList(
                orderDetailList = orderDetailList,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController
            )
        }

    }
}


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