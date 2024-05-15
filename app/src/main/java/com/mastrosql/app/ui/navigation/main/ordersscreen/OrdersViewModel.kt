package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.R
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Interface [OrdersUiState] to represent the different states of the Orders screen.
 * The states are:
 * - [OrdersUiState.Success]: Represents the successful state of the Orders screen with the list of orders.
 * - [OrdersUiState.Error]: Represents the error state of the Orders screen with the exception.
 * - [OrdersUiState.Loading]: Represents the loading state of the Orders screen.
 */
sealed interface OrdersUiState {

    data class Success(
        val ordersList: List<Order>, val modifiedOrderId: MutableIntState
    ) : OrdersUiState

    data class Error(val exception: Exception) : OrdersUiState
    data object Loading : OrdersUiState
}

/**
 * Factory for [OrdersViewModel] that takes [OrdersRepository] as a dependency
 *
 */

class OrdersViewModel(
    private val ordersRepository: OrdersRepository,
) : ViewModel() {

    var ordersUiState: OrdersUiState by mutableStateOf(OrdersUiState.Loading)
        private set

    private val _modifiedOrderId = mutableIntStateOf(0)

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
            ordersUiState = try {
                val ordersListResult = ordersRepository.getOrders().items
                OrdersUiState.Success(
                        ordersListResult, modifiedOrderId = _modifiedOrderId
                    )
            } catch (e: Exception) {
                when (e) {
                    is IOException -> OrdersUiState.Error(e)
                    is HttpException -> OrdersUiState.Error(e)
                    else -> OrdersUiState.Error(e)
                }
            }
        }
    }


    /**
     * Function to update the delivery state of the order in the list
     * @param orderId The id of the order to update
     * @param deliveryState The new delivery state of the order
     *
     */
    private fun updateOrdersItem(orderId: Int, deliveryState: Int) {
        (ordersUiState as? OrdersUiState.Success)?.let { successState ->
            val ordersList = successState.ordersList.toMutableList()
            val index = ordersList.indexOfFirst { it.id == orderId }
            if (index != -1) {
                ordersList[index] = ordersList[index].copy(deliveryState = deliveryState)
                ordersUiState = OrdersUiState.Success(ordersList, mutableIntStateOf(orderId))
            }
        }
    }

    /**
     * Function to update the delivery state of the order in the database
     * @param context The context to show the toast message
     * @param orderId The id of the order to update
     * @param deliveryState The new delivery state of the order
     */
    fun updateDeliveryState(context: Context, orderId: Int, deliveryState: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ordersRepository.updateDeliveryState(orderId, deliveryState)

                handleResponse(context, response) {
                    launch {
                        ToastUtils.showToast(
                            context, Toast.LENGTH_SHORT, "Modifica salvata con successo"
                        )
                    }

                    //If the response is successful, update the delivery state of the order
                    // without refreshing the list
                    updateOrdersItem(orderId, deliveryState)
                }

            } catch (e: Exception) {
                // Handle exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            }
        }
    }

    /**
     * Function to add a new order to the list
     * @param context The context to show the toast message
     * @param order The order to add
     */
    fun addNewOrder(context: Context, order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ordersRepository.addNewOrder(order)

                handleResponse(context, response) {
                    val newOrderResponse = response.body()
                    val newOrder = newOrderResponse?.getAddedOrder()
                    newOrder?.let { addedOrder ->
                        launch {
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                context.getString(R.string.new_order_added, addedOrder.number)
                            )
                        }
                        _modifiedOrderId.intValue = addedOrder.id

                        //Log.d("addNewOrder", "Added order: $addedOrder")

                        // Check if the current UI state is Success
                        if (ordersUiState is OrdersUiState.Success) {
                            // Add the new order to the beginning (index 0) of the list without refreshing
                            val currentOrders =
                                (ordersUiState as OrdersUiState.Success).ordersList.toMutableList()
                            currentOrders.add(0, addedOrder)
                            ordersUiState = OrdersUiState.Success(currentOrders, _modifiedOrderId)
                        } else {
                            // If the UI state is not Success (e.g., Loading or Error),
                            // trigger a refresh of the orders.
                            getOrders()
                        }
                    } ?: launch {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.error_no_order_added)
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            }
        }
    }

    /**
     * Function to handle the response from the API
     * @param context The context to show the toast message
     * @param response The response from the API
     * @param onSuccess The lambda function to execute when the response is successful
     */
    private inline fun handleResponse(
        context: Context, response: Response<*>, crossinline onSuccess: () -> Unit
    ) {

        when (response.code()) {
            200 -> onSuccess()
            401 -> NetworkSuccessHandler.handleUnauthorized(
                context, viewModelScope
            ) {
                // Handle unauthorized
                ordersUiState = OrdersUiState.Error(HttpException(response))
            }

            404 -> NetworkSuccessHandler.handleNotFound(
                context, viewModelScope, response.code()
            )

            500, 503 -> NetworkSuccessHandler.handleServerError(
                context, response, viewModelScope
            )

            else -> NetworkSuccessHandler.handleUnknownError(
                context, response, viewModelScope
            )
        }
    }
}
