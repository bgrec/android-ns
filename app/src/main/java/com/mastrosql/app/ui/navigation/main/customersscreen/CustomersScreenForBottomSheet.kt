package com.mastrosql.app.ui.navigation.main.customersscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersList
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersSearchView
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen

@Composable
fun CustomersScreenForBottomSheet(
    navController: NavController,
    onCustomerSelected: ((Int) -> Unit)? = null,
    viewModel: CustomersMasterDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val customersUiState = viewModel.customersUiState
    val modifier = Modifier.fillMaxSize()
    var customerMasterDataList: List<CustomerMasterData> = emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        //.padding(it),
        // verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                    customersUiState.exception,
                    viewModel::getCustomersMasterData,
                    modifier = modifier.fillMaxSize(),
                    navController = navController
                )
            }
        }


        val textState = remember { mutableStateOf(TextFieldValue("")) }
        CustomersSearchView(state = textState)
        CustomersList(
            customerMasterDataList = customerMasterDataList,
            searchedTextState = textState,
            onCustomerSelected = onCustomerSelected,
            modifier = Modifier.padding(4.dp),
            navController = navController
        )
    }
}