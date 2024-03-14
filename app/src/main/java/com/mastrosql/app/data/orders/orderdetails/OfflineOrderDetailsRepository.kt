package com.mastrosql.app.data.orders.orderdetails

import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class OfflineOrderDetailsRepository(
    private val orderDetailDao: OrderDetailsDao, override val outputWorkInfo: Flow<WorkInfo>
) : OrderDetailsRepository {
    override suspend fun getOrderDetails(orderId: Int?): OrderDetailsResponse {
        TODO()
    }

    override suspend fun getAllOrderDetails(): OrderDetailsResponse {
        TODO()
    }

    override fun getAllOrderDetailsStream(): Flow<List<OrderDetailsItem>> {
        //= orderDetailDao.getAllOrderDetails()
        TODO()
    }

    override fun getOrderDetailsStream(id: Int): Flow<OrderDetails?> {
        //orderDetailDao.getOrderDetailsItemById(id)
        TODO()
    }

    override suspend fun insertOrderDetails(orderDetail: OrderDetails) {
        //orderDetailDao.insert(orderDetailsItem)
        TODO()
    }

    override suspend fun deleteOrderDetails(orderDetail: OrderDetailsItem) =
        orderDetailDao.delete(orderDetail)

    override suspend fun updateOrderDetails(orderDetail: OrderDetailsItem) =
        orderDetailDao.update(orderDetail)

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    override suspend fun sendScannedCode(orderId: Int, scannedCode: String): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDetailItem(orderDetailId: Int): Response<JsonObject> {
        TODO("Not yet implemented")
    }
}