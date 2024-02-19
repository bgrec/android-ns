package com.mastrosql.app.data.orderdetail

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetail
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetailDao
import com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model.OrderDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrderDetailRepository(
    private val mastroAndroidApiService: MastroAndroidApiService,
    private val orderDetailDao: OrderDetailDao,
    context: Context
) : OrderDetailRepository {

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getOrderDetail(): OrderDetailResponse =
        mastroAndroidApiService.getAllOrderDetail()

    override fun getAllOrderDetailStream(): Flow<List<OrderDetail>> {
        TODO("Not yet implemented")
    }

    override fun getOrderDetailStream(id: Int): Flow<OrderDetail?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrderDetail(orderDetail: OrderDetail) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrderDetail(orderDetail: OrderDetail) {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrderDetail(orderDetail: OrderDetail) {
        TODO("Not yet implemented")
    }

    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }

}