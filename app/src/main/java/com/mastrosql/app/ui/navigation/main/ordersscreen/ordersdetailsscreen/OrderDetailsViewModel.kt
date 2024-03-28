package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.utils.DateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface OrderDetailsUiState {

    data class Success(
        val orderDetailsList: List<OrderDetailsItem>,
        val modifiedIndex: MutableIntState? = null,
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
        //savedStateHandle.get(OrderDetailsDestination.ORDER_ID_ARG)
        savedStateHandle[OrderDetailsDestination.ORDER_ID_ARG]
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
                // Store the current list as the old list before making the call
                val orderDetailsListOld =
                    (orderDetailsUiState as? OrderDetailsUiState.Success)?.orderDetailsList

                // Get the order details from the repository
                val orderDetailsListResult =
                    orderDetailsRepository.getOrderDetails(orderId.value).items

                //
                val modifiedIndex =
                    orderDetailsListOld?.findModifiedItem(
                        orderDetailsListResult,
                        OrderDetailsItem::id
                    ) { oldItem, newItem ->
                        // If the item id and quantity are the same, the item is not modified
                        oldItem.id == newItem.id && oldItem.quantity == newItem.quantity
                        //oldItem != newItem
                    } ?: -1

                // Update the UI state with the new list
                OrderDetailsUiState.Success(
                    orderDetailsList = orderDetailsListResult,
                    modifiedIndex = mutableIntStateOf(modifiedIndex),
                    orderId = orderId.value,
                    orderDescription = orderDescription.value
                )
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

                // Store the current list as the old list before making the call
                val orderDetailsListOld =
                    (orderDetailsUiState as? OrderDetailsUiState.Success)?.orderDetailsList

                // Get the order details from the repository
                val orderDetailsListResult = orderDetailsRepository.getAllOrderDetails().items

                // Find the modified item in the list
                val modifiedIndex =
                    orderDetailsListOld?.findModifiedItem(
                        orderDetailsListResult,
                        OrderDetailsItem::id
                    ) { oldItem, newItem ->
                        // If the item id is the same but the quantity is different, the item is modified
                        oldItem.id == newItem.id && oldItem.quantity != newItem.quantity
                    } ?: -1

                // Update the UI state with the new list
                OrderDetailsUiState.Success(
                    orderDetailsList = orderDetailsListResult,
                    //modifiedIndex = modifiedIndex?.let { mutableIntStateOf(it) },
                    modifiedIndex = mutableIntStateOf(modifiedIndex),
                    orderId = orderId.value,
                    orderDescription = orderDescription.value
                )
            } catch (e: IOException) {
                OrderDetailsUiState.Error(e)
            } catch (e: HttpException) {
                OrderDetailsUiState.Error(e)
            } catch (e: Exception) {
                OrderDetailsUiState.Error(e)
            }
        }
    }

    fun sendScannedCode(context: Context, orderId: Int, scannedCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.sendScannedCode(orderId, scannedCode)
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
                            getOrderDetails()
                        }

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
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
                            "Errore api: ${response.code()}"
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


    //delete the item of orderDetail
    fun deleteDetailItem(context: Context, orderDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.deleteDetailItem(orderDetailId)

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Riga eliminata con successo ${response.code()}"
                            )
                            // Refresh the list
                            getOrderDetails()
                        }

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        //TODO: Add other status codes and handle them
                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Collegamento riuscito, api not trovata ${response.code()}"
                        )

                        else -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Errore api: ${response.code()}"
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

    fun duplicateDetailItem(context: Context, orderDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.duplicateDetailItem(orderDetailId)
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                            // Refresh the list
                            getOrderDetails()


                        }

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        //TODO: Add other status codes and handle them
                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "errore ${response.code()}"
                        )

                        else -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Errore api: ${response.code()}"
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDetailsItem(
        orderDetailsItemId: Int, quantity: Double,
        batch: String,
        expirationDate: String
    ) {
        if (orderDetailsUiState is OrderDetailsUiState.Success) {
            val orderDetailsList =
                (orderDetailsUiState as OrderDetailsUiState.Success).orderDetailsList.toMutableList()
            val index = orderDetailsList.indexOfFirst { it.id == orderDetailsItemId }
            if (index != -1) {
                val formattedExpirationDate = DateHelper.formatDateToInput(expirationDate)

                orderDetailsList[index] = orderDetailsList[index].copy(
                    quantity = quantity,
                    batch = batch,
                    expirationDate = formattedExpirationDate
                )
                orderDetailsUiState = OrderDetailsUiState.Success(
                    orderDetailsList = orderDetailsList,
                    modifiedIndex = mutableIntStateOf(index),
                    orderId = orderId.value,
                    orderDescription = orderDescription.value
                )
            }
        }
    }

    fun updateDetailsItemData(
        context: Context,
        orderDetailsItemId: Int,
        quantity: Double,
        batch: String,
        expirationDate: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.updateDetailItem(
                    orderDetailsItemId,
                    quantity,
                    batch,
                    expirationDate
                )

                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "$errorMessage ${response.code()}"
                            )
                            // Refresh the list
                            //getOrderDetails()

                            updateDetailsItem(orderDetailsItemId, quantity, batch, expirationDate)
                            //getOrderDetails()
                        }

                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        //TODO: Add other status codes and handle them
                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "errore ${response.code()}"
                        )

                        else -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Errore api: ${response.code()}"
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
}

//Function to find the modified item in the list
fun <T, K : Comparable<K>> List<T>.findModifiedItem(
    other: List<T>,
    idExtractor: (T) -> K,
    comparator: (T, T) -> Boolean
): Int? {
    // If sizes are different, there is a modification
    if (this.size < other.size) {
        // Find the index of the order with the greatest id in the other list
        return other.indexOf(other.maxByOrNull { idExtractor(it) })
    } else if (this.size > other.size) {
        return null
    }

    // Check each element one by one
    for (i in this.indices) {
        if (!comparator(this[i], other[i])) {
            return i
        }
    }
    return null
}


//Old function to find the modified item in the list
fun <T> List<T>.findModifiedItemOld(other: List<T>, comparator: (T, T) -> Boolean): Int? {
    // If sizes are different, there is a modification
    if (this.size < other.size) {
        return other.lastIndex
    } else if (this.size > other.size) {
        return null
    }

    // Check each element one by one
    for (i in this.indices) {
        if (!comparator(this[i], other[i])) {
            return i
        }
    }
    return null
}


