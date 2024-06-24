package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound

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
import com.mastrosql.app.data.warehouse.outbound.WarehouseOutRepository
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.utils.ToastUtils
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
     * Mutable state to hold the Warehouse Outbound UI state.
     */
    var whOutboundUiState: WhOutboundUiState by mutableStateOf(WhOutboundUiState.Loading)
        private set

    private val _modifiedWhOutboundId = mutableIntStateOf(0)

    init {
        getWhOutbound()
    }

    fun getWhOutbound() {
        viewModelScope.launch {
            whOutboundUiState = WhOutboundUiState.Loading
            whOutboundUiState = try {
                val whOutboundListResult = whOutboundRepository.getWhOutbound().items
                WhOutboundUiState.Success(
                    whOutboundListResult, modifiedWhOutboundId = _modifiedWhOutboundId
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

    fun addNewWhOutbound(context: Context, warehouseOutbound: WarehouseOutbound) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = whOutboundRepository.addNewWhOutbound(warehouseOutbound)

                handleResponse(context, response) {
                    val newWhOutboundResponse = response.body()
                    val newWhOutbound = newWhOutboundResponse?.getAddedWhOutbound()
                    newWhOutbound?.let { addedWhOutbound ->
                        launch {
                            ToastUtils.showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                context.getString(
                                    R.string.new_wh_operation_added,
                                    addedWhOutbound.id
                                )
                            )
                        }
                        _modifiedWhOutboundId.intValue = addedWhOutbound.id

                        // Check if the current UI state is Success
                        if (whOutboundUiState is WhOutboundUiState.Success) {
                            // Add the new warehouse outbound to the beginning (index 0) of the list without refreshing
                            val currentWhOutbounds =
                                (whOutboundUiState as WhOutboundUiState.Success).whOutboundsList.toMutableList()
                            currentWhOutbounds.add(0, addedWhOutbound)
                            whOutboundUiState =
                                WhOutboundUiState.Success(currentWhOutbounds, _modifiedWhOutboundId)
                        } else {
                            // If the UI state is not Success (e.g., Loading or Error),
                            // trigger a refresh of the warehouse outbounds.
                            getWhOutbound()
                        }
                    } ?: launch {
                        ToastUtils.showToast(
                            context,
                            Toast.LENGTH_SHORT,
                            context.getString(R.string.error_no_wh_operation_added)
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

    private inline fun handleResponse(
        context: Context, response: Response<*>, crossinline onSuccess: () -> Unit
    ) {

        when (response.code()) {
            200 -> onSuccess()
            401 -> NetworkSuccessHandler.handleUnauthorized(
                context, viewModelScope
            ) {
                // Handle unauthorized
                whOutboundUiState = WhOutboundUiState.Error(HttpException(response))
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
