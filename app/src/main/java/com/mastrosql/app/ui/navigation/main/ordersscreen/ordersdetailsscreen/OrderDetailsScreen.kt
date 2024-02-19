package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailList
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.SearchView

object OrderDetailsDestination : NavigationDestination {
    override val route = "order_details"
    override val titleRes = R.string.order_details_edit
    const val orderIdArg = "orderId"
    val routeWithArgs = "$route/$orderIdArg"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navigateToItemEntry: () -> Unit,
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: OrderDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    orderId: Int
) {
    val orderDetailUiState = viewModel.orderDetailUiState
    val modifier = Modifier.fillMaxSize()

    when (orderDetailUiState) {
        is OrderDetailsUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController,
            loading = true
        )

        is OrderDetailsUiState.Success -> OrderDetailResultScreen(
            orderDetailUiState.orderDetailList,
            modifier = modifier.fillMaxWidth(),
            drawerState = drawerState,
            navController = navController,
            orderId = orderId
        )

        is OrderDetailsUiState.Error -> ErrorScreen(
            orderDetailUiState.exception,
            viewModel::getOrders,
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
    orderDetailList: List<OrderDetailsItem>,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController,
    orderId: Int
) {
    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState, title = R.string.drawer_orders,
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
        navigateToItemEntry = {},
        navigateBack = {},
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current),
        orderId = 1//orderId
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