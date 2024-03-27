package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [OrderDetailsRepository]'s data source.
 */
class OrderDetailsItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderDetailsRepository: OrderDetailsRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var orderDetailsUiState by mutableStateOf(OrderDetailsEUiState())
        private set

    private val orderDetailsItemId: Int =
        checkNotNull(savedStateHandle[OrderDetailsItemEditDestination.rowIdArg])

    init {
        viewModelScope.launch {
            orderDetailsUiState = orderDetailsRepository.getOrderDetailsStream(orderDetailsItemId)
                .filterNotNull()
                .first()
                .toOrderDetailsUiState(true)
        }
    }

    /**
     * Update the item in the [ItemsRepository]'s data source
     */
    suspend fun updateOrderDetails() {
        if (validateInput(orderDetailsUiState.orderDetails)) {
            orderDetailsRepository.updateOrderDetails(orderDetailsUiState.orderDetails.toOrderDetailsItem())
        }
    }

    /**
     * Updates the  with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(orderDetails: OrderDetails) {
        orderDetailsUiState =
            OrderDetailsEUiState(
                orderDetails = orderDetails,
                isEntryValid = validateInput(orderDetails)
            )
    }

    private fun validateInput(uiState: OrderDetails = orderDetailsUiState.orderDetails): Boolean {
        return with(uiState) {
            description.isNotBlank() //&& price.isNotBlank() //&& quantity.isNotBlank()
        }
    }

}
