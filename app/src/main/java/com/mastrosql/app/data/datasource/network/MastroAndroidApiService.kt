package com.mastrosql.app.data.datasource.network

import com.google.gson.JsonObject
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A public interface that exposes the [getAllCustomersMasterData] method
 */

interface MastroAndroidApiService {
    /**
     * Returns a [List] of [CustomerMasterData] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "customersMasterData" endpoint will be requested with the GET
     * HTTP method
     */

    /**
     * suspend fun getAllCustomersMasterData(): CustomersMasterDataResponse
     * without parameters takes the default values of mysql offset and limit
     */

    /*@GET("customersMasterData")
    suspend fun getAllCustomersMasterData(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000000
    ): CustomersMasterDataResponse*/

    @GET("clientsview")
    suspend fun getAllCustomersMasterData(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000000
    ): CustomersMasterDataResponse

    /**
     * Query parameters are added to the URL after a question mark. They are separated from the URL
     * by a "&" character. The query parameters are indicated by the @Query annotation.
     * The @Query annotation has a parameter that is the query parameter name and a second parameter
     * that is the value of that query parameter.
     */

    // @GET("movie/popular?api_key=${MOVIE_API_KEY}&language=en-US")
    @GET("clientsview")
    suspend fun getCustomersMasterDataPage(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000000
    ): CustomersMasterDataResponse

    @GET("articlesview")
    suspend fun getAllArticles(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000000
    ): ArticlesResponse

    @GET("ordersview")
    suspend fun getAllOrders(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000000
    ): OrdersResponse

    @GET("rigOrdc")
    suspend fun getOrderDetails(
        @Query("q") filter: String,  //"{\"NUME\": 4}" rigOrdc/?q={"NUME": 4}
        @Query("orderby") order : String = "{\"RIGA\": \"ASC\"}"
    ): OrderDetailsResponse

    @GET("rigOrdc")
    suspend fun getAllOrderDetails(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1000
    ): OrderDetailsResponse

    @GET("clientsview")
    suspend fun testApiCall(
        @Query("offset") offset: Int = 0,
        @Query("limit") pageSize: Int = 1
    ): Response<JsonObject>

}


// @Header("Authorization") authorization:String,
// @Header("Client-Id") clientId:String,