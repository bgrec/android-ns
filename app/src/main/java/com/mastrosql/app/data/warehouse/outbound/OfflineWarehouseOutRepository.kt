package com.mastrosql.app.data.warehouse.outbound

import androidx.work.WorkInfo
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutboundDao
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundAddResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class OfflineWarehouseOutRepository(
    private val whOutDao: WarehouseOutboundDao, override val outputWorkInfo: Flow<WorkInfo>
) : WarehouseOutRepository {

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    override suspend fun getWhOutbound(): WhOutboundResponse {
        TODO("Not yet implemented")
    }

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
        TODO("Not yet implemented")
    }

    override suspend fun getWhOutboundById(whOutboundId: Int): WhOutboundResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateWhOutboundData(whOutboundState: WhOutboundState): Response<WhOutboundAddResponse> {
        TODO("Not yet implemented")
    }
}
