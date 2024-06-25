package com.mastrosql.app.data.warehouse.outbound


import androidx.work.WorkInfo
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundAddResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WhOutboundResponse
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WarehouseOutRepository {

    /**
     * A [Flow] of [WorkInfo] representing the output of the work.
     */
    val outputWorkInfo: Flow<WorkInfo>

    /**
     * Update the [MastroAndroidApiService] to be used by the repository.
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)

    /**
     * Retrieves a list of all warehouse outbounds.
     */
    suspend fun getWhOutbound(): WhOutboundResponse

    /**
     * Retrieves a flow of all warehouse outbounds.
     */
    fun getAllWhOutboundStream(): Flow<List<WarehouseOutbound>>

    /**
     * Retrieves a flow of a warehouse outbound order identified by [id].
     */
    fun getWhOutboundStream(id: Int): Flow<WarehouseOutbound?>

    /**
     * Inserts a new warehouse outbound.
     */
    suspend fun insertWhOutbound(whOutbound: WarehouseOutbound)

    /**
     * Deletes a warehouse outbound.
     */
    suspend fun deleteWhOutbound(whOutbound: WarehouseOutbound)

    /**
     * Updates a warehouse outbound.
     */
    suspend fun updateWhOutbound(whOutbound: WarehouseOutbound)

    /**
     * Retrieves a warehouse outbound order by [id].
     */
    suspend fun addNewWhOutbound(whOutbound: WarehouseOutbound): Response<WhOutboundAddResponse>

    /**
     * Retrieves a warehouse outbound order by [id].
     */
    suspend fun getWhOutboundById(whOutboundId: Int): WhOutboundResponse

    /**
     * Retrieves a warehouse outbound order by [id].
     */
    suspend fun updateWhOutboundData(whOutboundState: WhOutboundState): Response<WhOutboundAddResponse>

}
