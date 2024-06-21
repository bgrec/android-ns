package com.mastrosql.app.data.customers.paged


import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersPagedMasterDataRepository {

    /**
     * Retrieves the Mastro Android API service.
     */
    fun getApiService(): MastroAndroidApiService

    /**
     * Retrieves paged customers master data from a remote data source.
     */
    suspend fun getPagedCustomersMasterData(offset: Int, limit: Int): CustomersMasterDataResponse

    /**
     * Updates the Mastro Android API service.
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)
}
