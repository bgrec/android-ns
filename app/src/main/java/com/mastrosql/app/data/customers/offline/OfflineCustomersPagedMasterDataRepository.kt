package com.mastrosql.app.data.customers.offline

import androidx.work.WorkInfo
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import kotlinx.coroutines.flow.Flow

class OfflineCustomersPagedMasterDataRepository(private val customerMasterDataDao: CustomersMasterDataDao) :
    CustomersMasterDataRepository {

    override val outputWorkInfo: Flow<WorkInfo>
        get() = TODO("Not yet implemented")

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchDataFromServer(): List<CustomerMasterData> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateData(data: List<CustomerMasterData>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteData() {
        customerMasterDataDao.deleteAll()
    }

    fun getCustomersMasterDataList(): List<CustomerMasterData> {
        return customerMasterDataDao.getCustomersMasterDataList()
    }
}