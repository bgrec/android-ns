package com.mastrosql.app.data.warehouse.outbound.whoutdetails

import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsDao
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class OfflineWhOutDetailsRepository(
    private val whOutDetailDao: WhOutDetailsDao, override val outputWorkInfo: Flow<WorkInfo>
) : WhOutDetailsRepository {

    override suspend fun getWhOutDetails(whOutId: Int?): WhOutDetailsResponse {
        TODO()
    }

    override suspend fun getAllWhOutDetails(): WhOutDetailsResponse {
        TODO()
    }

    override fun getAllWhOutDetailsStream(): Flow<List<WhOutDetailsItem>> {
        //= whOutDetailDao.getAllWhOutDetails()
        TODO()
    }

    override fun getWhOutDetailsStream(id: Int): Flow<WhOutDetailsItem?> {
        //whOutDetailDao.getWhOutDetailsItemById(id)
        TODO()
    }

    override suspend fun insertWhOutDetails(whOutDetail: WhOutDetailsItem) {
        //whOutDetailDao.insert(whOutDetailsItem)
        TODO()
    }

    override suspend fun deleteWhOutDetails(whOutDetail: WhOutDetailsItem) =
        whOutDetailDao.delete(whOutDetail)

    override suspend fun updateWhOutDetails(whOutDetail: WhOutDetailsItem) =
        whOutDetailDao.update(whOutDetail)

    /**
     * Updates the [MastroAndroidApiService] instance used for network operations.
     *
     * This method allows updating the [MastroAndroidApiService] instance to switch or update
     * the underlying network service used for API calls.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    override suspend fun sendScannedCode(whOutId: Int, scannedCode: String): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDetailItem(whOutDetailId: Int): Response<JsonObject> {
        TODO("Not yet implemented")
    }
    
    override suspend fun updateDetailItem(
        whOutDetailId: Int,
        quantity: Double,
        batch: String,
        expirationDate: String
    ): Response<JsonObject> {
        TODO("Not yet implemented")
    }
}