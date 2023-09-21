package com.mastrosql.app.data.customer

import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse

/**
 * Network and database Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkCustomersMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService
) : CustomersMasterDataRepository {

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse =
        mastroAndroidApiService.getAllCustomersMasterData()

}
