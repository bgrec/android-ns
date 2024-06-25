package com.mastrosql.app.data.warehouse.outbound

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutboundDao
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundAddResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

class NetworkWarehouseOutRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val wareHouseOutboundDao: WarehouseOutboundDao,
    context: Context
) : WarehouseOutRepository {

    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> = workManager
        .getWorkInfosByTagLiveData(TAG_OUTPUT)
        .asFlow()
        .mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    override suspend fun getWhOutbound(): WhOutboundResponse =
        mastroAndroidApiService.getAllWhOutbound()

    override fun getAllWhOutboundStream(): Flow<List<WarehouseOutbound>> {
        TODO("Not yet implemented")
    }

    override fun getWhOutboundStream(id: Int): Flow<WarehouseOutbound?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertWhOutbound(whOutbound: WarehouseOutbound) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWhOutbound(whOutbound: WarehouseOutbound) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWhOutbound(whOutbound: WarehouseOutbound) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewWhOutbound(whOutbound: WarehouseOutbound): Response<WhOutboundAddResponse> {
        val body = JsonObject().apply {
            addProperty("clientId", whOutbound.customerId)
        }
        return mastroAndroidApiService.insertNewWhOutbound(body)
    }

    override suspend fun getWhOutboundById(whOutboundId: Int): WhOutboundResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateWhOutboundData(whOutboundState: WhOutboundState): Response<WhOutboundAddResponse> {
        TODO("Not yet implemented")
    }

}
