package com.mastrosql.app.data.warehouse.outbound

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class NetworkWareHouseOutRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val ordersDao: OrdersDao,
    context: Context
) : WarehouseOutRepository {

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> = workManager
        .getWorkInfosByTagLiveData(TAG_OUTPUT)
        .asFlow()
        .mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

//    /**
//     * Fetches list of CustomersMasterData from mastroAndroidApi
//     * */
//
//    override suspend fun getOrders(): OrdersResponse = mastroAndroidApiService.getAllOrders()
//
//    /**
//     * Fetches order details by the specified [orderId].
//     *
//     * Retrieves the order details from the server API based on the provided [orderId].
//     * The order details are filtered using a JSON filter expression that matches the exact order ID.
//     */
//    override suspend fun getOrderByOrderId(orderId: Int): OrdersResponse {
//
//        val filter = "{\"NUME\" : {\"\$eq\": $orderId}}"
//        return mastroAndroidApiService.getOrderByFilter(filter)
//    }
//
//    /**
//     * Retrieves a stream of all orders as a [Flow] of [List] of [Order] objects.
//     *
//     * Returns a reactive stream of all orders from a local database or remote API.
//     * The stream emits updates whenever there are changes to the list of orders.
//     */
//    override fun getAllOrdersStream(): Flow<List<Order>> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Retrieves a stream of an order identified by [id] as a [Flow] of [Order] object.
//     *
//     * Returns a reactive stream of the order with the specified [id] from a local database or remote API.
//     * The stream emits updates whenever there are changes to the order.
//     */
//    override fun getOrdersStream(id: Int): Flow<Order?> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Inserts a new [Order] into the repository.
//     *
//     * This method inserts the provided [order] object into the underlying data source,
//     * which could be a local database or a remote API.
//     */
//    override suspend fun insertOrder(order: Order) {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Inserts a new [Order] into the repository.
//     *
//     * This method inserts the provided [order] object into the underlying data source,
//     * which could be a local database or a remote API.
//     */
//    override suspend fun deleteOrder(order: Order) {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Updates the specified [Order] in the repository.
//     *
//     * This method updates the provided [order] object in the underlying data source,
//     * which could be a local database or a remote API.
//     */
//    override suspend fun updateOrder(order: Order) {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Updates the delivery state of an order identified by [orderId] with the specified [deliveryState].
//     *
//     * This method sends a request to update the delivery state of an order to the server via the MastroAndroidApiService.
//     */
//    override suspend fun updateDeliveryState(
//        orderId: Int, deliveryState: Int
//    ): Response<JsonObject> {
//        val body = JsonObject().apply {
//            addProperty("orderId", orderId)
//            addProperty("deliveryState", deliveryState)
//        }
//        return mastroAndroidApiService.updateDeliveryState(body)
//    }
//
//    /**
//     * Updates the delivery state of an order identified by [orderId] with the specified [deliveryState].
//     *
//     * This method sends a request to update the delivery state of an order to the server via the MastroAndroidApiService.
//     */
//    override suspend fun updateOrderData(orderState: OrderState): Response<OrderAddResponse> {
//        val deliveryDateFormated = if (orderState.deliveryDate.value.text == "") {
//            "null"
//        } else {
//            orderState.deliveryDate.value.text
//        }
//
//        val body = JsonObject().apply {
//            addProperty("orderId", orderState.orderId.intValue)
//            addProperty("description", orderState.orderDescription.value.text)
//            addProperty("deliveryDate", deliveryDateFormated)
//        }
//        return mastroAndroidApiService.updateOrder(body)
//    }
//
//    /**
//     * Adds a new order to the server.
//     *
//     * This method sends a request to add a new order with the provided details to the server
//     * via the MastroAndroidApiService.
//     */
//    override suspend fun addNewOrder(order: Order): Response<OrderAddResponse> {
//
//        val deliveryDateFormated = if (order.deliveryDate == "") {
//            "null"
//        } else {
//            order.deliveryDate
//        }
//
//        val body = JsonObject().apply {
//            addProperty("clientId", order.clientId)
//            addProperty("destinationId", order.destinationId)
//            addProperty("description", order.description)
//            addProperty("insertDate", order.insertDate)
//            addProperty("deliveryDate", deliveryDateFormated)
//        }
//        return mastroAndroidApiService.insertNewOrder(body)
//    }
}
