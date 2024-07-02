package com.mastrosql.app.data.datasource.network

import com.google.gson.JsonObject
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataResponse
import com.mastrosql.app.ui.navigation.main.loginscreen.model.SupportedVersionResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderAddResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundAddResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsResponse
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

    //Customers API calls
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

    /**
     * Makes a test API call to retrieve client data with specified offset and limit.
     */
    // only for testing connection to the server
    @GET("clientsview")
    suspend fun testApiCall(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): Response<JsonObject>

    /**
     * Retrieves all destinations data with specified offset and limit.
     */
    @GET("clientsdestinationsview")
    suspend fun getAllDestinationsData(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): DestinationsDataResponse

    //End of Customers API calls

    //Articles API calls
    /**
     * Retrieves all articles with specified offset and limit.
     */
    @GET("articlesview")
    suspend fun getAllArticles(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): ArticlesResponse

    /**
     * Inserts an article into a document on the server.
     */
    @PUT("InsertArticleIntoDocument")
    suspend fun insertArticleIntoDocument(
        @Body body: JsonObject
    ): Response<JsonObject>

    //End of Articles API calls


    //Orders API calls
    /**
     * Retrieves all orders with specified offset and limit.
     */
    @GET("ordersview")
    suspend fun getAllOrders(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): OrdersResponse

    /**
     * Retrieves orders based on the specified filter.
     */
    @GET("ordersview")
    suspend fun getOrderByFilter(
        @Query("q") filter: String,
        /**
         * Example of filter parameter:
         * "{\"NUME\": 36519}" /ordersview?q={"NUME" : {"$eq": 36519}}
         */
    ): OrdersResponse

    /**
     * Retrieves order details based on the specified filter.
     */
    @GET("rigOrdc")
    suspend fun getOrderDetails(
        @Query("q") filter: String,
        /**
         * Example of filter parameter:
         * "{\"NUME\": 4}" rigOrdc/?q={"NUME": 4}
         */
    ): OrderDetailsResponse

    /**
     * Retrieves all order details with specified offset and limit.
     */
    @GET("rigOrdc")
    suspend fun getAllOrderDetails(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): OrderDetailsResponse

    /**
     * Sends scanned barcode data to the server.
     */
    @PUT("OrderBarcodeReader")
    suspend fun sendOrderScannedCode(
        @Body body: JsonObject
    ): Response<JsonObject>

    /**
     * Deletes a detail item based on the specified filter.
     */
    @DELETE("rigOrdc")
    suspend fun deleteOrderDetailItem(
        @Query("q") filter: String,
    ): Response<JsonObject>

    /**
     * Duplicates an order detail item on the server.
     */
    @PUT("DuplicateOrderRow")
    suspend fun duplicateOrderDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    /**
     * Updates an order detail item on the server.
     */
    @PUT("UpdateOrderRow")
    suspend fun updateOrderDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    /**
     * Inserts a new order into the server.
     */
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

    /**
     * Updates the delivery state of an order on the server.
     */
    @PUT("UpdateOrderDeliveryState")
    suspend fun updateOrderDeliveryState(
        @Body body: JsonObject
    ): Response<JsonObject>

    /**
     * Updates an existing order on the server.
     */
    @PUT("UpdateOrder")
    suspend fun updateOrder(
        @Body body: JsonObject
    ): Response<OrderAddResponse>

    //End of Orders API calls

    //Warehouse Outbound API calls

    @GET("whoutboundview")
    suspend fun getAllWhOutbound(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): WhOutboundResponse

    @PUT("InsertNewWhOutbound")
    suspend fun insertNewWhOutbound(
        @Body body: JsonObject
    ): Response<WhOutboundAddResponse>

    /**
     * Retrieves whout details based on the specified filter.
     */
    @GET("palmaRighe")
    suspend fun getWhOutDetails(
        @Query("q") filter: String,
        /**
         * Example of filter parameter:
         * "{\"NUME\": 4}" rigOrdc/?q={"NUME": 4}
         */
    ): WhOutDetailsResponse

    /**
     * Retrieves all whouut details with specified offset and limit.
     */
    @GET("palmaRighe")
    suspend fun getAllWhOutDetails(
        @Query("offset") offset: Int = 0, @Query("limit") pageSize: Int = 1000000
    ): WhOutDetailsResponse

    /**
     * Deletes a detail whout item based on the specified filter.
     */
    @DELETE("palmaRighe")
    suspend fun deleteWhOutDetailItem(
        @Query("q") filter: String,
    ): Response<JsonObject>

    /**
     * Sends scanned barcode data to the server.
     */
    @PUT("WhOutBarcodeReader")
    suspend fun sendWhOutScannedCode(
        @Body body: JsonObject
    ): Response<JsonObject>

    /**
     * Updates an whout detail item on the server.
     */
    @PUT("UpdateWhOutRow")
    suspend fun updateWhOutDetailItem(
        @Body body: JsonObject
    ): Response<JsonObject>

    //End of Warehouse Outbound API calls

    //Version Check
    /**
     * Retrieves the current version of the server.
     */
    @GET("SupportedVersion")
    suspend fun getSupportedVersion(
        //@Body body: JsonObject
    ): Response<SupportedVersionResponse>

    //End of Version Check

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

    /**
     * Initiates a login request to authenticate the user.
     */
    @GET("authentication/login")
    suspend fun login(
        @Query("app") appName: String, @Header("Authorization") authorization: String
    ): Response<JsonObject>

    /**
     * Initiates a logout request to terminate the user's session.
     */
    @GET("authentication/logout")
    suspend fun logout(): Response<JsonObject>

    /**
     * Retrieves the current login status from the server.
     */
    @GET("authentication/status")
    suspend fun getLoginStatus(): Response<JsonObject>

    /**
     * Retrieves the completion status of the authentication process from the server.
     */
    @GET("authentication/completed")
    suspend fun getLoginCompleted(): Response<JsonObject>
}
