package com.mastrosql.app.data.orders

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch orders data list from mastroAndroidApi.
 */

class NetworkOrdersRepository(
    private val mastroAndroidApiService: MastroAndroidApiService,
    private val ordersDao: OrdersDao,
    context: Context
) : OrdersRepository {

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getOrders(): OrdersResponse =
        mastroAndroidApiService.getAllOrders()

    override fun getAllOrdersStream(): Flow<List<Order>> {
        TODO("Not yet implemented")
    }

    override fun getOrdersStream(id: Int): Flow<Order?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order) {
        TODO("Not yet implemented")
    }

    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }


}