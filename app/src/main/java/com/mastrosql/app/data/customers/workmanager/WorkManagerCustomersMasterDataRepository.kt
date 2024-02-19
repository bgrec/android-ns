package com.mastrosql.app.data.customers.workmanager

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.worker.CleanupWorker
import com.mastrosql.app.worker.CustomersMasterDataWorker
import com.mastrosql.app.worker.DataSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.concurrent.TimeUnit


class WorkManagerCustomersMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService,
    context: Context

) : CustomersMasterDataRepository {

    private val workManager = WorkManager.getInstance(context)
    val applicationContext = context

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchDataFromServer(): List<CustomerMasterData> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateData(data: List<CustomerMasterData>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteData() {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomersMasterData(): CustomersMasterDataResponse {

        /* var continuation = workManager
             .beginUniqueWork(
                 IMAGE_MANIPULATION_WORK_NAME,
                 ExistingWorkPolicy.REPLACE,
                 OneTimeWorkRequest.from(CleanupWorker::class.java)
             )

         // Create low battery constraint
         val constraints = Constraints.Builder()
             .setRequiredNetworkType(NetworkType.CONNECTED)
             .setRequiresBatteryNotLow(true)
             .build()

         //Add WorkRequest to sync data
         val syncBuilder = PeriodicWorkRequestBuilder<>(

         )*/

        //scheduling the workmanager to run every 1 minutes

        // Create the constraints for low battery and network connectivity
        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresBatteryNotLow(true)

            .build()

        // Create a one-time work request for CleanupWorker
        val cleanupRequest = OneTimeWorkRequest.from(CleanupWorker::class.java)
        //OneTimeWorkRequestBuilder<CleanupWorker<CustomerMasterData>>()
        //.setConstraints(constraints) // You can set constraints if needed
        //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        //.build()

        // Create a periodic work request for DataSyncWorker

        val syncRequest = PeriodicWorkRequestBuilder<CustomersMasterDataWorker>(
            repeatInterval = 1, // Time in minutes
            repeatIntervalTimeUnit = TimeUnit.MINUTES        )
            .setConstraints(constraints)
            //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        // Enqueue the work request
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "Sync Work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            syncRequest
        )


        // Enqueue CleanupWorker as one-time work
        /* workManager.beginUniqueWork(
             "IMAGE_MANIPULATION_WORK_NAME",
             ExistingWorkPolicy.REPLACE,
             cleanupRequest
         ).enqueue()*/

/* ok working
        workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CustomersMasterDataWorker::class.java)
        ).enqueue()*/

        // Create a OneTimeWorkRequest for your worker
        val workRequest = OneTimeWorkRequestBuilder<CustomersMasterDataWorker>()
            .setConstraints(constraints)  // You can customize constraints if needed
            .build()

        // Enqueue the work request
        //WorkManager.getInstance(context).enqueue(workRequest)

        // Cancel all enqueued work
        WorkManager.getInstance(applicationContext).cancelAllWork()

        /*
        * // Cancel all enqueued work
WorkManager.getInstance(applicationContext).cancelAllWork()

// Optionally, you can provide a unique work name to cancel only specific work
// WorkManager.getInstance(applicationContext).cancelAllWorkByTag("uniqueWorkName")
* */


        // Enqueue DataSyncWorker as a separate periodic work
        /*workManager.enqueueUniquePeriodicWork(
            "Sync Work",
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )*/


        // Add WorkRequest to blur the image
        //val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()

        // Input the Uri for the blur operation along with the blur level
        //blurBuilder.setInputData(createInputDataForWorkRequest(blurLevel, imageUri))

        //blurBuilder.setConstraints(constraints)

        //continuation = continuation.then(blurBuilder.build())

        // Add WorkRequest to save the image to the filesystem
        /*val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT)
            .build()*/
        //continuation = continuation.then(save)

        // Actually start the work
        //continuation.enqueue()

        //mastroAndroidApiService.getAllCustomersMasterData()
        return mastroAndroidApiService.getAllCustomersMasterData()
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

        /*An app needs to send a message in a chat app. The app creates a Worker and enqueues the task as a WorkRequest. It expedites the WorkRequest with setExpedited().*/
        /*
        * val continuation = WorkManager.getInstance(context)
    .beginUniqueWork(
        Constants.IMAGE_MANIPULATION_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        OneTimeWorkRequest.from(CleanupWorker::class.java)
    ).then(OneTimeWorkRequest.from(WaterColorFilterWorker::class.java))
    .then(OneTimeWorkRequest.from(GrayScaleFilterWorker::class.java))
    .then(OneTimeWorkRequest.from(BlurEffectFilterWorker::class.java))
    .then(
        if (save) {
            workRequest<SaveImageToGalleryWorker>(tag = Constants.TAG_OUTPUT)
        } else /* upload */ {
            workRequest<UploadWorker>(tag = Constants.TAG_OUTPUT)
        }
    )
    */


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

/*
* AlarmManager	Alarms only.
* Unlike WorkManager, AlarmManager wakes a device from Doze mode.
*  It is therefore not efficient in terms of power and resource management.
*  Only use it for precise alarms or notifications such as calendar events — not background work
* */