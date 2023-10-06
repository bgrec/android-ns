package com.mastrosql.app.data.customer

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse
import com.mastrosql.app.worker.CleanupWorker
import com.mastrosql.app.worker.DataSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkCustomersMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService,
    private val customerMasterDataDao: CustomersMasterDataDao,
    context: Context
) : CustomersMasterDataRepository {

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse =
        mastroAndroidApiService.getAllCustomersMasterData()

    override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) {

    }

    override suspend fun fetchDataFromServer(): List<CustomerMasterData> {
        val customerMasterDataResponse = getCustomersMasterData()
        return customerMasterDataResponse.items
    }

    override suspend fun insertOrUpdateData(data: List<CustomerMasterData>) {
        customerMasterDataDao.insertAll(data)
    }

    override suspend fun deleteData() {
        customerMasterDataDao.deleteAll()
    }

    fun syncData() {
        // Add WorkRequest to Cleanup temporary images
        //beginWork not unique
        var continuation = workManager
            .beginUniqueWork(
                "IMAGE_MANIPULATION_WORK_NAME",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )

        // Create low battery constraint
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Add WorkRequest to blur the image
        val syncDataBuilder = OneTimeWorkRequestBuilder<DataSyncWorker<CustomerMasterData>>()

        // Input the Uri for the blur operation along with the blur level
        //syncDataBuilder.setInputData(createInputDataForWorkRequest(blurLevel, imageUri))

        syncDataBuilder.setConstraints(constraints)

        continuation = continuation.then(syncDataBuilder.build())

        /*
         // Add WorkRequest to save the image to the filesystem

         val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
             .addTag(TAG_OUTPUT)
             .build()
         continuation = continuation.then(save)
         */

        // Actually start the work
        continuation.enqueue()


    }

}
/*
class TwitchRepoImpl(
    private val twitchClient: TwitchClient = TwitchRetrofitInstance.api
) {
suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<FollowedLiveStreams>> = flow{
        emit(Response.Loading)
        val response = twitchClient.getFollowedStreams(
            authorization = authorizationToken,
            clientId = clientId,
            userId = userId
        )
        if (response.isSuccessful){
           emit(response.body())
        }else{
          emit(Response.Failure(Exception("Error!, code {${response.code()}}")))

        }
    }

}
sealed class Response<out T> {


    object Loading: Response<Nothing>()


    data class Success<out T>(
        val data:T
    ):Response<T>()


    data class Failure(
        val e:Exception
    ):Response<Nothing>()

}
 */