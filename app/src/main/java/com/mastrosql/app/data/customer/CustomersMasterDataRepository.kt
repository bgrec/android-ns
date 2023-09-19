package com.mastrosql.app.data.customer

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersMasterDataRepository {
    suspend fun getCustomersMasterData(): CustomersMasterDataResponse

    suspend fun getCustomersMasterDataPage(offset: Int, pageSize: Int): CustomersMasterDataResponse
/*
    /**
     * Retrieve all the customers from the the given data source.
     */
    fun getAllCustomersStream(): Flow<List<CustomerMasterData>>

    /**
     * Retrieve a customer from the given data source that matches with the [id].
     */
    fun getCustomerStream(id: Int): Flow<CustomerMasterData?>

    /**
     * Insert customer in the data source
     */
    suspend fun insertCustomer(customerMasterData: CustomerMasterData)

    /**
     * Delete customer from the data source
     */
    suspend fun deleteCustomer(customerMasterData: CustomerMasterData)

    /**
     * Update item in the data source
     */
    suspend fun updateCustomer(customerMasterData: CustomerMasterData)
    */

}

/**
 * Network Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkCustomersMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService
) : CustomersMasterDataRepository {
    /** Fetches list of CustomersMasterData from mastroAndroidApi*/
    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse =
        mastroAndroidApiService.getCustomersMasterData()

    override suspend fun getCustomersMasterDataPage(offset: Int, pageSize: Int): CustomersMasterDataResponse =
        mastroAndroidApiService.getCustomersMasterDataPage(offset, pageSize)
}
/*
    override fun getAllCustomersStream(): Flow<List<CustomerMasterData>> {
        TODO("Not yet implemented")
    }

    override fun getCustomerStream(id: Int): Flow<CustomerMasterData?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCustomer(customerMasterData: CustomerMasterData) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomer(customerMasterData: CustomerMasterData) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCustomer(customerMasterData: CustomerMasterData) {
        TODO("Not yet implemented")
    }
    //List<CustomerMasterData> = mastroAndroidApiService.getCustomersMasterData()
}
*/