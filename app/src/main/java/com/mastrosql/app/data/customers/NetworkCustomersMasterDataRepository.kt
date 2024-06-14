package com.mastrosql.app.data.customers

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkCustomersMasterDataRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val customerMasterDataDao: CustomersMasterDataDao,
    context: Context
) : CustomersMasterDataRepository {

    /**
     * Updates the [MastroAndroidApiService] instance used for network operations.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    private val workManager = WorkManager.getInstance(context)

    /**
     * Retrieves a [Flow] of [WorkInfo] objects from WorkManager by the specified tag.
     * Filters out empty results and emits the first non-null [WorkInfo] if available.
     */
    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse =
        mastroAndroidApiService.getAllCustomersMasterData()

    /**
     * Retrieves destinations data from the MastroAndroidApiService.
     */
    override suspend fun getDestinationsData(): DestinationsDataResponse =
        mastroAndroidApiService.getAllDestinationsData()

    /**
     * Inserts or updates customers master data received from the server.
     */
    override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) {

    }

    /**
     * Fetches customer master data from the server and returns it as a list.
     */
    override suspend fun fetchDataFromServer(): List<CustomerMasterData> {
        val customerMasterDataResponse = getCustomersMasterData()
        return customerMasterDataResponse.items
    }

    /**
     * Inserts or updates a list of CustomerMasterData objects in the local database.
     */
    override suspend fun insertOrUpdateData(data: List<CustomerMasterData>) {
        customerMasterDataDao.insertAll(data)
    }

    /**
     * Deletes all CustomerMasterData entries from the local database.
     */
    override suspend fun deleteData() {
        customerMasterDataDao.deleteAll()
    }
}