package com.mastrosql.app.data.orders.orderdetails

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrderDetailsRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val orderDetailsDao: OrderDetailsDao,
    context: Context
) : OrderDetailsRepository {

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    override suspend fun getOrderDetails(orderId: Int?): OrderDetailsResponse {
        val filter = "{\"NUME\": $orderId}"

        return mastroAndroidApiService.getOrderDetails(filter)
    }

    override suspend fun getAllOrderDetails(): OrderDetailsResponse =
        mastroAndroidApiService.getAllOrderDetails()


    override fun getAllOrderDetailsStream(): Flow<List<OrderDetailsItem>> {
        TODO("Not yet implemented")
    }

    override fun getOrderDetailsStream(id: Int): Flow<OrderDetails?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrderDetails(orderDetail: OrderDetails) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrderDetails(orderDetail: OrderDetailsItem) {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrderDetails(orderDetail: OrderDetailsItem) {
        TODO("Not yet implemented")
    }

    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }

    override suspend fun sendScannedCode(orderId: Int, scannedCode: String): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("orderId", orderId)
            addProperty("scannedCode", scannedCode)
        }
        return mastroAndroidApiService.sendScannedCode(body)
    }

    override suspend fun deleteDetailItem(orderDetailId: Int): Response<JsonObject> {
        val filter = "{\"numePro\": $orderDetailId}"

        return mastroAndroidApiService.deleteDetailItem(filter)
    }

    override suspend fun duplicateDetailItem(orderDetailId: Int): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("orderDetailId", orderDetailId)
        }
        return mastroAndroidApiService.duplicateDetailItem(body)
    }

}