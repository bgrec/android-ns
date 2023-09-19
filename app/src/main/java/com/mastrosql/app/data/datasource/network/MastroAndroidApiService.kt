package com.mastrosql.app.data.datasource.network

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A public interface that exposes the [getCustomersMasterData] method
 */

interface MastroAndroidApiService {
    /**
     * Returns a [List] of [CustomerMasterData] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "customersMasterData" endpoint will be requested with the GET
     * HTTP method
     */

    @GET("customersMasterData")
    suspend fun getCustomersMasterData(): CustomersMasterDataResponse

    @GET("customersMasterData")
    suspend fun getCustomersMasterDataPage(
        @Query("offset") offset: Int,
        @Query("limit") pageSize: Int
    ): CustomersMasterDataResponse
}


