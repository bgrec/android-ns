package com.mastrosql.app.data.orderdetails

import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow

class OfflineOrderDetailsRepository(
    private val orderDetailDao: OrderDetailsDao,
    override val outputWorkInfo: Flow<WorkInfo>
) : OrderDetailsRepository {
    override suspend fun getOrderDetail(): OrderDetailsResponse {
        TODO()
    }

    override fun getAllOrderDetailsStream(): Flow<List<OrderDetailsItem>> = orderDetailDao.getAllOrderDetails()

    override fun getOrderDetailsStream(id: Int): Flow<OrderDetails?> = orderDetailDao.getOrderDetailsItemById(id)

    override suspend fun insertOrderDetails(orderDetail: OrderDetails) {
        //orderDetailDao.insert(orderDetailsItem)
        TODO()
    }


    override suspend fun deleteOrderDetails(orderDetail: OrderDetailsItem) =
        orderDetailDao.delete(orderDetail)

    override suspend fun updateOrderDetails(orderDetail: OrderDetailsItem) =
        orderDetailDao.update(orderDetail)
}