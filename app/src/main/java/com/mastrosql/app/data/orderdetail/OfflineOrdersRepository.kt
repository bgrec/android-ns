package com.mastrosql.app.data.orderdetail

import androidx.work.WorkInfo
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetail
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetailDao
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetailResponse
import kotlinx.coroutines.flow.Flow

class OfflineOrderDetailRepository(
    private val orderDetailDao: OrderDetailDao,
    override val outputWorkInfo: Flow<WorkInfo>
) : OrderDetailRepository {
    override suspend fun getOrderDetail(): OrderDetailResponse {
        TODO()
    }

    override fun getAllOrderDetailStream(): Flow<List<OrderDetail>> = orderDetailDao.getAllOrderDetail()

    override fun getOrderDetailStream(id: Int): Flow<OrderDetail?> = orderDetailDao.getOrderDetailById(id)

    override suspend fun insertOrderDetail(orderDetail: OrderDetail) =
        orderDetailDao.insert(orderDetail)

    override suspend fun deleteOrderDetail(orderDetail: OrderDetail) =
        orderDetailDao.delete(orderDetail)

    override suspend fun updateOrderDetail(orderDetail: OrderDetail) =
        orderDetailDao.update(orderDetail)
}