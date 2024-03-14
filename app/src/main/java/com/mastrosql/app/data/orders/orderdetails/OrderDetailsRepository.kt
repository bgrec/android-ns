package com.mastrosql.app.data.orders.orderdetails


import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Repository that provides insert, update, delete, and retrieve of [OrderDetailsItem] from a given data source.
 */
interface OrderDetailsRepository {

    val outputWorkInfo: Flow<WorkInfo>

    suspend fun getOrderDetails(orderId: Int?): OrderDetailsResponse
    suspend fun getAllOrderDetails(): OrderDetailsResponse
    //suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

    /**
     * Retrieve all the Orders from the the given data source.
     */
    fun getAllOrderDetailsStream(): Flow<List<OrderDetailsItem>>

    /**
     * Retrieve an Order from the given data source that matches with the [id].
     */
    fun getOrderDetailsStream(id: Int): Flow<OrderDetails?>

    /**
     * Insert Order in the data source
     */
    suspend fun insertOrderDetails(orderDetail: OrderDetails)

    /**
     * Delete order from the data source
     */
    suspend fun deleteOrderDetails(orderDetail: OrderDetailsItem)

    /**
     * Update order in the data source
     */
    suspend fun updateOrderDetails(orderDetail: OrderDetailsItem)

    /**
     * Update the [MastroAndroidApiService] with a new [MastroAndroidApiService]
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)

    /**
     * Sends the scanned code to the server
     */
    suspend fun sendScannedCode(orderId: Int, scannedCode: String): Response<JsonObject>

    suspend fun deleteDetailItem(orderDetailId: Int): Response<JsonObject>
}