package com.mastrosql.app.data.orders


import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Order] from a given data source.
 */
interface OrdersRepository {

    val outputWorkInfo: Flow<WorkInfo>
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
}