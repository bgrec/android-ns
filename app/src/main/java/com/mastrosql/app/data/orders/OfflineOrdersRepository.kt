package com.mastrosql.app.data.orders

import androidx.work.WorkInfo
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import kotlinx.coroutines.flow.Flow

class OfflineOrdersRepository(
    private val ordersDao: OrdersDao,
    override val outputWorkInfo: Flow<WorkInfo>
) : OrdersRepository {
    override suspend fun getOrders(): OrdersResponse {
        TODO()
    }

    override fun getAllOrdersStream(): Flow<List<Order>> = ordersDao.getAllOrders()

    override fun getOrdersStream(id: Int): Flow<Order?> = ordersDao.getOrderById(id)

    override suspend fun insertOrder(order: Order) = ordersDao.insert(order)

    override suspend fun deleteOrder(order: Order) = ordersDao.delete(order)

    override suspend fun updateOrder(order: Order) = ordersDao.update(order)
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }
}