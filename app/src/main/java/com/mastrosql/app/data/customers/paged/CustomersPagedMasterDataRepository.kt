package com.mastrosql.app.data.customers.paged


import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersPagedMasterDataRepository {

    fun getApiService(): MastroAndroidApiService

    suspend fun getPagedCustomersMasterData(offset: Int, limit: Int): CustomersMasterDataResponse

}
