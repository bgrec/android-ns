package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.R
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents.OrderDetailsDestination
import com.mastrosql.app.utils.DateHelper
import com.mastrosql.app.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface OrderDetailsUiState {

    data class Success(
        val orderDetailsList: List<OrderDetailsItem>,
        var modifiedOrderDetailsItem: OrderDetailsItem? = null,
        val modifiedIndex: MutableIntState? = null,
        val orderId: Int? = null,
        val orderDescription: String? = null,
        val isDeleteRowActive: Boolean = true
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
    private val userPreferencesRepository: UserPreferencesRepository
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
        savedStateHandle.get(OrderDetailsDestination.ORDER_DESCRIPTION_ARG)
    )
    private val orderDescription: StateFlow<String?> = _orderDescription

    init {
        getOrderDetails()
        getIsDeleteRowActive()
    }

    private fun getIsDeleteRowActive() {
        viewModelScope.launch {
            userPreferencesRepository.getIsSwipeToDeleteDeactivated()
                .collect { isDeleteRowActive ->
                    orderDetailsUiState =
                        (orderDetailsUiState as? OrderDetailsUiState.Success)?.copy(
                            isDeleteRowActive = isDeleteRowActive
                        ) ?: orderDetailsUiState
                }
        }
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

                // Get the modified order details item
                var modifiedOrderDetailsItem: OrderDetailsItem? = null
                if (modifiedIndex >= 0 && modifiedIndex < orderDetailsListResult.size) {
                    modifiedOrderDetailsItem = orderDetailsListResult[modifiedIndex]
                }

                // Update the UI state with the new list
                OrderDetailsUiState.Success(
                    orderDetailsList = orderDetailsListResult,
                    modifiedIndex = mutableIntStateOf(modifiedIndex),
                    modifiedOrderDetailsItem = modifiedOrderDetailsItem,
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

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                context.getString(R.string.new_item_added)
                            )
                            // Refresh the list
                            getOrderDetails()
                        }

                        401 -> NetworkSuccessHandler.handleUnauthorized(
                            context,
                            viewModelScope
                        ) {
                            // Handle unauthorized
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        404 -> NetworkSuccessHandler.handleNotFound(
                            context,
                            viewModelScope,
                            response.code()
                        )

                        500, 503 -> NetworkSuccessHandler.handleServerError(
                            context,
                            response,
                            viewModelScope
                        )

                        else -> NetworkSuccessHandler.handleUnknownError(
                            context,
                            response,
                            viewModelScope
                        )
                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            } catch (e: Exception) {
                // Handle generic exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
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
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                context.getString(R.string.item_deleted, response.code())
                            )
                            // Refresh the list
                            getOrderDetails()
                        }

                        401 -> NetworkSuccessHandler.handleUnauthorized(
                            context,
                            viewModelScope
                        ) {
                            // Handle unauthorized
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        404 -> NetworkSuccessHandler.handleNotFound(
                            context,
                            viewModelScope,
                            response.code()
                        )

                        else -> NetworkSuccessHandler.handleUnknownError(
                            context,
                            response,
                            viewModelScope
                        )
                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            } catch (e: Exception) {
                // Handle generic exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            }
        }
    }

    fun duplicateDetailItem(context: Context, orderDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.duplicateDetailItem(orderDetailId)

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                context.getString(R.string.row_duplicated, response.code())
                            )
                            // Refresh the list
                            getOrderDetails()


                        }

                        401 -> NetworkSuccessHandler.handleUnauthorized(
                            context,
                            viewModelScope
                        ) {
                            // Handle unauthorized
                            orderDetailsUiState = OrderDetailsUiState.Error(HttpException(response))
                        }

                        404 -> NetworkSuccessHandler.handleNotFound(
                            context,
                            viewModelScope,
                            response.code()
                        )

                        else -> NetworkSuccessHandler.handleUnknownError(
                            context,
                            response,
                            viewModelScope
                        )
                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            } catch (e: Exception) {
                // Handle generic exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            }
        }
    }

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

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                //"$errorMessage ${response.code()}"
                                context.getString(R.string.update_order_details, response.code())
                            )
                            // Refresh the list
                            //getOrderDetails()

                            updateDetailsItem(
                                orderDetailsItemId,
                                quantity,
                                batch,
                                expirationDate
                            )
                            //getOrderDetails()
                        }

                        401 -> NetworkSuccessHandler.handleUnauthorized(
                            context,
                            viewModelScope
                        ) {
                            orderDetailsUiState =
                                OrderDetailsUiState.Error(HttpException(response))
                        }

                        404 -> NetworkSuccessHandler.handleNotFound(
                            context,
                            viewModelScope,
                            response.code()
                        )

                        500, 503 -> NetworkSuccessHandler.handleServerError(
                            context,
                            response,
                            viewModelScope
                        )

                        else -> NetworkSuccessHandler.handleUnknownError(
                            context,
                            response,
                            viewModelScope
                        )

                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                NetworkExceptionHandler.handleSocketTimeoutException(context, viewModelScope)
            } catch (e: Exception) {
                // Handle generic exception
                NetworkExceptionHandler.handleException(context, e, viewModelScope)
            }
        }
    }
}

/**
 * Function to find the modified item in the list
 * @param other The other list to compare
 * @param idExtractor The lambda function to extract the id from the item
 * @param comparator The lambda function to compare the items
 * @return The index of the modified item or null if there is no modification
 * @see findModifiedItemOld
 * @see findModifiedItem
 */

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


//Old function to find the modified item in the list not used anymore
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


