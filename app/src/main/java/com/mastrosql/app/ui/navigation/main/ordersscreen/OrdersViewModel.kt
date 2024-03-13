package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface OrdersUiState {

    data class Success(
        val ordersList: List<Order>,
        val modifiedOrderId: MutableState<Int>
    ) : OrdersUiState

    data class Error(val exception: Exception) : OrdersUiState
    data object Loading : OrdersUiState
}

/**
 * Factory for [OrdersViewModel] that takes [OrdersRepository] as a dependency
 */

class OrdersViewModel(
    private val ordersRepository: OrdersRepository,
) : ViewModel() {

    var ordersUiState: OrdersUiState by mutableStateOf(OrdersUiState.Loading)
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
            ordersUiState = OrdersUiState.Loading
            ordersUiState =
                try {
                    val ordersListResult =
                        ordersRepository.getOrders().items
                    OrdersUiState
                        .Success(
                            ordersListResult,
                            modifiedOrderId = mutableIntStateOf(-1)
                        )
                } catch (e: IOException) {
                    OrdersUiState.Error(e)
                } catch (e: HttpException) {
                    OrdersUiState.Error(e)
                } catch (e: Exception) {
                    OrdersUiState.Error(e)
                }
        }
    }

//    fun setEditIndex(index: Int) {
//        ordersUiState = when (val currentState = ordersUiState) {
//            is OrdersUiState.Success -> currentState.copy(modifiedOrderId = mutableStateOf(index))
//            else -> currentState
//        }
//    }
}