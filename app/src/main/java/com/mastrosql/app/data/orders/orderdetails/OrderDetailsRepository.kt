package com.mastrosql.app.data.orders.orderdetails


import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [OrderDetailsItem] from a given data source.
 */
interface OrderDetailsRepository {

    val outputWorkInfo: Flow<WorkInfo>

    suspend fun getOrderDetails(orderId: Int): OrderDetailsResponse
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
}