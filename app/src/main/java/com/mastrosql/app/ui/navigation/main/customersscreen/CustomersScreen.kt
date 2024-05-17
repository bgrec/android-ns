package com.mastrosql.app.ui.navigation.main.customersscreen

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
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersList
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersSearchView
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    drawerState: DrawerState,
    navController: NavController,
    viewModel: CustomersMasterDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val customersUiState = viewModel.customersUiState
    val modifier = Modifier.fillMaxSize()

    when (customersUiState) {
        is CustomersUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(),
            loading = true
        )

        is CustomersUiState.Success -> CustomersResultScreen(
            customersUiState.customersMasterDataList,
            modifier = modifier.fillMaxWidth(),
            drawerState = drawerState,
            navController = navController
        )

        is CustomersUiState.Error -> ErrorScreen(
            customersUiState.exception,
            viewModel::getCustomersMasterData,
            modifier = modifier.fillMaxSize(),
            navController = navController
        )
    }

}

@ExperimentalMaterial3Api
@Composable
fun CustomersResultScreen(
    customerMasterDataList: List<CustomerMasterData>,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController
) {
    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState, title = R.string.drawer_customers
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
            CustomersSearchView(state = textState)
            CustomersList(
                customerMasterDataList = customerMasterDataList,
                searchedTextState = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController
            )
        }

    }
}

@Preview
@Composable
fun CustomersScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    CustomersScreen(
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
 * fun CustomersResultScreen(
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