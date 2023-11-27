package com.mastrosql.app.data.customers

import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.worker.DataSyncOperations
import kotlinx.coroutines.flow.Flow

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersMasterDataRepository : DataSyncOperations<CustomerMasterData> {

    val outputWorkInfo: Flow<WorkInfo>
    suspend fun getCustomersMasterData(): CustomersMasterDataResponse
    suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

}
