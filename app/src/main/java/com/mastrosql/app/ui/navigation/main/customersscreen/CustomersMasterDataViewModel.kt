@file:Suppress("EmptyMethod")

package com.mastrosql.app.ui.navigation.main.customersscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface CustomersUiState {
    data class Success(
        val customersMasterDataList: List<CustomerMasterData>
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
        Log.i("CustomersModel", "CustomersMasterDataViewModel created!")
    }

    /**
     * Gets Customers Master Data information from the MastroAndroid API Retrofit service and updates the
     * [CustomerMasterData] [List] [MutableList].
     */
    fun getCustomersMasterData() {
        viewModelScope.launch {
            customersUiState = CustomersUiState.Loading
            customersUiState =
                try {
                    val customerMasterDataListResult =
                        customersMasterDataRepository.getCustomersMasterData().items

                    val trimmedCustomerList =
                        customerMasterDataListResult.map { it.trimAllStrings() }
                    CustomersUiState.Success(trimmedCustomerList)
                } catch (e: IOException) {
                    CustomersUiState.Error(e)
                } catch (e: HttpException) {
                    CustomersUiState.Error(e)
                } catch (e: Exception) {
                    CustomersUiState.Error(e)
                }
        }
    }
}