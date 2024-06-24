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
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.EditWhOutboundDataDialog
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.NewWhOutboundBottomSheet
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundList
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundSearchView
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundTopAppBar
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun WarehouseOutOperationsScreen(
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: WhOutboundViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // State
    val whOutboundUiState = viewModel.whOutboundUiState

    val modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()

    when (whOutboundUiState) {
        is WhOutboundUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(), loading = true
        )

        is WhOutboundUiState.Success -> WhOutboundResultScreen(
            navigateToWhOutboundDetails = navigateToWhOutboundDetails,
            whOutboundUiState = whOutboundUiState,
            modifier = modifier,
            drawerState = drawerState,
            navController = navController,
            viewModel = viewModel
        )

        is WhOutboundUiState.Error -> ErrorScreen(
            whOutboundUiState.exception,
            viewModel::getWhOutbound,
            modifier = modifier.fillMaxSize(),
            navController = navController
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun WhOutboundResultScreen(
    modifier: Modifier = Modifier,
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    whOutboundUiState: WhOutboundUiState.Success,
    drawerState: DrawerState,
    navController: NavController,
    viewModel: WhOutboundViewModel,
) {
    // Context used to show the toast
    val context = LocalContext.current

    WhOutboundsResult(modifier = modifier,
        navigateToWhOutboundDetails = navigateToWhOutboundDetails,
        whOutboundUiState = whOutboundUiState,
        drawerState = drawerState,
        navController = navController,
        onAddNewWhOutbound = { warehouseOutbound ->
            viewModel.addNewWhOutbound(
                context, warehouseOutbound
            )
        })
}

@ExperimentalMaterial3Api
@Composable
fun WhOutboundsResult(
    modifier: Modifier = Modifier,
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    whOutboundUiState: WhOutboundUiState.Success,
    drawerState: DrawerState,
    navController: NavController,
    onAddNewWhOutbound: (WarehouseOutbound) -> Unit,
) {

    // CoroutineScope to handle scrolling actions
    val coroutineScope = rememberCoroutineScope()

    // State to control the warehouse outbound edit data dialog visibility
    val showEditWhOutboundDataDialog = remember { mutableStateOf(false) }

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
            WhOutboundTopAppBar(drawerState = drawerState,
                title = stringResource(R.string.warehouse_outbound_operations),
                onAddWhOutboundClick = {
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
                        contentDescription = stringResource(R.string.scroll_to_top),
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

            // Search view for filtering the warehouse outbounds list
            WhOutboundSearchView(state = textState)

            // Warehouse Outbound list, lazy column with the warehouse outbounds
            WhOutboundList(
                modifier = Modifier.padding(4.dp),
                listState = listState,
                whOutboundList = whOutboundUiState.whOutboundsList,
                modifiedWhOutboundId = whOutboundUiState.modifiedWhOutboundId,
                searchTextState = textState,
                navigateToWhOutboundDetails = navigateToWhOutboundDetails,
                showEditWhOutboundDataDialog = showEditWhOutboundDataDialog
            )
        }

        if (showEditWhOutboundDataDialog.value) {
            // Warehouse Outbound data Alert dialog, used to show and edit the warehouse outbound  data
            EditWhOutboundDataDialog(modifier = modifier,
                showEditWhOutboundDataDialog = showEditWhOutboundDataDialog,
                whOutboundUiState = whOutboundUiState,
                onUpdateWhOutboundData = { })
        }

        if (showBottomSheet.value) {
            // Bottom sheet to add a new warehouse outbound
            NewWhOutboundBottomSheet(navController = navController,
                showBottomSheet = showBottomSheet,
                modifier = modifier,
                onDismissButton = { showBottomSheet.value = it },
                onConfirmButton = { whOutbound ->
                    onAddNewWhOutbound(whOutbound)
                    showBottomSheet.value = false
                })
        }
    }
}


