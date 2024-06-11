package com.mastrosql.app.data.datasource.network

import com.google.gson.JsonObject
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderAddResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * A public interface that exposes the [MastroAndroidApiService] methods
 *
 * !!! Remember to add the SUSPEND keyword to the method signature !!!
 *
 */

interface MastroAndroidApiService {

    /**
     * Returns a [CustomersMasterDataResponse] object which is a list of [CustomerMasterData]
     * Authentication is required for this API call.
     * The Authorization header is added to the request using the Interceptor in the [RetrofitInstance] class.
     * The Authorization header is a session string that is obtained from the login API call.
     */

    @GET("clientsview")
    suspend fun getAllCustomersMasterData(
        //@Header("Authorization") authorization: String = "Basic bWFzdHJvOm1hc3Rybw==",
        //@Header("Cookie") cookie: String = "session_11eeda4470830af4a032408d5c759f7c=2024-03-20 14:06:19-274",
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): CustomersMasterDataResponse

    /**
     * Query parameters are added to the URL after a question mark. They are separated from the URL
     * by a "&" character. The query parameters are indicated by the @Query annotation.
     * The @Query annotation has a parameter that is the query parameter name and a second parameter
     * that is the value of that query parameter.
     */

    @GET("clientsview")
    suspend fun getCustomersMasterDataPage(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): CustomersMasterDataResponse

    // only for testing connection to the server
    @GET("clientsview")
    suspend fun testApiCall(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): Response<JsonObject>

    @GET("clientsdestinationsview")
    suspend fun getAllDestinationsData(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): DestinationsDataResponse

    @GET("articlesview")
    suspend fun getAllArticles(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): ArticlesResponse

    @GET("ordersview")
    suspend fun getAllOrders(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): OrdersResponse

    @GET("ordersview")
    suspend fun getOrderByFilter(
        @Query("q") filter: String,
        /**
         * Example of filter parameter:
         * "{\"NUME\": 36519}" /ordersview?q={"NUME" : {"$eq": 36519}}
         */
    ): OrdersResponse

    @GET("rigOrdc")
    suspend fun getOrderDetails(
        @Query("q") filter: String,
        /**
         * Example of filter parameter:
         * "{\"NUME\": 4}" rigOrdc/?q={"NUME": 4}
         */
    ): OrderDetailsResponse

    @GET("rigOrdc")
    suspend fun getAllOrderDetails(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): OrderDetailsResponse


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

    @PUT("DuplicateOrderRow")
    suspend fun duplicateDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    @PUT("UpdateOrderRow")
    suspend fun updateDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    @PUT("InsertNewOrder")
    suspend fun insertNewOrder(
        @Body body: JsonObject
    ): Response<OrderAddResponse>

    /**
     *  Example of PATH parameter
     * @PUT("lisOrdc/{NUME}")
     * suspend fun updateDeliveryState(
     *     @Path("NUME") orderNumber: Int,
     *     @Body body: JsonObject
     * ): Response<JsonObject>
     */

    @PUT("UpdateOrderDeliveryState")
    suspend fun updateDeliveryState(
        @Body body: JsonObject
    ): Response<JsonObject>


    /**
     * ****************************************************************************************
     * Authentication API calls
     * ****************************************************************************************
     * The following API calls are used to authenticate the user.
     * ...service... /authentication/
     * /**
     *  * @Header("Authorization") authorization:String,
     *  * @Header("Client-Id") clientId:String,
     *  */
     */

    @GET("authentication/login")
    suspend fun login(
        @Query("app") appName: String, @Header("Authorization") authorization: String
    ): Response<JsonObject>

    @GET("authentication/logout")
    suspend fun logout(): Response<JsonObject>

    @GET("authentication/status")
    suspend fun getLoginStatus(): Response<JsonObject>

    @GET("authentication/completed")
    suspend fun getLoginCompleted(): Response<JsonObject>

}
