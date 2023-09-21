package com.mastrosql.app.data.customer

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse

/**
 * Repository that fetch customers data list from MastroAndroid API.
 */

interface CustomersMasterDataRepository {

    suspend fun getCustomersMasterData(): CustomersMasterDataResponse

}
