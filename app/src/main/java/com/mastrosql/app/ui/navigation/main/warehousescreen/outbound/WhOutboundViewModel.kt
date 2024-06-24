package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.warehouse.outbound.WarehouseOutRepository
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface WhOutboundUiState {


    @Suppress("KDocMissingDocumentation")
    data class Success(
        val whOutboundsList: List<WarehouseOutbound>,
        val modifiedWhOutboundId: MutableIntState
    ) : WhOutboundUiState

    @Suppress("KDocMissingDocumentation")
    data class Error(val exception: Exception) : WhOutboundUiState

    @Suppress("KDocMissingDocumentation")
    data object Loading : WhOutboundUiState
}

class WhOutboundViewModel(
    private val whOutboundRepository: WarehouseOutRepository,
) : ViewModel() {

    /**
     * Mutable state to hold the OrdersUiState
     */
    var ordersUiState: WhOutboundUiState by mutableStateOf(WhOutboundUiState.Loading)
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
            ordersUiState = WhOutboundUiState.Loading
            ordersUiState = try {
                val ordersListResult = whOutboundRepository.getWhOutbound().items
                WhOutboundUiState.Success(
                    ordersListResult, modifiedWhOutboundId = _modifiedOrderId
                )
            } catch (e: Exception) {
                when (e) {
                    is IOException -> WhOutboundUiState.Error(e)
                    is HttpException -> WhOutboundUiState.Error(e)
                    else -> WhOutboundUiState.Error(e)
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
    @Suppress("Destructure")
    private fun updateOrdersItemDeliveryState(orderId: Int, deliveryState: Int) {
        (ordersUiState as? WhOutboundUiState.Success)?.let { successState ->
            val ordersList = successState.whOutboundsList.toMutableList()
            val index = ordersList.indexOfFirst { it.id == orderId }
//            if (index != -1) {
//                ordersList[index] = ordersList[index].copy(deliveryState = deliveryState)
//                ordersUiState = WhOutboundUiState.Success(ordersList, mutableIntStateOf(orderId))
//            }
        }
    }

    /**
     * Function to update the state of the order in the list
     * @param orderState The order state to update
     *
     */
    private fun updateOrdersItemData(orderState: WhOutboundState) {
        val orderId = orderState.operationId.value
//        val orderDescription = orderState.orderDescription.value.text
//        val orderDeliveryDate = DateHelper.formatDateToInput(orderState.deliveryDate.value.text)

        (ordersUiState as? WhOutboundUiState.Success)?.let { (ordersList1, modifiedOrderId) ->
            val ordersList = ordersList1.toMutableList()
            val index = ordersList.indexOfFirst { it.id == orderId }
//            if (index != -1) {
//                ordersList[index] = ordersList[index].copy(
//                    description = orderDescription, deliveryDate = orderDeliveryDate
//                )
//                ordersUiState = WhOutboundUiState.Success(ordersList, mutableIntStateOf(orderId))
//            }
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
//                val response = whOutboundRepository.updateDeliveryState(orderId, deliveryState)
//
//                handleResponse(context, response) {
//                    launch {
//                        ToastUtils.showToast(
//                            context,
//                            Toast.LENGTH_SHORT,
//                            context.getString(R.string.delivery_state_updated)
//                        )
//                    }
//
//                    //If the response is successful, update the delivery state of the order
//                    // without refreshing the list
//                    updateOrdersItemDeliveryState(orderId, deliveryState)
//                }

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
    fun addNewOrder(context: Context, order: WarehouseOutbound) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//
//                val response = ordersRepository.addNewOrder(order)
//
//                handleResponse(context, response) {
//                    val newOrderResponse = response.body()
//                    val newOrder = newOrderResponse?.getAddedOrder()
//                    newOrder?.let { addedOrder ->
//                        launch {
//                            ToastUtils.showToast(
//                                context,
//                                Toast.LENGTH_SHORT,
//                                context.getString(R.string.new_order_added, addedOrder.number)
//                            )
//                        }
//                        _modifiedOrderId.intValue = addedOrder.id
//
//                        //Log.d("addNewOrder", "Added order: $addedOrder")
//
//                        // Check if the current UI state is Success
//                        if (ordersUiState is OrdersUiState.Success) {
//                            // Add the new order to the beginning (index 0) of the list without refreshing
//                            val currentOrders =
//                                (ordersUiState as OrdersUiState.Success).ordersList.toMutableList()
//                            currentOrders.add(0, addedOrder)
//                            ordersUiState = OrdersUiState.Success(currentOrders, _modifiedOrderId)
//                        } else {
//                            // If the UI state is not Success (e.g., Loading or Error),
//                            // trigger a refresh of the orders.
//                            getOrders()
//                        }
//                    } ?: launch {
//                        ToastUtils.showToast(
//                            context,
//                            Toast.LENGTH_SHORT,
//                            context.getString(R.string.error_no_order_added)
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                // Handle exception
//                NetworkExceptionHandler.handleException(context, e, viewModelScope)
//            } catch (e: SocketTimeoutException) {
//                // Handle socket timeout exception
//                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
//            }
//        }
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
                ordersUiState = WhOutboundUiState.Error(HttpException(response))
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

    /**
     * Function to update the order data in the database
     */
    fun updateOrderData(context: Context, orderState: WhOutboundState) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = ordersRepository.updateOrderData(orderState)
//
//                handleResponse(context, response) {
//                    launch {
//                        ToastUtils.showToast(
//                            context,
//                            Toast.LENGTH_SHORT,
//                            context.getString(R.string.order_update_text)
//                        )
//                    }
//
//                    //If the response is successful, update the state of the order
//                    // without refreshing the list
//                    updateOrdersItemData(orderState)
//                }
//
//            } catch (e: Exception) {
//                // Handle exception
//                NetworkExceptionHandler.handleException(context, e, viewModelScope)
//            } catch (e: SocketTimeoutException) {
//                // Handle socket timeout exception
//                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
//            }
//        }
    }
}