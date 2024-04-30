package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface OrdersUiState {

    data class Success(
        val ordersList: List<Order>,
        val modifiedOrderId: MutableIntState
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
            ordersUiState =
                try {
                    val ordersListResult =
                        ordersRepository.getOrders().items
                    OrdersUiState
                        .Success(
                            ordersListResult,//.toMutableList(),
                            //Not -1 to avoid the crash because of the index out of bound
                            modifiedOrderId = _modifiedOrderId
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

    fun updateDeliveryState(context: Context, orderId: Int, deliveryState: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ordersRepository.updateDeliveryState(orderId, deliveryState)
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifica salvata con successo" //${response.code()}
                            )

                            //If the response is successful, update the delivery state of the order
                            //without refreshing the list
                            updateOrdersItem(orderId, deliveryState)
                        }

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            ordersUiState = OrdersUiState.Error(HttpException(response))
                        }
                        //TODO: Add other status codes and handle them
                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Collegamento riuscito, api not trovata ${response.code()}"
                        )

                        500 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        503 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                        }

                        else -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Errore api: ${response.code()} $errorMessage"
                        )
                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                showToast(context, Toast.LENGTH_LONG, "Network error occurred: ${e.message}")
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                showToast(context, Toast.LENGTH_LONG, "HTTP error occurred: ${e.message}")
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "Connection timed out. Please try again later."
                )
            } catch (e: Exception) {
                // Handle generic exception
                showToast(context, Toast.LENGTH_LONG, "An unexpected error occurred: ${e.message}")
            }
        }
    }

    //Get the message in the body
    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody != null) {
            val jsonError = JSONObject(errorBody)
            return jsonError.optString("message", "{}")
        }
        return "{}"
    }

    private fun showToast(context: Context, toastLength: Int, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (message.isNotEmpty()) {
                val toast = Toast.makeText(context, message, toastLength)
                //Error for toast gravity with message
                //toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else {
                // Hide loading message by not showing any toast
            }
        }
    }

    //Function to update the delivery state of the order in the list
    private fun updateOrdersItem(orderId: Int, deliveryState: Int) {
        if (ordersUiState is OrdersUiState.Success) {
            val ordersList = (ordersUiState as OrdersUiState.Success).ordersList.toMutableList()
            val index = ordersList.indexOfFirst { it.id == orderId }
            if (index != -1) {
                ordersList[index] = ordersList[index].copy(deliveryState = deliveryState)
                ordersUiState = OrdersUiState.Success(ordersList, mutableIntStateOf(orderId))
            }
        }
    }

    fun addNewOrder(context: Context, order: Order) {
        /*if (ordersUiState is OrdersUiState.Success) {
            val ordersList = (ordersUiState as OrdersUiState.Success).ordersList.toMutableList()
            ordersList.add(order)
            ordersUiState = OrdersUiState.Success(ordersList, mutableIntStateOf(0))
        }*/
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ordersRepository.addNewOrder(order)

                handleResponse(context, response) {
                    val newOrderResponse = response.body()
                    val newOrder = newOrderResponse?.getAddedOrder()
                    newOrder?.let { addedOrder ->
                        showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            "Ordine ${addedOrder.number} aggiunto con successo"
                        )
                        _modifiedOrderId.intValue = addedOrder.id

                        // No need to refresh the list if the current UI state is Success
                        //getOrders()
                        Log.d("addNewOrder", "Added order: $addedOrder")
                        // Check if the current UI state is Success
                        if (ordersUiState is OrdersUiState.Success) {
                            // Add the new order to the beginning of the list without refreshing
                            val currentOrders =
                                (ordersUiState as OrdersUiState.Success).ordersList.toMutableList()
                            currentOrders.add(0, addedOrder)
                            ordersUiState = OrdersUiState.Success(currentOrders, _modifiedOrderId)
                        } else {
                            // If the UI state is not Success, we can't directly add the new order.
                            // We trigger a refresh of the orders.
                            getOrders()
                        }
                    } ?: showToast(
                        context,
                        Toast.LENGTH_SHORT,
                        "Ordine non aggiunto, nessun ordine restituito"
                    )
                }
            } catch (e: Exception) {
                // Handle exception
                handleException(context, e)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                handleSocketTimeoutException(context, e)
            }
        }
    }

    // Function to find the index of the newly added order in the list of items
    private fun findIndexOfNewOrder(newOrder: Order): Int {
        if (ordersUiState is OrdersUiState.Success) {
            val ordersList = (ordersUiState as OrdersUiState.Success).ordersList
            return ordersList.indexOfFirst { it == newOrder }
        }
        return -1 // Not found
    }

    private inline fun handleResponse(
        context: Context,
        response: Response<*>,
        crossinline onSuccess: () -> Unit
    ) {
        val errorBody = response.errorBody()?.string()
        val errorMessage = parseErrorMessage(errorBody)

        when (response.code()) {
            200 -> onSuccess()
            401 -> {
                showToast(
                    context,
                    Toast.LENGTH_SHORT,
                    "Ordine non aggiunto, non autorizzato"
                )
                ordersUiState = OrdersUiState.Error(HttpException(response))
            }

            404 -> showToast(
                context,
                Toast.LENGTH_LONG,
                "Collegamento riuscito, api not trovata ${response.code()}"
            )

            500 -> {
                showToast(
                    context,
                    Toast.LENGTH_SHORT,
                    "$errorMessage ${response.code()}"
                )
            }

            503 -> {
                showToast(
                    context,
                    Toast.LENGTH_SHORT,
                    "$errorMessage ${response.code()}"
                )
            }

            else -> showToast(
                context,
                Toast.LENGTH_LONG,
                "Errore api: ${response.code()} $errorMessage"
            )
        }
    }

    private fun handleException(context: Context, exception: Exception) {
        when (exception) {
            is IOException -> {
                // Handle IOException (e.g., network error)
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "Network error occurred: ${exception.message}"
                )
            }

            is HttpException -> {
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "HTTP error occurred: ${exception.message}"
                )
            }

            else -> {
                // Handle generic exception
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "An unexpected error occurred: ${exception.message}"
                )
            }
        }
    }

    private fun handleSocketTimeoutException(context: Context, exception: SocketTimeoutException) {
        showToast(
            context,
            Toast.LENGTH_LONG,
            "Connection timed out. Please try again later."
        )
    }

//    fun setEditIndex(index: Int) {
//        ordersUiState = when (val currentState = ordersUiState) {
//            is OrdersUiState.Success -> currentState.copy(modifiedOrderId = mutableStateOf(index))
//            else -> currentState
//        }
//    }
}