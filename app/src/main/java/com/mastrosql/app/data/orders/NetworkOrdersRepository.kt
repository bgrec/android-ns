package com.mastrosql.app.data.orders

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderAddResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrderState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrdersRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val ordersDao: OrdersDao,
    context: Context
) : OrdersRepository {

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

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getOrders(): OrdersResponse = mastroAndroidApiService.getAllOrders()

    override suspend fun getOrderByOrderId(orderId: Int): OrdersResponse {

        val filter = "{\"NUME\" : {\"\$eq\": $orderId}}"
        return mastroAndroidApiService.getOrderByFilter(filter)
    }

    override fun getAllOrdersStream(): Flow<List<Order>> {
        TODO("Not yet implemented")
    }

    override fun getOrdersStream(id: Int): Flow<Order?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDeliveryState(
        orderId: Int, deliveryState: Int
    ): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("orderId", orderId)
            addProperty("deliveryState", deliveryState)
        }
        return mastroAndroidApiService.updateDeliveryState(body)
    }

    override suspend fun updateOrderData(orderState: OrderState): Response<OrderAddResponse> {
        val deliveryDateFormated = if (orderState.deliveryDate.value.text == "") {
            "null"
        } else {
            orderState.deliveryDate.value.text
        }

        val body = JsonObject().apply {
            addProperty("orderId", orderState.orderId.intValue)
            addProperty("description", orderState.orderDescription.value.text)
            addProperty("deliveryDate", deliveryDateFormated)
        }
        return mastroAndroidApiService.updateOrder(body)
    }


    override suspend fun addNewOrder(order: Order): Response<OrderAddResponse> {

        val deliveryDateFormated = if (order.deliveryDate == "") {
            "null"
        } else {
            order.deliveryDate
        }

        val body = JsonObject().apply {
            addProperty("clientId", order.clientId)
            addProperty("destinationId", order.destinationId)
            addProperty("description", order.description)
            addProperty("insertDate", order.insertDate)
            addProperty("deliveryDate", deliveryDateFormated)
        }
        return mastroAndroidApiService.insertNewOrder(body)
    }
}
