package com.mastrosql.app.data.warehouse.outbound

import androidx.work.WorkInfo
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Order] from a given data source.
 */
class OfflineWarehouseOutRepository(
    private val ordersDao: OrdersDao, override val outputWorkInfo: Flow<WorkInfo>
) : WarehouseOutRepository {

//    /**
//     * Retrieves a list of orders from the server.
//     *
//     * This method sends a request to fetch a list of orders from the server
//     * via the MastroAndroidApiService.
//     */
//    override suspend fun getOrders(): OrdersResponse {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Retrieves a stream of all orders from the local database.
//     *
//     * This function returns a Flow that emits a list of orders every time
//     * the data in the orders database table is updated.
//     */
//    override fun getAllOrdersStream(): Flow<List<Order>> = ordersDao.getAllOrders()
//
//    /**
//     * Retrieves a stream of an order by its unique identifier from the local database.
//     *
//     * This function returns a Flow that emits the order with the specified ID whenever
//     * the data in the orders database table is updated.
//     */
//    override fun getOrdersStream(id: Int): Flow<Order?> = ordersDao.getOrderById(id)
//
//    /**
//     * Inserts a new order into the local database.
//     *
//     * This function inserts the provided [order] object into the orders database table.
//     */
//    override suspend fun insertOrder(order: Order) = ordersDao.insert(order)
//
//    /**
//     * Deletes an order from the local database.
//     *
//     * This function deletes the specified [order] object from the orders database table.
//     */
//    override suspend fun deleteOrder(order: Order) = ordersDao.delete(order)
//
//    /**
//     * Updates an existing order in the local database.
//     *
//     * This function updates the specified [order] object in the orders database table.
//     */
//    override suspend fun updateOrder(order: Order) = ordersDao.update(order)
//
    /**
     * Updates the [MastroAndroidApiService] instance used for API communication.
     *
     * This function updates the [mastroAndroidApiService] instance with a new implementation
     * of [MastroAndroidApiService], allowing the repository to use a different API service
     * for network operations.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    override suspend fun getWhOutbound(): WhOutboundResponse {
        TODO("Not yet implemented")
    }
//
//    /**
//     * Updates the delivery state of an order identified by [orderId].
//     *
//     * This function sends a request to update the delivery state of an order to the server.
//     * It expects an [orderId] and a [deliveryState] integer as parameters, which specify
//     * the order ID and the new delivery state respectively.
//     */
//    override suspend fun updateDeliveryState(
//        orderId: Int, deliveryState: Int
//    ): Response<JsonObject> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Adds a new order to the server.
//     *
//     * This function sends a request to the server to add a new order using the provided [order] object.
//     * The order details such as client ID, destination ID, description, insert date, and delivery date
//     * are encapsulated in the [order] parameter.
//     */
//    override suspend fun addNewOrder(order: Order): Response<OrderAddResponse> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Retrieves order details for a specific order ID from the server.
//     *
//     * This function sends a request to the server to fetch order details corresponding to the provided [orderId].
//     */
//    override suspend fun getOrderByOrderId(orderId: Int): OrdersResponse {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Updates order data on the server based on the provided [orderState].
//     *
//     * This function sends a request to the server to update order data such as order description and delivery date.
//     */
//    override suspend fun updateOrderData(orderState: OrderState): Response<OrderAddResponse> {
//        TODO("Not yet implemented")
//    }

}
