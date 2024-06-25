package com.mastrosql.app.data.warehouse.outbound.whoutdetails

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsDao
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsItem
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

class NetworkWhOutDetailsRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val whOutDetailsDao: WhOutDetailsDao,
    context: Context
) : WhOutDetailsRepository {


    /**
     * Updates the [MastroAndroidApiService] instance used for network operations.
     *
     * This method updates the [mastroAndroidApiService] instance to the provided
     * [newMastroAndroidApiService], enabling the repository to communicate with
     * the backend using the updated service.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    override suspend fun getWhOutDetails(whOutId: Int?): WhOutDetailsResponse {
        //val filter = "{\"NUME\": $whOutId}"
        //added order by RIGA ASC
        val filter = "{\"\$orderby\": {\"RIGA\": \"ASC\"}, \"NUME\" : {\"\$eq\": $whOutId}}"

        return mastroAndroidApiService.getWhOutDetails(filter)
    }

    override suspend fun getAllWhOutDetails(): WhOutDetailsResponse =
        mastroAndroidApiService.getAllWhOutDetails()

    override fun getAllWhOutDetailsStream(): Flow<List<WhOutDetailsItem>> {
        TODO("Not yet implemented")
    }

    override fun getWhOutDetailsStream(id: Int): Flow<WhOutDetailsItem?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertWhOutDetails(whOutDetail: WhOutDetailsItem) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWhOutDetails(whOutDetail: WhOutDetailsItem) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWhOutDetails(whOutDetail: WhOutDetailsItem) {
        TODO("Not yet implemented")
    }

    override suspend fun sendScannedCode(whOutId: Int, scannedCode: String): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("whOutId", whOutId)
            addProperty("scannedCode", scannedCode)
        }
        return mastroAndroidApiService.sendWhOutScannedCode(body)
    }

    override suspend fun deleteDetailItem(whOutDetailId: Int): Response<JsonObject> {
        val filter = "{\"numePro\": $whOutDetailId}"

        return mastroAndroidApiService.deleteWhOutDetailItem(filter)
    }

    override suspend fun duplicateDetailItem(whOutDetailId: Int): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("whOutDetailId", whOutDetailId)
        }
        return mastroAndroidApiService.duplicateOrderDetailItem(body)
    }

    override suspend fun updateDetailItem(
        whOutDetailId: Int, quantity: Double, batch: String, expirationDate: String
    ): Response<JsonObject> {
        val expirationDateFormated = if (expirationDate == "") {
            "null"
        } else {
            expirationDate
        }
        val body = JsonObject().apply {
            addProperty("whOutDetailId", whOutDetailId)
            addProperty("quantity", quantity)
            addProperty("batch", batch)
            addProperty("expirationDate", expirationDateFormated)
        }
        return mastroAndroidApiService.updateOrderDetailItem(body)
    }
}
