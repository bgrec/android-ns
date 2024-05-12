package com.mastrosql.app.ui.navigation.main.customersscreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface CustomersUiState {
    data class Success(
        val customersMasterDataList: List<CustomerMasterData>,
        var destinationsDataList: List<DestinationData>? = null,
        var modifiedCustomer: MutableState<CustomerMasterData>? = null,
        var modifiedDestination: MutableState<DestinationData>? = null,
        var modifiedCustomerDestinationsList: List<DestinationData>? = null
    ) : CustomersUiState

    data class Error(val exception: Exception) : CustomersUiState
    data object Loading : CustomersUiState
}

/**
 * Factory for [CustomersMasterDataViewModel] that takes [CustomersMasterDataRepository] as a dependency
 */

class CustomersMasterDataViewModel(
    private val customersMasterDataRepository: CustomersMasterDataRepository,
) : ViewModel() {

    var customersUiState: CustomersUiState by mutableStateOf(CustomersUiState.Loading)
        private set


    init {
        getCustomersMasterData()
        //getPagedCustomerMasterData()
        //Log.i("CustomersModel", "CustomersMasterDataViewModel created!")
    }

    /**
     * Gets Customers Master Data information from the MastroAndroid API Retrofit service and updates the
     * [CustomerMasterData] [List] [MutableList].
     */
    fun getCustomersMasterData() {
        viewModelScope.launch {
            customersUiState = CustomersUiState.Loading
            try {
                val customerMasterDataListResult =
                    customersMasterDataRepository.getCustomersMasterData().items

                /*
                    //Already implemented in the backend, so no need to trim the strings
                    val trimmedCustomerList = customerMasterDataListResult.map { it.trimAllStrings() }
                    CustomersUiState.Success(trimmedCustomerList)
                */

                // Update the UI state with the trimmed customer list
                customersUiState = CustomersUiState.Success(customerMasterDataListResult)

                // Fetch destinations data in the background after fetching customers data
                if (customersUiState is CustomersUiState.Success) {
                    getAllDestinationsData()
                }

            } catch (e: IOException) {
                customersUiState = CustomersUiState.Error(e)
            } catch (e: HttpException) {
                customersUiState = CustomersUiState.Error(e)
            } catch (e: Exception) {
                customersUiState = CustomersUiState.Error(e)
            }
        }
    }

    private fun getAllDestinationsData() {
        viewModelScope.launch {
            try {
                val destinationsDataListResult =
                    customersMasterDataRepository.getDestinationsData().items

                // Update the destinations list in UI
                updateDestinationsList(destinationsDataListResult)

            } catch (e: IOException) {
                Log.e("DestinationsData", "fetchDestinationsDataInBackground: $e")
                // Handle errors if needed
            } catch (e: HttpException) {
                // Handle errors if needed
                Log.e("DestinationsData", "fetchDestinationsDataInBackground: $e")
            } catch (e: Exception) {
                // Handle errors if needed
                Log.e("DestinationsData", "fetchDestinationsDataInBackground: $e")
            }
        }
    }

    /**
     * Updates the destinations list in the UI.
     */
    private fun updateDestinationsList(destinationsDataList: List<DestinationData>) {
        // Update UI with the fetched destinations data
        when (val currentState = customersUiState) {
            is CustomersUiState.Success -> {
                //To update customersUiState, we need to create a new instance of the state
                //val updatedUiState = currentState.copy(destinationsDataList = destinationsDataList)
                //customersUiState = updatedUiState

                // For the moment, we will update the destinations list in the existing state
                currentState.destinationsDataList = destinationsDataList
            }
            // Handle other states if needed
            else -> {
                // Do nothing or handle as per your requirement
            }
        }
    }

    fun updateModifiedCustomer(modifiedCustomer: CustomerMasterData) {
        // Update UI with the fetched destinations data
        when (val currentState = customersUiState) {
            is CustomersUiState.Success -> {
                // Update modifiedCustomer in the UI state
                currentState.modifiedCustomer = mutableStateOf(modifiedCustomer)

                // Filter destinations list by modified customer id
                val filteredDestinationsList =
                    currentState.destinationsDataList?.filter { it.customerId == modifiedCustomer.id }

                // Update modifiedCustomerDestinationsList in the UI state
                currentState.modifiedCustomerDestinationsList = filteredDestinationsList

                //Log.d("ModifiedDestinations", "ModifiedDestinations: $filteredDestinationsList")
                // Update customersUiState with the modified customer and filtered destinations list
                customersUiState = currentState
            }
            // Handle other states if needed
            else -> {
                // Do nothing or handle as per your requirement
            }
        }
    }

    fun updateModifiedDestination(modifiedDestination: DestinationData) {
        // Update UI with the fetched destinations data
        when (val currentState = customersUiState) {
            is CustomersUiState.Success -> {
                // Update modifiedDestination in the UI state
                currentState.modifiedDestination = mutableStateOf(modifiedDestination)

                // Update customersUiState with the modified destination
                customersUiState = currentState
            }
            // Handle other states if needed
            else -> {
                // Do nothing or handle as per your requirement
            }
        }
    }
}