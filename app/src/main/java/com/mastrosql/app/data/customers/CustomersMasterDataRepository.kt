package com.mastrosql.app.data.customers

import androidx.work.WorkInfo
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataResponse
import com.mastrosql.app.worker.DataSyncOperations
import kotlinx.coroutines.flow.Flow

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersMasterDataRepository : DataSyncOperations<CustomerMasterData> {

    /**
     * Flow representing the current state of work associated with a specific tag.
     * Observing this flow allows monitoring the progress and status of work enqueued with the given tag.
     */
    val outputWorkInfo: Flow<WorkInfo>

    /**
     * Retrieves customers master data from a remote source.
     * This function is suspendable and returns a [CustomersMasterDataResponse] object.
     */
    suspend fun getCustomersMasterData(): CustomersMasterDataResponse

/**
 * Retrieves destinations data from the server.
 */
    suspend fun getDestinationsData(): DestinationsDataResponse

/**
 * Inserts or updates customers master data received from the server.
 */
suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

/**
 * Updates the [MastroAndroidApiService] instance used for API communication.
 */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)
}
