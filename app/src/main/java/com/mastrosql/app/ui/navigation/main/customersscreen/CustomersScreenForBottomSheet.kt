package com.mastrosql.app.ui.navigation.main.customersscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
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
    onCustomerSelected: ((CustomerMasterData, DestinationData?, Boolean) -> Unit)? = null,
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

    val showCustomersList = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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

        if (showCustomersList.value) {

            // Customers title
            Row {
                Text(
                    text = stringResource(id = R.string.archives_client_title),
                    fontSize = typography.titleLarge.fontSize,
                    style = typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // Customers search view
            CustomersSearchView(state = customerTextState)

            // Customers list
            CustomersList(
                customerMasterDataList = customerMasterDataList,
                searchedTextState = customerTextState,
                onCustomerSelected = { customerMasterData ->

                    // Update CustomersUiState with the selected customer
                    viewModel.updateModifiedCustomer(customerMasterData)

                    modifiedCustomerDestinationsList =
                        (customersUiState as CustomersUiState.Success).modifiedCustomerDestinationsList
                            ?: emptyList()

                    // Check if the selected customer has destinations
                    if (modifiedCustomerDestinationsList.isNotEmpty()) {
                        // Show destinations list
                        showCustomersList.value = false
                    } else {
                        showCustomersList.value = true

                        val destinationData = null
                        val selectionCompleted = true

                        // Call the onCustomerSelected callback with the selected customer without destination
                        onCustomerSelected?.invoke(
                            customerMasterData,
                            destinationData,
                            selectionCompleted
                        )
                    }
                },
                modifier = Modifier.padding(4.dp),
                navController = navController
            )
        } else {
            // Destinations title
            Row {
                Text(
                    text = stringResource(id = R.string.archives_destinations_title),
                    fontSize = typography.titleLarge.fontSize,
                    style = typography.titleLarge
                )
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

                    // Call the onCustomerSelected callback with the selected customer and destination
                    (customersUiState as CustomersUiState.Success).modifiedCustomer?.let {

                        val selectionCompleted = true
                        onCustomerSelected?.invoke(
                            it.value, destinationData, selectionCompleted
                        )
                    }
                    // Update the destination data in the view model
                    if (destinationData != null) {
                        viewModel.updateModifiedDestination(destinationData)
                    }
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}