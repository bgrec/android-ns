package com.mastrosql.app.data.customer

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersMasterDataRepository {
    suspend fun getCustomersMasterData(): CustomersMasterDataResponse
}

/**
 * Network Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkCustomersMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService
) : CustomersMasterDataRepository {
    /** Fetches list of CustomersMasterData from mastroAndroidApi*/
    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse = mastroAndroidApiService.getCustomersMasterData()
            //List<CustomerMasterData> = mastroAndroidApiService.getCustomersMasterData()
}