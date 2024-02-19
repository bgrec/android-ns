package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.NavigationDestination
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersList
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersTopAppBar
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.SearchView

object OrdersResultDestination : NavigationDestination {
    override val route = "orders_list"
    override val titleRes = R.string.clients_orders_bar_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navigateToOrderDetails: (Int) -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrdersViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ordersUiState = viewModel.ordersUiState
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
            ordersUiState.ordersList,
            modifier = modifier.fillMaxWidth(),
            drawerState = drawerState,
            navController = navController
        )

        is OrdersUiState.Error -> ErrorScreen(
            ordersUiState.exception,
            viewModel::getOrders,
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController
        )

        else -> {}
    }

}

@ExperimentalMaterial3Api
@Composable
fun OrdersResultScreen(
    navigateToOrderDetails: (Int) -> Unit,
    ordersList: List<Order>,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController
) {
    //val itemsUiState by viewModel.itemsUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(topBar = {
        /*AppBar(
            drawerState = drawerState, title = R.string.drawer_orders,
        )*/
        OrdersTopAppBar(
            title = stringResource(OrdersResultDestination.titleRes),
            canNavigateBack = false,
            scrollBehavior = scrollBehavior
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            SearchView(state = textState)
            OrdersList(
                ordersList = ordersList,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController,
                navigateToOrderDetails = navigateToOrderDetails
            )
        }

    }
}

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