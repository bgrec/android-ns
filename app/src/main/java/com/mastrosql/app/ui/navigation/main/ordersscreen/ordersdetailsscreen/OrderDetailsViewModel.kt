package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface OrderDetailsUiState {
    data class Success(
        val orderDetailList: List<OrderDetailsItem>
    ) : OrderDetailsUiState

    data class Error(val exception: Exception) : OrderDetailsUiState
    data object Loading : OrderDetailsUiState
}

/**
 * Factory for [OrderDetailsViewModel] that takes [OrderDetailsRepository] as a dependency
 */

class OrderDetailsViewModel(
    private val orderDetailsRepository: OrderDetailsRepository,
) : ViewModel() {

    var orderDetailUiState: OrderDetailsUiState by mutableStateOf(OrderDetailsUiState.Loading)
        private set

    init {
        //getOrders()
    }

    /**
     * Gets OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun getOrders() {
        viewModelScope.launch {
            orderDetailUiState = OrderDetailsUiState.Loading
            orderDetailUiState =
                try {
                    val orderDetailsListResult =
                        orderDetailsRepository.getOrderDetails().items
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
}