package com.mastrosql.app.data.customer


import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersPagedMasterDataRepository {

    fun getApiService(): MastroAndroidApiService

    suspend fun getPagedCustomersMasterData(offset: Int, limit: Int): CustomersMasterDataResponse

}
