package com.mastrosql.app.ui.navigation.main.customersScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mastrosql.app.MastroAndroidApplication
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.data.customer.CustomersMasterDataRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface CustomersUiState {
    data class Success(val customersMasterDataList: List<CustomerMasterData>) : CustomersUiState
    object Error : CustomersUiState
    object Loading : CustomersUiState
}

class CustomersMasterDataViewModel(
    private val customersMasterDataRepository: CustomersMasterDataRepository
) : ViewModel() {

    /** The mutable State that stores the status of the most recent request */

    var customersUiState: CustomersUiState by mutableStateOf(CustomersUiState.Loading)
        private set

    /**
     * Call getCustomersMasterData() on init so we can display status immediately.
     */
    init {
        getCustomersMasterData()
    }

    /**
     * Gets Customers Master Data information from the MastroAndroid API Retrofit service and updates the
     * [CustomersMasterData] [List] [MutableList].
     */
    fun getCustomersMasterData() {
        viewModelScope.launch {
            customersUiState = CustomersUiState.Loading
            customersUiState =
                try {
                    val customerMasterDataListResult = customersMasterDataRepository.getCustomersMasterData().items
                    val trimmedCustomerList = customerMasterDataListResult.map { it.trimAllStrings() }
                    CustomersUiState.Success(trimmedCustomerList)
                } catch (e: IOException) {
                    Log.i("Customers 3", e.toString())
                    CustomersUiState.Error
                } catch (e: HttpException) {
                    Log.i("Customers 4", e.toString())
                    CustomersUiState.Error
                }
        }
    }

    /**
     * Factory for [CustomersMasterDataViewModel] that takes [CustomersMasterDataRepository] as a dependency
     */

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MastroAndroidApplication)
                val customersMasterDataRepository =
                    application.container.customersMasterDataRepository
                CustomersMasterDataViewModel(customersMasterDataRepository = customersMasterDataRepository)
            }
        }
    }

}


