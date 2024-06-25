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
import com.mastrosql.app.data.warehouse.outbound.whoutdetails.WhOutDetailsRepository
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.findModifiedItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents.WhOutDetailsDestination
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

sealed interface WhOutDetailsUiState {

    data class Success(
        val whOutDetailsList: List<WhOutDetailsItem>,
        var modifiedWhOutDetailsItem: WhOutDetailsItem? = null,
        val modifiedIndex: MutableIntState? = null,
        val whOutId: Int? = null,
        val whOutDescription: String? = null,
        val swipeActionsPreferences: SwipeActionsPreferences
    ) : WhOutDetailsUiState

    @Suppress("KDocMissingDocumentation")
    data class Error(val exception: Exception) : WhOutDetailsUiState

    @Suppress("KDocMissingDocumentation")
    data object Loading : WhOutDetailsUiState
}

class WhOutDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val whOutDetailsRepository: WhOutDetailsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Mutable state flow for UI state
    private val _whOutDetailsUiState =
        MutableStateFlow<WhOutDetailsUiState>(WhOutDetailsUiState.Loading)

    @Suppress("KDocMissingDocumentation")
    val whOutDetailsUiState: StateFlow<WhOutDetailsUiState> = _whOutDetailsUiState

    private val _whOutId: MutableStateFlow<Int?> = MutableStateFlow(
        savedStateHandle[WhOutDetailsDestination.WHOUT_ID_ARG]
    )

    @Suppress("KDocMissingDocumentation")
    val whOutId: StateFlow<Int?> = _whOutId

    private val _whOutDescription: MutableStateFlow<String?> = MutableStateFlow(
        savedStateHandle[WhOutDetailsDestination.WHOUT_DESCRIPTION_ARG]
    )
    private val whOutDescription: StateFlow<String?> = _whOutDescription

    private var _swipeActionsPreferences: SwipeActionsPreferences = SwipeActionsPreferences()

    init {
        viewModelScope.launch {
            getSwipeActionsPreferences()
            getWhOutDetails()
        }
    }

    /**
     * Gets the value of isDeleteRowActive from the UserPreferencesRepository and updates the
     * [WhOutDetailsUiState] with the new value.
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
        val currentState = _whOutDetailsUiState.value
        if (currentState is WhOutDetailsUiState.Success) {
            _whOutDetailsUiState.value = currentState.copy(swipeActionsPreferences = preferences)
        }
    }

    /**
     * Handles the network response and updates the [WhOutDetailsUiState] accordingly.
     */
    private fun handleNetworkResponse(
        context: Context, response: retrofit2.Response<*>, onSuccess: suspend () -> Unit
    ) = viewModelScope.launch {
        when (response.code()) {
            200 -> onSuccess()
            401 -> NetworkSuccessHandler.handleUnauthorized(context, viewModelScope) {
                _whOutDetailsUiState.value = WhOutDetailsUiState.Error(HttpException(response))
            }

            404 -> NetworkSuccessHandler.handleNotFound(context, viewModelScope, response.code())
            500, 503 -> NetworkSuccessHandler.handleServerError(context, response, viewModelScope)
            else -> NetworkSuccessHandler.handleUnknownError(context, response, viewModelScope)
        }
    }

    /**
     * Handles the exception and updates the [WhOutDetailsUiState] accordingly.
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

    fun getWhOutDetails() {
        viewModelScope.launch {
            _whOutDetailsUiState.value = withContext(Dispatchers.IO) {
                try {
                    // Store the current list as the old list before making the call
                    val previousWhOutDetailsList =
                        (whOutDetailsUiState.value as? WhOutDetailsUiState.Success)?.whOutDetailsList

                    // Get the whOut details from the repository
                    val currentWhOutDetailsListResult =
                        whOutDetailsRepository.getWhOutDetails(whOutId.value).items

                    // Find the modified item index in the list
                    val modifiedIndex = previousWhOutDetailsList?.findModifiedItem(
                        currentWhOutDetailsListResult, WhOutDetailsItem::id
                    ) { oldItem, newItem ->
                        // If the item id and quantity are the same, the item is not modified
                        oldItem.id == newItem.id && oldItem.quantity == newItem.quantity
                        //oldItem != newItem
                    } ?: -1

                    // Get the modified whOut details item
                    val modifiedWhOutDetailsItem =
                        currentWhOutDetailsListResult.getOrNull(modifiedIndex)

                    // Update the UI state with the new list
                    WhOutDetailsUiState.Success(
                        whOutDetailsList = currentWhOutDetailsListResult,
                        modifiedIndex = mutableIntStateOf(modifiedIndex),
                        modifiedWhOutDetailsItem = modifiedWhOutDetailsItem,
                        whOutId = whOutId.value,
                        whOutDescription = whOutDescription.value,
                        swipeActionsPreferences = _swipeActionsPreferences
                    )
                } catch (e: Exception) {
                    WhOutDetailsUiState.Error(e)
                }
            }
        }
    }

    /**
     * Gets all WhOutDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     * Currently not used in the app.
     */
    fun getAllWhOutDetails() {
        viewModelScope.launch {
            _whOutDetailsUiState.value = WhOutDetailsUiState.Loading
            _whOutDetailsUiState.value = try {

                // Store the current list as the old list before making the call
                val previousWhOutDetailsList =
                    (whOutDetailsUiState.value as? WhOutDetailsUiState.Success)?.whOutDetailsList

                // Get the whOut details from the repository
                val currentWhOutDetailsListResult =
                    whOutDetailsRepository.getAllWhOutDetails().items

                // Find the modified item in the list
                val modifiedIndex = previousWhOutDetailsList?.findModifiedItem(
                    currentWhOutDetailsListResult, WhOutDetailsItem::id
                ) { oldItem, newItem ->
                    // If the item id is the same but the quantity is different, the item is modified
                    oldItem.id == newItem.id && oldItem.quantity != newItem.quantity
                } ?: -1

                // Update the UI state with the new list
                WhOutDetailsUiState.Success(
                    whOutDetailsList = currentWhOutDetailsListResult,
                    //modifiedIndex = modifiedIndex?.let { mutableIntStateOf(it) },
                    modifiedIndex = mutableIntStateOf(modifiedIndex),
                    whOutId = whOutId.value,
                    whOutDescription = whOutDescription.value,
                    swipeActionsPreferences = _swipeActionsPreferences
                )
            } catch (e: Exception) {
                WhOutDetailsUiState.Error(e)
            }
        }
    }

    /**
     * Sends the scanned code to the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     */
    fun sendScannedCode(context: Context, whOutId: Int, scannedCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = whOutDetailsRepository.sendScannedCode(whOutId, scannedCode)
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context, Toast.LENGTH_SHORT, context.getString(R.string.new_item_added)
                        )
                        getWhOutDetails()
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }

    /**
     * Deletes the WhOutDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     */
    fun deleteDetailItem(context: Context, whOutDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = whOutDetailsRepository.deleteDetailItem(whOutDetailId)
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.item_deleted, response.code())
                        )
                        getWhOutDetails()
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }

    /**
     * Duplicates the WhOutDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     */
    fun duplicateDetailItem(context: Context, whOutDetailId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            ToastUtils.showToast(context, Toast.LENGTH_SHORT, "Not implemented")
//            try {
//                val response = whOutDetailsRepository.duplicateDetailItem(whOutDetailId)
//                handleNetworkResponse(context, response) {
//                    withContext(Dispatchers.Main) {
//                        ToastUtils.showToast(
//                            context,
//                            Toast.LENGTH_SHORT,
//                            context.getString(R.string.row_duplicated, response.code())
//                        )
//                        getWhOutDetails()
//                    }
//                }
//            } catch (e: Exception) {
//                handleException(context, e)
//            }
        }
    }

    /**
     * Updates the WhOutDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     */
    private fun updateDetailsItem(
        whOutDetailsItemId: Int, quantity: Double, batch: String, expirationDate: String
    ) {
        val currentState = _whOutDetailsUiState.value
        if (currentState is WhOutDetailsUiState.Success) {
            val whOutDetailsList = currentState.whOutDetailsList.toMutableList()
            val index = whOutDetailsList.indexOfFirst { it.id == whOutDetailsItemId }
            if (index != -1) {
                val formattedExpirationDate = DateHelper.formatDateToInput(expirationDate)

                whOutDetailsList[index] = whOutDetailsList[index].copy(
                    quantity = quantity, batch = batch, expirationDate = formattedExpirationDate
                )
                _whOutDetailsUiState.value = WhOutDetailsUiState.Success(
                    whOutDetailsList = whOutDetailsList,
                    modifiedIndex = mutableIntStateOf(index),
                    whOutId = whOutId.value,
                    whOutDescription = whOutDescription.value,
                    swipeActionsPreferences = _swipeActionsPreferences
                )
            }
        }
    }

    /**
     * Updates the WhOutDetailsItem from the MastroAndroid API Retrofit service and updates the
     * [WhOutDetailsItem] [List] [MutableList].
     */
    fun updateDetailsItemData(
        context: Context,
        whOutDetailsItemId: Int,
        quantity: Double,
        batch: String,
        expirationDate: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = whOutDetailsRepository.updateDetailItem(
                    whOutDetailsItemId, quantity, batch, expirationDate
                )
                handleNetworkResponse(context, response) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.updated_row_success_text, response.code())
                        )
                        updateDetailsItem(whOutDetailsItemId, quantity, batch, expirationDate)
                    }
                }
            } catch (e: Exception) {
                handleException(context, e)
            }
        }
    }
}

fun <T, K : Comparable<K>> List<T>.findModifiedItem(
    other: List<T>, idExtractor: (T) -> K, comparator: (T, T) -> Boolean
): Int? {
    // If sizes are different, there is a modification
    if (this.size < other.size) {
        // Find the index of the whout with the greatest id in the other list
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

