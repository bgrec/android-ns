package com.mastrosql.app.data.orderdetails

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrderDetailsRepository(
    private val mastroAndroidApiService: MastroAndroidApiService,
    private val orderDetailsDao: OrderDetailsDao,
    context: Context
) : OrderDetailsRepository {

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getOrderDetails(): OrderDetailsResponse =
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

}