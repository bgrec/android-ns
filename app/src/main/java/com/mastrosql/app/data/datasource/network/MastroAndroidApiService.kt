package com.mastrosql.app.data.datasource.network

import com.google.gson.JsonObject
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
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
        //@Header("Authorization") authorization: String = "Basic bWFzdHJvOm1hc3Rybw==",
        //@Header("Cookie") cookie: String = "session_11eeda4470830af4a032408d5c759f7c=2024-03-20 14:06:19-274",
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
        @Query("orderby") order: String = "{\"RIGA\": \"ASC\"}"
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

    @PUT("OrderBarcodeReader")
    suspend fun sendScannedCode(
        @Body body: JsonObject
    ): Response<JsonObject>

    @DELETE("rigOrdc")
    suspend fun deleteDetailItem(
        @Query("q") filter: String,
    ): Response<JsonObject>

    @PUT("InsertArticleIntoDocument")
    suspend fun insertArticleIntoDocument(
        @Body body: JsonObject
    ): Response<JsonObject>

    @PUT("duplicateOrderRow")
    suspend fun duplicateDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    @PUT("lisOrdc")
    suspend fun updateDeliveryState(
        @Body body: JsonObject
    ): Response<JsonObject>
  
  
    // Login API call,  ...service... /authentication/
    @GET("authentication/login")
    suspend fun login(
        @Query("app") appName: String,
        @Header("Authorization") authorization: String
    ): Response<JsonObject>

    @GET("authentication/logout")
    suspend fun logout(): Response<JsonObject>

    @GET("authentication/status")
    suspend fun getLoginStatus(): Response<JsonObject>

    @GET("authentication/completed")
    suspend fun getLoginCompleted(): Response<JsonObject>
  
}


// @Header("Authorization") authorization:String,
// @Header("Client-Id") clientId:String,