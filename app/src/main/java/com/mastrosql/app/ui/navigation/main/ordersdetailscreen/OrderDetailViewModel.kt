package com.mastrosql.app.ui.navigation.main.ordersdetailscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orderdetail.OrderDetailRepository
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetail
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface OrderDetailUiState {
    data class Success(
        val orderDetailList: List<OrderDetail>
    ) : OrderDetailUiState

    data class Error(val exception: Exception) : OrderDetailUiState
    data object Loading : OrderDetailUiState
}

/**
 * Factory for [OrderDetailViewModel] that takes [OrderDetailRepository] as a dependency
 */

class OrderDetailViewModel(
    private val orderDetailRepository: OrderDetailRepository,
) : ViewModel() {

    var orderDetailUiState: OrderDetailUiState by mutableStateOf(OrderDetailUiState.Loading)
        private set

    init {
        getOrders()
    }

    /**
     * Gets Orders information from the MastroAndroid API Retrofit service and updates the
     * [Order] [List] [MutableList].
     */
    fun getOrders() {
        viewModelScope.launch {
            orderDetailUiState = OrderDetailUiState.Loading
            orderDetailUiState =
                try {
                    val orderDetailListResult =
                        orderDetailRepository.getOrderDetail().items

                    // Trim all strings in the list
                    //Not used because it is already done in the API (see the ordersView)
                    /*val trimmedOrdersList =
                        ordersListResult.map { it.trimAllStrings() }
                    OrdersUiState.Success(trimmedOrdersList)*/

                    OrderDetailUiState.Success(orderDetailListResult)
                } catch (e: IOException) {
                    OrderDetailUiState.Error(e)
                } catch (e: HttpException) {
                    OrderDetailUiState.Error(e)
                } catch (e: Exception) {
                    OrderDetailUiState.Error(e)
                }
        }
    }
}