package com.mastrosql.app.data.customer

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse

class OfflineCustomersPagedMasterDataRepository(private val customerMasterDataDao: CustomersMasterDataDao) :
    CustomersMasterDataRepository {

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse {
        TODO("Not yet implemented")
    }

    fun getCustomersMasterDataList(): List<CustomerMasterData> {
        return customerMasterDataDao.getCustomersMasterDataList()
    }
}