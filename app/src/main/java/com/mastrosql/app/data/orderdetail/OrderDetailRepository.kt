package com.mastrosql.app.data.orderdetail


import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetail
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetailResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [OrderDetail] from a given data source.
 */
interface OrderDetailRepository {

    val outputWorkInfo: Flow<WorkInfo>
    suspend fun getOrderDetail(): OrderDetailResponse
    //suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

    /**
     * Retrieve all the Orders from the the given data source.
     */
    fun getAllOrderDetailStream(): Flow<List<OrderDetail>>

    /**
     * Retrieve an Order from the given data source that matches with the [id].
     */
    fun getOrderDetailStream(id: Int): Flow<OrderDetail?>

    /**
     * Insert Order in the data source
     */
    suspend fun insertOrderDetail(orderDetail: OrderDetail)

    /**
     * Delete order from the data source
     */
    suspend fun deleteOrderDetail(orderDetail: OrderDetail)

    /**
     * Update order in the data source
     */
    suspend fun updateOrderDetail(orderDetail: OrderDetail)
}