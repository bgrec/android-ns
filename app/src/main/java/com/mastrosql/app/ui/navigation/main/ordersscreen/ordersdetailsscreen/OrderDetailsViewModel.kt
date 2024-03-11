package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface OrderDetailsUiState {

    data class Success(
        val orderDetailsList: List<OrderDetailsItem>,
        val orderId: Int? = null,
        val orderDescription: String? = null
    ) : OrderDetailsUiState

    data class Error(val exception: Exception) : OrderDetailsUiState

    data object Loading : OrderDetailsUiState

}

/**
 * Factory for [OrderDetailsViewModel] that takes [OrderDetailsRepository] as a dependency
 */

class OrderDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderDetailsRepository: OrderDetailsRepository,
) : ViewModel() {

    var orderDetailsUiState: OrderDetailsUiState by mutableStateOf(OrderDetailsUiState.Loading)
        private set

    //private val orderId: Int = checkNotNull(savedStateHandle[OrderDetailsDestination.orderIdArg])
    private val _orderId: MutableStateFlow<Int?> = MutableStateFlow(
        savedStateHandle.get(OrderDetailsDestination.ORDER_ID_ARG)
    )
    val orderId: StateFlow<Int?> = _orderId

    private val _orderDescription: MutableStateFlow<String?> = MutableStateFlow(
        savedStateHandle.get<String?>(OrderDetailsDestination.ORDER_DESCRIPTION_ARG)
    )
    private val orderDescription: StateFlow<String?> = _orderDescription

    init {
        getOrderDetails()
    }

    /**
     * Gets OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun getOrderDetails() {
        viewModelScope.launch {
            orderDetailsUiState = try {
                val orderDetailsListResult =
                    orderDetailsRepository.getOrderDetails(orderId.value).items
                OrderDetailsUiState.Success(orderDetailsListResult, orderId.value, orderDescription.value)
            } catch (e: IOException) {
                OrderDetailsUiState.Error(e)
            } catch (e: HttpException) {
                OrderDetailsUiState.Error(e)
            } catch (e: Exception) {
                OrderDetailsUiState.Error(e)
            }
        }
    }

    fun getAllOrderDetails() {
        viewModelScope.launch {
            orderDetailsUiState = OrderDetailsUiState.Loading
            orderDetailsUiState = try {
                val orderDetailsListResult = orderDetailsRepository.getAllOrderDetails().items
                OrderDetailsUiState.Success(orderDetailsListResult)
            } catch (e: IOException) {
                OrderDetailsUiState.Error(e)
            } catch (e: HttpException) {
                OrderDetailsUiState.Error(e)
            } catch (e: Exception) {
                OrderDetailsUiState.Error(e)
            }
        }
    }

    fun sendScannedCode(scannedCode: String) {

    }
}