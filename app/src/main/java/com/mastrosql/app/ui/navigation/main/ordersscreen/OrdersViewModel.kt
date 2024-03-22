package com.mastrosql.app.ui.navigation.main.ordersscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
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
import java.io.IOException
import java.net.SocketTimeoutException

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
                            //Not -1 to avoid the crash because of the index out of bound
                            modifiedOrderId = mutableIntStateOf(0)
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
                                "Articolo inserito con successo " //${response.code()}
                            )
                            // Refresh the list
                            getOrders()
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
            return jsonError.optString("message","{}")
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

//    fun setEditIndex(index: Int) {
//        ordersUiState = when (val currentState = ordersUiState) {
//            is OrdersUiState.Success -> currentState.copy(modifiedOrderId = mutableStateOf(index))
//            else -> currentState
//        }
//    }
}