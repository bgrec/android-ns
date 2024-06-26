package com.mastrosql.app.data.orders


import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderAddResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrderState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Repository that provides insert, update, delete, and retrieve of [Order] from a given data source.
 */
interface OrdersRepository {

    @Suppress("KDocMissingDocumentation")
    val outputWorkInfo: Flow<WorkInfo>

    /**
     * Update the [MastroAndroidApiService] to be used by the repository.
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)

    /**
     * Retrieve all the Orders from the the given data source.
     */
    suspend fun getOrders(): OrdersResponse
    //suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

    /**
     * Retrieve all the Orders from the the given data source.
     */
    fun getAllOrdersStream(): Flow<List<Order>>

    /**
     * Retrieve an Order from the given data source that matches with the [id].
     */
    fun getOrdersStream(id: Int): Flow<Order?>

    /**
     * Insert Order in the data source
     */
    suspend fun insertOrder(order: Order)

    /**
     * Delete order from the data source
     */
    suspend fun deleteOrder(order: Order)

    /**
     * Update order in the data source
     */
    suspend fun updateOrder(order: Order)

    /**
     * Update the delivery state of the order in the data source
     */
    suspend fun updateDeliveryState(orderId: Int, deliveryState: Int): Response<JsonObject>

    /**
     * Add a new order to the data source
     */
    suspend fun addNewOrder(order: Order): Response<OrderAddResponse>

    /**
     * Retrieve an Order from the given data source that matches with the [orderId].
     */
    suspend fun getOrderByOrderId(orderId: Int): OrdersResponse

    /**
     * Edit an order in the data source
     */
    suspend fun updateOrderData(orderState: OrderState): Response<OrderAddResponse>

}
