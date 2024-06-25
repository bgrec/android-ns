package com.mastrosql.app.data.warehouse.outbound.whoutdetails

import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WhOutDetailsRepository {

    val outputWorkInfo: Flow<WorkInfo>

    suspend fun getWhOutDetails(whOutId: Int?): WhOutDetailsResponse

    suspend fun getAllWhOutDetails(): WhOutDetailsResponse
    //suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

    fun getAllWhOutDetailsStream(): Flow<List<WhOutDetailsItem>>

    fun getWhOutDetailsStream(id: Int): Flow<WhOutDetailsItem?>

    suspend fun insertWhOutDetails(whOutDetail: WhOutDetailsItem)

    suspend fun deleteWhOutDetails(whOutDetail: WhOutDetailsItem)

    suspend fun updateWhOutDetails(whOutDetail: WhOutDetailsItem)

    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)

    suspend fun sendScannedCode(whOutId: Int, scannedCode: String): Response<JsonObject>

    suspend fun deleteDetailItem(whOutDetailId: Int): Response<JsonObject>
    
    suspend fun updateDetailItem(
        whOutDetailId: Int, quantity: Double, batch: String, expirationDate: String
    ): Response<JsonObject>
}