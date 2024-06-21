package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.R
import com.mastrosql.app.data.datasource.network.NetworkExceptionHandler
import com.mastrosql.app.data.datasource.network.NetworkSuccessHandler
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.orderdetailscomponents.OrderDetailsDestination
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

/**
 * Sealed class for the UI state of the OrderDetails screen
 */
sealed interface OrderDetailsUiState {

    /**
     * Success state for the OrderDetails screen
     */
    @Suppress("KDocMissingDocumentation")
    data class Success(
        val orderDetailsList: List<com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem>,
        var modifiedOrderDetailsItem: com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem? = null,
        val modifiedIndex: MutableIntState? = null,
        val orderId: Int? = null,
        val orderDescription: String? = null,
        val swipeActionsPreferences: SwipeActionsPreferences
    ) : OrderDetailsUiState

    @Suppress("KDocMissingDocumentation")
    data class Error(val exception: Exception) : OrderDetailsUiState

    @Suppress("KDocMissingDocumentation")
    data object Loading : OrderDetailsUiState
}

/**
 * Factory for [OrderDetailsViewModel] that takes [OrderDetailsRepository]
 * and [UserPreferencesRepository] as a dependencies
 */

class OrderDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Mutable state flow for UI state
    private val _orderDetailsUiState =
        MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Loading)

    @Suppress("KDocMissingDocumentation")
    val orderDetailsUiState: StateFlow<OrderDetailsUiState> = _orderDetailsUiState

    private val _orderId: MutableStateFlow<Int?> = MutableStateFlow(
        savedStateHandle[OrderDetailsDestination.ORDER_ID_ARG]
    )

    @Suppress("KDocMissingDocumentation")
    val orderId: StateFlow<Int?> = _orderId

    private val _orderDescription: MutableStateFlow<String?> = MutableStateFlow(
        savedStateHandle[OrderDetailsDestination.ORDER_DESCRIPTION_ARG]
    )
    private val orderDescription: StateFlow<String?> = _orderDescription

    private var _swipeActionsPreferences: SwipeActionsPreferences = SwipeActionsPreferences()

    init {
        viewModelScope.launch {
            getSwipeActionsPreferences()
            getOrderDetails()
        }
    }

    /**
     * Gets the value of isDeleteRowActive from the UserPreferencesRepository and updates the
     * [OrderDetailsUiState] with the new value.
     */
    private fun getSwipeActionsPreferences() {
        viewModelScope.launch {
            userPreferencesRepository
                .getSwipeActionsPreferences()
                .collect {
                    _swipeActionsPreferences = it
                    updateUiStateWithSwipePreferences(it)
                }
        }
    }

    private fun updateUiStateWithSwipePreferences(preferences: SwipeActionsPreferences) {
        val currentState = _orderDetailsUiState.value
        if (currentState is OrderDetailsUiState.Success) {
            _orderDetailsUiState.value = currentState.copy(swipeActionsPreferences = preferences)
        }
    }

    /**
     * Handles the network response and updates the [OrderDetailsUiState] accordingly.
     */
    private fun handleNetworkResponse(
        context: Context, response: retrofit2.Response<*>, onSuccess: suspend () -> Unit
    ) = viewModelScope.launch {
        when (response.code()) {
            200 -> onSuccess()
            401 -> NetworkSuccessHandler.handleUnauthorized(context, viewModelScope) {
                _orderDetailsUiState.value = OrderDetailsUiState.Error(HttpException(response))
            }

            404 -> NetworkSuccessHandler.handleNotFound(context, viewModelScope, response.code())
            500, 503 -> NetworkSuccessHandler.handleServerError(context, response, viewModelScope)
            else -> NetworkSuccessHandler.handleUnknownError(context, response, viewModelScope)
        }
    }

    /**
     * Handles the exception and updates the [OrderDetailsUiState] accordingly.
     */
    private fun handleException(context: Context, e: Exception) = viewModelScope.launch {
        when (e) {
            is SocketTimeoutException -> NetworkExceptionHandler.handleSocketTimeoutException(
                context, viewModelScope
            )

            is IOException -> NetworkExceptionHandler.handleException(context, e, viewModelScope)
            is HttpException -> NetworkExceptionHandler.handleException(context, e, viewModelScope)
            else -> NetworkExceptionHandler.handleException(context, e, viewModelScope)
        }
    }

    /**
     * Gets OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun getOrderDetails() {
        viewModelScope.launch {
            _orderDetailsUiState.value = withContext(Dispatchers.IO) {
                try {
                    // Store the current list as the old list before making the call
                    val previousOrderDetailsList =
                        (orderDetailsUiState.value as? OrderDetailsUiState.Success)?.orderDetailsList

                    // Get the order details from the repository
                    val currentOrderDetailsListResult =
                        orderDetailsRepository.getOrderDetails(orderId.value).items

//                    // Find the modified item index in the list
//                    val modifiedIndex = previousOrderDetailsList?.findModifiedItem(
//                        currentOrderDetailsListResult, OrderDetailsItem::id
//                    ) { oldItem, newItem ->
//                        // If the item id and quantity are the same, the item is not modified
//                        oldItem.id == newItem.id && oldItem.quantity == newItem.quantity
//                        //oldItem != newItem
//                    } ?: -1
                    val modifiedIndex = -1

                    // Get the modified order details item
                    val modifiedOrderDetailsItem =
                        currentOrderDetailsListResult.getOrNull(modifiedIndex)

                    // Update the UI state with the new list
                    OrderDetailsUiState.Success(
                        orderDetailsList = currentOrderDetailsListResult,
                        modifiedIndex = mutableIntStateOf(modifiedIndex),
                        modifiedOrderDetailsItem = modifiedOrderDetailsItem,
                        orderId = orderId.value,
                        orderDescription = orderDescription.value,
                        swipeActionsPreferences = _swipeActionsPreferences
                    )
                } catch (e: Exception) {
                    OrderDetailsUiState.Error(e)
                }
            }
        }
    }

    /**
     * Gets all OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     * Currently not used in the app.
     */
    fun getAllOrderDetails() {
        viewModelScope.launch {
            _orderDetailsUiState.value = OrderDetailsUiState.Loading
            _orderDetailsUiState.value = try {

                // Store the current list as the old list before making the call
                val previousOrderDetailsList =
                    (orderDetailsUiState.value as? OrderDetailsUiState.Success)?.orderDetailsList

                // Get the order details from the repository
                val currentOrderDetailsListResult =
                    orderDetailsRepository.getAllOrderDetails().items

//                // Find the modified item in the list
//                val modifiedIndex = previousOrderDetailsList?.findModifiedItem(
//                    currentOrderDetailsListResult, OrderDetailsItem::id
//                ) { oldItem, newItem ->
//                    // If the item id is the same but the quantity is different, the item is modified
//                    oldItem.id == newItem.id && oldItem.quantity != newItem.quantity
//                } ?: -1
                val modifiedIndex = -1

                // Update the UI state with the new list
                OrderDetailsUiState.Success(
                    orderDetailsList = currentOrderDetailsListResult,
                    //modifiedIndex = modifiedIndex?.let { mutableIntStateOf(it) },
                    modifiedIndex = mutableIntStateOf(modifiedIndex),
                    orderId = orderId.value,
                    orderDescription = orderDescription.value,
                    swipeActionsPreferences = _swipeActionsPreferences
                )
            } catch (e: Exception) {
                OrderDetailsUiState.Error(e)
            }
        }
    }

    /**
     * Sends the scanned code to the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun sendScannedCode(context: Context, orderId: Int, scannedCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.sendScannedCode(orderId, scannedCode)
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context, Toast.LENGTH_SHORT, context.getString(R.string.new_item_added)
                        )
                        getOrderDetails()
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }

    /**
     * Deletes the OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun deleteDetailItem(context: Context, orderDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.deleteDetailItem(orderDetailId)
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.item_deleted, response.code())
                        )
                        getOrderDetails()
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }

    /**
     * Duplicates the OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    fun duplicateDetailItem(context: Context, orderDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderDetailsRepository.duplicateDetailItem(orderDetailId)
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.row_duplicated, response.code())
                        )
                        getOrderDetails()
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }

    /**
     * Updates the OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
    private fun updateDetailsItem(
        orderDetailsItemId: Int, quantity: Double, batch: String, expirationDate: String
    ) {
        val currentState = _orderDetailsUiState.value
        if (currentState is OrderDetailsUiState.Success) {
            val orderDetailsList = currentState.orderDetailsList.toMutableList()
            val index = orderDetailsList.indexOfFirst { it.id == orderDetailsItemId }
            if (index != -1) {
                val formattedExpirationDate = DateHelper.formatDateToInput(expirationDate)

                orderDetailsList[index] = orderDetailsList[index].copy(
                    quantity = quantity, batch = batch, expirationDate = formattedExpirationDate
                )
                _orderDetailsUiState.value = OrderDetailsUiState.Success(
                    orderDetailsList = orderDetailsList,
                    modifiedIndex = mutableIntStateOf(index),
                    orderId = orderId.value,
                    orderDescription = orderDescription.value,
                    swipeActionsPreferences = _swipeActionsPreferences
                )
            }
        }
    }

    /**
     * Updates the OrderDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [OrderDetailsItem] [List] [MutableList].
     */
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
                    orderDetailsItemId, quantity, batch, expirationDate
                )
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.update_order_details, response.code())
                        )
                        updateDetailsItem(orderDetailsItemId, quantity, batch, expirationDate)
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
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

//fun <T, K : Comparable<K>> List<T>.findModifiedItem(
//    other: List<T>, idExtractor: KProperty1<OrderDetailsItem, Int>, comparator: (T, T) -> Boolean
//): Int? {
//    // If sizes are different, there is a modification
//    if (this.size < other.size) {
//        // Find the index of the order with the greatest id in the other list
//        return other.indexOf(other.maxByOrNull { idExtractor(it) })
//    } else if (this.size > other.size) {
//        return null
//    }
//
//    // Check each element one by one
//    for (i in this.indices) {
//        if (!comparator(this[i], other[i])) {
//            return i
//        }
//    }
//    return null
//}


/**
 * Not used in the app
 * Function to find the modified item in the list
 */
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
