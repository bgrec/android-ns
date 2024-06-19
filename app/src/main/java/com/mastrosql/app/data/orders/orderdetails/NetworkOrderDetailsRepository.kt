package com.mastrosql.app.data.orders.orderdetails

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrderDetailsRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val orderDetailsDao: OrderDetailsDao,
    context: Context
) : OrderDetailsRepository {


    /**
     * Updates the [MastroAndroidApiService] instance used for network operations.
     *
     * This method updates the [mastroAndroidApiService] instance to the provided
     * [newMastroAndroidApiService], enabling the repository to communicate with
     * the backend using the updated service.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> = workManager
        .getWorkInfosByTagLiveData(TAG_OUTPUT)
        .asFlow()
        .mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Retrieves order details for a specific order identified by [orderId].
     */
    override suspend fun getOrderDetails(orderId: Int?): OrderDetailsResponse {
        //val filter = "{\"NUME\": $orderId}"
        //added order by RIGA ASC
        val filter = "{\"\$orderby\": {\"RIGA\": \"ASC\"}, \"NUME\" : {\"\$eq\": $orderId}}"

        return mastroAndroidApiService.getOrderDetails(filter)
    }

    //Not used
    /*override suspend fun getOrderDetails(orderId: Int?): OrderDetailsResponse {
        val body = JsonObject().apply {
            addProperty("orderId", orderId)
        }
        return mastroAndroidApiService.getOrderDetails(body)
    }*/

    /**
     * Retrieves all order details.
     */
    override suspend fun getAllOrderDetails(): OrderDetailsResponse =
        mastroAndroidApiService.getAllOrderDetails()

    /**
     * Retrieves a flow of all order details items.
     */
    override fun getAllOrderDetailsStream(): Flow<List<OrderDetailsItem>> {
        TODO("Not yet implemented")
    }

    /**
     * Retrieves a flow of order details item for the given [id].
     */
    override fun getOrderDetailsStream(id: Int): Flow<OrderDetailsItem?> {
        TODO("Not yet implemented")
    }

    /**
     * Inserts an [OrderDetailsItem] into the data source.
     *
     * This function is responsible for inserting a new order details item into the underlying
     * data source, which could be a local database or a remote API service.
     */
    override suspend fun insertOrderDetails(orderDetail: OrderDetailsItem) {
        TODO("Not yet implemented")
    }

    /**
     * Deletes an [OrderDetailsItem] from the data source.
     *
     * This function is responsible for deleting an existing order details item from the underlying
     * data source, which could be a local database or a remote API service.
     */
    override suspend fun deleteOrderDetails(orderDetail: OrderDetailsItem) {
        TODO("Not yet implemented")
    }

    /**
     * Updates an existing [OrderDetailsItem] in the data source.
     *
     * This function is responsible for updating an existing order details item in the underlying
     * data source, which could be a local database or a remote API service.
     */
    override suspend fun updateOrderDetails(orderDetail: OrderDetailsItem) {
        TODO("Not yet implemented")
    }

    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }


    /**
     * Sends the scanned code associated with a specific order to the server.
     *
     * This function sends the provided [scannedCode] along with the [orderId] to the server
     * via the MastroAndroidApiService, and returns the server response as a [Response] containing
     * a [JsonObject].
     */
    override suspend fun sendScannedCode(orderId: Int, scannedCode: String): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("orderId", orderId)
            addProperty("scannedCode", scannedCode)
        }
        return mastroAndroidApiService.sendScannedCode(body)
    }

    /**
     * Deletes a detail item identified by [orderDetailId] from the server.
     *
     * This function constructs a filter JSON using [orderDetailId] and sends it to the server
     * via the MastroAndroidApiService for deleting the corresponding detail item. It returns
     * the server response as a [Response] containing a [JsonObject].
     */
    override suspend fun deleteDetailItem(orderDetailId: Int): Response<JsonObject> {
        val filter = "{\"numePro\": $orderDetailId}"

        return mastroAndroidApiService.deleteDetailItem(filter)
    }

    /**
     * Duplicates a detail item identified by [orderDetailId] on the server.
     *
     * This function constructs a JSON body containing [orderDetailId] and sends it to the server
     * via the MastroAndroidApiService for duplicating the corresponding detail item. It returns
     * the server response as a [Response] containing a [JsonObject].
     */
    override suspend fun duplicateDetailItem(orderDetailId: Int): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("orderDetailId", orderDetailId)
        }
        return mastroAndroidApiService.duplicateDetailItem(body)
    }

    /**
     * Updates details of an order item identified by [orderDetailId] on the server.
     *
     * This function constructs a JSON body containing [orderDetailId], [quantity], [batch], and [expirationDate]
     * (formatted as a string) and sends it to the server via the MastroAndroidApiService for updating the
     * corresponding order detail item. If [expirationDate] is an empty string, it is formatted as "null" in
     * the JSON body.
     */
    override suspend fun updateDetailItem(
        orderDetailId: Int, quantity: Double, batch: String, expirationDate: String
    ): Response<JsonObject> {

    /**
     * Formats the expiration date for use in an API request.
     */
        val expirationDateFormated = if (expirationDate == "") {
            "null"
        }

        /**
         * Formats the expiration date for use in an API request.
         */
        else {
            expirationDate
        }

        val body = JsonObject().apply {
            addProperty("orderDetailId", orderDetailId)
            addProperty("quantity", quantity)
            addProperty("batch", batch)
            addProperty("expirationDate", expirationDateFormated)
        }
        return mastroAndroidApiService.updateDetailItem(body)
    }

}
