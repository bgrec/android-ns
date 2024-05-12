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

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse =
        mastroAndroidApiService.getAllCustomersMasterData()

    override suspend fun getDestinationsData(): DestinationsDataResponse =
        mastroAndroidApiService.getAllDestinationsData()


    override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) {

    }

    override suspend fun fetchDataFromServer(): List<CustomerMasterData> {
        val customerMasterDataResponse = getCustomersMasterData()
        return customerMasterDataResponse.items
    }

    override suspend fun insertOrUpdateData(data: List<CustomerMasterData>) {
        customerMasterDataDao.insertAll(data)
    }

    override suspend fun deleteData() {
        customerMasterDataDao.deleteAll()
    }
}