package com.mastrosql.app.ui.navigation.main.customersscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersList
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersSearchView
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.DestinationsList
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.DestinationsSearchView
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen

@Composable
fun CustomersScreenForBottomSheet(
    navController: NavController,
    onCustomerSelected: ((CustomerMasterData, DestinationData?) -> Unit)? = null,
    viewModel: CustomersMasterDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val customersUiState = viewModel.customersUiState
    val modifier = Modifier.fillMaxSize()
    var customerMasterDataList: List<CustomerMasterData> by remember { mutableStateOf(emptyList()) }
    var modifiedCustomerDestinationsList: List<DestinationData> by remember {
        mutableStateOf(
            emptyList()
        )
    }

    val showDestinations = remember { mutableStateOf(false) }

//    // Observe changes in showDestinations
//    LaunchedEffect(showDestinations.value) {
//        // When showDestinations becomes true and destinations data is updated
//        if (showDestinations.value && customersUiState is CustomersUiState.Success) {
//            // Get destinations data from the UI state
//            destinationsDataList = (customersUiState as CustomersUiState.Success).destinationsDataList ?: emptyList()
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Get the customers data from the view model
        when (customersUiState) {
            is CustomersUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            is CustomersUiState.Success -> {
                customerMasterDataList = customersUiState.customersMasterDataList
            }

            is CustomersUiState.Error -> {
                ErrorScreen(
                    exception = customersUiState.exception,
                    retryAction = viewModel::getCustomersMasterData,
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    navController = navController
                )
            }
        }

        val customerTextState = remember { mutableStateOf(TextFieldValue("")) }
        val destinationTextState = remember { mutableStateOf(TextFieldValue("")) }

        if (!showDestinations.value) {
            Row {
//                IconButton(onClick = { showDestinations.value = false }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Selezionare il cliente")
                Spacer(modifier = Modifier.padding(4.dp))

//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
//                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            // Customers search view
            CustomersSearchView(state = customerTextState)

            // Customers list
            CustomersList(
                customerMasterDataList = customerMasterDataList,
                searchedTextState = customerTextState,
                onCustomerSelected = { customerMasterData ->
                    // Call the onCustomerSelected callback if provided
                    onCustomerSelected?.invoke(customerMasterData, null)

                    // Update CustomersUiState with the selected customer
                    viewModel.updateModifiedCustomer(customerMasterData)

//                    if (customersUiState is CustomersUiState.Success
//                        && customersUiState.destinationsDataList != null
//                        && customersUiState.destinationsDataList!!.isNotEmpty()
//                    ) {
//                        Log.d("CustomersScreen", "DestinationsDataList: ${customersUiState.destinationsDataList}")
//                        destinationsDataList = customersUiState.destinationsDataList!!
//                        Log.d("CustomersScreen", "DestinationsDataList: $destinationsDataList")
//
//                    } else {
//                        // Fetch destinations data in the background after fetching customers data
//                        if (customersUiState is CustomersUiState.Success) {
//                            destinationsDataList = emptyList()
//                        }
//                    }
                    modifiedCustomerDestinationsList =
                        (customersUiState as CustomersUiState.Success).modifiedCustomerDestinationsList
                            ?: emptyList()

                    if (modifiedCustomerDestinationsList.isNotEmpty()) {
                        showDestinations.value = true
                    }

                },
                modifier = Modifier.padding(4.dp),
                navController = navController
            )
        } else {

//            LaunchedEffect(showDestinations.value) {
//                if (customersUiState is CustomersUiState.Success
//                    && customersUiState.destinationsDataList != null
//                    && customersUiState.destinationsDataList!!.isNotEmpty()
//                ) {
//                    destinationsDataList = customersUiState.destinationsDataList!!
//                }
//
//            }


            Row {
                IconButton(onClick = { showDestinations.value = false }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Selezionare Destinazioni ")
                Spacer(modifier = Modifier.padding(4.dp))

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))

            // Destinations search view
            DestinationsSearchView(state = destinationTextState)
            // Destinations list
            Log.d("DestinationsData", "DestinationsDataList: $modifiedCustomerDestinationsList")
            DestinationsList(
                destinationsDataList = modifiedCustomerDestinationsList,
                searchedTextState = destinationTextState,
                onDestinationSelected = { destinationData ->
                    // Call the onCustomerSelected callback if provided
                    (customersUiState as CustomersUiState.Success).modifiedCustomer?.let {
                        onCustomerSelected?.invoke(
                            it.value, destinationData
                        )
                    }

                    viewModel.updateModifiedDestination(destinationData)
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}